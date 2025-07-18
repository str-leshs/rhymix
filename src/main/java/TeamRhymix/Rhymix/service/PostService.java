package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.dto.PostRequestDto;

import java.time.LocalDate;
import java.util.List;

public interface PostService {
    /**
     * 오늘의 추천곡 저장 (기존 포스트가 있으면 수정, 없으면 새로 생성)
     */
    Post savePost(PostRequestDto request, String userId);


    /**
     * 특정 유저의 오늘의 추천곡을 조회
     */
    Post getTodayPost(String userId);

    /**
     * 특정 사용자가 등록한 모든 추천곡 조회
     */
    List<Post> getPostsByUserId(String userId);

    /**
     * 특정 사용자가 등록한 특정 추천곡 포스트 조회
     */
    Post getPostByDate(String userId, LocalDate date);

    List<Post> findPostsByUserAndMonth(String userId, LocalDate start, LocalDate end);

}
