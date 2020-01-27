import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

public class Main {

	public static void main(String[] args) throws Exception {

		URL url = new URL("http://api.nbp.pl/api/exchangerates/tables/a/2019-12-07/2020-01-27");
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

		JSONArray jsonArray = new JSONArray(json);
		ArrayList<Rekord> listaRekordow = new ArrayList<Rekord>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Rekord rekord = new Rekord();
			rekord.setTable(jsonObject.getString("table"));
			rekord.setNo(jsonObject.getString("no"));
			rekord.setEffectiveDate(jsonObject.getString("effectiveDate"));

			ArrayList<Rate> listaRate = new ArrayList<Rate>();
			JSONArray jsonArrayRate = (JSONArray) jsonObject.get("rates");

			for (int j = 0; j < jsonArrayRate.length(); j++) {
				JSONObject jsonObjectRate = jsonArrayRate.getJSONObject(j);
				Rate rate = new Rate();
				rate.setCurrency(jsonObjectRate.getString("currency"));
				rate.setCode(jsonObjectRate.getString("code"));
				rate.setMid(jsonObjectRate.getDouble("mid"));
				listaRate.add(rate);
			}

			rekord.setRates(listaRate);
			listaRekordow.add(rekord);

		}

		RestHighLevelClient client = new RestHighLevelClient(
				RestClient.builder(new HttpHost("localhost", 9200, "http")));

		for (Rekord rekord : listaRekordow) {
			IndexRequest indexRequest = new IndexRequest("waluty2", "rekord", rekord.getNo());
			indexRequest.source(new Gson().toJson(rekord));
			IndexResponse response = client.index(indexRequest).actionGet();
		}
		client.close();

	}

}
