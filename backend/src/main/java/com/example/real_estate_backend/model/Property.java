package com.example.real_estate_backend.model;



import jakarta.persistence.*;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PropertyType tipo;

    private Integer superficieMq;
    private Integer stanze;
    private Integer bagni;

    @Embedded
    private Location location;

    public enum PropertyType {
        APPARTAMENTO,
        VILLA,
        MONOLOCALE,
        UFFICIO
    }

    // getter & setter
    public Integer getBagni() {
        return bagni;
    }
    public Long getId() {
        return id;
    }
    public Location getLocation() {
        return location;
    }
    public Integer getStanze() {
        return stanze;
    }
    public Integer getSuperficieMq() {
        return superficieMq;
    }
    public PropertyType getTipo() {
        return tipo;
    }

    public void setBagni(Integer bagni) {
        this.bagni = bagni;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public void setStanze(Integer stanze) {
        this.stanze = stanze;
    }
    public void setSuperficieMq(Integer superficieMq) {
        this.superficieMq = superficieMq;
    }
    public void setTipo(PropertyType tipo) {
        this.tipo = tipo;
    }
}

