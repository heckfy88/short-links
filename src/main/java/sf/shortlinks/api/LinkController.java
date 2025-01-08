package sf.shortlinks.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sf.shortlinks.api.dto.*;
import sf.shortlinks.service.LinkService;

import java.net.URI;

@RestController
@RequestMapping("api/v1/link")
public class LinkController {

    private final LinkService linkService;

    @Autowired
    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    public CreateLinkRsDto create(@RequestBody CreateLinkRqDto dto) {
        return linkService.create(dto);
    }

    @PostMapping("/redirect")
    public ResponseEntity<Void> redirect(@RequestBody RedirectLinkRqDto dto) {
        String url = linkService.redirect(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }


    @PatchMapping
    public EditLinkRsDto edit(@RequestBody EditLinkRqDto dto) {
        return linkService.edit(dto);
    }
}