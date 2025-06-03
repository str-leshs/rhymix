package TeamRhymix.Rhymix.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private String username;         // 사용자 이름
    private String nickname;         // 로그인 아이디
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;
    private String bio;
    private String profileImage;
    private List<String> preferredGenres;
    private String selectedTheme;

    // 전체 필드 생성자
    public UserDto(String username, String nickname, String email, String password,
                   String confirmPassword, String phone, String bio,
                   String profileImage, List<String> preferredGenres, String selectedTheme) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phone = phone;
        this.bio = bio;
        this.profileImage = profileImage;
        this.preferredGenres = preferredGenres;
        this.selectedTheme = selectedTheme;
    }
}
