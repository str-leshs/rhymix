package TeamRhymix.Rhymix.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    @NotBlank(message = "이름은 필수입니다.")
    private String username;

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 12, message = "아이디는 4자 이상 12자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문자와 숫자만 입력 가능합니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String confirmPassword;

    private String phone;
    private String bio;
    private String profileImage;
    private List<String> preferredGenres;
    private String selectedTheme;

    // 장르 정규화
    public void normalizeGenres() {
        if (preferredGenres != null) {
            preferredGenres = preferredGenres.stream()
                    .filter(g -> g != null)
                    .map(g -> g.toLowerCase().replaceAll("[\\s\\-]", ""))
                    .toList();
        }
    }
}
