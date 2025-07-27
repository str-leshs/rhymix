package TeamRhymix.Rhymix.domain.track.service;

import TeamRhymix.Rhymix.domain.track.entity.Track;

public interface TrackService {
    Track findOrSaveTrack(String spotifyTrackId);
    Track findByTrackId(String trackId);
}

