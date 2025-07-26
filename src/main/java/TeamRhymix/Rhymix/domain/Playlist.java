package TeamRhymix.Rhymix.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "playlists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {

    @Id
    private String id;

    private String userId;

    private String title;

    private String type;

    private List<String> postIds;

    private LocalDateTime createdAt;
}
