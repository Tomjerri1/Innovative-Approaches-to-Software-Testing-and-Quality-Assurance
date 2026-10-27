package chnu.edu.inno.controller;

import chnu.edu.inno.model.Item;
import lombok.RequiredArgsConstructor;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Читання аудиту Javers: Changes (що змінилось) та Shadows (знімки об'єкта в минулому).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/audit")
public class AuditController {

    private final Javers javers;

    /** Усі зміни всіх Item. */
    @GetMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getItemsChanges() {
        QueryBuilder jqlQuery = QueryBuilder.byClass(Item.class);
        List<Change> changes = javers.findChanges(jqlQuery.build());
        return ResponseEntity.ok().body(javers.getJsonConverter().toJson(changes));
    }

    /** Усі shadows (історичні версії) всіх Item. */
    @GetMapping(value = "/shadows", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getItemsShadows() {
        QueryBuilder jqlQuery = QueryBuilder.byClass(Item.class);
        List<Shadow<Item>> shadows = javers.findShadows(jqlQuery.build());
        return ResponseEntity.ok().body(javers.getJsonConverter().toJson(shadows));
    }

    /** Shadows одного Item за id. */
    @GetMapping(value = "/shadows/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getItemShadow(@PathVariable String id) {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(id, Item.class)
                .withNewObjectChanges();
        List<Shadow<Item>> shadows = javers.findShadows(jqlQuery.build());
        return ResponseEntity.ok().body(javers.getJsonConverter().toJson(shadows));
    }

    /** Читабельна історія змін одного Item. */
    @GetMapping(value = "/changes/item/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getItemChanges(@PathVariable String id) {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(id, Item.class)
                .withNewObjectChanges();
        Changes changes = javers.findChanges(jqlQuery.build());
        return changes.prettyPrint();
    }
}
