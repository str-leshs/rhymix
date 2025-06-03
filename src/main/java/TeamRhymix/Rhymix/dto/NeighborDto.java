package TeamRhymix.Rhymix.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

//프론트에 전달할 이웃 DTO
@Getter
@Setter
@AllArgsConstructor
public class NeighborDto {
    private String nickname;
    private String profileImage;
    private List<String> genres;
}
