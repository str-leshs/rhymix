package TeamRhymix.Rhymix.domain.post.service;

import TeamRhymix.Rhymix.domain.track.dto.SpotifyTrackDto;

import java.util.List;

public interface SpotifySearchService {
    SpotifyTrackDto getTrackDetail(String trackId);
    List<SpotifyTrackDto> searchTracks(String query);
}
