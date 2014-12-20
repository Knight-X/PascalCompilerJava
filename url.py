import urllib.request  
from bs4 import BeautifulSoup  
url = 'http://mops.twse.com.tw/t21/sii/t21sc03_102_1_0.html'  
response = urllib.request.urlopen(url)  
html = response.read()  
sp = BeautifulSoup(html.decode('cp950'))   
sp  
