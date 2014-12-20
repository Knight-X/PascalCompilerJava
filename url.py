import urllib.request  
from bs4 import BeautifulSoup  
url = 'http://mops.twse.com.tw/t21/sii/t21sc03_102_1_0.html'  
response = urllib.request.urlopen(url)  
html = response.read()  
sp = BeautifulSoup(html.decode('cp950')) 

tblh = sp.find_all('table', attrs = {'border' : '0', 'width' : '100%' })  

tbl = tblh[0].find('table', attrs = {'bordercolor' : '#FF6600' })

trs = tbl.find_all('tr')

tds = trs[2].find_all('td')

print(tds[0].get_text(),
tds[1].get_text(), tds[2].get_text(), tds[3].get_text(), tds[4].get_text())

td2 = tds[2].get_text()
td2 = td2.strip().replace(",", "")
print(td2)
