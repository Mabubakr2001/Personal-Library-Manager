package dev.bakr.library_manager.service;

import dev.bakr.library_manager.mappers.BookMapper;
import dev.bakr.library_manager.model.*;
import dev.bakr.library_manager.repository.BookRepository;
import dev.bakr.library_manager.repository.ReaderBookRepository;
import dev.bakr.library_manager.repository.ReaderRepository;
import dev.bakr.library_manager.requests.BookDtoRequest;
import dev.bakr.library_manager.utils.SecurityCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/* This annotation integrates Mockito with JUnit 5. It enables the use of @Mock and @InjectMocks by automatically initializing
mock objects (a fake object created by Mockito to replace a real dependency during testing) , allowing us to test classes in
isolation without relying on real dependencies (mocking = creating fake versions of objects). */
@ExtendWith(MockitoExtension.class)
@DisplayName("Book Service Unit Tests")
class BookServiceTest {
    // creating the mocks
    @Mock
    private AuthorService authorService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private PublisherService publisherService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ReaderRepository readerRepository;
    @Mock
    private ReaderBookRepository readerBookRepository;
    @Mock
    private BookMapper bookMapper;


    // injecting the mocks
    @InjectMocks
    private BookService service;

    private BookDtoRequest bookDtoRequest;

    // is used to signal that the annotated method should be executed before each test
    @BeforeEach
    void setUp() {
        bookDtoRequest = BookDtoRequest.builder()
                .title("Dopamine Nation")
                .subtitle("Finding Balance in the Age of Indulgence")
                .description(
                        "Psychiatrist and author Dr. Anna Lembke explores why the relentless pursuit of pleasure leads to pain, and what to do about it.")
                .isbn("9781524746742")
                .pagesCount(290)
                .imageLink("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1665646968i/62981559.jpg")
                .printingType("Paperback")
                .publishingYear(2023)
                .authorFullName("Anna Lembke")
                .categoryName("Clinical Psychology")
                .publisherName("Penguin Random House")
                .build();
    }


    /* Use @Nested to group related tests together. This keeps the test class organized and easier to read, instead of putting
    all test methods in a single flat structure. */
    @DisplayName("Add Reader Book Tests")
    @Nested
    class AddReaderBookTests {

        @Test
        @DisplayName("Should be successfully adding new reader book when the reader exists")
        void shouldCreateNewBookAndAddToReader() {
            // Arrange

            //faking the logged-in user
            Reader reader = new Reader();
            reader.setId(1L);
            reader.setReaderBooks(new ArrayList<>());

            // create a reader principal from this faked logged-in user to give to be returned from the mocked (faked) static method
            ReaderPrincipal principal = new ReaderPrincipal(reader);

            /* This creates a fake auth user because in a unit test, there is no logged-in user → without faking it, we’d get
            null and then a NullPointerException. And we used MockedStatic because we want to mock a static method that doesn't
            depend on an object, and we did this inside a try-with-resource to make sure that the static mock gets automatically
            closed when the test ends because MockedStatic implements AutoCloseable, so why not using try-with-resource, instead
            of the verbose "finally". */
            try (MockedStatic<SecurityCheck> mockedSecurity = mockStatic(SecurityCheck.class)) {
                mockedSecurity.when(SecurityCheck::getAuthenticatedReader).thenReturn(principal);

                // when the x method is called then return y
                when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
                // simulating the case where a book with that ISBN does not exist in the database yet
                when(bookRepository.findByIsbn("9781524746742")).thenReturn(null);

                // create a mock (fake) book to return when running the test and calling the book mapper
                Book mappedBook = new Book();
                mappedBook.setIsbn("9781524746742");
                when(bookMapper.toEntity(bookDtoRequest)).thenReturn(mappedBook);

                when(authorService.findOrCreateAuthor("Anna Lembke")).thenReturn(new Author(1L, "Anna Lembke"));
                when(categoryService.findOrCreateCategory("Clinical Psychology")).thenReturn(new Category(1L, "Clinical Psychology"));
                when(publisherService.findOrCreatePublisher("Penguin Random House")).thenReturn(new Publisher(1L, "Penguin Random House"));

                /* if not doing this, then our mock repository (bookRepository) has no instructions for what to do when save()
                is called so the returned book will be null*/
                when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
                    Book book = invocation.getArgument(0); // get the book passed to save()
                    book.setId(1L); // simulate DB assigning an ID
                    return book; // return the same book, now with an ID
                });

                // Act
                String result = service.addReaderBook(bookDtoRequest);

                // Assert -> checks if the actual result of our code matches the expected result.
                assertEquals("We've successfully created the book and added it to your books.", result);

                verify(bookRepository).save(any(Book.class));
                verify(readerBookRepository).save(any(ReaderBook.class));
                verify(readerRepository).save(reader);
            }
        }

        @Test
        @DisplayName("Should return an exception if the book already exists in the reader's collection")
        void shouldReturnExistsExceptionWhenBookAlreadyInReaderCollection() {
            
        }
    }
}