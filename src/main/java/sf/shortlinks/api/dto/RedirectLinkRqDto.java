package sf.shortlinks.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record RedirectLinkRqDto(
        @JsonProperty("ownerUid")
        UUID ownerUid,
        @JsonProperty("shortUrl")
        String shortUrl
) {
}
