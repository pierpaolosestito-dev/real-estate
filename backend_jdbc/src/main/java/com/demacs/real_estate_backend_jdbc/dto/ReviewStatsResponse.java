package com.demacs.real_estate_backend_jdbc.dto;

public class ReviewStatsResponse {

    private long count;
    private double average;

    public ReviewStatsResponse() {}

    public ReviewStatsResponse(long count, double average) {
        this.count = count;
        this.average = average;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
