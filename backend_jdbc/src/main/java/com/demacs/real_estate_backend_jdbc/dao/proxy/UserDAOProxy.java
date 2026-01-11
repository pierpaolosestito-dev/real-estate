package com.demacs.real_estate_backend_jdbc.dao.proxy;

import com.demacs.real_estate_backend_jdbc.dao.UserDAO;
import com.demacs.real_estate_backend_jdbc.model.User;
import com.demacs.real_estate_backend_jdbc.utils.SessionManager;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

public class UserDAOProxy implements UserDAO {

    private final UserDAO delegate;

    public UserDAOProxy(UserDAO delegate) {
        this.delegate = delegate;
    }

    private User getCurrentUser() {
        Long currentId = SessionManager.getInstance().get();
        if (currentId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nessun utente loggato");
        }
        return delegate.findById(currentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente non valido"));
    }

    private void checkUpdatePermission(Long targetUserId) {
        User current = getCurrentUser();

        boolean isAdmin = current.getRuolo().equalsIgnoreCase("ADMIN");
        boolean isSelf = current.getId().equals(targetUserId);

        if (!(isAdmin || isSelf)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non hai i permessi per aggiornare questo utente");
        }
    }

    private void checkDeletePermission(Long targetUserId) {
        User current = getCurrentUser();

        boolean isAdmin = current.getRuolo().equalsIgnoreCase("ADMIN");
        boolean isSelf = current.getId().equals(targetUserId);

        if (!(isAdmin || isSelf)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non hai i permessi per eliminare questo utente");
        }
    }

    // =====================================
    // METODI LETTURA → Nessun controllo
    // =====================================

    @Override
    public List<User> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return delegate.findByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsById(Long id) {
        return delegate.existsById(id);
    }

    // =====================================
    // CREATE → ammesso a tutti
    // =====================================
    @Override
    public User save(User user) {
        return delegate.save(user);
    }

    // =====================================
    // UPDATE → B: admin o self
    // =====================================
    @Override
    public void update(User user) {
        checkUpdatePermission(user.getId());
        delegate.update(user);
    }

    // =====================================
    // DELETE → B: admin o self
    // =====================================
    @Override
    public void deleteById(Long id) {
        checkDeletePermission(id);
        delegate.deleteById(id);
    }
}
