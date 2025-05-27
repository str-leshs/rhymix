package TeamRhymix.Rhymix.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class NeighborDto {
    private String nickname;
    private String profileImage;
    private List<String> genres;
}
