package com.example.backendtest.repository;

import com.example.backendtest.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findTopByUsernameAndActiveIsTrue(String username);

    User findByUserIdAndActiveIsTrue(Long userId);
}