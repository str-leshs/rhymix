package TeamRhymix.Rhymix.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    //오늘의 추천곡을 등록할 때 사용하는 DTO
    private String trackId;   // 사용자가 선택한 Spotify track ID
    private String mood;
    private String weather;
    private String comment;
}
