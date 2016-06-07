package jp.natsukishina.csvmapper;

/**
 * CSVファイルの変換で発生した例外を扱うクラス
 * @author 417.72KI
 * @see java.lang.RuntimeException
 */
public class CSVException extends RuntimeException {

	/**
	 * {@inheritDoc}
	 */
	public CSVException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public CSVException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public CSVException(Throwable cause) {
		super(cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public CSVException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public CSVException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
