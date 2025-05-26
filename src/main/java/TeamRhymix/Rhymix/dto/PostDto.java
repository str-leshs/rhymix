package TeamRhymix.Rhymix.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDto {
    private String userId;         // 유저 아이디 (사용자와 연결)
    private String title;          // 곡 제목
    private String artist;         // 아티스트명
    private String cover;          // 커버 이미지 URL
    private String weather;        // 날씨
    private String mood;           // 기분
    private String comment;        // 코멘트
}
