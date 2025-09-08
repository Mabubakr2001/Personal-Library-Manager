package dev.bakr.library_manager.service;

import dev.bakr.library_manager.exceptions.ExistsException;
import dev.bakr.library_manager.mappers.BookMapper;
import dev.bakr.library_manager.model.*;
import dev.bakr.library_manager.repository.BookRepository;
import dev.bakr.library_manager.repository.ReaderBookRepository;
import dev.bakr.library_manager.repository.ReaderRepository;
import dev.bakr.library_manager.requests.BookDtoRequest;
import dev.bakr.library_manager.responses.ReaderBookDtoResponse;
import dev.bakr.library_manager.utils.SecurityCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private ReaderRepository readerRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private ReaderBookRepository readerBookRepository;
    @Mock
    private AuthorService authorService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private PublisherService publisherService;

    @InjectMocks
    private BookService underTestService;


    private Reader inMemoryReader;
    private ReaderPrincipal principal;
    private Book inMemoryBook;

    @BeforeEach
    void setUp() {
        inMemoryReader = Reader.builder()
                .id(1L)
                .readerBooks(new ArrayList<>())
                .build();
        principal = new ReaderPrincipal(inMemoryReader);

        inMemoryBook = Book.builder()
                .id(1L)
                .isbn("9781524746742")
                .author(new Author())
                .category(new Category())
                .publisher(new Publisher())
                .build();
    }

    @Test
    void shouldGetReaderBooks() {
        // given (simulates the returned reader from the database)

        // mocking the security (static)
        try (MockedStatic<SecurityCheck> mockedSecurity = mockStatic(SecurityCheck.class)) {
            // when
            mockedSecurity.when(SecurityCheck::getAuthenticatedReader).thenReturn(principal);
            when(readerRepository.findById(principal.getId())).thenReturn(Optional.of(inMemoryReader));

            // doing the test
            List<ReaderBookDtoResponse> underTestResult = underTestService.getReaderBooks();

            // then
            assertEquals(List.of(), underTestResult);
            verify(readerRepository, times(1)).findById(principal.getId());
        }
    }


    @Test
    void shouldGetReaderBook() {
        // given
        ReaderBook inMemoryReaderBook = new ReaderBook();
        inMemoryReaderBook.setBook(inMemoryBook);

        ReaderBookId readerBookId = new ReaderBookId(inMemoryReader.getId(), inMemoryBook.getId());

        try (MockedStatic<SecurityCheck> mockedSecurity = mockStatic(SecurityCheck.class)) {
            mockedSecurity.when(SecurityCheck::getAuthenticatedReader).thenReturn(principal);
            when(readerRepository.findById(1L)).thenReturn(Optional.of(inMemoryReader));

            when(readerBookRepository.findById(readerBookId)).thenReturn(Optional.of(inMemoryReaderBook));

            ReaderBookDtoResponse underTestResult = underTestService.getReaderBook(1L);

            assertThat(inMemoryBook.getId()).isEqualTo(underTestResult.id());

            verify(readerRepository, times(1)).findById(principal.getId());
            verify(readerBookRepository, times(1)).findById(readerBookId);
        }
    }

    @Test
    @Disabled
    void updateReaderBook() {
    }

    @Test
    @Disabled
    void deleteReaderBook() {
    }

    @Nested
    class AddReaderBook {
        private BookDtoRequest bookDtoRequest;

        @BeforeEach
        void setUp() {
            bookDtoRequest = BookDtoRequest.builder()
                    .title("Dopamine Nation")
                    .subtitle("Finding Balance in the Age of Indulgence")
                    .description("ssssssssssssssssssssssssssss")
                    .isbn("9781524746742")
                    .pagesCount(290)
                    .imageLink("vvvvvvvvvvvvvvvvvvvvvvvv")
                    .printingType("Paperback")
                    .publishingYear(2023)
                    .authorFullName("Anna Lembke")
                    .categoryName("Clinical Psychology")
                    .publisherName("Penguin Random House")
                    .build();
        }

        @Test
        void shouldAddReaderBookSuccessfully() {
            // given

            try (MockedStatic<SecurityCheck> mockedSecurity = mockStatic(SecurityCheck.class)) {
                // when
                mockedSecurity.when(SecurityCheck::getAuthenticatedReader).thenReturn(principal);
                when(readerRepository.findById(1L)).thenReturn(Optional.of(inMemoryReader));
                when(bookMapper.toEntity(bookDtoRequest)).thenReturn(inMemoryBook);
                when(authorService.findOrCreateAuthor(bookDtoRequest.authorFullName())).thenReturn(new Author(1L, "Anna Lembke"));
                when(categoryService.findOrCreateCategory(bookDtoRequest.categoryName())).thenReturn(new Category(1L, "Clinical Psychology"));
                when(publisherService.findOrCreatePublisher(bookDtoRequest.publisherName())).thenReturn(new Publisher(1L, "Penguin Random House"));
                when(bookRepository.save(inMemoryBook)).thenAnswer(invocation -> {
                    Book savedBook = invocation.getArgument(0);
                    savedBook.setId(1L);
                    return savedBook;
                });

                String actualMessage = underTestService.addReaderBook(bookDtoRequest);

                // then
                assertEquals("We've successfully created the book and added it to your books.", actualMessage);
                assertFalse(inMemoryReader.getReaderBooks().isEmpty());
                verify(readerRepository, times(1)).save(inMemoryReader);
                verify(bookRepository, times(1)).save(inMemoryBook);
            }
        }

        @Test
        void shouldThrowAnExceptionSayingTheBookInCollection() {
            // given

            try (MockedStatic<SecurityCheck> mockedSecurity = mockStatic(SecurityCheck.class)) {
                // when
                mockedSecurity.when(SecurityCheck::getAuthenticatedReader).thenReturn(principal);
                when(readerRepository.findById(principal.getId())).thenReturn(Optional.of(inMemoryReader));
                when(bookRepository.findByIsbn(bookDtoRequest.isbn())).thenReturn(Optional.of(inMemoryBook));
                // Argument value doesn’t matter in this test; we only need it to return true
                when(readerBookRepository.existsById(any(ReaderBookId.class))).thenReturn(true);

                // then
                ExistsException ex = assertThrows(ExistsException.class, () -> underTestService.addReaderBook(bookDtoRequest));
                assertEquals("You already have this book in your collection!", ex.getMessage());
                verify(readerRepository, times(1)).findById(principal.getId());
                verify(bookRepository, times(1)).findByIsbn(bookDtoRequest.isbn());
                verify(readerBookRepository, times(1)).existsById(any(ReaderBookId.class));
            }
        }

        @Test
        void shouldAddTheExistedBookToTheCollection() {
            try (MockedStatic<SecurityCheck> mockedSecurity = mockStatic(SecurityCheck.class)) {
                mockedSecurity.when(SecurityCheck::getAuthenticatedReader).thenReturn(principal);
                when(readerRepository.findById(principal.getId())).thenReturn(Optional.of(inMemoryReader));
                when(bookRepository.findByIsbn(bookDtoRequest.isbn())).thenReturn(Optional.of(inMemoryBook));

                String actualResult = underTestService.addReaderBook(bookDtoRequest);

                assertEquals("This book already exists in the database. We've added it to your books.", actualResult);
                assertFalse(inMemoryReader.getReaderBooks().isEmpty());
                verify(readerRepository, times(1)).findById(principal.getId());
                verify(bookRepository, times(1)).findByIsbn(bookDtoRequest.isbn());
                verify(readerBookRepository, times(1)).save(any(ReaderBook.class));
                verify(readerRepository, times(1)).save(inMemoryReader);
            }
        }
    }
}