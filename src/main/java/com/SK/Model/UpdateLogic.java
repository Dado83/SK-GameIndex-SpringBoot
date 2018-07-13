package com.SK.Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class UpdateLogic {

    private static final Logger LOGGER = Logger.getLogger(UpdateLogic.class.getName());
    private List<String> linkURLs = new ArrayList<>();
    private Set<GameReview> games = new HashSet<>();
    long elapsedTime;

    public Map<String, String> update() {
	LOGGER.info("entering update()");
	long startTime = System.nanoTime();
	List<Double> avg_single_index_load_time = new ArrayList<>();

	loadLinkURLs();

	int size = linkURLs.size();

	for (int br = 0; br < linkURLs.size(); br++) {
	    long start = System.nanoTime();
	    int step = br;
	    games.add(setData(linkURLs.get(br)));

	    LOGGER.info("Ucitanih igara: " + games.size());

	    double result = System.nanoTime() - start;

	    LOGGER.info("Vreme potrebno za 1 iteraciju: " + result / 1000000000 + " sekundi.");

	    int timeNeededToComplete = (int) (((result * size) / 1000000000) / 60);
	    LOGGER.info("Potrebno vreme za ucitavanje svih linkova: " + timeNeededToComplete + " minuta.");

	    int remainingTime = (int) (((result * (size - step)) / 1000000000) / 60);
	    LOGGER.info("Potrebno vreme za ucitavanje ostatka linkova: " + remainingTime + " minuta.");

	    elapsedTime = (start - startTime) / 1000000000 / 60;
	    LOGGER.info("Proteklo vrijeme ucitavanja linkova... " + elapsedTime);

	    avg_single_index_load_time.add(result / 1000000000);
	}

	correctTitle(games);
	saveToFile(games);

	double sum = avg_single_index_load_time.stream().mapToDouble(Double::doubleValue).sum();
	double avg_load_time = sum / avg_single_index_load_time.size();

	Map<String, String> map = new HashMap<>();
	map.put("status", "azuriranje zavrseno");
	map.put("totalTime", "proteklo vrijeme: " + elapsedTime + " minuta.");
	map.put("avgTime", "vrijeme potrebno za 1 iteraciju: " + avg_load_time + " sekundi.");

	return map;
    }

    public void saveToFile(Set<GameReview> ig) {
	String desktop = System.getProperty("user.home") + "/desktop";
	Gson gson = new Gson();
	Type type = new TypeToken<Set<GameReview>>() {
	}.getType();

	try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
		new FileOutputStream(new File(desktop + "/SKGameIndex.txt")), Charset.forName("utf-8").newEncoder()))) {
	    String toJson = gson.toJson(ig, type);
	    writer.write(toJson);
	} catch (IOException e) {
	    LOGGER.severe("ERROR in saving file to desktop");
	}
    }

    public void correctTitle(Set<GameReview> gameSet) {
	LOGGER.info("entering ispraviNaslov()");
	for (GameReview i : gameSet) {
	    if (i.getTitle().contains("http") || i.getTitle().equals("")) {
		Document doc = null;
		try {
		    doc = Jsoup.connect(i.getLink()).get();
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		Elements date = doc.select("img[alt]");
		ArrayList<Element> alt = new ArrayList<>();
		date.forEach((e) -> {
		    alt.add(e);
		});
		if (alt.size() < 10) {
		} else {
		    i.setTitle(alt.get(9).attr("alt"));
		}
		LOGGER.info(i.getTitle() + " ...ISPRAVLJEN");
	    }
	}
    }

    public GameReview setData(String link) {
	LOGGER.info("entering setData()");
	GameReview game;
	Document doc = null;
	try {
	    doc = Jsoup.connect(link).get();
	} catch (IOException e1) {
	    LOGGER.severe("ERROR in 'doc = Jsoup.connect(link).get();'");
	}
	Elements title = doc.select(".na");
	Elements score = doc.select(".oc");
	Elements author = doc.select(".pd");
	Elements date = doc.select("img[alt]");

	ArrayList<Element> dateTemp = new ArrayList<>();
	date.forEach((e) -> {
	    dateTemp.add(e);
	});
	String year = "";
	year = dateTemp.stream().filter((e) -> (e.attr("alt").matches("[0-9]+"))).map((e) -> e.attr("alt") + ".")
		.reduce(year, String::concat);

	if (score.text().matches("[0-9]+")) {
	    game = new GameReview(title.text(), author.text(), Integer.parseInt(score.text()), year, link);
	} else {
	    game = new GameReview(title.text(), author.text(), year, link);
	}
	return game;
    }

    public void loadLinkURLs() {
	LOGGER.info("entering loadLinksURLs()");
	LOGGER.info("Povezujem se na SK\n...ucitavam linkove...");
	Document temp = null;
	try {
	    temp = Jsoup.connect("http://www.sk.rs/indexes/sections/op.html").get();
	} catch (IOException e1) {
	    LOGGER.severe("ERROR in jsoup connect to SK");
	}
	Elements links = temp.select("a[href]");

	links.forEach((e) -> {
	    linkURLs.add("http://www.sk.rs" + e.attr("href").substring(5));
	});
	LOGGER.info("Broj linkova prije filtera: " + linkURLs.size());
	List<String> templList = linkURLs.stream().filter(ss -> !ss.contains("indexe")).collect(Collectors.toList());
	linkURLs = new ArrayList<>(templList);
	LOGGER.info("Broj linkova poslije filtera: " + linkURLs.size());
    }

}
