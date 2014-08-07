package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import dirsync.utils.FileComparison;
import dirsync.utils.FileComparison.ComparatorType;

public class FileComparisonTest {

	private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir")
			+ File.separator + "dirsynccomparetest");
	private File file1, file2, file3;
	private FileComparison sameFiles, differentEmptyFiles, differentFiles;

	@Before
	public void setUp() throws Exception {
		deleteTemp(TEMP_DIR);
		writeTestFiles();
		sameFiles = new FileComparison(file1, file1);
		differentEmptyFiles = new FileComparison(file1, file2);
		differentFiles = new FileComparison(file1, file3);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		deleteTemp(TEMP_DIR);
	}

	@Test
	public void testSameFiles() {
		assertTrue(sameFiles.isEqual(ComparatorType.SIZE_AND_LASTMODIFIED));
		assertTrue(sameFiles.isEqual(ComparatorType.CRC32));
		assertTrue(sameFiles.isEqual(ComparatorType.MD5));
		assertTrue(sameFiles.isEqual(ComparatorType.SHA1));
	}
	
	@Test
	public void testEmptyFiles() {
		assertTrue(differentEmptyFiles.isEqual(ComparatorType.SIZE_AND_LASTMODIFIED));
		assertTrue(differentEmptyFiles.isEqual(ComparatorType.CRC32));
		assertTrue(differentEmptyFiles.isEqual(ComparatorType.MD5));
		assertTrue(differentEmptyFiles.isEqual(ComparatorType.SHA1));
	}
	
	@Test
	public void testDifferentFiles() {
		assertFalse(differentFiles.isEqual(ComparatorType.SIZE_AND_LASTMODIFIED));
		assertFalse(differentFiles.isEqual(ComparatorType.CRC32));
		assertFalse(differentFiles.isEqual(ComparatorType.MD5));
		assertFalse(differentFiles.isEqual(ComparatorType.SHA1));
	}
	
	private void writeTestFiles() throws IOException, InterruptedException {
		TEMP_DIR.mkdirs();
		file1 = new File(TEMP_DIR + File.separator + "file1");
		file1.createNewFile();
		Thread.sleep(1);
		file2 = new File(TEMP_DIR + File.separator + "file2");
		file2.createNewFile();
		file3 = new File(TEMP_DIR + File.separator + "file3");
		file3.createNewFile();
		
		FileWriter fw = new FileWriter(file3);
		fw.write("test");
		fw.close();
	}
	
	private static void deleteTemp(File file) {
		System.gc();
		if(file.isDirectory()) {
			File[] fileList = file.listFiles();
			for(File f: fileList) {
				if(f.isDirectory()) {
					deleteTemp(f);
				}
				f.delete();
			}
		}
		file.delete();
	}
}
