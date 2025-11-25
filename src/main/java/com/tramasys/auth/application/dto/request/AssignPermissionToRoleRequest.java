package com.tramasys.auth.application.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignPermissionToRoleRequest {
    private String permissionName;
}
