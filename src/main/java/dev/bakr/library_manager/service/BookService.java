package dev.bakr.library_manager.service;

import dev.bakr.library_manager.exceptions.ExistsException;
import dev.bakr.library_manager.exceptions.InvalidInputsException;
import dev.bakr.library_manager.exceptions.NotFoundException;
import dev.bakr.library_manager.mappers.BookMapper;
import dev.bakr.library_manager.model.*;
import dev.bakr.library_manager.repository.BookRepository;
import dev.bakr.library_manager.repository.ReaderBookRepository;
import dev.bakr.library_manager.repository.ReaderRepository;
import dev.bakr.library_manager.requests.BookDtoRequest;
import dev.bakr.library_manager.requests.ReaderBookDtoRequest;
import dev.bakr.library_manager.responses.ReaderBookDtoResponse;
import dev.bakr.library_manager.utils.SecurityCheck;
import dev.bakr.library_manager.utils.StatusValidator;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookService {
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final PublisherService publisherService;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final ReaderBookRepository readerBookRepository;
    private final BookMapper bookMapper;

    public BookService(AuthorService authorService,
            CategoryService categoryService,
            PublisherService publisherService,
            BookRepository bookRepository,
            ReaderRepository readerRepository, ReaderBookRepository readerBookRepository,
            BookMapper bookMapper) {
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.publisherService = publisherService;
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.readerBookRepository = readerBookRepository;
        this.bookMapper = bookMapper;
    }

    public List<ReaderBookDtoResponse> getReaderBooks() {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        Reader reader = readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        return reader.getReaderBooks().stream().map(readerBook -> {
            var theBookItself = readerBook.getBook();
            return ReaderBookDtoResponse.builder()
                    .id(theBookItself.getId())
                    .title(theBookItself.getTitle())
                    .subtitle(theBookItself.getSubtitle())
                    .description(theBookItself.getDescription())
                    .isbn(theBookItself.getIsbn())
                    .pagesCount(theBookItself.getPagesCount())
                    .imageLink(theBookItself.getImageLink())
                    .printingType(theBookItself.getPrintingType())
                    .publishingYear(theBookItself.getPublishingYear())
                    .authorName(theBookItself.getAuthor().getFullName())
                    .categoryName(theBookItself.getCategory().getName())
                    .publisherName(theBookItself.getPublisher().getName())
                    .readingStatus(readerBook.getStatus())
                    .addingDate(readerBook.getAddingDate())
                    .leftOffPage(readerBook.getLeftOffPage())
                    .quotes(readerBook.getQuotes())
                    .words(readerBook.getWords()).build();
        }).toList();
    }

    public ReaderBookDtoResponse getReaderBook(Long bookId) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        Reader reader = readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        ReaderBookId readerBookToGetId = ReaderBook.createCompositeKey(reader.getId(), bookId);
        var readerBookToGet = readerBookRepository.findById(readerBookToGetId).orElseThrow(() -> new NotFoundException(
                "You don't have this book in your collection!"
        ));

        var theBookItself = readerBookToGet.getBook();

        return new ReaderBookDtoResponse(theBookItself.getId(),
                                         theBookItself.getTitle(),
                                         theBookItself.getSubtitle(),
                                         theBookItself.getDescription(),
                                         theBookItself.getIsbn(),
                                         theBookItself.getPagesCount(),
                                         theBookItself.getImageLink(),
                                         theBookItself.getPrintingType(),
                                         theBookItself.getPublishingYear(),
                                         theBookItself.getAuthor().getFullName(),
                                         theBookItself.getCategory().getName(),
                                         theBookItself.getPublisher().getName(),
                                         readerBookToGet.getStatus(),
                                         readerBookToGet.getAddingDate(),
                                         readerBookToGet.getLeftOffPage(),
                                         readerBookToGet.getQuotes(),
                                         readerBookToGet.getWords()
        );
    }

    public String addReaderBook(BookDtoRequest bookDtoRequest) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        // Still check to avoid NullPointerException if reader was deleted after token issued (Always prefer robustness over optimism)
        Reader reader = readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        Book existingBookInDatabase = bookRepository.findByIsbn(bookDtoRequest.isbn());

        ReaderBook readerBookToAdd;

        if (existingBookInDatabase == null) {
            Book newBookEntity = bookMapper.toEntity(bookDtoRequest);
            newBookEntity.setAuthor(authorService.findOrCreateAuthor(bookDtoRequest.authorFullName()));
            newBookEntity.setCategory(categoryService.findOrCreateCategory(bookDtoRequest.categoryName()));
            newBookEntity.setPublisher(publisherService.findOrCreatePublisher(bookDtoRequest.publisherName()));
            Book savedBook = bookRepository.save(newBookEntity);

            readerBookToAdd = new ReaderBook(reader, savedBook);
            readerBookRepository.save(readerBookToAdd);

            reader.getReaderBooks().add(readerBookToAdd);
            readerRepository.save(reader);

            return "We've successfully created the book and added it to your books.";
        } else {
            var readerBookToAddId = ReaderBook.createCompositeKey(reader.getId(), existingBookInDatabase.getId());
            boolean isBookExistsInReaderCollection = readerBookRepository.existsById(readerBookToAddId);
            if (isBookExistsInReaderCollection) {
                throw new ExistsException("You already have this book in your collection!");
            }

            readerBookToAdd = new ReaderBook(reader, existingBookInDatabase);
            readerBookRepository.save(readerBookToAdd);

            reader.getReaderBooks().add(readerBookToAdd);
            readerRepository.save(reader);

            return "This book already exists in the database. We've added it to your books.";
        }
    }

    public ReaderBookDtoResponse updateReaderBook(Long bookId, ReaderBookDtoRequest readerBookDtoRequest) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        // Still check to avoid NullPointerException if reader was deleted after token issued (Always prefer robustness over optimism)
        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        var readerBookToUpdateId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        var readerBookToUpdate = readerBookRepository.findById(readerBookToUpdateId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection."
        ));

        boolean isStatusValid = StatusValidator.validateStatus(readerBookDtoRequest.status());

        if (!isStatusValid) {
            throw new InvalidInputsException("Enter a valid status (UNREAD, READING, READ)! Can be lowercase.");
        }

        readerBookToUpdate.setStatus(readerBookDtoRequest.status());
        readerBookToUpdate.setLeftOffPage(readerBookDtoRequest.leftOffPage());

        var updatedReaderBook = readerBookRepository.save(readerBookToUpdate);

        var theBookItself = updatedReaderBook.getBook();

        return new ReaderBookDtoResponse(theBookItself.getId(),
                                         theBookItself.getTitle(),
                                         theBookItself.getSubtitle(),
                                         theBookItself.getDescription(),
                                         theBookItself.getIsbn(),
                                         theBookItself.getPagesCount(),
                                         theBookItself.getImageLink(),
                                         theBookItself.getPrintingType(),
                                         theBookItself.getPublishingYear(),
                                         theBookItself.getAuthor().getFullName(),
                                         theBookItself.getCategory().getName(),
                                         theBookItself.getPublisher().getName(),
                                         updatedReaderBook.getStatus(),
                                         updatedReaderBook.getAddingDate(),
                                         updatedReaderBook.getLeftOffPage(),
                                         updatedReaderBook.getQuotes(),
                                         updatedReaderBook.getWords()
        );
    }

    public String deleteReaderBook(Long bookId) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        // in case it is authenticated but was deleted accidentally from the database
        Reader reader = readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        ReaderBookId readerBookToDeleteId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        var readerBookToDelete = readerBookRepository.findById(readerBookToDeleteId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection."));
        readerBookRepository.delete(readerBookToDelete);


        Book bookToDeleteIfNoReaders = readerBookToDelete.getBook();

        reader.getReaderBooks().removeIf(rb -> rb.getBook().equals(bookToDeleteIfNoReaders));
        readerRepository.save(reader);

        // Optional: if no other readers have the book, delete it
        if (readerBookRepository.countReadersByBookId(bookToDeleteIfNoReaders.getId()) == 0) {
            bookRepository.delete(bookToDeleteIfNoReaders);
        }

        return "Book deleted successfully.";
    }
}
