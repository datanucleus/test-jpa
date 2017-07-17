package mydomain.model;

import javax.persistence.*;

@Entity(name = "person")
@SqlResultSetMappings({
        @SqlResultSetMapping(name = "RSM_TEST",
                entities = {@EntityResult(entityClass = Person.class)})
})
public class Person {
    @Id
    Long id;

    String name;

    public Person(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
