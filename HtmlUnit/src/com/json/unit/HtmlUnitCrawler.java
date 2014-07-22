package com.json.unit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.Document;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class HtmlUnitCrawler {

	public static void main(String[] args) throws Exception {
		// http://detail.tmall.com/item.htm?id=20150366437
		// http://detail.tmall.com/item.htm?id=35259286943测试
		String url = "http://detail.tmall.com/item.htm?id=20150366437";
		Set<Cookie> ck = null;
		try {
			WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
			// 设置webClient的相关参数
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(false);
			webClient
					.setAjaxController(new NicelyResynchronizingAjaxController());
			// webClient.getOptions().setTimeout(50000);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			// 模拟浏览器打开一个目标网址
			HtmlPage rootPage = webClient.getPage(url);
			CookieManager cm = webClient.getCookieManager();
			// 得到cookie
			ck = cm.getCookies();
			System.out.println(ck.toString());
		} catch (Exception e) {
		}
		HtmlUnitCrawler.crawler(ck);
	}

	public static void crawler(Set<Cookie> ck) throws Exception, IOException {
		//http://ext.mdskip.taobao.com/extension/dealRecords.htm?bid_page=1&page_size=15&is_start=false&item_type=b&ends=1406019344000&starts=1405414544000&item_id=20150366437&user_tag=35196960&old_quantity=1957&seller_num_id=751075004&isFromDetail=yes&totalSQ=2320&sbn=a791c871795ba412034d28203cac658f
		//http://ext.mdskip.taobao.com/extension/dealRecords.htm?_ksTS=1405676551455_1842&callback=jsonp1843&bid_page=1&page_size=15&is_start=false&item_type=b&ends=1405754209000&starts=1405149409000&item_id=35259286943&user_tag=572067872&old_quantity=1665&seller_num_id=802987756&isFromDetail=yes&totalSQ=6539&sbn=5c17badc4f9c31e53b3a7eb7748b48ac&sold_total_num=996
		String url = "http://ext.mdskip.taobao.com/extension/dealRecords.htm?_ksTS=1405676551455_1842&callback=jsonp1843&bid_page=1&page_size=15&is_start=false&item_type=b&ends=1406019344000&starts=1405414544000&item_id=20150366437&user_tag=35196960&old_quantity=1957&seller_num_id=751075004&isFromDetail=yes&totalSQ=2320&sbn=a791c871795ba412034d28203cac658f";// 想采集的网址
		String refer = "http://detail.tmall.com/item.htm?spm=a230r.1.14.2.qv9Mra&id=20150366437&ad_id=&am_id=&cm_id=140105335569ed55e27b&pm_id=&sku_properties=";
		URL link = new URL(url);
		WebClient wc = new WebClient(BrowserVersion.FIREFOX_24);
		WebRequest request = new WebRequest(link, "post");
		// request.setCharset("UTF-8");
		// request.setProxyHost("120.120.120.x");
		// request.setProxyPort(8080);
		// 设置请求报文头里的refer字段
		request.setAdditionalHeader("Referer", refer);
		//设置请求报文头里的User-Agent字段
		request.setAdditionalHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		// wc.addRequestHeader("User-Agent",
		// "Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		// wc.addRequestHeader和request.setAdditionalHeader功能应该是一样的。选择一个即可。
		// 其他报文头字段可以根据需要添加
		// 开启cookie管理
		wc.getCookieManager().setCookiesEnabled(true);
		// 开启js解析。对于变态网页，这个是必须的
		wc.getOptions().setJavaScriptEnabled(true);
		// 开启css解析。对于变态网页，这个是必须的。
		wc.getOptions().setCssEnabled(false);
		wc.setAjaxController(new NicelyResynchronizingAjaxController());
		wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
		wc.getOptions().setThrowExceptionOnScriptError(false);
		wc.getOptions().setTimeout(10000);
		// 设置cookie。如果你有cookie，可以在这里设置
		Set<Cookie> cookies = ck;

		Iterator<Cookie> i = cookies.iterator();

		while (i.hasNext()) {
			wc.getCookieManager().addCookie(i.next());
		}
		// System.out.println(wc.getCookieManager().getCookies().toString());
		// 准备工作已经做好了
		com.gargoylesoftware.htmlunit.JavaScriptPage page = null;
		page = wc.getPage(request);
		String content = page.getContent();// 网页内容保存在content里
		String html = content.replace("jsonp1843({html:\"", "").split("\",")[0].replace("&quot;", "").replace("\\\"", "");		
		List<String> list = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		if (html == null) {
			System.out.println("采集 " + url + " 失败!!!");
		} else {
			System.out.println(html);			
			org.jsoup.nodes.Document doc = Jsoup.parse(html);
			Elements date = doc.select("p[class=date]");
			Elements time = doc.select("p.time");
			Elements cate = doc.select("td[class=cell-align-l][style]");
			Elements price = doc.select("td.price");
			Elements name = doc.select("td[class=cell-align-l][buyer]");//td[class=cell-align-l][buyer]
			Elements num = doc.select("td[class=quantity]");
			//System.out.println(name.get(0).text());
			
				for (int k = 0; k <= 14; k++) {
					// System.out.println(k);
					String info = name.get(k).text() + " " + cate.get(k).text()
							+ " " + date.get(k).text() + " "
							+ time.get(k).text() + " " + num.get(k).text()
							+ " " + price.get(k).text();
					sb.append(info);
					sb.append("\r\n");
					//System.out.println(info);
					list.add(k, info);
				}
				File file = new File("G:"+File.separator+url.split("item_id=")[1].split("&")[0]+".txt");
				OutputStream out = new FileOutputStream(file);
				byte[] b = sb.toString().getBytes();
				for(byte tem:b){
					out.write(tem);
				}
				out.close();
			//System.out.println("list="+list.size()+" "+list.get(3));
		}
		// 搞定了
		CookieManager CM = wc.getCookieManager(); // WC = Your WebClient's name
		Set<Cookie> cookies_ret = CM.getCookies();// 返回的Cookie在这里，下次请求的时候可能可以用上啦。

	}
}
