package chnu.edu.inno.repository;

import chnu.edu.inno.model.Item;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @JaversSpringDataAuditable - Javers автоматично комітить кожен save/saveAll/delete
 * цього репозиторію (створення, зміни, видалення = ObjectRemoved/TERMINAL).
 */
@JaversSpringDataAuditable
@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
}
