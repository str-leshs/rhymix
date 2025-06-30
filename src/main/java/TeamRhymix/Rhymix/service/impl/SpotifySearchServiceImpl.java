package TeamRhymix.Rhymix.service.impl;

import TeamRhymix.Rhymix.dto.SpotifyTrackDto;
import TeamRhymix.Rhymix.service.SpotifyAuthService;
import TeamRhymix.Rhymix.service.SpotifySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

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
}

