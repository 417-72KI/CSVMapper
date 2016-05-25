package jp.natsukishina.csvmapper;

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

	private String file;
	private FileCharDetecter(String file) {
		this.file = file;
	}

	/**
	 * 文字コードを判定するメソッド.
	 *
	 * @param ファイルパス
	 * @return 文字コード
	 */
	private String detector() {
		byte[] buf = new byte[4096];
		String fileName = this.file;
		try (FileInputStream fis = new FileInputStream(fileName);) {

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
			if (encType != null) {
				System.out.println("文字コード = " + encType);
			} else {
				System.out.println("文字コードを判定できませんでした");
			}

			// 判定の初期化
			detector.reset();

			return encType;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}