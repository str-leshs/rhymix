package TeamRhymix.Rhymix.domain.chat.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Chat {
    private String userNickname;     // 댓글 단 사람
    private String text;             // 댓글 내용
    private LocalDateTime createdAt; // 댓글 시간
}