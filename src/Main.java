import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

	static String jsonString;

	public static void main(String[] args) throws Exception {

		URL url = new URL("http://api.nbp.pl/api/exchangerates/tables/B/");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		String json = "";
		while ((output = br.readLine()) != null) {
			System.out.println(output);
			json += output;
		}

		conn.disconnect();

		JSONArray jsonArr = new JSONArray(json);
		JSONObject jsonObj = jsonArr.getJSONObject(0);
		jsonArr = jsonObj.getJSONArray("rates");

		List<Rate> rateList = new ArrayList<>();
		for (int i = 0; i < jsonArr.length(); i++) {
			jsonObj = jsonArr.getJSONObject(i);
			Rate waluta = new Rate();
			waluta.setCurrency(jsonObj.getString("currency"));
			waluta.setCode(jsonObj.getString("code"));
			waluta.setMid(jsonObj.getDouble("mid"));
			rateList.add(waluta);
		}
		System.out.println();
	}

}
