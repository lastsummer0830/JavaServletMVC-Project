package common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException(
                    "config.properties 파일을 찾을 수 없습니다. src/main/resources에 생성했는지 확인하세요."
                );
            }

            props.load(input);

        } catch (IOException e) {
            throw new RuntimeException("config.properties 읽기 실패", e);
        }
    }

    public static String getKmdbServiceKey() {
        return props.getProperty("KMDB_SERVICE_KEY");
    }

    public static String getKmdbApiUrl() {
        return props.getProperty("KMDB_API_URL");
    }
}