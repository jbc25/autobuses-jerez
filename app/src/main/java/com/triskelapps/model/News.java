package com.triskelapps.model;


import com.triskelapps.util.DateUtils;

public class News {

    public static final String FIELD_DATE = "date";

    private String id;
    private String date;
    private String title;
    private String subtitle;
    private String media;
    private String url;
    private String image;

    public String getDateHumanFormat() {

        return DateUtils.convertDateApiToUserFormat(getDate());
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
