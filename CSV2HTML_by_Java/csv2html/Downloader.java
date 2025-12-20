package csv2html;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import utility.ImageUtility;

/**
 * ダウンローダ：CSVファイル・画像ファイル・サムネイル画像ファイルをダウンロードする。
 */
public class Downloader extends IO
{
	/**
	 * ダウンローダのコンストラクタ。
	 * @param aTable テーブル
	 */
	public Downloader(Table aTable)
	{
		super(aTable);

		return;
	}

	/**
	 * 総理大臣の情報を記したCSVファイルをダウンロードする。
	 */
	public void downloadCSV()
	{
		// CSVファイルのURLを取得
		String urlString = this.attributes().csvUrl();

		// URLからテキスト（行リスト）を読み込む
		List<String> lines = IO.readTextFromURL(urlString);

		// ローカルのCSVファイルパスを作成
		String baseDirectory = this.attributes().baseDirectory();
		String csvFileName = "data.csv";
		String csvFilePath = baseDirectory + csvFileName;

		// ファイルに書き出す
		IO.writeText(lines, csvFilePath);

		System.out.println("Downloaded CSV: " + urlString + " -> " + csvFilePath);

		return;
	}

	/**
	 * 総理大臣の画像群をダウンロードする。
	 */
	public void downloadImages()
	{
		int indexOfImage = this.attributes().indexOfImage();
		this.downloadPictures(indexOfImage);

		return;
	}

	/**
	 * 総理大臣の画像群またはサムネイル画像群をダウンロードする。
	 * @param indexOfPicture 画像のインデックス
	 */
	private void downloadPictures(int indexOfPicture)
	{
		return;
	}

	/**
	 * 総理大臣の画像群をダウンロードする。
	 */
	public void downloadThumbnails()
	{
		int indexOfThumbnail = this.attributes().indexOfThumbnail();
		this.downloadPictures(indexOfThumbnail);

		return;
	}

	/**
	 * 総理大臣の情報を記したCSVファイルをダウンロードして、画像群やサムネイル画像群もダウロードする。
	 */
	public void perform()
	{
		// CSVファイルをダウンロード
		this.downloadCSV();

		// 画像とサムネイルのダウンロード（Phase 4で実装予定）
		// this.downloadImages();
		// this.downloadThumbnails();

		return;
	}
}
