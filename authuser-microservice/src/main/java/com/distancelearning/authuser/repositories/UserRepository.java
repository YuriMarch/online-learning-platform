package com.distancelearning.authuser.repositories;

import com.distancelearning.authuser.models.User;
import com.distancelearning.authuser.specifications.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
//    Page<User> findAll(Pageable pageable, Specification<User> spec);
    @Query(value = "select * from tb_users where user_type = 'INSTRUCTOR'", nativeQuery = true)
    Page<User> findAllInstructors(SpecificationTemplate.UserSpec spec, Pageable pageable);
}
