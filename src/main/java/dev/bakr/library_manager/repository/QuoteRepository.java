package dev.bakr.library_manager.repository;

import dev.bakr.library_manager.model.Quote;
import dev.bakr.library_manager.model.ReaderBookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    @Query("SELECT COUNT(q) > 0 FROM Quote q WHERE q.text = :quoteText AND q.readerBook.id = :readerBookId")
    boolean existsByTextAndReaderBookId(@Param("quoteText") String quoteText,
            @Param("readerBookId") ReaderBookId readerBookId);

    @Query("SELECT q FROM Quote q WHERE q.id = :quoteId AND q.readerBook.id = :readerBookId")
    Quote findByIdAndReaderBookId(@Param("quoteId") Long quoteId, @Param("readerBookId") ReaderBookId readerBookId);
}
