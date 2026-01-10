package com.example.real_estate_backend.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SessionManager {

    private static final Path SESSION_FILE = Path.of("session.txt");

    // ---- SINGLETON ----
    private static SessionManager instance = null;

    private SessionManager() {} // costruttore privato

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // ---- METODI DI SESSIONE ----

    /** Salva l'ID utente sovrascrivendo il file */
    public void set(Long userId) {
        try {
            Files.writeString(SESSION_FILE, userId.toString());
        } catch (IOException e) {
            throw new RuntimeException("Errore durante il salvataggio della sessione", e);
        }
    }

    /** Recupera l'ID utente se presente, altrimenti null */
    public Long get() {
        try {
            if (!Files.exists(SESSION_FILE)) {
                return null;
            }
            String content = Files.readString(SESSION_FILE).trim();
            if (content.isEmpty()) {
                return null;
            }
            return Long.parseLong(content);
        } catch (IOException | NumberFormatException e) {
            return null;
        }
    }

    /** Cancella la sessione */
    public void clear() {
        try {
            if (Files.exists(SESSION_FILE)) {
                Files.delete(SESSION_FILE);
            }
        } catch (IOException e) {
            // per didattico non serve loggare
        }
    }
}
