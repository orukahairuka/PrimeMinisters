#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'AOKI Atsushi'
__version__ = '1.0.7'
__date__ = '2021/01/10 (Created: 2016/01/01)'

import datetime
# import locale
import os
import os.path
import re
import subprocess

# from PIL import Image

from csv2html.downloader import Downloader
from csv2html.io import IO
from csv2html.reader import Reader
from csv2html.table import Table
from csv2html.tuple import Tuple
from csv2html.writer import Writer

# pylint: disable=R0201
# R0201: Method could be a function (no-self-use)

class Translator:
	"""トランスレータ：CSVファイルをHTMLページへと変換するプログラム。"""

	def __init__(self, classOfAttributes):
		"""トランスレータのコンストラクタ。"""

		super().__init__()
		classOfAttributes.flush_base_directory()
		self._input_table = Table('input', classOfAttributes)
		self._output_table = Table('output', classOfAttributes)

	def compute_string_of_days(self, period):
		"""在位日数を計算して、それを文字列にして応答する。"""

		# 在位期間の文字列から在位日数を計算する
		try:
			dates = period.split('〜')
			start_date_str = dates[0]
			end_date_str = dates[1] if len(dates) > 1 else ''

			start_date = datetime.datetime.strptime(start_date_str, '%Y年%m月%d日')
			
			if end_date_str == '' or end_date_str is None:
				end_date = datetime.datetime.now()
			else:
				end_date = datetime.datetime.strptime(end_date_str, '%Y年%m月%d日')

			delta = end_date - start_date
			days = delta.days + 1
			
			return f"{days:,}"
		except Exception:
			return ''

	def compute_string_of_image(self, a_tuple):
		"""サムネイル画像から画像へ飛ぶためのHTML文字列を作成して、それを応答する。"""

		# 画像のHTML文字列を作成する
		
		attributes = a_tuple.attributes()
		keys = attributes.keys()
		values = a_tuple.values()
		
		# 画像とサムネイルのファイル名を取得
		image_filename = ""
		thumbnail_filename = ""
		
		try:
			image_index = keys.index("image")
			if image_index < len(values):
				image_filename = values[image_index]
		except ValueError:
			pass
			
		try:
			thumbnail_index = keys.index("thumbnail")
			if thumbnail_index < len(values):
				thumbnail_filename = values[thumbnail_index]
			else:
				thumbnail_filename = image_filename
		except ValueError:
			thumbnail_filename = image_filename

		if not image_filename:
			return ""

		base_url = attributes.base_url()

		
		image_url = base_url + image_filename
		thumbnail_url = base_url + thumbnail_filename
		
		return f'<a href="{image_url}"><img src="{thumbnail_url}" alt="{image_filename}" /></a>'

	def execute(self):
		"""CSVファイルをHTMLページへと変換する。"""

		# ダウンローダに必要なファイル群をすべてダウンロードしてもらい、
		# 入力となるテーブルを獲得する。
		a_downloader = Downloader(self._input_table)
		a_downloader.perform()

		# ダウンロードしたCSVファイルを読み込む。
		a_reader = Reader(self._input_table)
		a_reader.perform()

		# トランスレータに入力となるテーブルを渡して変換してもらい、
		# 出力となるテーブルを獲得する。
		print(self._input_table)
		self.translate()
		print(self._output_table)

		# ライタに出力となるテーブルを渡して、
		# Webページを作成してもらう。
		a_writer = Writer(self._output_table)
		a_writer.perform()

		# 作成したページをウェブブラウザで閲覧する。
		class_attributes = self._output_table.attributes().__class__
		base_directory = class_attributes.base_directory()
		index_html = class_attributes.index_html()
		a_command = "open -a 'Safari' " + base_directory + os.sep + index_html
		subprocess.getoutput(a_command)

	@classmethod
	def perform(cls, class_attributes):
		"""属性リストのクラスを受け取り、CSVファイルをHTMLページへと変換する。"""

		# トランスレータのインスタンスを生成する。
		a_translator = cls(class_attributes)
		# トランスレータにCSVファイルをHTMLページへ変換するように依頼する。
		a_translator.execute()

	def translate(self):
		"""CSVファイルを基にしたテーブルから、HTMLページを基にするテーブルに変換する。"""

		# 入力テーブルの各タプルを処理して、出力テーブルに追加する
		input_attributes = self._input_table.attributes()
		output_attributes = self._output_table.attributes()
		
		output_keys = output_attributes.keys()
		
		for input_tuple in self._input_table.tuples():
			output_values = []
			for key in output_keys:
				if key == 'days':
					# 在位期間から日数を計算
					try:
						period_index = input_attributes.keys().index('period')
						period = input_tuple.values()[period_index]
						days_string = self.compute_string_of_days(period)
						output_values.append(days_string)
					except ValueError:
						output_values.append('')
				elif key == 'image':
					# 画像HTMLを生成
					image_html = self.compute_string_of_image(input_tuple)
					output_values.append(image_html)
				else:
					# その他のキーはそのままコピー
					try:
						input_index = input_attributes.keys().index(key)
						output_values.append(input_tuple.values()[input_index])
					except ValueError:
						output_values.append('')
			
			# 出力タプルを作成してテーブルに追加
			output_tuple = Tuple(output_attributes, output_values)
			self._output_table.add(output_tuple)
