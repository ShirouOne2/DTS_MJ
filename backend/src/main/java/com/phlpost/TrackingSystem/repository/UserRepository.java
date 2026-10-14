package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    List<User> findByDepartmentId(UUID departmentId);

    boolean existsByUsername(String username);
}
