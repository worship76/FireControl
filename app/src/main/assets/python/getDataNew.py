import requests
from bs4 import BeautifulSoup
res = requests.get('http://www.weather.com.cn/weather1d/101100101.shtml')
res.encoding = 'utf-8'
html = res.text
soup = BeautifulSoup(html,'html.parser')#解析文档
weathers = soup.find('div',class_="con today clearfix").find('div',class_="today clearfix").find_all('script')
print(weathers)
