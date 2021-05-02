package pl.atom.atomes.eventstore.db;

import pl.atom.atomes.eventstore.PersistentEvent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class DbEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID aggregateId;
    private String payload;
    private String type;
    private Long version;

    public void setId(Long id) {
        this.id = id;
    }

    public DbEventEntity setAggregateId(UUID aggregateId) {
        this.aggregateId = aggregateId;
        return this;
    }

    public DbEventEntity setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public DbEventEntity setType(String type) {
        this.type = type;
        return this;
    }

    public Long getVersion() {
        return version;
    }

    public DbEventEntity setVersion(Long version) {
        this.version = version;
        return this;
    }

    public Long getId() {
        return id;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getPayload() {
        return payload;
    }

    public String getType() {
        return type;
    }

    public PersistentEvent toPersistentEntity() {
        return new PersistentEvent(aggregateId, type, version, payload);
    }
}
