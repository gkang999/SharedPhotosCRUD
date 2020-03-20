package SharedPhotosUtils;

import java.util.Properties;

public class ConfigReader {
	Properties configFile;

	public ConfigReader() {
		configFile = new java.util.Properties();
		try {
			configFile.load(this.getClass().getClassLoader().getResourceAsStream("conf.cfg"));
		} catch (Exception eta) {
			eta.printStackTrace();
		}
	}
	
	public ConfigReader(String configFileName) {
		configFile = new java.util.Properties();
		try {
			configFile.load(this.getClass().getClassLoader().getResourceAsStream(configFileName));
		} catch (Exception eta) {
			eta.printStackTrace();
		}
	}

	public String getProperty(String key) {
		String value = this.configFile.getProperty(key);
		return value;
	}
}