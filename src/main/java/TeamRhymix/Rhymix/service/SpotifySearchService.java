package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.dto.SpotifyTrackDto;

import java.util.List;

public interface SpotifySearchService {
    SpotifyTrackDto getTrackDetail(String trackId);
    List<SpotifyTrackDto> searchTracks(String query);
}
