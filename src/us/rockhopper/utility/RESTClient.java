package us.rockhopper.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RESTClient {
	public RESTClient() {

	}

	public GameAction getDecision(GameState state) throws ClientProtocolException, IOException {

		List<String> contents = new ArrayList<>(state.getData().length);
		for (int n : state.getData()) {
			contents.add(Integer.toString(n));
		}

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet getRequest = new HttpGet("http://localhost:5000/get_decision/" + String.join(",", contents));

		HttpResponse response = httpClient.execute(getRequest);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = null;
		while ((line = br.readLine()) != null) {
			return GameAction.values()[Integer.parseInt(line.replaceAll("\\s", ""))];
		}
		return GameAction.ACTION_NULL;
	}
}
