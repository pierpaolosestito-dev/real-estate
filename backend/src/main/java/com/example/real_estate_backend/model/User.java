package com.example.real_estate_backend.model;



import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cognome;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role ruolo;

    public enum Role {
        ADMIN,
        VENDITORE,
        ACQUIRENTE
    }

    public String getCognome() {
        return cognome;
    }
    public String getEmail() {
        return email;
    }
    public Long getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public Role getRuolo() {
        return ruolo;
    }
    
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setRuolo(Role ruolo) {
        this.ruolo = ruolo;
    }

    // getter & setter
}
