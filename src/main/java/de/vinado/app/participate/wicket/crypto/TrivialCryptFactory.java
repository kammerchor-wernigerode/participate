package de.vinado.app.participate.wicket.crypto;

import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.crypt.TrivialCrypt;

public class TrivialCryptFactory implements CryptFactory {

    @Override
    public ICrypt create() {
        return new TrivialCrypt();
    }
}
