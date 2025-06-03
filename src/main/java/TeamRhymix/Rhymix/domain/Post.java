package TeamRhymix.Rhymix.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {

    @Id
    private String id;
    private String userId;
    private String title;
    private String artist;
    private String cover;
    private String weather;
    private String mood;
    private String comment;


    //  댓글 목록
    private List<Comment> comments;

    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private String text;
        private String userNickname;
        private LocalDateTime createdAt;
    }
}
