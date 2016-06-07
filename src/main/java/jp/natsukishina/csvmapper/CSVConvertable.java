package jp.natsukishina.csvmapper;

import java.util.List;

/**
 * CSVファイルとの相互変換用インターフェース
 *
 * @author 417.72KI
 *
 */
public interface CSVConvertable {

	/**
	 * CSVの要素内に改行が入りうるかを確認する
	 *
	 * @return 改行が入ったCSVを読み込む可能性がある場合はtrue<br>
	 *         CSVのレコード内に改行が入りえない場合はfalse
	 */
	public boolean includeLines();

	/**
	 * CSVの1行に出力する要素を配列にする
	 *
	 * @return 出力する要素の配列
	 */
	public String[] array4exportCSV();

	/**
	 * CSVから1行インポートする
	 *
	 * @param row インポートされる行
	 */
	public void importFromCSV(List<String> row);

}