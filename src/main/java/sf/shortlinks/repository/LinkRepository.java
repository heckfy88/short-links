package sf.shortlinks.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sf.shortlinks.domain.Link;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LinkRepository extends JpaRepository<Link, UUID> {
    int countLinksByOwnerUid(UUID uuid);

    @Modifying
    @Transactional
    @Query(value = "delete from link " +
            "where created_at + expiration_time <= now() " +
            "or is_active = false",
            nativeQuery = true)
    void deleteExpiredOrInactiveLinks();

    @Query(value = "select * from link " +
            "where owner_uid = :ownerUid " +
            "and url = :url " +
            "and counter < lim " +
            "and now() < (created_at + expiration_time::interval) " +
            "and is_active = true",
            nativeQuery = true)
    Optional<Link> findValidLinkByUrl(@Param("ownerUid") UUID ownerUid,
                                      @Param("url") String url);

    @Query(value = "select * from link " +
            "where owner_uid = :ownerUid " +
            "and short_url = :shortUrl " +
            "and counter < lim " +
            "and now() < (created_at + expiration_time::interval) " +
            "and is_active = true",
            nativeQuery = true)
    Optional<Link> findValidLinkByShortUrl(@Param("ownerUid") UUID ownerUid,
                                           @Param("shortUrl") String shortUrl);

}
