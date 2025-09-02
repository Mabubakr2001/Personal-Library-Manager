package dev.bakr.library_manager.service;

import dev.bakr.library_manager.exceptions.ExistsException;
import dev.bakr.library_manager.exceptions.NotFoundException;
import dev.bakr.library_manager.mappers.QuoteMapper;
import dev.bakr.library_manager.model.ReaderBook;
import dev.bakr.library_manager.model.ReaderPrincipal;
import dev.bakr.library_manager.repository.QuoteRepository;
import dev.bakr.library_manager.repository.ReaderBookRepository;
import dev.bakr.library_manager.repository.ReaderRepository;
import dev.bakr.library_manager.requests.QuoteDtoRequest;
import dev.bakr.library_manager.responses.QuoteDtoResponse;
import dev.bakr.library_manager.utils.SecurityCheck;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteService {
    private final QuoteRepository quoteRepository;
    private final ReaderRepository readerRepository;
    private final ReaderBookRepository readerBookRepository;
    private final QuoteMapper quoteMapper;

    public QuoteService(QuoteRepository quoteRepository,
            ReaderRepository readerRepository,
            ReaderBookRepository readerBookRepository, QuoteMapper quoteMapper) {
        this.quoteRepository = quoteRepository;
        this.readerRepository = readerRepository;
        this.readerBookRepository = readerBookRepository;
        this.quoteMapper = quoteMapper;
    }

    public QuoteDtoResponse addQuote(Long bookId, QuoteDtoRequest quoteDtoRequest) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        var readerBookId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        var readerBook = readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection to add the quote!"
        ));

        boolean isQuoteExistsInReaderBook = quoteRepository.existsByTextAndReaderBookId(quoteDtoRequest.text(),
                                                                                        readerBookId
        );
        if (isQuoteExistsInReaderBook) {
            throw new ExistsException("You already have this quote in this book copy!");
        }

        var newQuote = quoteMapper.toEntity(quoteDtoRequest);
        newQuote.setReaderBook(readerBook);
        var savedQuote = quoteRepository.save(newQuote);

        readerBook.getQuotes().add(savedQuote);
        readerBookRepository.save(readerBook);

        return quoteMapper.toDto(newQuote);
    }

    public QuoteDtoResponse updateQuote(Long bookId, Long quoteId, QuoteDtoRequest quoteDtoRequest) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        var readerBookId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection to update its quote!"
        ));

        var theQuoteToUpdate = quoteRepository.findByIdAndReaderBookId(quoteId, readerBookId);

        if (theQuoteToUpdate == null) {
            throw new ExistsException("This quote isn't found in this book copy to update it!");
        }

        theQuoteToUpdate.setText(quoteDtoRequest.text());
        theQuoteToUpdate.setPageNumber(quoteDtoRequest.pageNumber());
        quoteRepository.save(theQuoteToUpdate);

        return quoteMapper.toDto(theQuoteToUpdate);
    }

    public String deleteQuote(Long bookId, Long quoteId) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        var readerBookId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        var readerBook = readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection to delete its quote!"
        ));

        var theQuoteToDelete = quoteRepository.findByIdAndReaderBookId(quoteId, readerBookId);
        if (theQuoteToDelete == null) {
            throw new ExistsException("This quote isn't found in this book copy to delete it!");
        }
        quoteRepository.delete(theQuoteToDelete);

        readerBook.getQuotes().removeIf((quote) -> quote.equals(theQuoteToDelete));
        readerBookRepository.save(readerBook);

        return "You've successfully deleted the quote.";
    }

    public QuoteDtoResponse getQuote(Long bookId, Long quoteId) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        var readerBookId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "This book copy isn't found in your collection to get its quote!"
        ));

        var theQuoteToGet = quoteRepository.findByIdAndReaderBookId(quoteId, readerBookId);

        if (theQuoteToGet == null) {
            throw new ExistsException("This quote isn't found in this book copy to get from");
        }

        return quoteMapper.toDto(theQuoteToGet);
    }

    public List<QuoteDtoResponse> getQuotes(Long bookId) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        var readerBookId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        var readerBook = readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "This book copy isn't found in your collection to get the quotes from!"
        ));

        return readerBook.getQuotes().stream().map(quoteMapper::toDto).toList();
    }
}
