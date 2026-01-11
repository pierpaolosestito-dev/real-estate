package com.demacs.real_estate_backend_jdbc.dto;

public class CreateReviewRequest {

    private int rating;
    private String commento;

    public CreateReviewRequest() {}

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }
}
