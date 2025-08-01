package TeamRhymix.Rhymix.domain.track.repository;

import TeamRhymix.Rhymix.domain.track.entity.Track;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrackRepository extends MongoRepository<Track, String> {
    Optional<Track> findByTrackId(String trackId);
}
