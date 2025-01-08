package sf.shortlinks.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateLinkDto(
        @JsonProperty("url")
        String url,
        @JsonProperty("shortUrl")
        String shortUrl,
        @JsonProperty("ownerUid")
        UUID ownerUid,
        @JsonProperty("duration")
        int duration
) {
}
