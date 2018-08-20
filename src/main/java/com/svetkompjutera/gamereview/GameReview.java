package com.svetkompjutera.gamereview;

public class GameReview {

    private String title;
    private String author;
    private String score;
    private String date;
    private String link;
    private String platform;

    public GameReview() {

    }

    public GameReview(String title, String author, String score, String date, String link, String platform) {
	this.title = title;
	this.author = author;
	this.score = score;
	this.date = date;
	this.link = link;
	this.platform = platform;
    }

    public GameReview(String title, String author, String date, String link, String platform) {
	this(title, author, "-1", date, link, platform);
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getAuthor() {
	return author;
    }

    public void setAuthor(String author) {
	this.author = author;
    }

    public String getScore() {
	return score;
    }

    public void setScore(String score) {
	this.score = score;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public String getLink() {
	return link;
    }

    public void setLink(String link) {
	this.link = link;
    }

    public int getYear() {
	int year = Integer.parseInt(getDate().substring(3, 7));
	return year;
    }

    public String getPlatform() {
	return platform;
    }

    public void setPlatform(String platform) {
	this.platform = platform;
    }

    @Override
    public String toString() {
	return title + "::" + author + "::" + score + "::" + date + "::" + link;
    }

}
