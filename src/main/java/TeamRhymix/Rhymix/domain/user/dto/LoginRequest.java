package TeamRhymix.Rhymix.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String nickname;
    private String password;
}
