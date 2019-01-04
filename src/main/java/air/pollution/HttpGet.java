package air.pollution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpGet {

    String from(String urlAddress) throws IOException {
        final int timeoutInMilliseconds = 3000;

        StringBuilder result = new StringBuilder();
        URL url = new URL(urlAddress);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(timeoutInMilliseconds);
        conn.setReadTimeout(timeoutInMilliseconds);
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }

        return result.toString();
    }
}
