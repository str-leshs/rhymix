package TeamRhymix.Rhymix.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private String username; // 사용자 이름
    private String nickname; // 로그인 아이디
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;
    private String bio;
    private String profileImage;
    private List<String> preferredGenres;
}