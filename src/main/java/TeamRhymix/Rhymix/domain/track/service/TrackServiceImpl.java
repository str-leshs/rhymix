package TeamRhymix.Rhymix.domain.track.service;

import TeamRhymix.Rhymix.domain.track.entity.Track;
import TeamRhymix.Rhymix.domain.track.dto.SpotifyTrackDto;
import TeamRhymix.Rhymix.domain.track.repository.TrackRepository;
import TeamRhymix.Rhymix.domain.post.service.SpotifySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;
    private final SpotifySearchService spotifySearchService;

    @Override
    public Track findOrSaveTrack(String spotifyTrackId) {
        return trackRepository.findByTrackId(spotifyTrackId)
                .orElseGet(() -> {
                    SpotifyTrackDto dto = spotifySearchService.getTrackDetail(spotifyTrackId);
                    Track newTrack = Track.builder()
                            .trackId(dto.getTrackId())
                            .title(dto.getTitle())
                            .artist(dto.getArtist())
                            .album(dto.getAlbum()) // <- 실제 앨범명
                            .coverImage(dto.getAlbumImageUrl())
                            .duration(dto.getDuration()) // <- ms → s 변환 완료 상태
                            .build();
                    return trackRepository.save(newTrack);
                });
    }

    @Override
    public Track findByTrackId(String trackId) {
        if (trackId == null || trackId.isBlank()) {
            throw new IllegalArgumentException("trackId는 null일 수 없습니다.");
        }

        return trackRepository.findByTrackId(trackId)
                .orElseThrow(() -> new IllegalArgumentException("해당 trackId에 대한 Track 정보가 없습니다: " + trackId));
    }


}
