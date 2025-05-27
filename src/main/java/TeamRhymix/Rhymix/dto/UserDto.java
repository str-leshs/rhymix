package TeamRhymix.Rhymix.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username; // 사용자 이름
    private String nickname; // 로그인 아이디
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;
}