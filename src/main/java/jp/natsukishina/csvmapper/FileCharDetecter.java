package jp.natsukishina.csvmapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * 文字コードを判定するクラス.
 */
public class FileCharDetecter {

	/**
	 * 指定されたファイルの文字コードを判定する
	 * @param filePath 判定するファイルのパス
	 * @return 文字コード
	 */
	public static String detect(String filePath) {
		return new FileCharDetecter(filePath).detector();
	}

	/**
	 * 指定されたファイルの文字コードを判定する
	 * @param file 判定するファイル
	 * @return 文字コード
	 */
	public static String detect(File file) {
		return new FileCharDetecter(file).detector();
	}

	private File file;
	private FileCharDetecter(String filePath) {
		this(new File(filePath));
	}

	private FileCharDetecter(File file) {
		this.file = file;
	}

	/**
	 * 文字コードを判定するメソッド
	 *
	 * @return 文字コード
	 */
	private String detector() {
		byte[] buf = new byte[4096];
		try (FileInputStream fis = new FileInputStream(file);) {

			// 文字コード判定ライブラリの実装
			UniversalDetector detector = new UniversalDetector(null);

			// 判定開始
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			// 判定終了
			detector.dataEnd();

			// 文字コード判定
			String encType = detector.getDetectedCharset();
//			if (encType != null) {
//				System.out.println("文字コード = " + encType);
//			} else {
//				System.out.println("文字コードを判定できませんでした");
//			}

			// 判定の初期化
			detector.reset();

			return encType;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}