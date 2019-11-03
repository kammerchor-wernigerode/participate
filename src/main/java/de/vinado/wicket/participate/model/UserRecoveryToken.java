package de.vinado.wicket.participate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "user_rec_tokens")
@Where(clause = "valid_date > current_timestamp() or valid_date is null")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRecoveryToken implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date validDate;

    /**
     * @param user      The associated {@link User}
     * @param token     Recovery token
     * @param validDate Date, the token expires
     */
    public UserRecoveryToken(final User user, final String token, final Date validDate) {
        this.user = user;
        this.token = token;
        this.validDate = validDate;
    }
}
