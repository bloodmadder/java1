import java.util.List;

public class StatisticsCalc {
    public static double averageTemperature(List<WeatherData> data) {
        return data.stream().mapToDouble(WeatherData::getTemperature).average().orElse(Double.NaN);
    }

    public static double minTemperature(List<WeatherData> data) {
        return data.stream().mapToDouble(WeatherData::getTemperature).min().orElse(Double.NaN);
    }

    public static double maxTemperature(List<WeatherData> data) {
        return data.stream().mapToDouble(WeatherData::getTemperature).max().orElse(Double.NaN);
    }

    public static double averageHumidity(List<WeatherData> data) {
        return data.stream().mapToDouble(WeatherData::getHumidity).average().orElse(Double.NaN);
    }

    public static double minHumidity(List<WeatherData> data) {
        return data.stream().mapToDouble(WeatherData::getHumidity).min().orElse(Double.NaN);
    }

    public static double maxHumidity(List<WeatherData> data) {
        return data.stream().mapToDouble(WeatherData::getHumidity).max().orElse(Double.NaN);
    }
}

