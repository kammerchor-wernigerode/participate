package de.vinado.wicket.participate.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.MimeType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.pivovarit.function.ThrowingSupplier.sneaky;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.apache.tika.metadata.TikaMetadataKeys.RESOURCE_NAME_KEY;

/**
 * Email attachment wrapper object
 *
 * @author Vincent Nadoll
 */
@Builder
@ToString(exclude = "data")
@EqualsAndHashCode
@AllArgsConstructor
public class EmailAttachment {

    // @formatter:off
    private @Getter @NonNull String name;
    private MimeType mimeType;
    private @Getter @NonNull byte[] data;
    // @formatter:on

    /**
     * Returns the media type of the attachment. The type will be guessed based on the file's metadata if no {@link
     * #mimeType} is provided.
     *
     * @return the media type
     */
    public MimeType getMimeType() {
        final InputStream inputStream = new ByteArrayInputStream(data);

        return ofNullable(this.mimeType).orElseGet(sneaky(() -> {
            final Detector detector = TikaConfig.getDefaultConfig().getDetector();

            final TikaInputStream stream = TikaInputStream.get(inputStream);
            final Metadata metadata = new Metadata();

            metadata.add(RESOURCE_NAME_KEY, name);

            final MediaType mediaType = detector.detect(requireNonNull(stream), metadata);
            return MimeType.valueOf(mediaType.toString());
        }));
    }

    /**
     * @return the byte array resource of the data
     */
    public InputStreamSource getInputStream() {
        return new ByteArrayResource(data);
    }
}
