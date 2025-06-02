package TeamRhymix.Rhymix.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "neighbors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeighborRelation {
    @Id
    private String id;
    private String username;       // 나 (로그인한 사용자)
    private String neighborName;   // 이웃 (상대 닉네임)

}
