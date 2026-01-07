package com.example.real_estate_backend.model;

import com.example.real_estate_backend.model.Location;
import com.example.real_estate_backend.model.Announcement.AnnouncementType;
import com.example.real_estate_backend.model.Property.PropertyType;

public class CreateAnnouncementRequest {

    private String titolo;
    private String descrizione;
    private Double prezzo;
    private AnnouncementType tipo;
    private String imageUrl;

    private Long venditoreId;

    private CreatePropertyRequest immobile;

    // =====================
    // INNER DTO: IMMOBILE
    // =====================
    public static class CreatePropertyRequest {
        private PropertyType tipo;
        private Integer superficieMq;
        private Integer stanze;
        private Integer bagni;
        private Location location;

        public PropertyType getTipo() { return tipo; }
        public void setTipo(PropertyType tipo) { this.tipo = tipo; }

        public Integer getSuperficieMq() { return superficieMq; }
        public void setSuperficieMq(Integer superficieMq) { this.superficieMq = superficieMq; }

        public Integer getStanze() { return stanze; }
        public void setStanze(Integer stanze) { this.stanze = stanze; }

        public Integer getBagni() { return bagni; }
        public void setBagni(Integer bagni) { this.bagni = bagni; }

        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }
    }

    // =====================
    // GETTER / SETTER
    // =====================
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public Double getPrezzo() { return prezzo; }
    public void setPrezzo(Double prezzo) { this.prezzo = prezzo; }

    public AnnouncementType getTipo() { return tipo; }
    public void setTipo(AnnouncementType tipo) { this.tipo = tipo; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Long getVenditoreId() { return venditoreId; }
    public void setVenditoreId(Long venditoreId) { this.venditoreId = venditoreId; }

    public CreatePropertyRequest getImmobile() { return immobile; }
    public void setImmobile(CreatePropertyRequest immobile) { this.immobile = immobile; }
}
