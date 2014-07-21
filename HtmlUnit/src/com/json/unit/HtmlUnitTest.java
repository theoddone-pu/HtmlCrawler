package com.json.unit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

public class HtmlUnitTest {

//http://detail.tmall.com/item.htm?id=35259286943
	public static void main(String[] args) throws Exception {
		String url="";
		try{
	        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
	        //设置webClient的相关参数
	        webClient.getOptions().setJavaScriptEnabled(true);
	        webClient.getOptions().setCssEnabled(false);
	        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
	        //webClient.getOptions().setTimeout(50000);
	        webClient.getOptions().setThrowExceptionOnScriptError(false);
	        //模拟浏览器打开一个目标网址
	            HtmlPage rootPage= webClient.getPage(url);
	            System.out.println("为了获取js执行的数据 线程开始沉睡等待");
	            Thread.sleep(3000);//主要是这个线程的等待 因为js加载也是需要时间的
	            System.out.println("线程结束沉睡");
	            String html = rootPage.asText();
	            System.out.println(html);
	            }catch(Exception e){
	            }

	}
}
