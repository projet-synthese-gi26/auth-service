package com.tramasys.auth.application.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionUpdateRequest {
    private String description;
}
