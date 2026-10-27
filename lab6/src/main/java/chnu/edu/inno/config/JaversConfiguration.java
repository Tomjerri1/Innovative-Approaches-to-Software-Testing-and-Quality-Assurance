package chnu.edu.inno.config;

import org.javers.spring.auditable.AuthorProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Javers бере автора коміту з AuthorProvider (commitMetadata.author у відповіді /audit/...).
 * Javers instance і MongoRepository створює javers-spring-boot-starter-mongo.
 */
@Configuration
public class JaversConfiguration {

    private static class SimpleAuthorProvider implements AuthorProvider {
        @Override
        public String provide() {
            return "ADMIN";
        }
    }

    @Bean
    public AuthorProvider provideJaversAuthor() {
        return new SimpleAuthorProvider();
    }
}
