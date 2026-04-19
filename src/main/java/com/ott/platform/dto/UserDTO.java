package com.ott.platform.dto;

import com.ott.platform.model.User;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserDTO {
    private Long   userId;
    private String name;
    private String email;
    private String userType;

    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getUserId(), user.getName(), user.getEmail(), user.getUserType());
    }
}
