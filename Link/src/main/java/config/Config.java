package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final String CONFIG_FILE = "/config.properties";
    private static int maxTimeHours;
    private static int minClicks;

    static {
        Properties props = new Properties();
        try (InputStream is = Config.class.getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new RuntimeException("Не найден " + CONFIG_FILE);
            }
            props.load(is);
            maxTimeHours = Integer.parseInt(props.getProperty("maxTimeHours", "24"));
            minClicks = Integer.parseInt(props.getProperty("minClicks", "3"));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения config.properties", e);
        } catch (NumberFormatException e) {
            maxTimeHours = 24;
            minClicks = 3;
        }
    }

    public static int getMaxTimeHours() {
        return maxTimeHours;
    }

    public static int getMinClicks() {
        return minClicks;
    }
}
