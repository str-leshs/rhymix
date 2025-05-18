package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PostService {
    /**
     * 오늘의 추천곡 저장 (기존 포스트가 있으면 수정, 없으면 새로 생성)
     */
    Post savePost(PostDto dto);

    /**
     * 특정 유저의 오늘의 추천곡을 조회
     */
    Post getTodayPost(String userId);

    /**
     * 특정 사용자가 등록한 모든 추천곡 조회
     */
    List<Post> getPostsByUserId(String userId);

}
