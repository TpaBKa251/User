package ru.tpu.hostel.user.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.repository.UserRepository;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // Сервис для работы с JWT
    private final UserRepository userRepository; // Репозиторий для получения данных о пользователе

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String jwt;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Извлекаем токен из заголовка Authorization

            try {
                // Извлекаем ID пользователя из токена
                UUID userId = jwtService.getUserIdFromToken(jwt);

                if (userId != null) {
                    // Получаем пользователя по ID из базы данных
                    User user = userRepository.findById(userId).orElse(null);

                    if (user != null) {
                        // Создаем объект аутентификации для пользователя
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user, jwt, user.getAuthorities()
                        );
                        // Устанавливаем аутентификацию в контекст безопасности
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (ExpiredJwtException e) {
                log.debug("Token is expired :(");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired");
                return; // Если токен истек, возвращаем ошибку
            } catch (Exception e) {
                log.error("Error processing JWT", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return; // Если произошла ошибка при обработке токена, возвращаем ошибку
            }
        }
        filterChain.doFilter(request, response); // Передаем запрос дальше по цепочке
    }
}

