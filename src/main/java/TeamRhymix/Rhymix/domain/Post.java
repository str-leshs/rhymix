package TeamRhymix.Rhymix.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")  // MongoDB의 posts 컬렉션에 저장
public class Post {

    @Id
    private String id;
    private String userId;         // 유저 아이디 (사용자와 연결)
    private String title;          // 곡 제목
    private String artist;         // 아티스트명
    private String cover;          // 커버 이미지 URL
    private String weather;        // 날씨
    private String mood;           // 기분
    private String comment;        // 코멘트
    private LocalDateTime createdAt;  // 등록 시간

}