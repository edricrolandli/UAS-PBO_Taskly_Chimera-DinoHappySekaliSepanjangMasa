package com.taskly.backend.security;

import com.taskly.backend.model.User;
import com.taskly.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Ekstraksi token JWT dari header Authorization HTTP request
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // Memotong kata "Bearer " untuk mengambil token murni

                // 2. Validasi keaslian token
                if (tokenProvider.validateToken(token)) {
                    String email = tokenProvider.getEmailFromToken(token);
                    System.out.println("[DEBUG] JWT Email extracted: " + email);

                    // 3. Cari user di database untuk mencocokkan hak akses (Otorisasi OOP)
                    User user = userRepository.findByEmail(email).orElse(null);

                    if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
                        System.out.println("[DEBUG] User found: " + email + ", Role: " + user.getRole().name());

                        // 4. Daftarkan identitas user ke dalam sistem Spring Security Context
                        // PENTING: Principal harus email string agar auth.getName() return email
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                email, null, Collections.singletonList(authority));

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else if (user == null) {
                        System.err.println("[ERROR] User not found for email: " + email);
                    }
                } else {
                    System.err.println("[ERROR] Invalid or expired token");
                }
            }
        } catch (Exception e) {
            // Gagalkan otentikasi secara aman jika token rusak/manipulasi
            System.err.println("[ERROR] JWT Filter Exception: " + e.getMessage());
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }

        // Melanjutkan rute request ke filter berikutnya (JPA / Controller)
        filterChain.doFilter(request, response);
    }
}
