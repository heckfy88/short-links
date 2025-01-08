package sf.shortlinks.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;


@Table(name = "link")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false,
            updatable = false, insertable = false
    )
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "owner_uid", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID ownerUid;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "short_url", nullable = false)
    private String shortUrl;

    @org.hibernate.annotations.Generated
    @Column(
            name = "created_at",
            updatable = false
    )
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "counter", nullable = false)
    private int counter;

    @Column(name = "lim", nullable = false)
    private int limit;

    @Column(name = "expiration_time", nullable = false)
    @JdbcTypeCode(SqlTypes.TIME)
    private LocalTime expirationTime;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    private boolean isActive;

    public void setActive(boolean active) {
        isActive = active;
    }
}