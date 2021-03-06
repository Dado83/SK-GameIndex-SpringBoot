package com.svetkompjutera.gamereview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.core.io.ClassPathResource;


@Service
class GameReviewService {

    private static final Logger LOGGER = Logger.getLogger(GameReviewService.class.getName());
    private StringBuilder gameIndexGson = new StringBuilder();
    private Set<GameReview> gameIndexSet;
    private List<GameReview> gameIndexList;

    public void initialize() {
        LOGGER.info("entering init");
        readData();
    }

    private void readData() {
        LOGGER.info("Entering readLocalData()");
        try {

            InputStream inStream = new ClassPathResource("static/SKGameIndex.json").getInputStream();

            Reader streamReader = new InputStreamReader(inStream, Charset.forName("utf-8").newDecoder());
            try (BufferedReader reader = new BufferedReader(streamReader)) {
                String s = "";
                while ((s = reader.readLine()) != null) {
                    gameIndexGson.append(s);
                }
            }
        } catch (MalformedURLException e) {
            LOGGER.info("Nema konekcije sa url");
        } catch (IOException e) {
            LOGGER.info("Nema konekcije sa url fajlom");
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Set<GameReview>>() {
        }.getType();
        gameIndexSet = gson.fromJson(gameIndexGson.toString(), type);
        gameIndexList = new ArrayList<>(gameIndexSet);
        Collections.sort(gameIndexList, (g1, g2) -> g2.getLink().compareTo(g1.getLink()));
    }

    public List<GameReview> home() {
        LOGGER.info("entering home");
        return gameIndexList;
    }

    public List<GameReview> search(GameReview review) {
        LOGGER.info("entering search");

        if (review.getScore().equals("")) {
            review.setScore("-1");
        }

        List<GameReview> searchResult = new ArrayList<>();
        gameIndexList.stream()
                .filter((game) -> ((game.getAuthor().toLowerCase().contains(review.getAuthor().toLowerCase()))
                && (game.getTitle().toLowerCase().contains(review.getTitle().toLowerCase()))
                && (Integer.valueOf(game.getScore()) >= Integer.valueOf(review.getScore())))
                && (game.getPlatform().toLowerCase().contains(review.getPlatform().toLowerCase())))
                .forEachOrdered(i -> searchResult.add(i));
        return searchResult;
    }

}
