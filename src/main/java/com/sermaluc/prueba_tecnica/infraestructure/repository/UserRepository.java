package com.sermaluc.prueba_tecnica.infraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import com.sermaluc.prueba_tecnica.domain.models.UserModel;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    boolean existsByEmail(String email);
}