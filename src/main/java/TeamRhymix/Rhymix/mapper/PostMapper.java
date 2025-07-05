package TeamRhymix.Rhymix.mapper;

import TeamRhymix.Rhymix.domain.Post;
import TeamRhymix.Rhymix.dto.PostDto;
import TeamRhymix.Rhymix.repository.TrackRepository;
import TeamRhymix.Rhymix.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final TrackRepository trackRepository;

    public PostDto toDto(Post post) {
        PostDto dto = new PostDto();

        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setTrackId(post.getTrackId());
        dto.setWeather(post.getWeather());
        dto.setMood(post.getMood());
        dto.setComment(post.getComment());
        dto.setChats(post.getChats());

        // 트랙 정보 추가
        trackRepository.findByTrackId(post.getTrackId()).ifPresent(track -> {
            dto.setTrackTitle(track.getTitle());
            dto.setTrackArtist(track.getArtist());
            dto.setAlbum(track.getAlbum());
            dto.setCoverImage(track.getCoverImage());
            dto.setDuration(track.getDuration());
        });

        return dto;
    }
}
