package TeamRhymix.Rhymix.dto;

import TeamRhymix.Rhymix.domain.Chat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDto {
    private String id;
    private String userId;
    private String trackId;
    private String trackTitle;
    private String trackArtist;
    private String album;
    private String coverImage;
    private int duration;

    private String weather;
    private String mood;
    private String comment;
    private List<Chat> chats;
}
