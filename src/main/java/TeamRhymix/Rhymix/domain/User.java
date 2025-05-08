package TeamRhymix.Rhymix.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id; // MongoDB 고유 식별자
    private String username; // 사용자 이름 (예: 홍길동)
    private String email;
    private String password;
    private String nickname; // 로그인 및 블로그용 아이디 (예: gildong2)
    private String bio;
    private String profileImage;
    private Date joinedAt;
    private List<String> preferredGenres;
}