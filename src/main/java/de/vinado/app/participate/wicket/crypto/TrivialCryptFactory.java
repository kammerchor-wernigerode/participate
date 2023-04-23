package de.vinado.app.participate.wicket.crypto;

import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.crypt.TrivialCrypt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.wicket", name = "runtime-configuration", havingValue = "DEVELOPMENT")
public class TrivialCryptFactory implements CryptFactory {

    @Override
    public ICrypt create() {
        return new TrivialCrypt();
    }
}
