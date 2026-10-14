package com.philpost.dts.website.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.philpost.dts.website.entity.Department;
import com.philpost.dts.website.entity.Role;
import com.philpost.dts.website.entity.User;
import com.philpost.dts.website.repository.DepartmentRepository;
import com.philpost.dts.website.repository.UserRepository;

// config/DataSeeder.java
@Component
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-password:ChangeMe123!}")
    private String adminPassword;

    public DataSeeder(DepartmentRepository departmentRepository, UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        Department it = departmentRepository.findByCode("IT")
                .orElseGet(() -> departmentRepository.save(
                        Department.builder().name("Information Technology").code("IT").build()));

        userRepository.save(User.builder()
                .department(it)
                .username("admin")
                .firstName("System")
                .lastName("Administrator")
                .passwordHash(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build());
    }
}