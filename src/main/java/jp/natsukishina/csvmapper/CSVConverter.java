package jp.natsukishina.csvmapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * CSVファイルとの相互変換を扱うクラス<br>
 * 使用する際は{@link CSVConvertable}を実装したクラスが必要
 * @author 417.72KI
 *
 */
public abstract class CSVConverter {

	/**
	 * 最後に読み込んだCSVの文字コードを格納する
	 */
	private static String inputCharCode;

	/**
	 * リスト内の要素を1レコードとしてCSVファイルに出力する<br>
	 * 出力時の文字コードは最後に読み込んだCSVファイルの文字コード<br>
	 * (※CSVファイルが一度も読み込まれていない場合はUTF-8)
	 *
	 * @param filePath
	 *            出力先ファイルパス(絶対パス)
	 * @param list
	 *            出力するリスト
	 * @throws IOException
	 */
	public static void output(String filePath, List<? extends CSVConvertable> list) throws IOException {
		output(filePath, list, inputCharCode == null ? "UTF-8" : inputCharCode);
	}

	/**
	 * リスト内の要素を1レコードとしてCSVファイルに出力する<br>
	 * 出力時の文字コードはUTF-8
	 *
	 * @param filePath
	 *            出力先ファイルパス(絶対パス)
	 * @param list
	 *            出力するリスト
	 * @param charCode
	 *            出力文字コード
	 * @throws IOException
	 */
	public static void output(String filePath, List<? extends CSVConvertable> list, String charCode)
			throws IOException {
		if (list == null || list.isEmpty()) {
			return;
		}

		File file = new File(filePath);
		{
			//親フォルダが無い場合はフォルダを作成(再帰的に)
			File parent = file.getParentFile();
			if(!parent.exists()) {
				parent.mkdirs();
			}
		}
		file.createNewFile();
		try (PrintWriter pw = new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charCode)))) {
			for (CSVConvertable c : list) {
				for (int i = 0; i < c.array4exportCSV().length; i++) {
					String str = c.array4exportCSV()[i];
					str = str.replace("\"", "\"\"");
					if (c.includeLines()) {
						pw.print("\"");
					}
					pw.print(str);
					if (c.includeLines()) {
						pw.print("\"");
					}
					if (i < c.array4exportCSV().length - 1) {
						pw.print(",");
					}
				}
				pw.println();
			}
			System.out.println("output to " + filePath);
		}
	}

	/**
	 * CSVファイルを解析し、指定されたクラスのリストに変換する<br>
	 * 変換には {@link CSVConvertable#importFromCSV(LinkedList)}を使用する
	 *
	 * @param filePath
	 *            CSVファイルのパス
	 * @param clazz
	 *            変換するクラス
	 * @return 指定クラスのインスタンスリスト
	 * @throws CSVException
	 */
	public static <E extends CSVConvertable> List<E> convertFromCSVFile(String filePath, Class<E> clazz) {
		try {
			List<LinkedList<String>> buildList = build(filePath);
			List<E> convertedList = new ArrayList<>();
			for (LinkedList<String> list : buildList) {
				E element = clazz.newInstance();
				element.importFromCSV(list);
				convertedList.add(element);
			}
			return convertedList;
		} catch (InstantiationException e) {
			throw new CSVException("Class<" + clazz.getName() + "> must have a default Constructor", e);
		} catch (IllegalAccessException | IOException e) {
			throw new CSVException(e);
		}
	}

	/**
	 * CSVファイルを解析してフィールド配列のリストを生成する
	 *
	 * @param filePath
	 *            CSVファイルのパス
	 * @return 生成されたフィールド配列のリスト
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static List<LinkedList<String>> build(String filePath) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		inputCharCode = FileCharDetecter.detect(filePath);
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath), inputCharCode))) {
			List<LinkedList<String>> csv = new ArrayList<>();
			String recordStr;
			while ((recordStr = buildRecord(reader)) != null) {
				LinkedList<String> record = new LinkedList<>();
				splitRecord(recordStr, record);
				if (!record.isEmpty()) {
					csv.add(record);
				}
			}
			return csv;
		}
	}

	/**
	 * BufferedReaderから1レコード分のテキストを取り出す。
	 *
	 * @param reader
	 *            行データを取り出すBufferedReader。
	 * @return 1レコード分のテキスト。
	 * @throws IOException
	 *             入出力エラー
	 */
	private static String buildRecord(BufferedReader reader) throws IOException {
		String result = reader.readLine();
		int pos;
		if (result != null && 0 < result.length() && 0 <= (pos = result.indexOf("\""))) {
			boolean inString = true;
			String rawline = result;
			String newline = null;
			StringBuffer buff = new StringBuffer(1024);
			while (true) {
				while (0 <= (pos = rawline.indexOf("\"", ++pos))) {
					inString = !inString;
				}
				if (inString && (newline = reader.readLine()) != null) {
					buff.append(rawline);
					buff.append("\n");
					pos = -1;
					rawline = newline;
					continue;
				} else {
					if (inString || 0 < buff.length()) {
						buff.append(rawline);
						if (inString) {
							buff.append("\"");
						}
						result = buff.toString();
					}
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 1レコード分のテキストを分割してフィールドの配列にする。
	 *
	 * @param src
	 *            1レコード分のテキストデータ。
	 * @param dest
	 *            フィールドの配列の出力先。
	 */
	private static void splitRecord(String src, LinkedList<String> dest) {
		String[] columns = src.split(",", -1);
		int maxlen = columns.length;
		int startPos, endPos, columnlen;
		StringBuffer buff = new StringBuffer(1024);
		String column;
		boolean isInString, isEscaped;
		for (int index = 0; index < maxlen; index++) {
			column = columns[index];
			if ((endPos = column.indexOf("\"")) < 0) {
				dest.addLast(column);
			} else {
				isInString = (endPos == 0);
				isEscaped = false;
				columnlen = column.length();
				buff.setLength(0);
				startPos = (isInString) ? 1 : 0;
				while (startPos < columnlen) {
					if (0 <= (endPos = column.indexOf("\"", startPos))) {
						buff.append((startPos < endPos) ? column.substring(startPos, endPos) : isEscaped ? "\"" : "");
						isEscaped = !isEscaped;
						isInString = !isInString;
						startPos = ++endPos;
					} else {
						buff.append(column.substring(startPos));
						if (isInString && index < maxlen - 1) {
							column = columns[++index];
							columnlen = column.length();
							buff.append(",");
							startPos = 0;
						} else {
							break;
						}
					}
				}
				dest.addLast(buff.toString());
			}
		}
	}

	/**
	 * 最後に読み込んだCSVファイルの文字コードを返す。
	 * CSVファイルが一度も読み込まれていない場合はnullを返す。
	 * @return 最後に読み込んだCSVファイルの文字コード
	 */
	public static String getInputCharCode() {
		return inputCharCode;
	}

}
