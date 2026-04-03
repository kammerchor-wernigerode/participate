package de.kammerchorwernigerode.app.participate.util;

import java.net.URI;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Urns {

    public static final String NID = "participate";

    public static URI create(@NonNull String nss, @NonNull String fragment) {
        return URI.create("urn:" + NID + ":" + nss + "#" + fragment);
    }
}
