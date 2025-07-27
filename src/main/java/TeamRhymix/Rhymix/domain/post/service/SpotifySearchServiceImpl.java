package TeamRhymix.Rhymix.domain.post.service;

import TeamRhymix.Rhymix.domain.track.dto.SpotifyTrackDto;
import TeamRhymix.Rhymix.domain.auth.service.SpotifyAuthService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpotifySearchServiceImpl implements SpotifySearchService {

    private final SpotifyAuthService spotifyAuthService;

    @Override
    public SpotifyTrackDto getTrackDetail(String trackId) {
        String accessToken = spotifyAuthService.getAccessToken();
        String url = "https://api.spotify.com/v1/tracks/" + trackId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        JSONObject json = new JSONObject(response.getBody());

        return SpotifyTrackDto.builder()
                .trackId(json.getString("id"))
                .title(json.getString("name"))
                .artist(json.getJSONArray("artists").getJSONObject(0).getString("name"))
                .album(json.getJSONObject("album").getString("name"))
                .albumImageUrl(json.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url"))
                .duration(json.getInt("duration_ms") / 1000)
                .build();
    }

    @Override
    public List<SpotifyTrackDto> searchTracks(String query) {
        String accessToken = spotifyAuthService.getAccessToken();
        String url = "https://api.spotify.com/v1/search?q=" + query + "&type=track&limit=10";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        JSONObject json = new JSONObject(response.getBody());
        JSONArray items = json.getJSONObject("tracks").getJSONArray("items");

        List<SpotifyTrackDto> result = new ArrayList<>();

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);

            SpotifyTrackDto dto = SpotifyTrackDto.builder()
                    .trackId(item.getString("id"))
                    .title(item.getString("name"))
                    .artist(item.getJSONArray("artists").getJSONObject(0).getString("name"))
                    .album(item.getJSONObject("album").getString("name"))
                    .albumImageUrl(item.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url"))
                    .duration(item.getInt("duration_ms") / 1000)
                    .build();

            result.add(dto);
        }

        return result;
    }

}

