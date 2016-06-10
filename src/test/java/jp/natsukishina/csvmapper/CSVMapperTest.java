package jp.natsukishina.csvmapper;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.natsukishina.csvmapper.file.CSVFile;

public class CSVMapperTest {

	private static final String RESOURCE_DIR = "src/test/resources";
	private static final String RESOURCE_CSV_FILE = "test.csv";
	private static final String RESOURCE_CSV_FILE_NOT_EXISTS = "testnotexists.csv";
	private static final String RESOURCE_TXT_FILE = "test.txt";
	private static final String OUTPUT_CSV_FILE = "output.csv";
	private static final String OUTPUT_TXT_FILE = "output.txt";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		File file = new File(RESOURCE_DIR, OUTPUT_CSV_FILE);
		if (file.exists()) {
			file.delete();
		}
	}

	@Test
	public void 正常系_CSVファイルから読み込みCSVファイルへ出力() {
		CSVFile csvFile = new CSVFile(RESOURCE_DIR, RESOURCE_CSV_FILE);
		assertThat(csvFile.exists(), is(true));
		List<TestData> list = CSVMapper.convertFromCSVFile(csvFile, TestData.class);
		checkList(list);
		IntStream.range(0, list.size()).forEach(i -> {
			list.get(i).col1 = "hoge" + (i + 1);
			list.get(i).col2 = "fuga" + (i + 1);
		});

		CSVMapper.output(new CSVFile(RESOURCE_DIR, OUTPUT_CSV_FILE), list);
	}

	@Test
	public void 正常系_ファイルパスを直接指定して読み込みファイルパスを直接指定して出力() {
		List<TestData> list = CSVMapper.convertFromCSVFile(RESOURCE_DIR + "/" + RESOURCE_CSV_FILE, TestData.class);
		checkList(list);
		IntStream.range(0, list.size()).forEach(i -> {
			list.get(i).col1 = "hoge" + (i + 1);
			list.get(i).col2 = "fuga" + (i + 1);
		});

		CSVMapper.output(RESOURCE_DIR + "/" + OUTPUT_CSV_FILE, list);
	}

	@Test
	public void 正常系_CSVファイルから読み込みファイルパスを直接指定して出力() {
		CSVFile csvFile = new CSVFile(RESOURCE_DIR, RESOURCE_CSV_FILE);
		assertThat(csvFile.exists(), is(true));
		List<TestData> list = CSVMapper.convertFromCSVFile(csvFile, TestData.class);
		checkList(list);
		IntStream.range(0, list.size()).forEach(i -> {
			list.get(i).col1 = "hoge" + (i + 1);
			list.get(i).col2 = "fuga" + (i + 1);
		});

		CSVMapper.output(RESOURCE_DIR + "/" + OUTPUT_CSV_FILE, list);
	}

	@Test
	public void 正常系_ファイルパスを直接指定して読み込みCSVファイルへ出力() {
		List<TestData> list = CSVMapper.convertFromCSVFile(RESOURCE_DIR + "/" + RESOURCE_CSV_FILE, TestData.class);
		checkList(list);
		IntStream.range(0, list.size()).forEach(i -> {
			list.get(i).col1 = "hoge" + (i + 1);
			list.get(i).col2 = "fuga" + (i + 1);
		});

		CSVMapper.output(new CSVFile(RESOURCE_DIR, OUTPUT_CSV_FILE), list);
	}

	private void checkList(List<TestData> list) {
		assertThat(list, notNullValue());
		assertThat(list.size(), is(4));
		assertThat(list.get(0).col1, is("1-1"));
		assertThat(list.get(0).col2, is("1-2"));
		assertThat(list.get(0).col3, is("1-3"));
		assertThat(list.get(0).col4, is("1-4"));
		assertThat(list.get(1).col1, is("2-1"));
		assertThat(list.get(1).col2, is("2-2"));
		assertThat(list.get(1).col3, is("2-3"));
		assertThat(list.get(1).col4, is("2-4"));
		assertThat(list.get(2).col1, is("3-1"));
		assertThat(list.get(2).col2, is("3-2"));
		assertThat(list.get(2).col3, is("3-3"));
		assertThat(list.get(2).col4, is("3-4"));
		assertThat(list.get(3).col1, is("4-1"));
		assertThat(list.get(3).col2, is("4-2"));
		assertThat(list.get(3).col3, is("4-3"));
		assertThat(list.get(3).col4, is("4-4"));
	}

	@Test(expected = CSVException.class)
	public void 異常系_CSVファイル生成でtxtファイル指定時() {
		new CSVFile(RESOURCE_DIR, RESOURCE_TXT_FILE);
	}

	@Test(expected = CSVException.class)
	public void 異常系_CSVFileクラス生成でディレクトリ指定時() {
		new CSVFile(RESOURCE_DIR);
	}

	@Test(expected = CSVException.class)
	public void 異常系_読み込みで存在しないファイル指定時() {
		CSVMapper.convertFromCSVFile(new CSVFile(RESOURCE_DIR, RESOURCE_CSV_FILE_NOT_EXISTS), TestData.class);
	}

	@Test(expected = CSVException.class)
	public void 異常系_存在しないファイルを直接指定して読み込み時() {
		CSVMapper.convertFromCSVFile(RESOURCE_DIR + "/" + RESOURCE_CSV_FILE_NOT_EXISTS, TestData.class);
	}

	@Test(expected = CSVException.class)
	public void 異常系_CSVでないファイルパスを直接指定して読み込み時() {
		CSVMapper.convertFromCSVFile(RESOURCE_DIR + "/" + RESOURCE_TXT_FILE, TestData.class);
	}

	@Test(expected = CSVException.class)
	public void 異常系_ディレクトリ指定して読み込み時() {
		CSVMapper.convertFromCSVFile(RESOURCE_DIR, TestData.class);
	}

	@Test(expected = CSVException.class)
	public void 異常系_CSVでないファイルへ出力_CSVFile生成時() {
		List<TestData> list = CSVMapper.convertFromCSVFile(RESOURCE_DIR + "/" + RESOURCE_CSV_FILE, TestData.class);
		checkList(list);
		IntStream.range(0, list.size()).forEach(i -> {
			list.get(i).col1 = "hoge" + (i + 1);
			list.get(i).col2 = "fuga" + (i + 1);
		});

		CSVMapper.output(new CSVFile(RESOURCE_DIR, OUTPUT_TXT_FILE), list);
	}

	@Test(expected = CSVException.class)
	public void 異常系_ファイルパスを直接指定してCSVでないファイルへ出力() {
		List<TestData> list = CSVMapper.convertFromCSVFile(RESOURCE_DIR + "/" + RESOURCE_CSV_FILE, TestData.class);
		checkList(list);
		IntStream.range(0, list.size()).forEach(i -> {
			list.get(i).col1 = "hoge" + (i + 1);
			list.get(i).col2 = "fuga" + (i + 1);
		});

		CSVMapper.output(RESOURCE_DIR + "/" + OUTPUT_TXT_FILE, list);
	}

	@Test(expected = CSVException.class)
	public void 異常系_ディレクトリへ出力_CSVFile生成時() {
		List<TestData> list = CSVMapper.convertFromCSVFile(RESOURCE_DIR + "/" + RESOURCE_CSV_FILE, TestData.class);
		checkList(list);
		IntStream.range(0, list.size()).forEach(i -> {
			list.get(i).col1 = "hoge" + (i + 1);
			list.get(i).col2 = "fuga" + (i + 1);
		});

		CSVMapper.output(new CSVFile(RESOURCE_DIR), list);
	}

	@Test(expected = CSVException.class)
	public void 異常系_ファイルパスを直接指定してディレクトリへ出力() {
		List<TestData> list = CSVMapper.convertFromCSVFile(RESOURCE_DIR + "/" + RESOURCE_CSV_FILE, TestData.class);
		checkList(list);
		IntStream.range(0, list.size()).forEach(i -> {
			list.get(i).col1 = "hoge" + (i + 1);
			list.get(i).col2 = "fuga" + (i + 1);
		});

		CSVMapper.output(RESOURCE_DIR, list);
	}

	@Test
	public void testIsCSVFile() {
		File csvFile = new File(RESOURCE_DIR, RESOURCE_CSV_FILE);
		assertThat(CSVMapper.isCSVFile(csvFile), is(true));
		File txtFile = new File(RESOURCE_DIR, RESOURCE_TXT_FILE);
		assertThat(CSVMapper.isCSVFile(txtFile), is(false));
	}

	public static class TestData implements CSVMappable {
		private String col1;
		private String col2;
		private String col3;
		private String col4;

		private TestData() {
			System.out.println("init TestData");
		}

		@Override
		public boolean includeLines() {
			return false;
		}

		@Override
		public String[] array4exportCSV() {
			return new String[] { col1, col2, col3, col4 };
		}

		@Override
		public void importFromCSV(List<String> list) {
			col1 = list.get(0);
			col2 = list.get(1);
			col3 = list.get(2);
			col4 = list.get(3);
		}

	}
}
