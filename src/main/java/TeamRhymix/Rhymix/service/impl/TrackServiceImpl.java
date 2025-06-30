package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.domain.Track;
import TeamRhymix.Rhymix.dto.SpotifyTrackDto;
import TeamRhymix.Rhymix.repository.TrackRepository;
import TeamRhymix.Rhymix.service.SpotifySearchService;
import TeamRhymix.Rhymix.service.TrackService;
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
                            .album("Unknown")
                            .coverImage(dto.getAlbumImageUrl())
                            .duration(180)
                            .build();
                    return trackRepository.save(newTrack);
                });
    }

    @Override
    public Track findByTrackId(String trackId) {
        return trackRepository.findByTrackId(trackId)
                .orElseThrow(() -> new IllegalArgumentException("해당 trackId에 대한 Track 정보가 없습니다: " + trackId));
    }

}
