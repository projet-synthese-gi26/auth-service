package com.tramasys.auth.application.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String identifier; // username OR phone OR email
    private String password;
}
