package dev.bakr.library_manager.utils;

import dev.bakr.library_manager.model.ReaderPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class SecurityCheck {
    public static ReaderPrincipal getAuthenticatedReader() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (ReaderPrincipal) auth.getPrincipal();
    }
}
