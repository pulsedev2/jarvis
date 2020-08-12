package fr.pulsedev.jarvis.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesValue {
    InputStream inputStream;
    String file;

    public PropertiesValue(String file) {
        this.file = file;
    }

    public String get(String requested) {
        return getProperties().getProperty(requested);
    }

    public Properties getProperties() {

        try {
            Properties prop = new Properties();
            String propFileName = file;

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            return prop;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
