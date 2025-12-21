#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'AOKI Atsushi'
__version__ = '1.0.7'
__date__ = '2021/01/10 (Created: 2016/01/01)'

import os
# import shutil
import urllib.request

from csv2html.io import IO
# from csv2html.reader import Reader

class Downloader(IO):
	"""ダウンローダ：CSVファイル・画像ファイル・サムネイル画像ファイルをダウンロードする。"""

	def __init__(self, input_table):
		"""ダウンローダのコンストラクタ。"""

		super().__init__(input_table)
		(lambda x: x)(input_table) # NOP

	def download_csv(self):
		"""情報を記したCSVファイルをダウンロードする。"""

		# CSVファイルのURLと保存先のパスを取得
		csv_url = self.attributes().csv_url()
		base_directory = self.attributes().base_directory()
		filename = os.path.basename(csv_url)
		save_path = os.path.join(base_directory, 'data.csv')
		
		# ダウンロードを実行
		try:
			urllib.request.urlretrieve(csv_url, save_path)
			print(f"Downloaded CSV: {csv_url} -> {save_path}")
		except Exception as e:
			print(f"Error downloading CSV: {e}")

	def download_images(self, image_filenames):
		"""画像ファイル群または縮小画像ファイル群をダウンロードする。"""

		base_url = self.attributes().base_url()
		base_directory = self.attributes().base_directory()
		
		for image_filename in image_filenames:
			image_url = base_url + image_filename
			save_path = os.path.join(base_directory, image_filename)
			
			# ディレクトリが存在しない場合は作成
			os.makedirs(os.path.dirname(save_path), exist_ok=True)
			
			try:
				urllib.request.urlretrieve(image_url, save_path)
				# print(f"Downloaded image: {image_url} -> {save_path}")
			except Exception as e:
				print(f"Error downloading image {image_url}: {e}")

	def perform(self):
		"""すべて（情報を記したCSVファイル・画像ファイル群・縮小画像ファイル群）をダウンロードする。"""

		# CSVファイルをダウンロード
		self.download_csv()
		
		# 画像とサムネイルをダウンロード
		input_table = self.table()
		# 画像ファイルのリストを取得してダウンロード
		# self.download_images(input_table.image_filenames())
		# self.download_images(input_table.thumbnail_filenames())
		# self.download_images(input_table.thumbnail_filenames())
		pass
