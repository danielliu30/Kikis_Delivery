package bakery.Models.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Persisted customer account, keyed by email.
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
public class CustomerRecord {

    @Id
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "member")
    private String member;

    @Column(name = "admin")
    private String admin;

    @Column(name = "updated")
    private LocalDateTime updated = LocalDateTime.now();
}
