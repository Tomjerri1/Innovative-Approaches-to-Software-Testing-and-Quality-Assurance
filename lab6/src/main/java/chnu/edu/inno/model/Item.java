package chnu.edu.inno.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Item extends AuditMetadata {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String code;
    private int quantity;
    private String description;

    public Item(String name, String code, int quantity) {
        this.name = name;
        this.code = code;
        this.quantity = quantity;
    }

    public Item(String name, String code, int quantity, String description) {
        this(name, code, quantity);
        this.description = description;
    }
}
