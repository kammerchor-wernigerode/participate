import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Healthcheck {

    public static void main(String[] args) throws IOException {
        URL healthcheckEndpoint = new URL("http://localhost:" + getPort() + "/actuator/health");
        HttpURLConnection connection = (HttpURLConnection) healthcheckEndpoint.openConnection();

        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() != 200) {
            System.exit(1);
        }

        String body = extractResponseBody(connection);
        connection.disconnect();

        System.exit(body.contains("UP") ? 0 : 1);
    }

    private static String extractResponseBody(HttpURLConnection connection) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[128];

        for (int length; (length = connection.getInputStream().read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }

        return result.toString(StandardCharsets.UTF_8.name());
    }

    public static int getPort() {
        return Optional.ofNullable(System.getenv("MANAGEMENT_SERVER_PORT"))
            .map(Integer::parseInt)
            .orElse(8081);
    }
}
