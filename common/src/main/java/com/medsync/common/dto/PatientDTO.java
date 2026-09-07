package com.medsync.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private String gender;
    private String disease;
    private Long doctorId;
    private String doctorName;
}
