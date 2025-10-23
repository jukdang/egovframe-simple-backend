package egovframework.theimc.api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egovframework.theimc.api.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findById(String id);

  User findByIdAndPassword(String id, String password);

  boolean existsById(String id);

}