package sf.shortlinks.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record EditLinkRqDto(
        @JsonProperty("ownerUid")
        UUID ownerUid,
        @JsonProperty("shortUrl")
        String shortUrl,
        @JsonProperty("limit")
        Integer limit,
        @JsonProperty("duration")
        Integer duration,
        @JsonProperty("isActive")
        boolean isActive
) {
}