package com.tramasys.auth.application.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionCreateRequest {
    private String name; // service:object:action
    private String description;
}
