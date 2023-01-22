package com.example.server.DAO;

import com.example.server.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET role = 'ADMIN' WHERE username = :name", nativeQuery = true)
    void updateRoleToAdmin(@Param("name") String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET role = 'USER' WHERE username = :name", nativeQuery = true)
    void updateRoleToUser(@Param("name") String name);

    boolean existsById(Long userId);

    @Transactional
    void deleteUserByUsername(String Username);

    boolean existsByUsername(String username);

    Optional<User> findById(Long userId);

    Optional<User> findByUsername(String username);


}
