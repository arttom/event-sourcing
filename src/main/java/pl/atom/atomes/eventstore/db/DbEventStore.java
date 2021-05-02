package pl.atom.atomes.eventstore.db;

import pl.atom.atomes.aggregate.DomainEvent;
import pl.atom.atomes.eventstore.EventMapper;
import pl.atom.atomes.eventstore.EventStore;
import pl.atom.atomes.eventstore.PersistentEvent;

import java.util.List;
import java.util.UUID;

public class DbEventStore implements EventStore {

    private final DbEventEntityRepository repository;

    public DbEventStore(DbEventEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DomainEvent> getAggregateDomainEvents(UUID aggregateId, EventMapper mapper) {
        return repository.findAllByAggregateIdAsStream(aggregateId)
                .map(DbEventEntity::toPersistentEntity)
                .map(mapper::toDomainEvent)
                .toList();
    }

    @Override
    public List<DomainEvent> getAggregateDomainEvents(UUID aggregateId, Long fromVersion, EventMapper mapper) {
        return repository.findByAggregateIdAndFromVersionAsStream(aggregateId, fromVersion)
                .map(DbEventEntity::toPersistentEntity)
                .map(mapper::toDomainEvent)
                .toList();
    }

    @Override
    public void save(DomainEvent event, EventMapper mapper) {
        save(mapper.toPersistentEvent(event));
    }
    @Override
    public void save(List<DomainEvent> pendingEvents, EventMapper mapper) {
        save(
                pendingEvents.stream().map(mapper::toPersistentEvent).toList()
        );
    }

    private void save(PersistentEvent event) {
        DbEventEntity eventEntity = new DbEventEntity()
                .setAggregateId(event.aggregateId())
                .setPayload(event.payload())
                .setType(event.type())
                .setVersion(event.version());
        repository.save(eventEntity);
    }

    private void save(List<PersistentEvent> pendingEvents) {
        List<DbEventEntity> dbEvents = pendingEvents.stream()
                .map(pe -> new DbEventEntity()
                        .setAggregateId(pe.aggregateId())
                        .setPayload(pe.payload())
                        .setType(pe.type())
                        .setVersion(pe.version()))
                .toList();
        repository.saveAll(dbEvents);
    }
}
