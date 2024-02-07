package src.config_reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {
    private static final String CONFIG_FILE = "./config/config.properties";
    private static final String DEFAULT_PROFILE = "default";

    public static int getMinChatOccurrences() {
        Map<String, Integer> configMap = readConfigFile();
        return configMap.getOrDefault(DEFAULT_PROFILE + ".nb_min_chats_per_adress_to_be_trained", 0);
    }

    private static Map<String, Integer> readConfigFile() {
        Map<String, Integer> configMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("[")) {
                    String profile = line.substring(1, line.length() - 1);
                    while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                        String[] parts = line.split("=");
                        String key = profile + "." + parts[0];
                        int value = Integer.parseInt(parts[1]);
                        configMap.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configMap;
    }
}
