package TeamRhymix.Rhymix.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String username; // 사용자 이름
    private String nickname; // 로그인 아이디
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;
}