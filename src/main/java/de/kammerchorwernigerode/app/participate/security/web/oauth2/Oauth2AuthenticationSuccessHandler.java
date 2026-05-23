package de.kammerchorwernigerode.app.participate.security.web.oauth2;

import de.kammerchorwernigerode.app.participate.user.infrastructure.jpa.UserAccountRecord;
import de.kammerchorwernigerode.app.participate.user.infrastructure.jpa.UserAccountRecordRepository;
import de.kammerchorwernigerode.app.participate.user.infrastructure.jpa.UserRecord;
import de.kammerchorwernigerode.app.participate.user.infrastructure.jpa.UserRecordRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationSuccessHandler subject;
    private final UserAccountRecordRepository userAccountRecordRepository;
    private final UserRecordRepository userRecordRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                        Authentication authentication) throws IOException, ServletException {
        subject.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication)
        throws ServletException, IOException {
        if (authentication instanceof OAuth2AuthenticationToken token) {
            onAuthenticationSuccess(token);
        }

        subject.onAuthenticationSuccess(req, res, authentication);
    }

    private void onAuthenticationSuccess(OAuth2AuthenticationToken authentication) {
        if (authentication.getPrincipal() instanceof DefaultOidcUser user) {
            synchronizeAccount(authentication.getAuthorizedClientRegistrationId(), user);
        }
    }

    @Transactional
    public void synchronizeAccount(String provider, DefaultOidcUser user) {
        UserAccountRecord.Id accountId = createAccountId(provider, user);
        Optional<UserAccountRecord> account = userAccountRecordRepository.findById(accountId);
        if (account.isPresent()) {
            updateAccount(account.get(), user);
        } else {
            createAccount(provider, user);
        }
    }

    private void updateAccount(UserAccountRecord account, DefaultOidcUser user) {
        UserRecord record = account.getUser();
        updateUser(record, user);
    }

    private void createAccount(String provider, DefaultOidcUser user) {
        Optional<UserRecord> record = userRecordRepository.findByEmailAddress(user.getEmail());
        if (record.isPresent()) {
            updateUser(record.get(), user);
        } else {
            createUser(provider, user);
        }
    }

    private void updateUser(UserRecord record, DefaultOidcUser user) {
        record.setName(user.getName());
        record.setEmailAddress(user.getEmail());
        record.setEmailVerified(user.getEmailVerified());
        userRecordRepository.save(record);
    }

    private void createUser(String provider, DefaultOidcUser user) {
        UserRecord record = new UserRecord(user.getName());
        updateUser(record, user);
        UserAccountRecord account = new UserAccountRecord();
        account.setNew(true);
        account.setId(createAccountId(provider, user));
        account.setUser(record);
        record.getAccounts().add(account);
        userAccountRecordRepository.save(account);
    }

    private static UserAccountRecord.Id createAccountId(String provider, DefaultOidcUser user) {
        return new UserAccountRecord.Id(provider, user.getSubject());
    }
}
