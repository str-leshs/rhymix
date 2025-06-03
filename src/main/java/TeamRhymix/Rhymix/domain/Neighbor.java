package TeamRhymix.Rhymix.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

//이웃 관계를 저장 도메인
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "neighbors")
public class Neighbor {

    @Id
    private String id;
    private String ownerNickname;
    private List<String> neighbors;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyNeighbor {
        private String nickname;
        private String profileImage;
    }
}
