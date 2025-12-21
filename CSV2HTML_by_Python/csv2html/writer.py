#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'AOKI Atsushi'
__version__ = '1.0.7'
__date__ = '2021/01/10 (Created: 2016/01/01)'

import os

from csv2html.io import IO

# pylint: disable=R0201
# R0201: Method could be a function (no-self-use)

class Writer(IO):
	"""ライタ：情報のテーブルをHTMLページとして書き出す。"""

	def __init__(self, output_table):
		"""ライタのコンストラクタ。HTMLページを基にするテーブルを受け取る。"""

		super().__init__(output_table)
		(lambda x: x)(output_table) # NOP

	def perform(self):
		"""HTMLページを基にするテーブルから、インデックスファイル(index_html)に書き出す。"""

		class_attributes = self.attributes().__class__
		base_directory = class_attributes.base_directory()
		index_html = class_attributes.index_html()

		html_filename = os.path.join(base_directory, index_html)
		with open(html_filename, 'w', encoding='utf-8') as a_file:
			self.write_header(a_file)
			self.write_body(a_file)
			self.write_footer(a_file)

	def write_body(self, file):
		"""ボディを書き出す。つまり、属性リストを書き出し、タプル群を書き出す。"""

		file.write("<body>\n")
		file.write(f"<h1>{self.attributes().caption_string()}</h1>\n")
		
		file.write("<table>\n")
		
		# 属性リスト（ヘッダー）を書き出す
		file.write("<tr>\n")
		for name in self.attributes().names():
			file.write(f"<th>{self.html_canonical_string(name)}</th>\n")
		file.write("</tr>\n")
		
		# タプル群（データ）を書き出す
		tuples = self.table().tuples()
		
		# 画像列のインデックスを取得
		image_index = -1
		try:
			image_index = self.attributes().keys().index("image")
		except ValueError:
			pass

		for i, a_tuple in enumerate(tuples):
			file.write("<tr>\n")
			values = a_tuple.values()
			for j, value in enumerate(values):
				# 画像列はHTMLタグを含むのでエスケープしない
				if j == image_index:
					file.write(f"<td>{value}</td>\n")
				else:
					file.write(f"<td>{self.html_canonical_string(value)}</td>\n")
			file.write("</tr>\n")
			
		file.write("</table>\n")
		file.write("</body>\n")

	def write_footer(self, file):
		"""フッタを書き出す。"""

		file.write("</html>\n")

	def write_header(self, file):
		"""ヘッダを書き出す。"""

		file.write("<!DOCTYPE html>\n")
		file.write('<html lang="ja">\n')
		file.write("<head>\n")
		file.write('<meta charset="utf-8">\n')
		file.write(f"<title>{self.attributes().title_string()}</title>\n")
		
		file.write("<style>\n")
		file.write("body { background-color: #ffffff; margin: 20px; padding: 10px; font-family: serif; font-size: 10pt; }\n")
		file.write("a { text-decoration: underline; color: #000000; }\n")
		file.write("a:link { background-color: #ffddbb; }\n")
		file.write("a:visited { background-color: #ccffcc; }\n")
		file.write("a:hover, a:active { background-color: #dddddd; }\n")
		file.write("img { border: 0px; vertical-align: middle; }\n")
		file.write("table { border-collapse: collapse; border: 1px solid #ffffff; background-color: #ffffff; width: 100%; }\n")
		file.write("th { background-color: #ffddee; text-align: center; padding: 4px; border: 1px solid #ffffff; }\n")
		file.write("td { padding: 4px; border: 1px solid #ffffff; text-align: center; }\n")
		file.write("tr:nth-child(odd) td { background-color: #ddeeff; }\n")
		file.write("tr:nth-child(even) td { background-color: #ffffcc; }\n")
		file.write("h1 { font-size: 16pt; margin-bottom: 10px; background-color: #EBEBEB; padding: 4px; }\n")
		file.write("</style>\n")
		
		file.write("</head>\n")
