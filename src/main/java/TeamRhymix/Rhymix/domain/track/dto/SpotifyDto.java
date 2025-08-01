package TeamRhymix.Rhymix.domain.track.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpotifyDto {
    private String access_token;
    private String token_type;
    private int expires_in;
}
