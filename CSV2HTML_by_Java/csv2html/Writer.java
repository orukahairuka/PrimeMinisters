package csv2html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;
import utility.StringUtility;

/**
 * ライタ：情報のテーブルをHTMLページとして書き出す。
 */
public class Writer extends IO
{
	/**
	 * ライタのコンストラクタ。
	 * @param aTable テーブル
	 */
	public Writer(Table aTable)
	{
		super(aTable);

		return;
	}

	/**
	 * HTMLページを基にするテーブルからインデックスファイル(index.html)に書き出す。
	 */
	public void perform()
	{
		try
		{
			Attributes attributes = this.attributes();
			String fileStringOfHTML = attributes.baseDirectory() + attributes.indexHTML();
			File aFile = new File(fileStringOfHTML);
			FileOutputStream outputStream = new FileOutputStream(aFile);
			OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, StringUtility.encodingSymbol());
			BufferedWriter aWriter = new BufferedWriter(outputWriter);

			this.writeHeaderOn(aWriter);
			this.writeTableBodyOn(aWriter);
			this.writeFooterOn(aWriter);

			aWriter.close();
		}
		catch (UnsupportedEncodingException | FileNotFoundException anException) { anException.printStackTrace(); }
		catch (IOException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * 属性リストを書き出す。
	 * @param aWriter ライタ
	 */
	public void writeAttributesOn(BufferedWriter aWriter)
	{
		try
		{
			Attributes attributes = this.attributes();

			// テーブル開始タグ
			aWriter.write("<table>");
			aWriter.newLine();

			// テーブルヘッダー行
			aWriter.write("<tr>");
			aWriter.newLine();

			// 各属性名をth要素として出力
			for (int index = 0; index < attributes.size(); index++)
			{
				String attributeName = attributes.at(index);
				aWriter.write("<th>" + IO.htmlCanonicalString(attributeName) + "</th>");
				aWriter.newLine();
			}

			aWriter.write("</tr>");
			aWriter.newLine();
		}
		catch (IOException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * フッタを書き出す。
	 * @param aWriter ライタ
	 */
	public void writeFooterOn(BufferedWriter aWriter)
	{
		try
		{
			// テーブル終了タグ
			aWriter.write("</table>");
			aWriter.newLine();

			// BODY終了タグ
			aWriter.write("</body>");
			aWriter.newLine();

			// HTML終了タグ
			aWriter.write("</html>");
			aWriter.newLine();
		}
		catch (IOException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * ヘッダを書き出す。
	 * @param aWriter ライタ
	 */
	public void writeHeaderOn(BufferedWriter aWriter)
	{
		try
		{
			Attributes attributes = this.attributes();

			// DOCTYPE宣言
			aWriter.write("<!DOCTYPE html>");
			aWriter.newLine();

			// HTML開始タグ
			aWriter.write("<html lang=\"ja\">");
			aWriter.newLine();

			// HEAD部分
			aWriter.write("<head>");
			aWriter.newLine();
			aWriter.write("<meta charset=\"" + StringUtility.encodingSymbol() + "\">");
			aWriter.newLine();
			aWriter.write("<title>" + IO.htmlCanonicalString(attributes.titleString()) + "</title>");
			aWriter.newLine();

			// スタイルを定義
			aWriter.write("<style>");
			aWriter.newLine();
			// Body: 白背景、serifフォント、余白設定
			aWriter.write("body { background-color: #ffffff; margin: 20px; padding: 10px; font-family: serif; font-size: 10pt; }");
			aWriter.newLine();
			// リンク: 下線あり、状態別の背景色
			aWriter.write("a { text-decoration: underline; color: #000000; }");
			aWriter.newLine();
			aWriter.write("a:link { background-color: #ffddbb; }");
			aWriter.newLine();
			aWriter.write("a:visited { background-color: #ccffcc; }");
			aWriter.newLine();
			aWriter.write("a:hover, a:active { background-color: #dddddd; }");
			aWriter.newLine();
			// 画像: 枠なし、中央揃え
			aWriter.write("img { border: 0px; vertical-align: middle; }");
			aWriter.newLine();
			// テーブル: 白枠線、幅100%
			aWriter.write("table { border-collapse: collapse; border: 1px solid #ffffff; background-color: #ffffff; width: 100%; }");
			aWriter.newLine();
			// ヘッダセル: ピンク背景、中央揃え、白枠線
			aWriter.write("th { background-color: #ffddee; text-align: center; padding: 4px; border: 1px solid #ffffff; }");
			aWriter.newLine();
			// データセル: 基本スタイル、白枠線
			aWriter.write("td { padding: 4px; border: 1px solid #ffffff; text-align: center; }");
			aWriter.newLine();
			// 行ごとの色分け（奇数行: 青、偶数行: 黄色）
			aWriter.write("tr:nth-child(odd) td { background-color: #ddeeff; }");  // 奇数行: 青
			aWriter.newLine();
			aWriter.write("tr:nth-child(even) td { background-color: #ffffcc; }"); // 偶数行: 黄色
			aWriter.newLine();
			// 見出し（h1）のスタイル
			aWriter.write("h1 { font-size: 16pt; margin-bottom: 10px; background-color: #EBEBEB; padding: 4px; }");
			aWriter.newLine();
			aWriter.write("</style>");
			aWriter.newLine();
			aWriter.write("</head>");
			aWriter.newLine();

			// BODY開始タグ
			aWriter.write("<body>");
			aWriter.newLine();
			aWriter.write("<h1>" + IO.htmlCanonicalString(attributes.captionString()) + "</h1>");
			aWriter.newLine();
		}
		catch (IOException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * ボディを書き出す。
	 * @param aWriter ライタ
	 */
	public void writeTableBodyOn(BufferedWriter aWriter)
	{
		this.writeAttributesOn(aWriter);
		this.writeTuplesOn(aWriter);

		return;
	}

	/**
	 * タプル群を書き出す。
	 * @param aWriter ライタ
	 */
	public void writeTuplesOn(BufferedWriter aWriter)
	{
		try
		{
			Attributes attributes = this.attributes();
			int imageIndex = attributes.indexOfImage();

			// 各タプル（データ行）を出力
			for (Tuple aTuple : this.tuples())
			{
				aWriter.write("<tr>");
				aWriter.newLine();

				// 各値をtd要素として出力
				List<String> values = aTuple.values();
				for (int index = 0; index < values.size(); index++)
				{
					String value = values.get(index);
					// 画像列はHTMLタグを含むのでエスケープしない
					String cellContent = (index == imageIndex) ? value : IO.htmlCanonicalString(value);
					aWriter.write("<td>" + cellContent + "</td>");
					aWriter.newLine();
				}

				aWriter.write("</tr>");
				aWriter.newLine();
			}
		}
		catch (IOException anException) { anException.printStackTrace(); }

		return;
	}
}
