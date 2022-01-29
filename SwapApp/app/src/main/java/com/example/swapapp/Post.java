package com.example.swapapp;

public class Post {

    private String userPosted;

    private String title;
    private String description;
    private Long timePosted;
    private boolean traded;

    public Post() { }

    public Post(String userPosted, String title) {
        this.userPosted = userPosted;
        this.title = title;
        description = "No Description";
        timePosted = System.currentTimeMillis();
        traded = false;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTraded() {
        return traded;
    }

    public void setTraded(boolean traded) {
        this.traded = traded;
    }
}
