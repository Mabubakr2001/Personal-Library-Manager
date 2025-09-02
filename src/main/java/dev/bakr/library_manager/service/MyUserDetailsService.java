package dev.bakr.library_manager.service;

import dev.bakr.library_manager.model.Reader;
import dev.bakr.library_manager.model.ReaderPrincipal;
import dev.bakr.library_manager.repository.ReaderRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final ReaderRepository readerRepository;

    public MyUserDetailsService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Reader reader = readerRepository.findByUsername(username);

        if (reader == null) {
            throw new UsernameNotFoundException("User not found! With username: " + username);
        }

        return new ReaderPrincipal(reader);
    }
}
