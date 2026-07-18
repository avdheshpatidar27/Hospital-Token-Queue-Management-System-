package com.hospitalqueue.repository;

import com.hospitalqueue.model.Token;
import com.hospitalqueue.model.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Extending JpaRepository<Token, Long> gives you save(), findAll(),
// findById(), deleteById(), count(), existsById()... for FREE. You never
// write the implementation -- Spring generates it at startup.
//
// "Token" = the entity this repository manages.
// "Long"  = the type of that entity's @Id field.
public interface TokenRepository extends JpaRepository<Token, Long> {

    // ===== Derived query methods =====
    // Spring Data JPA reads the METHOD NAME itself and builds the SQL query
    // from it -- you never write the query. Read it like a sentence:
    // "find Tokens where deleted = false"
    List<Token> findByDeletedFalse();

    // "find Tokens where deleted = false AND status = (whatever you pass in)"
    List<Token> findByDeletedFalseAndStatus(TokenStatus status);
}
