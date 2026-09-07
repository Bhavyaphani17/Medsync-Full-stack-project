package com.medsync.auth_service.dto;

import com.medsync.common.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
}
