package com.demacs.real_estate_backend_jdbc.dto;

public class CreateAnnouncementRequest {

    private String titolo;
    private String descrizione;
    private Double prezzo;
    private String tipo;
    private String imageUrl;
    private Long venditoreId;
    private CreatePropertyRequest immobile;

    public CreateAnnouncementRequest() {}

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(Double prezzo) {
        this.prezzo = prezzo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getVenditoreId() {
        return venditoreId;
    }

    public void setVenditoreId(Long venditoreId) {
        this.venditoreId = venditoreId;
    }

    public CreatePropertyRequest getImmobile() {
        return immobile;
    }

    public void setImmobile(CreatePropertyRequest immobile) {
        this.immobile = immobile;
    }
}
