package jp.natsukishina.csvmapper.file;

import java.io.File;
import java.net.URI;

import jp.natsukishina.csvmapper.CSVConverter;
import jp.natsukishina.csvmapper.CSVException;

/**
 * CSVファイルを扱うための{@link java.io.File}のラッパークラス
 * @author 417.72KI
 *
 */
public class CSVFile extends File {

	/**
	 * @see java.io.File#File(String)
	 * @param pathname - パス名文字列
	 * @throws NullPointerException pathname引数がnullである場合
	 * @throws CSVException CSVファイルでない場合
	 */
	public CSVFile(String pathname) {
		super(pathname);
		validate();
	}

	/**
	 * @see java.io.File#File(URI)
	 * @param uri - 階層型の絶対URI。形式は、"file"、空でないパス・コンポーネント、未定義の権限、クエリー、フラグメント・コンポーネントと同等
	 * @throws NullPointerException uriがnullの場合
	 * @throws IllegalArgumentException 上記のパラメータの前提条件が満たされていない場合
	 * @throws CSVException CSVファイルでない場合
	 */
	public CSVFile(URI uri) {
		super(uri);
		validate();
	}

	/**
	 * @see java.io.File#File(String, String)
	 * @param parent - 親パス名文字列
	 * @param child - 子パス名文字列
	 * @throws NullPointerException childがnullである場合
	 * @throws CSVException CSVファイルでない場合
	 */
	public CSVFile(String parent, String child) {
		super(parent, child);
		validate();
	}

	/**
	 * @see java.io.File#File(File, String)
	 * @param parent - 親抽象パス名
	 * @param child - 子パス名文字列
	 * @throws NullPointerException childがnullである場合
	 * @throws CSVException CSVファイルでない場合
	 */
	public CSVFile(File parent, String child) {
		super(parent, child);
		validate();
	}

	private void validate() {
		if (!CSVConverter.isCSVFile(this)) {
			throw new CSVException(getName() + " is not csv File!");
		}
	}
}
