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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.natsukishina.csvmapper.CSVMappable.Column;
import jp.natsukishina.csvmapper.file.CSVFile;

/**
 * CSVファイルのマッピングを行うクラス<br>
 * 使用する際は{@link CSVMappable}を実装したクラスが必要
 *
 * @author 417.72KI
 */
public abstract class CSVMapper {
	private static final Logger LOG = LoggerFactory.getLogger(CSVMapper.class);
	private static final String DEFAULT_CHAR_CODE = "UTF-8";

	/**
	 * 最後に読み込んだCSVの文字コードを格納する
	 */
	private static String inputCharCode;

	/**
	 * リスト内の要素を1レコードとしてCSVファイルに出力する<br>
	 * 出力時の文字コードはUTF-8
	 *
	 * @param file
	 *            出力先ファイル
	 * @param list
	 *            出力するリスト
	 * @param charCode
	 *            出力文字コード
	 * @return ファイル出力が完了したらtrue, listが空の場合falseを返す
	 * @throws CSVException
	 *             CSV出力時のエラー
	 */
	public static boolean output(CSVFile file, List<? extends CSVMappable> list, String charCode) throws CSVException {
		if (list == null || list.isEmpty()) {
			return false;
		}

		try {
			// 親フォルダが無い場合はフォルダを作成(再帰的に)
			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			file.createNewFile();
		} catch (IOException e) {
			throw new CSVException(e);
		}

		try (PrintWriter pw = new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charCode)))) {
			for (CSVMappable c : list) {
				Class<? extends CSVMappable> cls = c.getClass();
				Field[] columns = Arrays.stream(cls.getDeclaredFields())
						.filter(field -> field.getDeclaredAnnotation(Column.class) != null)
						.toArray(Field[]::new);
				String[] strs = new String[columns.length];
				Arrays.stream(columns).forEach(field -> {
					try {
						field.setAccessible(true);
						Column column = field.getDeclaredAnnotation(Column.class);
						Object obj;
						obj = field.get(c);
						String str;
						if (obj == null) {
							str = "";
						} else {
							str = obj.toString();
						}
						str = str.replace("\"", "\"\"");

						StringBuffer sb = new StringBuffer();
						if (c.includeLines()) {
							sb.append("\"");
						}
						sb.append(str);
						if (c.includeLines()) {
							sb.append("\"");
						}
						strs[column.value()] = sb.toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				pw.println(StringUtils.join(strs, ","));
			}
			LOG.info("output to " + file.getAbsolutePath());
			return true;
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			throw new CSVException(e);
		}
	}

	/**
	 * リスト内の要素を1レコードとしてCSVファイルに出力する<br>
	 * 出力時の文字コードは最後に読み込んだCSVファイルの文字コード<br>
	 * (※CSVファイルが一度も読み込まれていない場合はUTF-8)
	 *
	 * @param file
	 *            出力先ファイル
	 * @param list
	 *            出力するリスト
	 * @return ファイル出力が完了したらtrue, listが空の場合falseを返す
	 * @throws CSVException
	 *             CSV出力時のエラー
	 */
	public static boolean output(CSVFile file, List<? extends CSVMappable> list) throws CSVException {
		return output(file, list, inputCharCode == null ? DEFAULT_CHAR_CODE : inputCharCode);
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
	 * @return ファイル出力が完了したらtrue, listが空の場合falseを返す
	 * @throws CSVException
	 *             CSV出力時のエラー
	 */
	public static boolean output(String filePath, List<? extends CSVMappable> list, String charCode) throws CSVException {
		CSVFile file = new CSVFile(filePath);
		return output(file, list, charCode);
	}

	/**
	 * リスト内の要素を1レコードとしてCSVファイルに出力する<br>
	 * 出力時の文字コードは最後に読み込んだCSVファイルの文字コード<br>
	 * (※CSVファイルが一度も読み込まれていない場合はUTF-8)
	 *
	 * @param filePath
	 *            出力先ファイルパス(絶対パス)
	 * @param list
	 *            出力するリスト
	 * @return ファイル出力が完了したらtrue, listが空の場合falseを返す
	 * @throws CSVException
	 *             CSV出力時のエラー
	 */
	public static boolean output(String filePath, List<? extends CSVMappable> list) throws CSVException {
		return output(filePath, list, inputCharCode == null ? DEFAULT_CHAR_CODE : inputCharCode);
	}

	/**
	 * CSVファイルを解析し、指定されたクラスのリストに変換する<br>
	 *
	 * @param <E>
	 *            CSVConvertableを実装したクラス
	 * @param filePath
	 *            CSVファイルのパス
	 * @param clazz
	 *            変換するクラス
	 * @return 指定クラスのインスタンスリスト
	 * @throws CSVException
	 *             CSV読み込み時のエラー
	 */
	public static <E extends CSVMappable> List<E> convertFromCSVFile(String filePath, Class<E> clazz)
			throws CSVException {
		return convertFromCSVFile(new CSVFile(filePath), clazz);
	}

	/**
	 * CSVファイルを解析し、指定されたクラスのリストに変換する<br>
	 *
	 * @param <E>
	 *            CSVConvertableを実装したクラス
	 * @param file
	 *            CSVファイル
	 * @param clazz
	 *            変換するクラス
	 * @return 指定クラスのインスタンスリスト
	 * @throws CSVException
	 *             CSV読み込み時のエラー
	 */
	public static <E extends CSVMappable> List<E> convertFromCSVFile(CSVFile file, Class<E> clazz) throws CSVException {
		try {
			List<LinkedList<String>> buildList = build(file);
			List<E> convertedList = new ArrayList<>();
			for (LinkedList<String> list : buildList) {
				try {
					Constructor<E> constructor = clazz.getDeclaredConstructor();
					constructor.setAccessible(true);
					E element = constructor.newInstance();
					Class<?> cls = element.getClass();
					List<Field> columnList = Arrays.stream(cls.getDeclaredFields())
							.filter(field -> field.getDeclaredAnnotation(Column.class) != null)
							.collect(Collectors.toList());
					if (list.size() < columnList.size()) {
						LOG.error("invalid row: " + list);
						continue;
					}
					columnList.forEach(field -> {
						try {
							field.setAccessible(true);
							Column column = field.getDeclaredAnnotation(Column.class);
							String value = list.get(column.value());
							field.set(element, value);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					convertedList.add(element);
				} catch (RuntimeException e) {
					System.out.println("skip row: " + list);
					continue;
				}
			}
			return convertedList;
		} catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new CSVException("Class<" + clazz.getName() + "> must have a default Constructor", e);
		} catch (IllegalAccessException | IOException e) {
			throw new CSVException(e);
		}
	}

	/**
	 * CSVファイルを解析してフィールド配列のリストを生成する
	 *
	 * @param file
	 *            CSVファイル
	 * @return 生成されたフィールド配列のリスト
	 * @throws IOException
	 *             ファイルの入出力に関するエラー
	 */
	private static List<LinkedList<String>> build(CSVFile file) throws IOException {
		inputCharCode = FileCharDetecter.detect(file);
		if (inputCharCode == null) {
			inputCharCode = DEFAULT_CHAR_CODE;
		}
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), inputCharCode))) {
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
	 * 最後に読み込んだCSVファイルの文字コードを返す。 CSVファイルが一度も読み込まれていない場合はnullを返す。
	 *
	 * @return 最後に読み込んだCSVファイルの文字コード
	 */
	public static String getInputCharCode() {
		return inputCharCode;
	}

	/**
	 * ファイルがCSVファイルかどうか判定する。<br>
	 * 判定基準は拡張子が'.csv'となっていること
	 *
	 * @param file
	 *            ファイル
	 * @return CSVファイルならtrue
	 */
	public static boolean isCSVFile(File file) {
		if (file == null) {
			return false;
		}

		if (file.exists() && !file.isFile()) {
			return false;
		}

		String suffix = getSuffix(file.getName());
		if (suffix == null) {
			return false;
		}
		return suffix.toLowerCase().equals("csv");
	}

	/**
	 * ファイル名から拡張子を返します。
	 *
	 * @param fileName
	 *            ファイル名
	 * @return ファイルの拡張子
	 */
	private static String getSuffix(String fileName) {
		if (fileName == null)
			return null;
		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			return fileName.substring(point + 1);
		}
		return fileName;
	}
}
