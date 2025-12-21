#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'AOKI Atsushi'
__version__ = '1.0.7'
__date__ = '2021/01/10 (Created: 2016/01/01)'

import csv

class IO:
	"""入出力：リーダ・ダウンローダ・ライタを抽象する。"""

	def __init__(self, a_table):
		"""入出力のコンストラクタ。"""

		super().__init__()
		self._table = a_table

	def attributes(self):
		"""属性リストを応答する。"""

		return self.table().attributes()

	def read_csv(self, filename):
		"""指定されたファイルをCSVとして読み込み、行リストを応答する。"""


		encodings = ['utf-8', 'shift_jis', 'cp932']
		for encoding in encodings:
			try:
				with open(filename, 'r', encoding=encoding, newline='') as file:
					csv_reader = csv.reader(file)
					rows = list(csv_reader)
					return rows
			except UnicodeDecodeError:
				continue
			except Exception as e:
				print(f"Error reading CSV with encoding {encoding}: {e}")
				return []
		print("Failed to read CSV with supported encodings.")
		return []

	@classmethod
	def html_canonical_string(cls, a_string):
		"""指定された文字列をHTML内に記述できる正式な文字列に変換して応答する。"""

		table = {
			'&'  : '&amp;',
			'>'  : '&gt;',
			'<'  : '&lt;',
			'"'  : '&quot;',
			' '  : '&nbsp;',
			'\t' : '',
			'\r' : '',
			'\n' : '<br>',
			'\f' : '',
		}

		if a_string is None:
			return ''
		
		# HTML特殊文字をエスケープ（&を最初に処理する必要がある）
		canonical_string = a_string
		for char, entity in table.items():
			canonical_string = canonical_string.replace(char, entity)
			
		return canonical_string

	def table(self):
		"""テーブルを応答する。"""

		return self._table

	def tuples(self):
		"""タプル群を応答する。"""

		return self.table().tuples()

	def write_csv(self, filename, rows):
		"""指定されたファイルにCSVとして行たち(rows)を書き出す。"""
		with open(filename, 'w', encoding='utf-8', newline='') as file:
			csv_writer = csv.writer(file)
			csv_writer.writerows(rows)
