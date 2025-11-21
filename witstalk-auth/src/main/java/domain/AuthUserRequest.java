package domain;

import lombok.Data;

@Data
public class AuthUserRequest {
    private Long id;
    private String username;
    private String nickName;
    private String password;
}
