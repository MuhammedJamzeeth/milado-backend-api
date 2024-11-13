package com.milado_api_main.repositries;


import java.util.Optional;
import com.milado_api_main.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	boolean existsByEmail(final String emailId);

	Optional<Account> findByEmail(String emailId);

}
