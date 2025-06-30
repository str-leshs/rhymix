package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.dto.SpotifyTrackDto;

public interface SpotifySearchService {
    SpotifyTrackDto getTrackDetail(String trackId);
}
