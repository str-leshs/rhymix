package TeamRhymix.Rhymix.mapper;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toEntity(PostDto dto) {
        Post post = new Post();
        post.setUserId(dto.getUserId());
        post.setTitle(dto.getTitle());
        post.setArtist(dto.getArtist());
        post.setCover(dto.getCover());
        post.setWeather(dto.getWeather());
        post.setMood(dto.getMood());
        post.setComment(dto.getComment());
        post.setCreatedAt(java.time.LocalDateTime.now());
        return post;
    }

    public PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setArtist(post.getArtist());
        dto.setCover(post.getCover());
        dto.setWeather(post.getWeather());
        dto.setMood(post.getMood());
        dto.setComment(post.getComment());
        return dto;
    }
}
