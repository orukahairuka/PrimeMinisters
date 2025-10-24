package csv2html;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

/**
 * トランスレータ：CSVファイルをHTMLページへと変換するプログラム。
 */
public class Translator extends Object
{
	/**
	 * CSVに由来するテーブルを記憶するフィールド。
	 */
	private Table inputTable;

	/**
	 * HTMLに由来するテーブルを記憶するフィールド。
	 */
	private Table outputTable;

	/**
	 * 属性リストのクラスを指定したトランスレータのコンストラクタ。
	 * @param classOfAttributes 属性リストのクラス
	 */
	public Translator(Class<? extends Attributes> classOfAttributes)
	{
		super();

		Attributes.flushBaseDirectory();

		try
		{
			Constructor<? extends Attributes> aConstructor = classOfAttributes.getConstructor(String.class);

			this.inputTable = new Table(aConstructor.newInstance("input"));
			this.outputTable = new Table(aConstructor.newInstance("output"));
		}
		catch (NoSuchMethodException |
			   InstantiationException |
			   IllegalAccessException |
			   InvocationTargetException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * 在位日数を計算して、それを文字列にして応答する。
	 * @param periodString 在位期間の文字列
	 * @return 在位日数の文字列
	 */
	public String computeNumberOfDays(String periodString)
	{
		// 日付文字列を「〜」で分割
		String[] dates = periodString.split("〜");
		if (dates.length < 2) { return ""; }

		try
		{
			// 日付フォーマットを定義
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy年MM月dd日");

			// 開始日と終了日をパース
			java.util.Date startDate = format.parse(dates[0].trim());

			// 終了日が空の場合は現在日を使用
			java.util.Date endDate;
			if (dates[1].trim().isEmpty())
			{
				endDate = new java.util.Date();
			}
			else
			{
				endDate = format.parse(dates[1].trim());
			}

			// 日数を計算（ミリ秒の差を日数に変換）
			long diffInMillis = endDate.getTime() - startDate.getTime();
			long days = diffInMillis / (1000 * 60 * 60 * 24);

			return String.valueOf(days);
		}
		catch (Exception anException)
		{
			// パースエラーの場合は空文字列を返す
			return "";
		}
	}

	/**
	 * サムネイル画像から画像へ飛ぶためのHTML文字列を作成して、それを応答する。
	 * @param aString 画像の文字列
	 * @param aTuple タプル
	 * @param no 番号
	 * @return サムネイル画像から画像へ飛ぶためのHTML文字列
	 */
	public String computeStringOfImage(String aString, Tuple aTuple, int no)
	{
		return null;
	}

	/**
	 * CSVファイルをHTMLページへ変換する。
	 */
	public void execute()
	{
		// 必要な情報をダウンロードする。
		Downloader aDownloader = new Downloader(this.inputTable);
		aDownloader.perform();

		// ダウンロードしたCSVファイルを読み込む。
		Reader aReader = new Reader(this.inputTable);
		aReader.perform();

		// CSVに由来するテーブルをHTMLに由来するテーブルへと変換する。
		System.out.println(this.inputTable);
		this.translate();
		System.out.println(this.outputTable);

		// HTMLに由来するテーブルから書き出す。
		Writer aWriter = new Writer(this.outputTable);
		aWriter.perform();

		// ブラウザを立ち上げて閲覧する。
		try
		{
			Attributes attributes = this.outputTable.attributes();
			String fileStringOfHTML = attributes.baseDirectory() + attributes.indexHTML();
			ProcessBuilder aProcessBuilder = new ProcessBuilder("open", "-a", "Safari", fileStringOfHTML);
			aProcessBuilder.start();
		}
		catch (Exception anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * 属性リストのクラスを受け取って、CSVファイルをHTMLページへと変換するクラスメソッド。
	 * @param classOfAttributes 属性リストのクラス
	 */
	public static void perform(Class<? extends Attributes> classOfAttributes)
	{
		// トランスレータのインスタンスを生成する。
		Translator aTranslator = new Translator(classOfAttributes);
		// トランスレータにCSVファイルをHTMLページへ変換するように依頼する。
		aTranslator.execute();

		return;
	}

	/**
	 * CSVファイルを基にしたテーブルから、HTMLページを基にするテーブルに変換する。
	 */
	public void translate()
	{
		Attributes inputAttributes = this.inputTable.attributes();
		Attributes outputAttributes = this.outputTable.attributes();

		// inputTableの各Tupleを処理
		for (Tuple inputTuple : this.inputTable.tuples())
		{
			List<String> outputValues = new ArrayList<String>();

			// outputのkeysに合わせて値を設定
			for (int index = 0; index < outputAttributes.size(); index++)
			{
				String key = outputAttributes.keys().get(index);

				if (key.equals("days"))
				{
					// 在位日数を計算
					int periodIndex = inputAttributes.indexOf("period");
					if (periodIndex >= 0 && periodIndex < inputTuple.values().size())
					{
						String periodString = inputTuple.values().get(periodIndex);
						String daysString = this.computeNumberOfDays(periodString);
						outputValues.add(daysString);
					}
					else
					{
						outputValues.add("");
					}
				}
				else
				{
					// 対応するinputの値を取得
					int inputIndex = inputAttributes.indexOf(key);
					if (inputIndex >= 0 && inputIndex < inputTuple.values().size())
					{
						outputValues.add(inputTuple.values().get(inputIndex));
					}
					else
					{
						outputValues.add("");
					}
				}
			}

			// 新しいTupleを作成してoutputTableに追加
			Tuple outputTuple = new Tuple(outputAttributes, outputValues);
			this.outputTable.add(outputTuple);
		}

		return;
	}
}
