package sf.shortlinks.service;

import sf.shortlinks.api.dto.*;

public interface LinkService {
    CreateLinkRsDto create(CreateLinkRqDto dto);

    EditLinkRsDto edit(EditLinkRqDto dto);

    String redirect(RedirectLinkRqDto dto);
}
