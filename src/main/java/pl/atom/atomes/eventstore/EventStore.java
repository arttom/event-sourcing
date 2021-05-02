package pl.atom.atomes.eventstore;

import pl.atom.atomes.aggregate.DomainEvent;

import java.util.List;
import java.util.UUID;

public interface EventStore {

    List<PersistentEvent> getAggregateEvents(UUID aggregateId);

    List<PersistentEvent> getAggregateEvents(UUID aggregateId, Long fromVersion);

    void save(PersistentEvent event);

    void save(List<PersistentEvent> pendingEvents);
}
