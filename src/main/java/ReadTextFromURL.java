package main.java;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ReadTextFromURL implements Constants {
	static int count1 = 1;
	static int count2 = 0;

	public static void main(String[] args) throws Exception {
		if(args.length!=1){
			System.out.println(args[0]);
			System.out.println(args.length);
			System.out.println("Invalid arguments... please enter search query");
		}else{
			System.out.println(args[0]);
			String query = args[0];
			Crawler cr = new Crawler();	
			List<String> urlList = cr.fetchURLsFromBing(query);
			for (String url : urlList) {
				System.out.println("first for... count1 = " + count1 + " count2 = " + count2);
				String data = cr.fetchText(url);
				cr.writeFile(OUTPUT_PATH + count1++, data);
				List<String> resultList1 = cr.fetchURLList(url);				
				for (String urlLevel1 : resultList1) {
					System.out.println("second for... count1 = " + count1 + " count2 = " + count2);
					if(cr.shouldCrawlURL(urlLevel1)){
						String data1 = cr.fetchText(urlLevel1);
						cr.writeFile(OUTPUT_PATH + count1++, data1);
						List<String> resultList2 = cr.fetchURLList(urlLevel1);
						for (String urlLevel2 : resultList2) {
							System.out.println("third for... count1 = " + count1 + " count2 = " + count2);
							if(cr.shouldCrawlURL(urlLevel2)){
								String data2 = cr.fetchText(urlLevel2);
								cr.writeFile(OUTPUT_PATH + count1++, data2);
							}
						}
					}
				}
				count2++;
				if(count2 == 2){
					break;
				}
			}
		}
	}
}