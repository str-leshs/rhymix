package TeamRhymix.Rhymix.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponseDto {
    //추천글 등록 후 프론트로 넘길 응답 DTO
    private String postId;
    private String trackTitle;
    private String trackArtist;
    private String mood;
    private String weather;
    private String comment;
}
