package br.com.vidroforte.catalog.repository;

import br.com.vidroforte.catalog.model.User;
import br.com.vidroforte.catalog.model.StatusAprovacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  Optional<User> findByCnpj(String cnpj);
  List<User> findByStatus(StatusAprovacao status);
}

