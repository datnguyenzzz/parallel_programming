import re
from lxml import etree

pattern = re.compile(r"([\w]+)\.((doc)?(html)?(psd)?){1}")

with open("test1.txt") as file_input:
    test = pattern.finditer(file_input.read())
    root = etree.Element("all")
    for i in test:
        element = etree.Element("p")
        element.text = i.group(0)
        root.append(element)
    print(etree.tostring(root))
