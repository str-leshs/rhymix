package TeamRhymix.Rhymix.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private String text;
    private String userNickname;
    private LocalDateTime createdAt;
}
