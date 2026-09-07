package com.medsync.doctor_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String specialization;

    private Integer experience;

    private String department;

    private Double consultationFee;

    private String phone;

    private String availability;
}
