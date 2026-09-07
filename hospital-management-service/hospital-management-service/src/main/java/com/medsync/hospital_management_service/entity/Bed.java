package com.medsync.hospital_management_service.entity;

import com.medsync.common.enums.BedStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bed_number", nullable = false)
    private String bedNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BedStatus status;

    @Column(name = "patient_id")
    private Long patientId;
}