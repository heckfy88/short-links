package sf.shortlinks.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sf.shortlinks.api.dto.*;
import sf.shortlinks.api.exception.ConstraintException;
import sf.shortlinks.api.exception.LinkNotFound;
import sf.shortlinks.domain.Link;
import sf.shortlinks.generator.LinkGenerator;
import sf.shortlinks.repository.LinkRepository;
import sf.shortlinks.service.LinkService;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class LinkServiceImpl implements LinkService {

    private static final Logger logger = LoggerFactory.getLogger(LinkServiceImpl.class);
    private final LinkRepository linkRepository;
    private final LinkGenerator linkGenerator;
    @Value("${link.duration:360}")
    private int systemDuration;
    @Value("${link.limit:5}")
    private int systemLimit;

    @Autowired
    public LinkServiceImpl(
            LinkRepository linkRepository,
            LinkGenerator linkGenerator
    ) {
        this.linkRepository = linkRepository;
        this.linkGenerator = linkGenerator;
    }

    @Override
    public CreateLinkRsDto create(CreateLinkRqDto dto) {
        Link savedLink;
        try {
            Optional<Link> originalLink = linkRepository.findValidLinkByUrl(dto.ownerUid(), dto.url());

            if (dto.ownerUid() != null && originalLink.isPresent()) {
                Link link = originalLink.get();
                link.setShortUrl(linkGenerator.generateShortUrl());

                savedLink = linkRepository.save(link);
            } else {
                savedLink = linkRepository.save(
                        Link.builder()
                                .url(dto.url())
                                .shortUrl(linkGenerator.generateShortUrl())
                                .ownerUid(prepareOwnerUid(dto.ownerUid()))
                                .limit(prepareLimit(dto.limit()))
                                .expirationTime(prepareExpirationTime(dto.duration()))
                                .isActive(true)
                                .build()
                );
            }
        } catch (Exception e) {
            logger.info("Unsuccessful short link creation:\n{}", e.getMessage());
            throw new ConstraintException("Unsuccessful short link creation", e.getMessage());

        }

        return new CreateLinkRsDto(
                savedLink.getOwnerUid(),
                savedLink.getShortUrl(),
                savedLink.getCreatedAt()
                        .plusHours(savedLink.getExpirationTime().getHour())
                        .plusMinutes(savedLink.getExpirationTime().getMinute())
        );
    }

    @Override
    public EditLinkRsDto edit(EditLinkRqDto dto) {
        EditLinkRsDto rsDto;
        try {
            Link updatedLink = update(dto);

            rsDto = new EditLinkRsDto(
                    updatedLink.getOwnerUid(),
                    updatedLink.getShortUrl(),
                    updatedLink.getCreatedAt()
                            .plusHours(dto.duration() / 60)
                            .plusMinutes(dto.duration() % 60),
                    updatedLink.getLimit(),
                    updatedLink.isActive()
            );
        } catch (Exception e) {
            logger.info("Unsuccessful short link update\n{}", e.getMessage());
            throw new ConstraintException("Unsuccessful short link update", e.getMessage());

        }
        return rsDto;
    }

    @Override
    public String redirect(RedirectLinkRqDto dto) {
        Link link = linkRepository.findValidLinkByShortUrl(dto.ownerUid(), dto.shortUrl())
                .orElseThrow(()
                        -> new LinkNotFound("Active link was not found for short link: ", dto.shortUrl()));

        link.setCounter(link.getCounter() + 1);
        if (link.getCounter() >= link.getLimit()) {
            link.setActive(false);
            logger.info("Short link limit reached: {}", link.getShortUrl());
        }
        linkRepository.save(link);

        return link.getUrl();
    }

    private LocalTime prepareExpirationTime(Integer userDuration) {
        return (userDuration != null && userDuration < systemDuration) ?
                LocalTime.of(userDuration / 60, userDuration % 60)
                : LocalTime.of(systemDuration / 60, systemDuration % 60);
    }

    private int prepareLimit(Integer userLimit) {
        return (userLimit != null && userLimit > systemLimit) ? userLimit : systemLimit;
    }

    private UUID prepareOwnerUid(UUID userUid) {
        return (userUid != null) ? userUid : UUID.randomUUID();
    }

    private Link update(EditLinkRqDto dto) {
        Link link = linkRepository.findValidLinkByShortUrl(dto.ownerUid(), dto.shortUrl())
                .orElseThrow(()
                        -> new LinkNotFound("Active link was not found for short link: ", dto.shortUrl()));

        link.setShortUrl((dto.shortUrl() == null) ? link.getShortUrl() : dto.shortUrl());
        link.setExpirationTime((dto.duration() == null) ? link.getExpirationTime() :
                LocalTime.of(dto.duration() / 60, dto.duration() % 60));
        link.setLimit((dto.limit() == null) ? link.getLimit() : dto.limit());
        // Неактивная ссылка удалится если флаг меняется на false на следующей итерации LinkScheduler
        link.setActive(dto.isActive());

        return linkRepository.save(link);
    }
}