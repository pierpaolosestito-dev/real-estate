package com.example.real_estate_backend.model;



import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titolo;

    @Column(name = "image_url")
    private String imageUrl;


    @Column(length = 2000)
    private String descrizione;

    private Double prezzo;

    @Enumerated(EnumType.STRING)
    private AnnouncementType tipo;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property immobile;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private User venditore;

    private LocalDate dataPubblicazione;

    public enum AnnouncementType {
        VENDITA,
        AFFITTO
    }

    public LocalDate getDataPubblicazione() {
        return dataPubblicazione;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public Long getId() {
        return id;
    }
    public Property getImmobile() {
        return immobile;
    }
    public Double getPrezzo() {
        return prezzo;
    }
    public AnnouncementType getTipo() {
        return tipo;
    }
    public String getTitolo() {
        return titolo;
    }
    public User getVenditore() {
        return venditore;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setDataPubblicazione(LocalDate dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setImmobile(Property immobile) {
        this.immobile = immobile;
    }
    public void setPrezzo(Double prezzo) {
        this.prezzo = prezzo;
    }
    public void setTipo(AnnouncementType tipo) {
        this.tipo = tipo;
    }
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
    public void setVenditore(User venditore) {
        this.venditore = venditore;
    }
    // getter & setter
}
