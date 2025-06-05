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

    //장르 정규화: 모두 소문자화, 공백/하이픈 제거
    public void normalizeGenres() {
        if (preferredGenres != null) {
            preferredGenres = preferredGenres.stream()
                    .filter(g -> g != null)
                    .map(g -> g.toLowerCase().replaceAll("[\\s\\-]", ""))
                    .toList();
        }
    }
}
