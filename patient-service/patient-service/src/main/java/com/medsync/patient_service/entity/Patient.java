package com.medsync.patient_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private Integer age;

    private String gender;

    private String disease;

    @Column(name = "doctor_id")
    private Long doctorId;
}
