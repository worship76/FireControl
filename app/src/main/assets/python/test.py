import requests,re,time,json

#城市ID
areaid = '101100101'
#获取毫秒
t = time.time()
millisecond = int(round(t * 1000))
#设置headers
headers = {'Accept': 'text/html, application/xhtml+xml, image/jxr, */*',
               'Accept-Encoding':'gzip, deflate',
               'Accept-Language':'zh-Hans-CN, zh-Hans; q=0.5',
               'Connection':'Keep-Alive',
               'Host':'d1.weather.com.cn',
               'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36',
               'Referer':'http://www.weather.com.cn/weather1dn/'+areaid+'.shtml'
           }
res = requests.get('http://d1.weather.com.cn/sk_2d/'+areaid+'.html?_='+str(millisecond),params=None, headers=headers)
res.raise_for_status()
content = res.text
tqRegex = re.compile(r'\{[^\}]*\}')
jsonstr = tqRegex.search(content)
# print(jsonstr.group())
dict_json = json.loads(jsonstr.group())
# print(dict_json)
print('当前温度为'+dict_json['temp']+'℃')