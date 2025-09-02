package dev.bakr.library_manager.repository;

import dev.bakr.library_manager.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Optional<Reader> findById(Long readerId);

    Reader findByUsername(String readerUsername);

    Reader findByEmail(String readerEmail);

    Boolean existsByUsername(String readerUsername);
}
