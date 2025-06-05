package TeamRhymix.Rhymix.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String nickname;
    private String password;
}
