package com.SK.Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Service
public class GameReviewService {

	private static final Logger LOG = Logger.getLogger(SK_Controller.class.getName());
	private StringBuilder gameIndexGson = new StringBuilder();
	private Set<GameReview> gameIndexSet;
	private List<GameReview> gameIndexList;


	void readDataFromFileWeb() {
		LOG.info("entering readDataFromFileWeb()");
		try {
			URL url = new URL("http://fairplay.hol.es/SKGameIndex.txt");
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(url.openStream(), Charset.forName("utf-8").newDecoder()))) {
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
	}


	void readDataFromFileLocal() {
		LOG.info("entering readDataFromFileLocal()");
		try {
			File file = new File(System.getProperty("user.home") + "/desktop/SKGameIndex.txt");
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), Charset.forName("utf-8").newDecoder()))) {
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
	}


	public void initialize() {
		LOG.info("entering init");
		readDataFromFileWeb();
		Gson gson = new Gson();
		Type type = new TypeToken<Set<GameReview>>() {
		}.getType();
		gameIndexSet = gson.fromJson(gameIndexGson.toString(), type);
		gameIndexList = new ArrayList<>(gameIndexSet);
		Collections.sort(gameIndexList, (g1, g2) -> g2.getLink().compareTo(g1.getLink()));
	}


	public List<GameReview> home() {
		LOG.info("entering home");
		return gameIndexList;
	}


	public List<GameReview> search(GameReview review) {
		LOG.info("entering search");
		List<GameReview> searchResult = new ArrayList<>();
		gameIndexList.stream()
				.filter((game) -> ((game.getAuthor().toLowerCase().contains(review.getAuthor()))
						&& (game.getTitle().toLowerCase().contains(review.getTitle()))
						&& (game.getScore() >= review.getScore())))
				.forEachOrdered(i -> searchResult.add(i));
		return searchResult;
	}
}
