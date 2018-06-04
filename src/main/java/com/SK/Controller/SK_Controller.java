package com.SK.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SK_Controller {
	
	private static final Logger LOG = Logger.getLogger(SK_Controller.class.getName());
	private StringBuilder gameIndexGson = new StringBuilder();
	private Set<GameReview> gameIndexSet;
	private List<GameReview> gameIndexList;
	
	
	@PostConstruct
	public void init() {
		LOG.info("entering init");
		try {
			URL url = new URL("http://fairplay.hol.es/SKGameIndex.txt");
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
				String s = "";
				while ((s = reader.readLine()) != null) {
					gameIndexGson.append(s);
				}
			}		
		} catch (MalformedURLException e) {
			LOG.info("Nema konekcije sa url");
		} catch (IOException e) {
			LOG.info("Nema konekcije sa url fajlom");
		}
		Gson gson = new Gson();
		Type type = new TypeToken<Set<GameReview>>() {}.getType();
		gameIndexSet = gson.fromJson(gameIndexGson.toString(), type);
		gameIndexList = new ArrayList<>(gameIndexSet);
		Collections.sort(gameIndexList, (g1, g2) -> g2.getLink().compareTo(g1.getLink()));
	}

}
