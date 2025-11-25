package com.tramasys.auth.application.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private String id;
    private String name;
    private String description;
}
