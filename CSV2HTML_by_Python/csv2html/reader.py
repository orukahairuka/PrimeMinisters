#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'AOKI Atsushi'
__version__ = '1.0.7'
__date__ = '2021/01/10 (Created: 2016/01/01)'

import os

from csv2html.io import IO
from csv2html.tuple import Tuple

class Reader(IO):
	"""リーダ：情報を記したCSVファイルを読み込んでテーブルに仕立て上げる。"""

	def __init__(self, input_table):
		"""リーダのコンストラクタ。"""

		super().__init__(input_table)
		(lambda x: x)(input_table) # NOP

	def perform(self):
		"""ダウンロードしたCSVファイルを読み込む。"""

		# CSVファイルのパスを取得
		base_directory = self.attributes().base_directory()
		csv_file_path = os.path.join(base_directory, 'data.csv')
		
		# CSVファイルを読み込む
		if not os.path.exists(csv_file_path):
			print(f"CSV file not found: {csv_file_path}")
			return


		rows = self.read_csv(csv_file_path)
		
		if not rows:
			return

		# 1行目を属性名（Names）として設定
		header_names = rows[0]
		self.attributes()._names = header_names
		
		# 2行目以降をタプルとしてテーブルに追加
		for values in rows[1:]:
			a_tuple = Tuple(self.attributes(), values)
			self.table().add(a_tuple)
			
		print(f"Loaded {len(rows) - 1} records from CSV")
