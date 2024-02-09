package src.config_reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {
    public  Map<String, String> config;
    private  final String CONFIG_FILE = "./config/config.properties";
    private  final String DEFAULT_PROFILE = "default";

    public String getParamValue(String param) {
        return this.config.getOrDefault(DEFAULT_PROFILE + "."+param, null);
    }

    public void readConfigFile() {
        config = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("[")) {
                    String profile = line.substring(1, line.length() - 1);
                    while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                        String[] parts = line.split("=");
                        String key = profile + "." + parts[0];
                        String value = parts[1];
                        config.put(key.trim(), value.trim());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
