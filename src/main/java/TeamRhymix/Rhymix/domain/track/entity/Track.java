package TeamRhymix.Rhymix.domain.track.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tracks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {

    @Id
    private String id;  //몽고디비 ID

    private String trackId; //스포티파이 트랙 ID
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String coverImage;
    private int duration;
}

