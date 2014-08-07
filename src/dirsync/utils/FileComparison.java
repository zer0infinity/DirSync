package dirsync.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;


public class FileComparison {

	private final File sourceFile, file;
	private byte[] sourceFileByteArray, fileByteArray;

	public enum ComparatorType {
		SIZE_AND_LASTMODIFIED, CRC32, MD5, SHA1;
	}
	
	public FileComparison(File sourceFile, File file) {
		this.sourceFile = sourceFile;
		this.file = file;
	}
	
	public boolean isEqual() {
		return isEqual(ComparatorType.SIZE_AND_LASTMODIFIED);
	}

	public boolean isEqual(ComparatorType comparator) {
		if(comparator != ComparatorType.SIZE_AND_LASTMODIFIED) {
			convertFileToByteArray();
		}
		switch (comparator) {
		case SIZE_AND_LASTMODIFIED:
			return compareSizeAndLastModified();
		case CRC32:
			return compareCRC();
		case MD5:
			return compare("MD5");
		case SHA1:
			return compare("SHA1");
		default:
			return compareSizeAndLastModified();
		}
	}

	private void convertFileToByteArray() {
		sourceFileByteArray = FileIO.toByteArray(sourceFile);
		fileByteArray = FileIO.toByteArray(file);
	}

	private boolean compareSizeAndLastModified() {
		long timeDifference = Math.abs(sourceFile.lastModified() - file.lastModified());
		int twoSeconds = 2000;
		if (timeDifference < twoSeconds && sourceFile.length() == file.length())
			return true;
		return false;
	}

	private boolean compareCRC() {
		CRC32 sourceFileCRC = new CRC32();
		sourceFileCRC.update(sourceFileByteArray);

		CRC32 fileCRC = new CRC32();
		fileCRC.update(fileByteArray);
		
		if (sourceFileCRC.getValue() == fileCRC.getValue())
			return true;
		return false;
	}

	private boolean compare(String algorithm) {
		MessageDigest sourceFile = null, file = null;
		try {
			sourceFile = MessageDigest.getInstance(algorithm);
			file = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
		sourceFile.update(sourceFileByteArray);
		file.update(fileByteArray);
		if (MessageDigest.isEqual(sourceFile.digest(), file.digest()))
			return true;
		return false;
	}
}
