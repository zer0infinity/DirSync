package test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import dirsync.log.Log;
import dirsync.utils.FileIO;

public class FileIOTest {

	private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir")
			+ File.separator + "dirsyncfileio");
	private File sourceDir, destinationDir, subSourceDir, blacklistFileAbsolutePath;
	private String blacklistFile = "blacklistfile.txt";
	private ArrayList<String> blacklist = new ArrayList<String>();;

	@Before
	public void setUp() throws Exception {
		Log.getLogger().setLevel(Level.ERROR);
		initFiles();
	}

	private void initFiles() throws IOException {
		deleteTemp(TEMP_DIR);
		TEMP_DIR.mkdirs();
		sourceDir = new File(TEMP_DIR + File.separator + "testfolder1");
		destinationDir = new File(TEMP_DIR + File.separator + "testfolder2");
		subSourceDir = new File(sourceDir + File.separator + "testsubfolder1");
		blacklistFileAbsolutePath = new File(subSourceDir + File.separator + blacklistFile);
		
		writeTestFiles(sourceDir);
	}

	@AfterClass
	public static void tearDown() throws Exception {
//		deleteTemp(TEMP_DIR);
	}

	@Test
	public void testFileCount() {
		FileIO.sync(sourceDir, destinationDir, blacklist);
		assertEquals(sourceDir.length(), destinationDir.length());
	}

	@Test
	public void testFileNames() {
		FileIO.sync(sourceDir, destinationDir, blacklist);
		assertArrayEquals(sourceDir.list(), destinationDir.list());
	}

	@Test
	public void testDeleteObsoleteFiles() {
		writeTestFiles(destinationDir);
		FileIO.sync(sourceDir, destinationDir, blacklist);
		assertArrayEquals(sourceDir.list(), destinationDir.list());
	}

	@Test
	public void testBlacklistFolder() {
		blacklist.add(subSourceDir.toString());
		writeTestFiles(subSourceDir);
		
		FileIO.sync(sourceDir, destinationDir, blacklist);
		assertTrue(Arrays.toString(sourceDir.listFiles()).contains(subSourceDir.toString()));
		assertFalse(Arrays.toString(destinationDir.listFiles()).contains(subSourceDir.getName()));
		blacklist.remove(subSourceDir.toString());
	}
	
	@Test
	public void testBlacklistFile() {
		blacklist.add(blacklistFile);
		writeTestFiles(subSourceDir);
		writeFile(new File(sourceDir + File.separator + blacklistFile));
		
		FileIO.sync(sourceDir, destinationDir, blacklist);
		assertTrue(Arrays.toString(sourceDir.listFiles()).contains(blacklistFile));
		assertTrue(Arrays.toString(destinationDir.listFiles()).contains(blacklistFile));
		blacklist.remove(blacklistFile);
	}
	
	@Test
	public void testBlacklistFileAbsoluthPath() {
		blacklist.add(blacklistFileAbsolutePath.toString());
		writeTestFiles(subSourceDir);
		writeFile(blacklistFileAbsolutePath);
		
		FileIO.sync(sourceDir, destinationDir, blacklist);
		assertTrue(Arrays.toString(subSourceDir.listFiles()).contains(blacklistFileAbsolutePath.toString()));
		assertFalse(Arrays.toString(destinationDir.listFiles()).contains(blacklistFileAbsolutePath.toString()));
		blacklist.remove(blacklistFileAbsolutePath.toString());
	}
	
	@Test
	public void testBlacklist() {
		blacklist.add(subSourceDir.toString());
		blacklist.add(blacklistFile);
		blacklist.add(blacklistFileAbsolutePath.toString());
		writeTestFiles(subSourceDir);
		writeFile(new File(sourceDir + File.separator + blacklistFile));
		writeFile(blacklistFileAbsolutePath);
		
		FileIO.sync(sourceDir, destinationDir, blacklist);
		assertTrue(Arrays.toString(sourceDir.listFiles()).contains(subSourceDir.toString()));
		assertFalse(Arrays.toString(destinationDir.listFiles()).contains(subSourceDir.getName()));
		assertTrue(Arrays.toString(sourceDir.listFiles()).contains(blacklistFile));
		assertTrue(Arrays.toString(destinationDir.listFiles()).contains(blacklistFile));
		assertTrue(Arrays.toString(subSourceDir.listFiles()).contains(blacklistFileAbsolutePath.toString()));
		assertFalse(Arrays.toString(destinationDir.listFiles()).contains(blacklistFileAbsolutePath.toString()));
	}
	
	private void writeTestFiles(File dir) {
		dir.mkdirs();
		for (int i = 0; i < 10; ++i) {
			writeFile(new File(dir + File.separator + (int) (Math.random() * 10000)));
		}
	}

	private void writeFile(File file) {
		FileWriter fw;
		try {
			fw = new FileWriter(file);
			fw.write(Integer.toString((int)(Math.random() * 10000)));
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

	private static void deleteTemp(File file) {
		System.gc();
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (File f : fileList) {
				if (f.isDirectory()) {
					deleteTemp(f);
				}
				f.delete();
			}
		}
		file.delete();
	}
}
