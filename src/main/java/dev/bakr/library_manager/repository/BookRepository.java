package dev.bakr.library_manager.repository;

import dev.bakr.library_manager.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    Book findByIsbn(String bookIsbn);
}
