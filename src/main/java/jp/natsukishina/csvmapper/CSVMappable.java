package jp.natsukishina.csvmapper;

import java.lang.annotation.*;

/**
 * <p>CSVファイルのマッピング用インターフェース</p>
 * ※このインターフェースの実装クラスは必ずデフォルトコンストラクターを使用できるようにしてください。<br>
 * (privateでも構いません)
 *
 * @author 417.72KI
 *
 */
public interface CSVMappable {
	/**
	 * CSVの要素内に改行が入りうるかを確認する
	 *
	 * @return 改行が入ったCSVを読み込む可能性がある場合はtrue<br>
	 * CSVのレコード内に改行が入りえない場合はfalse
	 */
	boolean includeLines();

	/**
	 * 各行のカラム番号を管理するアノテーション
	 * @author 417.72KI
	 *
	 */
	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Column {
		/**
		 * カラム番号を返す。必ず0から始めること。
		 * @return カラム番号
		 */
		int value();
	}
}