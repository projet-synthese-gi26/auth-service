package com.tramasys.auth.domain.port.out;

import com.tramasys.auth.domain.model.TramasysService;
import com.tramasys.auth.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port OUT pour gestion des utilisateurs (implémenté par un adapter JPA).
 * On garde le contrat minimal pour rester simple.
 */
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    /**
     * Recherche par username OR email OR phone.
     * L'adapter implémente l'optimisation.
     */
    Optional<User> findByUsernameOrEmailOrPhone(String username, String email, String phone);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    List<User> findAllByService(TramasysService service);
    List<User> findAll();
}