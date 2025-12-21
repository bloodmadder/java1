import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeatherDataManager {
    private final List<WeatherData> dataList = new ArrayList<>();

    public synchronized void addData(WeatherData data) {
        dataList.add(data);
    }

    public synchronized List<WeatherData> getDataCopy() {
        return new ArrayList<>(dataList);
    }

    public synchronized void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            for (WeatherData data : dataList) {
                writer.write(data.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
