package TeamRhymix.Rhymix.dto;

import TeamRhymix.Rhymix.domain.Chat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDto {
    private String id;
    private String userId;         // 유저 아이디 (사용자와 연결)
    private String title;          // 곡 제목
    private String artist;         // 아티스트명
    private String cover;          // 커버 이미지 URL
    private String weather;        // 날씨
    private String mood;           // 기분
    private String comment;        // 코멘트
    private List<Chat> chats;      // 댓글 목록
}
