package TeamRhymix.Rhymix.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TrackDto {
    //트랙 정보 전달용 (Spotify 응답 기반)
    private String trackId;  // Spotify 트랙 ID
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String coverImage;
    private int duration;
}
