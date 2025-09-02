package dev.bakr.library_manager.service;

import dev.bakr.library_manager.model.Author;
import dev.bakr.library_manager.repository.AuthorRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author findOrCreateAuthor(String authorFullName) {
        return authorRepository.findByFullName(authorFullName)
                .orElseGet(() -> {
                    Author author = new Author();
                    author.setFullName(authorFullName);
                    return authorRepository.save(author);
                });
    }
}
