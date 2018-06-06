package com.SK.Model;


public class GameReview{
	
    private String title;
    private String author;
    private int score;
    private String date;
    private String link;
    
    
    public GameReview() {}
    
    
	public GameReview(String title, String author, int score, String date, String link){
    	this.title = title;
    	this.author = author;
    	this.score = score;
    	this.date = date;
    	this.link = link;
    }
    
    public GameReview(String t, String a, String d, String l){
    	this(t, a, -1, d, l);
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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
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

	@Override
    public String toString(){
        return title + "::" + author + "::" + score + "::" + date + "::" + link ;
    }

}
