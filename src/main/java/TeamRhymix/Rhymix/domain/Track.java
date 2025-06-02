package TeamRhymix.Rhymix.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {
    private String title;
    private String artist;
    private String cover;
    private String weather;
    private String mood;
    private String comment;
}

