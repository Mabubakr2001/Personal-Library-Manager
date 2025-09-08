//package dev.bakr.library_manager.repository;
//
//import dev.bakr.library_manager.model.Book;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@DataJpaTest
//class BookRepositoryTest {
//    @Autowired
//    private BookRepository underTestBookRepo;
//
//    @AfterEach
//    void tearDown() {
//        underTestBookRepo.deleteAll();
//    }
//
//    @Test
//    void itShouldFindBookByIsbn() {
//        // given
//        String isbn = "9781524746742";
//        Book inMemoryBook = new Book();
//        inMemoryBook.setIsbn(isbn);
//        inMemoryBook.setTitle("Dopamine Nation");
//        inMemoryBook.setPagesCount(290);
//
//        underTestBookRepo.save(inMemoryBook);
//
//        // when (testing the unit)
//        Optional<Book> expectedBook = underTestBookRepo.findByIsbn(isbn);
//
//        // then
//        assertThat(expectedBook.get().getIsbn()).isEqualTo(isbn);
//    }
//
//    @Test
//    void itShouldNotFindBookByIsbn() {
//        // given
//        String isbn = "9781524746742";
//
//        // when (testing the unit)
//        Optional<Book> expectedBook = underTestBookRepo.findByIsbn(isbn);
//
//        // then
//        assertThat(expectedBook).isEmpty();
//    }
//}