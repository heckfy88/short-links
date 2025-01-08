package sf.shortlinks.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record EditLinkRsDto(
        @JsonProperty("ownerUid")
        UUID ownerUid,
        @JsonProperty("shortUrl")
        String shortUrl,
        @JsonProperty("expirationDateTime")
        LocalDateTime expirationDateTime,
        @JsonProperty("limit")
        int limit,
        @JsonProperty("isActive")
        boolean isActive
) {
}
