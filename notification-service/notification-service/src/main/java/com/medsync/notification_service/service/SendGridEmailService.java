package com.medsync.notification_service.service;

import com.medsync.notification_service.dto.AppointmentConfirmationEmailRequest;
import com.medsync.notification_service.dto.DoctorScheduleEmailRequest;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SendGridEmailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${sendgrid.from-name}")
    private String fromName;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");

    public void sendAppointmentConfirmation(AppointmentConfirmationEmailRequest request) {
        String subject = "Appointment Confirmed - MedSync";
        String body = """
                Dear %s,

                Your appointment with Dr. %s has been confirmed for %s.

                Please arrive 10 minutes early. If you need to reschedule or cancel, please contact us.

                Thank you,
                MedSync Hospital
                """.formatted(
                request.getPatientName(),
                request.getDoctorName(),
                request.getAppointmentDate().format(DATE_FORMATTER)
        );

        send(request.getPatientEmail(), subject, body);
    }

    public void sendDoctorDailySchedule(DoctorScheduleEmailRequest request) {
        String subject = "Your Appointment Schedule - MedSync";
        String appointmentList = request.getAppointments().stream()
                .map(entry -> "- %s at %s".formatted(entry.getPatientName(), entry.getTime()))
                .collect(Collectors.joining("\n"));

        String body = """
                Dear Dr. %s,

                Here is your appointment schedule for today:

                %s

                Thank you,
                MedSync Hospital
                """.formatted(request.getDoctorName(), appointmentList);

        send(request.getDoctorEmail(), subject, body);
    }

    private void send(String toEmail, String subject, String body) {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                log.error("SendGrid returned error status {}: {}", response.getStatusCode(), response.getBody());
            } else {
                log.info("Email sent to {} with subject '{}'", toEmail, subject);
            }
        } catch (IOException ex) {
            log.error("Failed to send email to {}: {}", toEmail, ex.getMessage());
        }
    }
}