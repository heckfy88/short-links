package sf.shortlinks.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateLinkRqDto(
        @JsonProperty("url")
        String url,
        @JsonProperty("ownerUid")
        UUID ownerUid,
        @JsonProperty("duration")
        Integer duration,
        @JsonProperty("limit")
        Integer limit
) {
}
