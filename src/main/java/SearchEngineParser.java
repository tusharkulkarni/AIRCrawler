package main.java;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import org.json.*;



public class SearchEngineParser {
	static private BufferedWriter logFile = null;
	private static String charset = java.nio.charset.StandardCharsets.UTF_8.name();

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
	
	public static void main(String[] args) throws MalformedURLException, IOException, Exception {
		String url = "";
		String query = "nutrition information for diabetes";

		// TODO Auto-generated method stub
		/*if(args.length != 3) {
			System.out.println("Invalid arguments. Syntax: func_name 'keyword' file_path depth");
			return;
		}*/
		
		createLogFile("seed.txt");
		
		try {
			url = "https://api.datamarket.azure.com/Bing/Search/v1/Composite?Sources=%27web%2Bnews%27&Query=" + URLEncoder.encode("'" + query + "'", "UTF-8") + "&$format=json";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fetchHTTPData(url);
		closeLogFile();
	}
	
	public static void  fetchHTTPData(String query) throws MalformedURLException, IOException, Exception{
		final String USER_AGENT = "Mozilla/5.0";
		int responseCode = 0;

		HttpURLConnection httpConn = (HttpURLConnection) new URL(query).openConnection();
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
					String description =  WebArr.getJSONObject(j).getString("Description");
					System.out.println("Description : " + description.toString());
					System.out.println("URL : " + extractedURL);
					writeLogFile(extractedURL);
				}
				
				JSONArray NewsArr = arr.getJSONObject(i).getJSONArray("News");
				for(int j = 0; j < NewsArr.length(); ++j) {
					String extractedURL = NewsArr.getJSONObject(j).getString("Url");
					writeLogFile(extractedURL);
				}
			}
		}else{
			System.out.println(responseCode);
		}
	}
}
