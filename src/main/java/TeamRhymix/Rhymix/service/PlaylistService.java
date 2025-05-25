package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.Playlist;
import TeamRhymix.Rhymix.dto.PlaylistWithTracksDto;

public interface PlaylistService {
    Playlist generateMonthlyPlaylist(String nickname, int year, int month);

    PlaylistWithTracksDto getPlaylistWithTracks(String playlistId);

    Playlist findLatestMonthlyPlaylistByNickname(String nickname);


}
