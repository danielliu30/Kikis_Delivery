package bakery.Models.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * A single-use email verification token. The pending signup is stored alongside
 * it so verification survives a restart.
 */
@Entity
@Table(name = "validation_tokens")
@Getter
@Setter
public class ValidationTokenRecord {

    @Id
    @Column(name = "token_id", nullable = false)
    private String tokenId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "pending_customer", nullable = false)
    private String pendingCustomer;

    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiration;
}
