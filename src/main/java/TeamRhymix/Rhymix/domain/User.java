package TeamRhymix.Rhymix.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L; //기본값으로 설정 -> 세션 직렬화 시 사용

    @Id
    private String id; // MongoDB 고유 식별자
    private String username; // 사용자 이름 (예: 홍길동)
    private String email;
    private String password;
    private String nickname; // 로그인 및 블로그용 아이디
    private String bio;
    private String profileImage;
    private String phone;
    private Date joinedAt;
    @Builder.Default
    private List<String> preferredGenres = new ArrayList<>();
    @Builder.Default
    private List<String> neighbors = new ArrayList<>();
    private String selectedTheme;
    private String diaryTitle;
    private String diaryContent;
    private String diaryImage;


}