package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.mapper.PostMapper;
import TeamRhymix.Rhymix.repository.PostRepository;
import TeamRhymix.Rhymix.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    /**
     * 오늘 날짜 기준으로 추천곡이 이미 등록되어 있으면 수정, 없으면 새로 저장합니다.
     */
    @Override
    public Post savePost(PostDto dto) {
        String userId = dto.getUserId();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        Post existing = postRepository.findTodayPostByUserId(userId, startOfDay, endOfDay);

        if (existing != null) {
            existing.setTitle(dto.getTitle());
            existing.setArtist(dto.getArtist());
            existing.setCover(dto.getCover());
            existing.setMood(dto.getMood());
            existing.setWeather(dto.getWeather());
            existing.setComment(dto.getComment());
            existing.setCreatedAt(LocalDateTime.now());
            return postRepository.save(existing);
        } else {
            Post newPost = postMapper.toEntity(dto);
            newPost.setCreatedAt(LocalDateTime.now());
            return postRepository.save(newPost);
        }
    }

    /**
     * 오늘 날짜 기준으로 특정 사용자의 추천곡을 조회합니다.
     */
    @Override
    public Post getTodayPost(String userId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);
        return postRepository.findTodayPostByUserId(userId, start, end);
    }


    @Override
    public List<Post> getPostsByUserId(String userId) {
        return postRepository.findByUserId(userId);
    }


}
