import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

public class WeatherHttpServer {
    private final WeatherDataManager manager = new WeatherDataManager();
    private final WeatherApiClient apiClient;

    public WeatherHttpServer(WeatherApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private void handleClient(Socket client) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))) {

            String line = reader.readLine();
            if (line == null) return;

            String[] request = line.split(" ");
            String method = request[0];
            String path = request[1];

            if ("POST".equals(method) && "/weather".equals(path)) {
                while (!reader.readLine().isEmpty()) {} // пропускаем заголовки
                StringBuilder body = new StringBuilder();
                while (reader.ready()) body.append((char) reader.read());

                String json = body.toString();
                double temp = Double.parseDouble(json.split("\"temperature\":")[1].split(",")[0]);
                double humidity = Double.parseDouble(json.split("\"humidity\":")[1].split("}")[0]);

                WeatherData data = new WeatherData(temp, humidity, LocalDateTime.now());
                manager.addData(data);
                manager.saveToFile("weather_data.txt");

                writer.write("HTTP/1.1 200 OK\r\n\r\nData received\n");
            } else if ("GET".equals(method) && "/stats".equals(path)) {
                List<WeatherData> copy = manager.getDataCopy();
                String response = "Avg Temp: " + StatisticsCalc.averageTemperature(copy) +
                        ", Min Temp: " + StatisticsCalc.minTemperature(copy) +
                        ", Max Temp: " + StatisticsCalc.maxTemperature(copy) +
                        "\nAvg Humidity: " + StatisticsCalc.averageHumidity(copy) +
                        ", Min Humidity: " + StatisticsCalc.minHumidity(copy) +
                        ", Max Humidity: " + StatisticsCalc.maxHumidity(copy) + "\n";

                writer.write("HTTP/1.1 200 OK\r\n\r\n" + response);
            } else if ("GET".equals(method) && path.startsWith("/forecast")) {
                String city = path.split("=")[1];
                WeatherData forecast = apiClient.getForecast(city);
                List<WeatherData> localData = manager.getDataCopy();

                String response = "Local Avg Temp: " + StatisticsCalc.averageTemperature(localData) +
                        ", Forecast Temp: " + (forecast != null ? forecast.getTemperature() : "N/A") + "\n";
                writer.write("HTTP/1.1 200 OK\r\n\r\n" + response);
            } else {
                writer.write("HTTP/1.1 404 Not Found\r\n\r\n");
            }

            writer.flush();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
