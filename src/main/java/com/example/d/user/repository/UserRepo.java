package com.example.d.user.repository;

import com.example.d.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<Users, Integer> {
      Optional<Users> findByUsername(String username);
      Optional<Users> findByEmail(String email);
      boolean existsByUsername(String username);
}
