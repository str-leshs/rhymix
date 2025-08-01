package TeamRhymix.Rhymix.domain.post.entity;

import TeamRhymix.Rhymix.domain.chat.entity.Chat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "posts")  // MongoDB의 posts 컬렉션에 저장
public class Post {

    @Id
    private String id;
    private String userId;         // 유저 아이디 (사용자와 연결)
    private String trackId;

    private String weather;        // 날씨
    private String mood;           // 기분
    private String comment;        // 코멘트
    private LocalDateTime createdAt;  // 등록 시간
    private List<Chat> chats = new ArrayList<>(); // 댓글


}