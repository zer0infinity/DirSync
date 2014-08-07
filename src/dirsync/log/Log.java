package dirsync.log;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import dirsync.DirSync;

public class Log {

	private static Logger LOGGER = Logger.getLogger(Log.class);
	static {
		new Log();
	}
	
	private Log() {
		String pattern = "%d{yyyy-MM-dd HH:mm:ss}: %m %n";
		PatternLayout layout = new PatternLayout(pattern);
		LOGGER.setLevel(Level.ALL);
		LOGGER.addAppender(getConsoleLogger(layout));
		LOGGER.addAppender(getFileLogger(layout, DirSync.LOGFILE));
	}

	private ConsoleAppender getConsoleLogger(PatternLayout layout) {
		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		consoleAppender.setFollow(true);
		return consoleAppender;
	}

	private FileAppender getFileLogger(PatternLayout layout, String logfile) {
		FileAppender fileAppender = new FileAppender();
		try {
			fileAppender = new FileAppender(layout, logfile, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileAppender;
	}
	
	/**
	 * get logger
	 * 
	 * @return Logger
	 */
	public static Logger getLogger() {
		return LOGGER;
	}
}
