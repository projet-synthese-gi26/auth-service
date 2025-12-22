package com.tramasys.auth.adapters.out.persistence.spring;

import com.tramasys.auth.adapters.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;

import com.tramasys.auth.domain.model.TramasysService;
import java.util.List;

import java.util.Optional;
import java.util.UUID;

public interface SpringUserRepository extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = {"roles", "roles.permissions", "permissions"})
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhone(String phone);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    /**
     * Recherche optimisée par username OR email OR phone.
     * La requête JPQL native n'est pas nécessaire, Spring Data peut composer une méthode:
     */
    @EntityGraph(attributePaths = {"roles", "roles.permissions", "permissions"})
    Optional<UserEntity> findByUsernameOrEmailOrPhone(String username, String email, String phone);

    @EntityGraph(attributePaths = {"roles", "roles.permissions", "permissions"})
    List<UserEntity> findAllByService(TramasysService service);
}
