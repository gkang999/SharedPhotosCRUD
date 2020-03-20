package SharedPhotosUtils;

public class SysOLog {
	// Simple log utility
	public static void log(String string) {
		ConfigReader configFile = new ConfigReader();
		Boolean loggingEnabled = configFile.getProperty("SysOLogEnabled").equals("true") ? true : false;
		if(loggingEnabled) {
			System.out.println(string);
		}
	}
}
