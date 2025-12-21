import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String apiKey = "0d240c09149a2ef56316f4d4c629661f";
        WeatherApiClient apiClient = new WeatherApiClient(apiKey);

        WeatherHttpServer server = new WeatherHttpServer(apiClient);
        Thread serverThread = new Thread(() -> {
            try {
                server.start(8080);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        Thread.sleep(1000);

        Runnable postTask = () -> sendPost(8080, 20 + Math.random() * 10, 40 + Math.random() * 20);
        Thread t1 = new Thread(postTask);
        Thread t2 = new Thread(postTask);
        Thread t3 = new Thread(postTask);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        String stats = sendGet("http://localhost:8080/stats");
        System.out.println("Statistics:\n" + stats);

        String forecast = sendGet("http://localhost:8080/forecast?city=Kazan");
        System.out.println("Forecast:\n" + forecast);
    }

    private static void sendPost(int port, double temp, double humidity) {
        try {
            URL url = new URL("http://localhost:" + port + "/weather");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String json = "{\"temperature\":" + temp + ",\"humidity\":" + humidity + "}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int responseCode = conn.getResponseCode();
            System.out.println("POST response code: " + responseCode);
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sendGet(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            reader.close();
            conn.disconnect();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

