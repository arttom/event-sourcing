package pl.atom.atomes.eventstore;

import pl.atom.atomes.aggregate.DomainEvent;

import java.util.List;
import java.util.UUID;

public interface EventStore {

    List<DomainEvent> getAggregateDomainEvents(UUID aggregateId, EventMapper mapper);
    List<DomainEvent> getAggregateDomainEvents(UUID aggregateId, Long fromVersion, EventMapper mapper);

    void save(DomainEvent event, EventMapper mapper);

    void save(List<DomainEvent> pendingEvents, EventMapper mapper);
}
