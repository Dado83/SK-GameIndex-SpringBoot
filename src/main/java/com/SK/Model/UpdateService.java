package com.SK.Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
public class UpdateService {

    private static final Logger LOGGER = Logger.getLogger(UpdateService.class.getName());
    private List<String> linkURLs = new ArrayList<>();
    private Set<GameReview> games = new HashSet<>();
    private long elapsedTime;
    private long startTime;
    private List<Double> avg_single_index_load_time = new ArrayList<>();
    private int size;

    public Map<String, String> update() {
	LOGGER.info("entering update()");

	startTime = System.nanoTime();

	loadLinkURLs();
	addGames();
	correctTitle(games);
	saveToFile(games);

	double sum = avg_single_index_load_time.stream().mapToDouble(Double::doubleValue).sum();
	double avg_load_time = sum / avg_single_index_load_time.size();

	Map<String, String> messageForREST = new HashMap<>();
	messageForREST.put("status", "azuriranje zavrseno");
	messageForREST.put("totalTime", "proteklo vrijeme: " + elapsedTime + " minuta.");
	messageForREST.put("avgTime", "vrijeme potrebno za 1 iteraciju: " + avg_load_time + " sekundi.");

	return messageForREST;
    }

    private void loadLinkURLs() {
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

	List<String> tempList = linkURLs.stream().filter(ss -> !ss.contains("indexe")).collect(Collectors.toList());
	linkURLs = new ArrayList<>(tempList);
	LOGGER.info("Broj linkova poslije filtera: " + linkURLs.size());
    }

    private void addGames() {
	LOGGER.info("entering addGames()");
	size = linkURLs.size();

	for (int br = 0; br < linkURLs.size(); br++) {
	    long start = System.nanoTime();
	    int step = br;
	    games.add(setGameReviewData(linkURLs.get(br)));
	    LOGGER.info("Ucitanih igara: " + games.size() + "/" + linkURLs.size());

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
    }

    private GameReview setGameReviewData(String link) {
	LOGGER.info("entering setData()");
	LOGGER.info("adding " + link);
	GameReview game = null;
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
	Elements platform = doc.select(".kz");

	ArrayList<Element> dateTemp = new ArrayList<>();
	date.forEach((e) -> {
	    dateTemp.add(e);
	});
	String year = "";
	year = dateTemp.stream().filter((e) -> (e.attr("alt").matches("[0-9]+"))).map((e) -> e.attr("alt") + ".")
		.reduce(year, String::concat);

	if (score.text().matches("[0-9]+")) {
	    if (platform.isEmpty() || !isPlatformHtmlElement(platform.first().text())
		    || isPCSpecsHtmlElement(platform.first().text())) {
		game = new GameReview(title.text(), author.text(), score.text(), year, link, "PC");
	    } else {
		game = new GameReview(title.text(), author.text(), score.text(), year, link, platform.first().text());
	    }
	} else {
	    if (platform.isEmpty() || !isPlatformHtmlElement(platform.first().text())
		    || isPCSpecsHtmlElement(platform.first().text())) {
		game = new GameReview(title.text(), author.text(), year, link, "PC");
	    } else {
		game = new GameReview(title.text(), author.text(), year, link, platform.first().text());
	    }
	}
	LOGGER.info(game.getPlatform());
	return game;
    }

    private boolean isPlatformHtmlElement(String element) {
	String string = element.toLowerCase();
	boolean platform = (string.contains("pc") || string.contains("windows") || string.contains("xbox")
		|| string.contains("ps3") || string.contains("ps2") || string.contains("ps4")
		|| string.contains("playstation") || string.contains("ds") || string.contains("wii")
		|| string.contains("nintendo") || string.contains("3ds") || string.contains("psvita")
		|| string.contains("gameboy") || string.contains("sega") || string.contains("atari")
		|| string.contains("gamecube") || string.contains("dreamcast") || string.contains("gage")
		|| string.contains("nes") || string.contains("mac") || string.contains("linux"));
	return platform;
    }

    private boolean isPCSpecsHtmlElement(String element) {
	String string = element.toLowerCase();
	boolean specs = string.contains("ram");
	return specs;
    }

    private void correctTitle(Set<GameReview> gameSet) {
	LOGGER.info("entering ispraviNaslov()");
	for (GameReview i : gameSet) {
	    if (i.getTitle().contains("http") || i.getTitle().equals("")) {
		Document doc = null;
		try {
		    doc = Jsoup.connect(i.getLink()).get();
		} catch (IOException e1) {
		    LOGGER.severe("error connecting to game link using jsoup");
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

    private void saveToFile(Set<GameReview> ig) {
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

}
