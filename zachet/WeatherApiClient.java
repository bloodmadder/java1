import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

public class WeatherApiClient {
    private final String apiKey;

    public WeatherApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public WeatherData getForecast(String city) {
        try {
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int code = connection.getResponseCode();
            if (code != 200) {
                System.out.println("API response code: " + code);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            String json = sb.toString();

            String mainBlock = json.split("\"main\":\\{")[1].split("}")[0];

            double temp = Double.parseDouble(mainBlock.split("\"temp\":")[1].split(",")[0]);
            double humidity = Double.parseDouble(mainBlock.split("\"humidity\":")[1].split(",")[0]);

            return new WeatherData(temp, humidity, LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
