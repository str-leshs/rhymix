package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.Track;

public interface TrackService {
    Track findOrSaveTrack(String spotifyTrackId);
    Track findByTrackId(String trackId);
}

