package dirsync.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import dirsync.DirSync;
import dirsync.log.Log;

public class FileIO {

	private static Logger LOG = Log.getLogger();

	public static ArrayList<String> toString(File file) {
		ArrayList<String> content = new ArrayList<String>();
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null)
				content.add(line);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static byte[] toByteArray(File file) {
		FileInputStream fis;
		byte[] content = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			fis.read(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static void sync(File sourceFiles, File toFolder, ArrayList<String> blackList) {
		toFolder.mkdirs();
		for (File file : sourceFiles.listFiles()) {
			if (isBlacklisted(file, blackList)) {
				LOG.info("Skip " + file);
				continue;
			}
			File newFile = new File(toFolder + File.separator + file.getName());
			if (file.isDirectory()) {
				newFile.mkdirs();
				sync(file, newFile, blackList);
				continue;
			}
			if (newFile.exists()) {
				FileComparison comparison = new FileComparison(file, newFile);
				if (comparison.isEqual()) {
					LOG.info(file);
					LOG.info("...already exists, continue...");
					continue;
				}
			}
			copy(file, newFile);
		}
		if(!DirSync.COPY)
			deleteObsolete(toFolder, sourceFiles);
	}

	private static boolean isBlacklisted(File file, ArrayList<String> blackList) {
		for (String s : blackList) {
			if (s.equalsIgnoreCase(file.toString()))
				return true;
		}
		return false;
	}

	private static void deleteObsolete(File files, File sourceFolder) {
		File[] fileList = files.listFiles();
		for (File file : fileList) {
			File oldFile = new File(sourceFolder + File.separator
					+ file.getName());
			if (file.isDirectory()) {
				deleteObsolete(file, oldFile);
				delete(file, oldFile);
				continue;
			}
			delete(file, oldFile);
		}
	}

	private static void delete(File file, File sourceFile) {
		if (!sourceFile.exists()) {
			LOG.info("Delete " + file);
			file.delete();
		}
	}

	private static void copy(File sourceFile, File newFile) {
		LOG.info("Copying " + sourceFile);
		try {
			FileInputStream source = new FileInputStream(sourceFile);
			FileOutputStream destination = new FileOutputStream(newFile);
			FileChannel sourceFileChannel = source.getChannel();
			FileChannel destinationFileChannel = destination.getChannel();

			sourceFileChannel.transferTo(0, sourceFileChannel.size(),
					destinationFileChannel);
			source.close();
			destination.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			LOG.error("...retry...");
			copy(sourceFile, newFile);
			return;
		}
		newFile.setLastModified(sourceFile.lastModified());
		LOG.info("...done");
	}
}
