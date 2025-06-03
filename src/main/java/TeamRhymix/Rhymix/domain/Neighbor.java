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
    public static class MyNeighbor {    // 이웃 관계(Neighbor) 안의 구성 요소로써 이웃 하나(MyNeighbor) 정의하기 위해 중첩클래스 사용했음
        private String nickname;
        private String profileImage;
    }
}
