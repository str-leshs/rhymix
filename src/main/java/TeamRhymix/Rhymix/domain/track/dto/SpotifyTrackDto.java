package TeamRhymix.Rhymix.domain.track.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SpotifyTrackDto {
    //Spotify API에서 받아올 트랙 정보와 매핑되는 DTO
    private String trackId;
    private String title;
    private String artist;
    private String album;
    private String albumImageUrl;
    private int duration; // (초 단위)
}
