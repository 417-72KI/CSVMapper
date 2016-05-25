package jp.natsukishina.csvmapper;

/**
 * CSVファイルの変換で発生した例外を扱うクラス
 * @author 417.72KI
 *
 */
public class CSVException extends RuntimeException {

	public CSVException() {
	}

	public CSVException(String paramString) {
		super(paramString);
	}

	public CSVException(Throwable paramThrowable) {
		super(paramThrowable);
	}

	public CSVException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public CSVException(String paramString, Throwable paramThrowable, boolean paramBoolean1, boolean paramBoolean2) {
		super(paramString, paramThrowable, paramBoolean1, paramBoolean2);
	}

}
