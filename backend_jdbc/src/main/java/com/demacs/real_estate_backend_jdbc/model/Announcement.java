package com.demacs.real_estate_backend_jdbc.model;





import java.time.LocalDate;

public class Announcement {

    private Long id;
    private String titolo;
    private String descrizione;
    private Double prezzo;
    private String tipo; // "VENDITA", "AFFITTO"
    private String imageUrl;
    private LocalDate dataPubblicazione;
    private Property immobile;
    private User venditore;

    public Announcement() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDate getDataPubblicazione() {
        return dataPubblicazione;
    }

    public void setDataPubblicazione(LocalDate dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }

    public Property getImmobile() {
        return immobile;
    }

    public void setImmobile(Property immobile) {
        this.immobile = immobile;
    }

    public User getVenditore() {
        return venditore;
    }

    public void setVenditore(User venditore) {
        this.venditore = venditore;
    }
}
