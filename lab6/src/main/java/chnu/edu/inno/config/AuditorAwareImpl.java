package chnu.edu.inno.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * Повертає "поточного користувача" для полів @CreatedBy / @LastModifiedBy.
 * Тут - ім'я користувача ОС (як у лекції); зі Spring Security брався б Authentication.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(System.getProperty("user.name"));
    }
}
