package SharedPhotosUtils;

public class SysOLog {
	// Simple log utility
	public static void log(String string) {
		boolean loggingEnabled = true;
		// System.getenv("SysOLogEnabled").equals("true") ? true : false;
		if(loggingEnabled) {
			System.out.println(string);
		}
	}
}
