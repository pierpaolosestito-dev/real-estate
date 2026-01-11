package com.demacs.real_estate_backend_jdbc.model;





public class Property {

    private Long id;
    private String tipo; // "APPARTAMENTO", "VILLA", ...
    private Integer superficieMq;
    private Integer stanze;
    private Integer bagni;
    private Location location;

    public Property() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
