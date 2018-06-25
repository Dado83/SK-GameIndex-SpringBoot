package com.SK.Model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
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

	private static final Logger logger = Logger.getLogger(UpdateLogic.class.getName());
	private List<String> linkURLs = new ArrayList<>();
	private Set<GameReview> igre = new HashSet<>();
	long protekloVr;


	public UpdateLogic() {

	}


	public Map<String, String> update() {
		logger.info("entering update()");
		long mjerenje = System.nanoTime();
		List<Double> avg_single_index_load_time = new ArrayList<>();
		
		loadLinkURLs();

		int size = linkURLs.size();

		for (int br = 0; br < linkURLs.size(); br++) {
			long start = System.nanoTime();
			int step = br;
			igre.add(setData(linkURLs.get(br)));

			logger.info("Ucitanih igara: " + igre.size());

			double result = System.nanoTime() - start;

			logger.info("Vreme potrebno za 1 iteraciju: " + result / 1000000000 + " sekundi.");

			int potrebnoMin = (int) (((result * size) / 1000000000) / 60);
			logger.info("Potrebno vreme za ucitavanje svih linkova: " + potrebnoMin + " minuta.");

			int ostatakMin = (int) (((result * (size - step)) / 1000000000) / 60);
			logger.info("Potrebno vreme za ucitavanje ostatka linkova: " + ostatakMin + " minuta.");

			protekloVr = (start - mjerenje) / 1000000000 / 60;
			logger.info("Proteklo vrijeme ucitavanja linkova... " + protekloVr);
			
			avg_single_index_load_time.add(result / 1000000000);
		}

		ispraviNaslov(igre);
		saveToFile(igre);
		
		double sum = avg_single_index_load_time.stream().mapToDouble(Double::doubleValue).sum();
		double avg_load_time = sum / avg_single_index_load_time.size();
		
		Map <String, String> map = new HashMap<>();
		map.put("status", "azuriranje zavrseno");
		map.put("totalTime", "proteklo vrijeme: " + protekloVr + " minuta.");
		map.put("avgTime", "vrijeme potrebno za 1 iteraciju: " + avg_load_time + " sekundi.");
		
		return map;
	}


	public void saveToFile(Set<GameReview> ig) {
		String desktop = System.getProperty("user.home") + "/desktop";
		Gson gson = new Gson();
		Type type = new TypeToken<Set<GameReview>>() {
		}.getType();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(desktop + "/SKGameIndex.txt"))) {
			String toJson = gson.toJson(ig, type);
			writer.write(toJson);
		} catch (IOException e) {
			logger.severe("ERROR in saving file to desktop");
		}
	}


	public void ispraviNaslov(Set<GameReview> igraL) {
		logger.info("entering ispraviNaslov()");
		for (GameReview i : igraL) {
			if (i.getTitle().contains("http") || i.getTitle().equals("")) {
				Document doc = null;
				try {
					doc = Jsoup.connect(i.getLink()).get();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Elements godina = doc.select("img[alt]");
				ArrayList<Element> alt = new ArrayList<>();
				godina.forEach((e) -> {
					alt.add(e);
				});
				if (alt.size() < 10) {
				} else {
					i.setTitle(alt.get(9).attr("alt"));
				}
				logger.info(i.getTitle() + " ...ISPRAVLJEN");
			}
		}
	}


	public GameReview setData(String link) {
		logger.info("entering setData()");
		GameReview igra;
		Document doc = null;
		try {
			doc = Jsoup.connect(link).get();
		} catch (IOException e1) {
			logger.severe("ERROR in 'doc = Jsoup.connect(link).get();'");
		}
		Elements naslov = doc.select(".na");
		Elements ocjena = doc.select(".oc");
		Elements autor = doc.select(".pd");
		Elements godina = doc.select("img[alt]");

		ArrayList<Element> alt = new ArrayList<>();
		godina.forEach((e) -> {
			alt.add(e);
		});
		String god = "";
		god = alt.stream().filter((e) -> (e.attr("alt").matches("[0-9]+"))).map((e) -> e.attr("alt") + ".").reduce(god,
				String::concat);

		if (ocjena.text().matches("[0-9]+")) {
			igra = new GameReview(naslov.text(), autor.text(), Integer.parseInt(ocjena.text()), god, link);
		} else {
			igra = new GameReview(naslov.text(), autor.text(), god, link);
		}
		return igra;
	}


	public void loadLinkURLs() {
		logger.info("entering loadLinksURLs()");
		logger.info("Povezujem se na SK\n...ucitavam linkove...");
		Document temp = null;
		try {
			temp = Jsoup.connect("http://www.sk.rs/indexes/sections/op.html").get();
		} catch (IOException e1) {
			logger.severe("ERROR in jsoup connect to SK");
		}
		Elements links = temp.select("a[href]");

		links.forEach((e) -> {
			linkURLs.add("http://www.sk.rs" + e.attr("href").substring(5));
		});
		logger.info("Broj linkova prije filtera: " + linkURLs.size());
		List<String> templList = linkURLs.stream().filter(ss -> !ss.contains("indexe")).collect(Collectors.toList());
		linkURLs = new ArrayList<>(templList);
		logger.info("Broj linkova poslije filtera: " + linkURLs.size());
	}

}
