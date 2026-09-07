package com.medsync.storage_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final ITemplateEngine templateEngine;

    public byte[] generatePdf(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process(templateName, context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }
}