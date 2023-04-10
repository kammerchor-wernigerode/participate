package de.vinado.app.participate.wicket.crypto;

import org.apache.wicket.util.crypt.ICrypt;

public interface CryptFactory {

    ICrypt create();
}
