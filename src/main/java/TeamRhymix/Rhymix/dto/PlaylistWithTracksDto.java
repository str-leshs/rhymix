package TeamRhymix.Rhymix.dto;

import TeamRhymix.Rhymix.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistWithTracksDto {
    private String id;
    private String title;
    private String type;
    private List<Post> tracks;
}

