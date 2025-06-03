package TeamRhymix.Rhymix.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Comment {
    private String text;
    private String userNickname;
    private LocalDateTime createdAt;
}
