package sf.shortlinks.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sf.shortlinks.repository.LinkRepository;

@Service
@ConditionalOnProperty(value = "${spring.scheduling.enabled:true}", havingValue = "true")
public class LinkScheduler {
    private static final Logger logger = LoggerFactory.getLogger(LinkScheduler.class);

    private final LinkRepository linkRepository;

    public LinkScheduler(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Scheduled(cron = "${spring.scheduling.cron:0 0 0 * * *}")
    public void removeInactiveLinks() {
        logger.info("Removing inactives links");
        linkRepository.deleteExpiredOrInactiveLinks();
    }
}
