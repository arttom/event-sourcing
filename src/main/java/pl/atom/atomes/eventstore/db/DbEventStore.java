package pl.atom.atomes.eventstore.db;

import pl.atom.atomes.eventstore.EventStore;
import pl.atom.atomes.eventstore.PersistentEvent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DbEventStore implements EventStore {

    private final DbEventEntityRepository repository;

    public DbEventStore(DbEventEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PersistentEvent> getAggregateEvents(UUID aggregateId) {
        return repository.findByAggregateId(aggregateId)
                .stream()
                .map(DbEventEntity::toPersistentEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersistentEvent> getAggregateEvents(UUID aggregateId, Long fromVersion) {
        return repository.findByAggregateIdAndFromVersion(aggregateId, fromVersion)
                .stream()
                .map(DbEventEntity::toPersistentEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void save(PersistentEvent event) {
        DbEventEntity eventEntity = new DbEventEntity()
                .setAggregateId(event.aggregateId())
                .setPayload(event.payload())
                .setType(event.type())
                .setVersion(event.version());
        repository.save(eventEntity);
    }

    @Override
    public void save(List<PersistentEvent> pendingEvents) {
        List<DbEventEntity> dbEvents = pendingEvents.stream()
                .map(pe -> new DbEventEntity()
                        .setAggregateId(pe.aggregateId())
                        .setPayload(pe.payload())
                        .setType(pe.type())
                        .setVersion(pe.version()))
                .collect(Collectors.toList());
        repository.saveAll(dbEvents);
    }
}
