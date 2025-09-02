package dev.bakr.library_manager.service;

import dev.bakr.library_manager.exceptions.NotFoundException;
import dev.bakr.library_manager.model.Reader;
import dev.bakr.library_manager.model.ReaderPrincipal;
import dev.bakr.library_manager.repository.ReaderRepository;
import dev.bakr.library_manager.responses.ReaderDtoResponse;
import dev.bakr.library_manager.utils.SecurityCheck;
import org.springframework.stereotype.Service;

@Service
public class ReaderService {
    private final ReaderRepository readerRepository;

    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    public ReaderDtoResponse getAuthenticatedReader() {
        ReaderPrincipal readerPrincipal = SecurityCheck.getAuthenticatedReader();
        Long authenticatedReaderId = readerPrincipal.getId();

        readerRepository.findById(authenticatedReaderId).orElseThrow(() -> new NotFoundException(
                "Looks like the reader with id: " + authenticatedReaderId + " has been removed from the database!"));

        Reader authenticatedReader = readerPrincipal.getReader();

        return new ReaderDtoResponse(authenticatedReaderId,
                                     authenticatedReader.getUsername(),
                                     authenticatedReader.getEmail(),
                                     authenticatedReader.getIsEnabled()
        );
    }
}
