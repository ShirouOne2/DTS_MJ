package com.philpost.dts.website.repository;

import com.philpost.dts.website.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByDepartment_DepartmentIdAndIsActiveTrue(Long departmentId);
    @Query("select u from User u join fetch u.department order by u.lastName, u.firstName")
    List<User> findAllWithDepartment();
}