package csv2html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
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
			aWriter.write("<style>");
			aWriter.newLine();
			aWriter.write("table { border-collapse: collapse; width: 100%; }");
			aWriter.newLine();
			aWriter.write("th, td { border: 1px solid black; padding: 8px; text-align: left; }");
			aWriter.newLine();
			aWriter.write("th { background-color: #f2f2f2; }");
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
			// 各タプル（データ行）を出力
			for (Tuple aTuple : this.tuples())
			{
				aWriter.write("<tr>");
				aWriter.newLine();

				// 各値をtd要素として出力
				for (String value : aTuple.values())
				{
					aWriter.write("<td>" + IO.htmlCanonicalString(value) + "</td>");
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
