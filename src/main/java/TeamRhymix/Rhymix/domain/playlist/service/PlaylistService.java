package TeamRhymix.Rhymix.domain.playlist.service;

import TeamRhymix.Rhymix.domain.playlist.entity.Playlist;
import TeamRhymix.Rhymix.domain.playlist.dto.PlaylistDto;

public interface PlaylistService {
    Playlist generateMonthlyPlaylist(String nickname, int year, int month);

    PlaylistDto getPlaylistWithTracks(String playlistId);

    Playlist findLatestMonthlyPlaylistByNickname(String nickname);

    PlaylistDto generateThemePlaylist(String nickname, String tag);

    PlaylistDto getThemePlaylistPreview(String nickname, String tag);
}
