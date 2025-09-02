package dev.bakr.library_manager.service;

import dev.bakr.library_manager.exceptions.ExistsException;
import dev.bakr.library_manager.exceptions.NotFoundException;
import dev.bakr.library_manager.mappers.WordMapper;
import dev.bakr.library_manager.model.ReaderBook;
import dev.bakr.library_manager.model.ReaderPrincipal;
import dev.bakr.library_manager.repository.ReaderBookRepository;
import dev.bakr.library_manager.repository.ReaderRepository;
import dev.bakr.library_manager.repository.WordRepository;
import dev.bakr.library_manager.requests.WordDtoRequest;
import dev.bakr.library_manager.responses.WordDtoResponse;
import dev.bakr.library_manager.utils.SecurityCheck;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordService {
    private final WordRepository wordRepository;
    private final ReaderRepository readerRepository;
    private final ReaderBookRepository readerBookRepository;
    private final WordMapper wordMapper;

    public WordService(WordRepository wordRepository,
            ReaderRepository readerRepository,
            ReaderBookRepository readerBookRepository, WordMapper wordMapper) {
        this.wordRepository = wordRepository;
        this.readerRepository = readerRepository;
        this.readerBookRepository = readerBookRepository;
        this.wordMapper = wordMapper;
    }

    public WordDtoResponse addWord(Long bookId, WordDtoRequest wordDtoRequest) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        var readerBookId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        var readerBook = readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection to add the word!"
        ));

        boolean isWordExistsInReaderBook = wordRepository.existsByWordContentAndReaderBookId(wordDtoRequest.wordContent(),
                                                                                             readerBookId
        );
        if (isWordExistsInReaderBook) {
            throw new ExistsException("You already have this word in this book copy!");
        }

        var newWord = wordMapper.toEntity(wordDtoRequest);
        newWord.setReaderBook(readerBook);
        var savedWord = wordRepository.save(newWord);

        readerBook.getWords().add(savedWord);
        readerBookRepository.save(readerBook);

        return wordMapper.toDto(newWord);
    }

    public WordDtoResponse updateWord(Long bookId, Long wordId, WordDtoRequest wordDtoRequest) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        var readerBookId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection to update its word!"
        ));

        var theWordToUpdate = wordRepository.findByIdAndReaderBookId(wordId, readerBookId);

        if (theWordToUpdate == null) {
            throw new ExistsException("This word isn't found in this book copy to update it!");
        }

        theWordToUpdate.setWordContent(wordDtoRequest.wordContent());
        theWordToUpdate.setTranslation(wordDtoRequest.translation());
        theWordToUpdate.setRelatedSentence(wordDtoRequest.relatedSentence());
        theWordToUpdate.setPageNumber(wordDtoRequest.pageNumber());
        wordRepository.save(theWordToUpdate);

        return wordMapper.toDto(theWordToUpdate);
    }

    public String deleteWord(Long bookId, Long wordId) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        var readerBookId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        var readerBook = readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "Book not found in your collection to delete its word!"
        ));

        var theWordToDelete = wordRepository.findByIdAndReaderBookId(wordId, readerBookId);
        if (theWordToDelete == null) {
            throw new ExistsException("This word isn't found in this book copy to delete it!");
        }
        wordRepository.delete(theWordToDelete);

        readerBook.getWords().removeIf((word) -> word.equals(theWordToDelete));
        readerBookRepository.save(readerBook);

        return "You've successfully deleted the word.";
    }

    public WordDtoResponse getWord(Long bookId, Long wordId) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        var readerBookId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "This book copy isn't found in your collection to get its word!"
        ));

        var theWordToGet = wordRepository.findByIdAndReaderBookId(wordId, readerBookId);

        if (theWordToGet == null) {
            throw new ExistsException("This word isn't found in this book copy to get from");
        }

        return wordMapper.toDto(theWordToGet);
    }

    public List<WordDtoResponse> getWords(Long bookId) {
        ReaderPrincipal authenticatedReader = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = authenticatedReader.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Reader not found in the database!"));

        var readerBookId = ReaderBook.createCompositeKey(authenticatedReaderId, bookId);

        var readerBook = readerBookRepository.findById(readerBookId).orElseThrow(() -> new NotFoundException(
                "This book copy isn't found in your collection to get the words from!"
        ));

        return readerBook.getWords().stream().map(wordMapper::toDto).toList();
    }
}
