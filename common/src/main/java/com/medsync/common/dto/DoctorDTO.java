package com.medsync.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

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
