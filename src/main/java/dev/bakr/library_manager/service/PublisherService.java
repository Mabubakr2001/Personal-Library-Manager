package dev.bakr.library_manager.service;

import dev.bakr.library_manager.model.Publisher;
import dev.bakr.library_manager.repository.PublisherRepository;
import org.springframework.stereotype.Service;

@Service
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public Publisher findOrCreatePublisher(String publisherName) {
        return publisherRepository.findByName(publisherName)
                .orElseGet(() -> {
                    Publisher publisher = new Publisher();
                    publisher.setName(publisherName);
                    return publisherRepository.save(publisher);
                });
    }
}
