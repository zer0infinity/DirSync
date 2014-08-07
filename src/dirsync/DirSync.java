package dirsync;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import dirsync.log.Log;
import dirsync.utils.FileIO;

public class DirSync {

	public static final String PROGRAM = "DirSync";
	public static final String VERSION = "0.1";
	public static final String CODER = "David Tran";

	public static String FILE = "DirSyncPath.txt";
	public static String BLACKLIST = "blacklist.txt";
	public static String FOLDER = "SyncedFiles";
	public static String LOGFILE = PROGRAM + ".log";
	public static boolean COPY = false;

	private static Logger LOG = Log.getLogger();

	public DirSync() {
		File pathFile = new File(FILE);
		File folder = new File(FOLDER);
		ArrayList<String> pathList = FileIO.toString(pathFile);
		checkIntegrity(pathFile, folder, pathList);

		File blacklistFile = new File(BLACKLIST);
		ArrayList<String> blackList = new ArrayList<String>();
		if (blacklistFile.exists())
			blackList = FileIO.toString(blacklistFile);

		startSync(folder, pathList, blackList);
	}

	private void checkIntegrity(File pathFile, File folder,
			ArrayList<String> pathList) {
		if (!pathFile.exists()) {
			LOG.fatal(FILE + " does not exists");
			System.exit(0);
		}
		if (!folder.exists() && !folder.mkdirs()) {
			LOG.fatal(FOLDER + " is not valid");
			System.exit(0);
		}
		if (pathList.isEmpty()) {
			LOG.fatal(FILE + " is empty");
			System.exit(0);
		}
	}

	private void startSync(File folder, ArrayList<String> pathList,
			ArrayList<String> blackList) {
		LOG.info("Sync in progress...");
		for (String path : pathList)
			FileIO.sync(new File(path), folder, blackList);
		LOG.info("Sync finished");
	}

	public static void main(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			if (args[i].toLowerCase().endsWith("-file"))
				if (args[i + 1].length() > 0)
					FILE = args[i + 1];
			if (args[i].toLowerCase().endsWith("-folder"))
				if (args[i + 1].length() > 0)
					FOLDER = args[i + 1];
			if (args[i].toLowerCase().endsWith("-blacklist"))
				if (args[i + 1].length() > 0)
					BLACKLIST = args[i + 1];
			if (args[i].toLowerCase().endsWith("-log"))
				if (args[i + 1].length() > 0)
					LOGFILE = args[i + 1];
			if (args[i].toLowerCase().endsWith("-copy"))
				COPY = true;
			if (args[i].toLowerCase().endsWith("-help")
					|| args[i].toLowerCase().endsWith("-h")) {
				showHelp();
				System.exit(0);
			}
		}

		LOG.info(PROGRAM + " started");
		LOG.info("Destination Folder: " + FOLDER + "\n");
		new DirSync();
	}

	private static void showHelp() {
		System.out.println("\n" + PROGRAM + " " + VERSION + " - " + CODER
				+ "\n\n" + "  " + PROGRAM
				+ ".jar [-file] [-folder] [-log] [-copy] [-help]" + "\n\n"
				+ "  * [-file yourfile.txt] (default) " + FILE + "\n"
				+ "  * [-folder SyncPath] (default) " + FOLDER + " folder"
				+ "\n" + "  * [-log yourlog.txt] (default) " + LOGFILE + "\n"
				+ "  * [-copy] Copy to " + FOLDER + " instead of synchronize"
				+ LOGFILE);
	}
}
