package com.tramasys.auth.application.dto.response;

import com.tramasys.auth.domain.model.TramasysService;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private TramasysService service; // <--- Add this
    private Set<String> roles;
    private Set<String> permissions;
}