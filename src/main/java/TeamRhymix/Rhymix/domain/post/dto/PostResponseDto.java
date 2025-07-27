package TeamRhymix.Rhymix.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponseDto {
    private String postId;
    private String trackTitle;
    private String trackArtist;
    private String album;
    private String coverImage;
    private int duration;

    private String mood;
    private String weather;
    private String comment;
}
