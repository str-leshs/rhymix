package TeamRhymix.Rhymix.mapper;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final TrackService trackService; // trackId 기반 조회용

    public PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setTrackId(post.getTrackId());  // trackId만 설정
        dto.setMood(post.getMood());
        dto.setWeather(post.getWeather());
        dto.setComment(post.getComment());
        dto.setChats(post.getChats());
        return dto;
    }

    public Post toEntity(PostDto dto) {
        Post post = new Post();
        post.setId(dto.getId());
        post.setUserId(dto.getUserId());
        post.setTrackId(dto.getTrackId());  // trackId만 저장
        post.setMood(dto.getMood());
        post.setWeather(dto.getWeather());
        post.setComment(dto.getComment());
        post.setChats(dto.getChats());
        post.setCreatedAt(LocalDateTime.now());
        return post;
    }

}
