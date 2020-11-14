#!usr/bin/python3
# -*- coding: utf-8 -*-

import re
import unittest

pattern = re.compile(r"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{5,}(?!^MTC)$")

class TestRegexPattern(unittest.TestCase):

    def setUp(self):
        self.pattern = pattern

    def tearDown(self):
        self.pattern = None

    def test_correct_string(self):
        correct_string = "Ts3ysMTC123"
        self.assertIsNotNone(re.match(self.pattern,correct_string))

    def test_incorrect_string(self):
        incorrect_strings = ["Tsss","dc83","TKJ2"]
        for string in incorrect_strings:
            self.assertIsNone(re.match(self.pattern,string))

    def test_except_string(self):
        string = "MTC"
        self.assertIsNone(re.match(self.pattern,string))


if __name__=="__main__":
    unittest.main()
