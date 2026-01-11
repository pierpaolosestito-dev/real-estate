package com.demacs.real_estate_backend_jdbc.dto;

import com.demacs.real_estate_backend_jdbc.model.Location;

public class CreatePropertyRequest {

    private String tipo;
    private Integer superficieMq;
    private Integer stanze;
    private Integer bagni;
    private Location location;

    public CreatePropertyRequest() {}

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getSuperficieMq() {
        return superficieMq;
    }

    public void setSuperficieMq(Integer superficieMq) {
        this.superficieMq = superficieMq;
    }

    public Integer getStanze() {
        return stanze;
    }

    public void setStanze(Integer stanze) {
        this.stanze = stanze;
    }

    public Integer getBagni() {
        return bagni;
    }

    public void setBagni(Integer bagni) {
        this.bagni = bagni;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
