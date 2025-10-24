package csv2html;

import java.io.File;
import java.util.List;
import utility.StringUtility;

/**
 * リーダ：情報を記したCSVファイルを読み込んでテーブルに仕立て上げる。
 */
public class Reader extends IO
{
	/**
	 * リーダのコンストラクタ。
	 * @param aTable テーブル
	 */
	public Reader(Table aTable)
	{
		super(aTable);

		return;
	}

	/**
	 * ダウンロードしたCSVファイルを読み込む。
	 */
	public void perform()
	{
		// CSVファイルのパスを取得
		String baseDirectory = this.attributes().baseDirectory();
		String csvFilePath = baseDirectory + "data.csv";

		// CSVファイルを読み込む
		List<String> lines = IO.readTextFromFile(csvFilePath);

		if (lines.isEmpty()) { return; }

		// 1行目（ヘッダー）を属性名として設定
		String headerLine = lines.get(0);
		List<String> headerNames = IO.splitString(headerLine, ",");
		this.attributes().names(headerNames);

		// 2行目以降をデータとして読み込み、Tupleを作成
		for (int index = 1; index < lines.size(); index++)
		{
			String line = lines.get(index);
			List<String> values = IO.splitString(line, ",");

			// Tupleを作成してTableに追加
			Tuple aTuple = new Tuple(this.attributes(), values);
			this.table().add(aTuple);
		}

		System.out.println("Loaded " + (lines.size() - 1) + " records from CSV");

		return;
	}
}
