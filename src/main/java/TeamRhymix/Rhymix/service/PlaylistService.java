package TeamRhymix.Rhymix.service;

import TeamRhymix.Rhymix.domain.Playlist;
import TeamRhymix.Rhymix.dto.PlaylistDto;

public interface PlaylistService {
    Playlist generateMonthlyPlaylist(String nickname, int year, int month);

    PlaylistDto getPlaylistWithTracks(String playlistId);

    Playlist findLatestMonthlyPlaylistByNickname(String nickname);

    PlaylistDto generateThemePlaylist(String nickname, String tag);

    PlaylistDto getThemePlaylistPreview(String nickname, String tag);
}
