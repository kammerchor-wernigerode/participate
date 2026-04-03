package de.kammerchorwernigerode.app.participate.common.uuidv5;

import com.fasterxml.uuid.StringArgGenerator;
import com.fasterxml.uuid.UUIDType;
import com.fasterxml.uuid.impl.NameBasedGenerator;

import java.net.URI;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public abstract class UuidV5Generator<T> {

    private static final String SHA_1 = "SHA-1";

    @NonNull
    private final Function<T, URI> extractor;

    public UUID generate(@NonNull T name) {
        URI uri = extractor.apply(name);
        return uuidV5From(uri.toString());
    }

    private static UUID uuidV5From(String name) {
        StringArgGenerator generator = generator();
        return generator.generate(name);
    }

    @SneakyThrows
    private static StringArgGenerator generator() {
        MessageDigest digest = MessageDigest.getInstance(SHA_1);
        return new NameBasedGenerator(NameBasedGenerator.NAMESPACE_OID, digest, UUIDType.NAME_BASED_SHA1);
    }
}
