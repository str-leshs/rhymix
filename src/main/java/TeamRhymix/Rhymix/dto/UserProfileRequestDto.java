package TeamRhymix.Rhymix.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserProfileRequestDto {
    private String email;
    private String nickname;
    private String bio;
    private String profileImage;
    private List<String> preferredGenres;
}
