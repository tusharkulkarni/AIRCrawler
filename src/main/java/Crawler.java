package main.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler implements Constants{
	static private BufferedWriter logFile = null;
	private static String charset = java.nio.charset.StandardCharsets.UTF_8.name();
	static List<String> crawledUrls = new ArrayList<String>();

	public boolean shouldCrawlURL(String url)throws IOException{	
		Boolean result = false;
		if(crawledUrls.contains(url)){
		}else{
			Document page = null;
			System.out.println("Crawling url : " + url);
			page = Jsoup.connect(url).get();

			File file = new File(KEYWORD_LIST_PATH);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String [] keywordList = br.readLine().split(",");
			result = StringUtils.indexOfAny(page.getElementsByTag("meta").toString(), keywordList)>0?true:false;
			if(result){
				crawledUrls.add(url);
			}			
		}
		return result;
	}

	public String fetchText(String url) throws IOException, MalformedURLException {
		Document page = null;
		page = Jsoup.connect(url).get();
		System.out.println(shouldCrawlURL(url));
		System.out.println("Fetch Text from : " + url);
		return page.body().text();
	}

	public List<String>  fetchURLsFromBing(String query) throws MalformedURLException, IOException, Exception{
		List<String> urlList=new ArrayList<String>();
		final String USER_AGENT = "Mozilla/5.0";
		int responseCode = 0;
		String url = new String();
		try {
			url = "https://api.datamarket.azure.com/Bing/Search/v1/Composite?Sources=%27web%2Bnews%27&Query=" + URLEncoder.encode("'" + query + "'", "UTF-8") + "&$format=json";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		HttpURLConnection httpConn = (HttpURLConnection) new URL(url).openConnection();
		httpConn.setDoOutput(true); // Triggers POST.
		httpConn.setRequestProperty("Accept-Charset", charset);
		httpConn.setRequestProperty("User-Agent", USER_AGENT);

		final String accountKey = "8MH0OsNlBwWREfNEP7rKr1183QUiSHHa30EyE9dimB8";
		final String accountKeyEnc = Base64.getEncoder().encodeToString((accountKey + ":" + accountKey).getBytes());
		httpConn.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

		responseCode = httpConn.getResponseCode();
		if ( responseCode == 200) { //OK
			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			String inputLine;
			StringBuffer responseBuffer = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				responseBuffer.append(inputLine);

			}
			JSONObject obj = new JSONObject(responseBuffer.toString()).getJSONObject("d");
			JSONArray arr = obj.getJSONArray("results");
			for (int i = 0; i < arr.length(); ++i) {
				JSONArray WebArr = arr.getJSONObject(i).getJSONArray("Web");
				for(int j = 0; j < WebArr.length() && j<10; ++j) {
					String extractedURL = WebArr.getJSONObject(j).getString("Url");
					urlList.add(extractedURL);
					//writeLogFile(extractedURL);
				}
			}
		}else{
			System.out.println(responseCode);
		}
		return urlList;
	}

	public void writeFile(String filename, String data)throws IOException{
		/*filename = "save.txt";
		data = "save this text";*/
		File file = new File(filename);
		FileWriter fw = new FileWriter(file);
		fw.write(data);
		fw.close();
		System.out.println("file written to path : " + file.getAbsolutePath());

	}

	public List<String> fetchURLList(String url)throws Exception{
		List<String> urlList = new ArrayList<String>();

		Document page = null;
		page = Jsoup.connect(url).get();
		Elements elems = page.getElementsByTag("a");
		for (Element element : elems) {
			if(element.attr("href").startsWith("http")){
				urlList.add(element.attr("href"));
			}
		}
		return urlList;
	}

	static void createLogFile(String fileName) {
		try {
			logFile = new BufferedWriter(new FileWriter(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void closeLogFile() {
		try {
			logFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void writeLogFile(String text) {
		try {
			logFile.write(text + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}