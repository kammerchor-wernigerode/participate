package de.kammerchorwernigerode.app.participate.user.infrastructure.jpa;

import de.kammerchorwernigerode.app.participate.util.Urns;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;

import java.io.Serializable;
import java.net.URI;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_accounts")
@Data
@NoArgsConstructor
public class UserAccountRecord implements Persistable<UserAccountRecord.Id> {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "providerRef", column = @Column(name = "provider_ref", nullable = false)),
        @AttributeOverride(name = "accountRef", column = @Column(name = "account_ref", nullable = false)),
    })
    private Id id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_accounts_user_id"))
    private UserRecord user;

    @Column(name = "password")
    private String password;

    @Transient
    private boolean isNew;

    @PostPersist
    protected void postPersist() {
        this.isNew = false;
    }

    // @checkstyle:off: NeedBraces
    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (this == obj) return true;
        if (!getClass().equals(ProxyUtils.getUserClass(obj))) return false;
        UserAccountRecord that = (UserAccountRecord) obj;
        return this.getId() != null && this.getId().equals(that.getId());
    }
    // @checkstyle:on: NeedBraces

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += null == getId() ? 0 : getId().hashCode() * 31;
        return hashCode;
    }

    @Override
    public String toString() {
        Class<?> userClass = ProxyUtils.getUserClass(this);
        return userClass.getSimpleName() + "(id=" + getId() + ")";
    }


    @Embeddable
    public record Id(String providerRef, String accountRef) implements Serializable {

        public URI toUri() {
            String nss = "user_account:" + providerRef;
            return Urns.create(nss, accountRef);
        }

        @NonNull
        @Override
        public String toString() {
            return toUri().toString();
        }
    }
}
