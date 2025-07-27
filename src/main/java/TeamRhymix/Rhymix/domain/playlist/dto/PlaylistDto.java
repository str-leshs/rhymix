package TeamRhymix.Rhymix.domain.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDto {
    private String id;
    private String title;
    private String type;
    List<PlaylistTrackInfo> tracks;
}

