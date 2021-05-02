package pl.atom.atomes.eventstore.memory;

import pl.atom.atomes.aggregate.DomainEvent;
import pl.atom.atomes.eventstore.EventMapper;
import pl.atom.atomes.eventstore.EventStore;
import pl.atom.atomes.eventstore.PersistentEvent;

import java.util.*;

public class InMemoryEventStore implements EventStore {

    private final Map<UUID, List<PersistentEvent>> store = new HashMap<>();

    @Override
    public List<DomainEvent> getAggregateDomainEvents(UUID aggregateId, EventMapper mapper) {
        return store.getOrDefault(aggregateId, new ArrayList<>())
                .stream()
                .map(mapper::toDomainEvent)
                .toList();
    }

    @Override
    public List<DomainEvent> getAggregateDomainEvents(UUID aggregateId, Long fromVersion, EventMapper mapper) {
        return store.getOrDefault(aggregateId, new ArrayList<>())
                .stream()
                .filter(event -> event.version() > fromVersion)
                .map(mapper::toDomainEvent)
                .toList();
    }

    @Override
    public void save(DomainEvent event, EventMapper mapper) {
        save(mapper.toPersistentEvent(event));
    }

    @Override
    public void save(List<DomainEvent> pendingEvents, EventMapper mapper) {
        save(pendingEvents
                .stream()
                .map(mapper::toPersistentEvent)
                .toList()
        );
    }

    private void save(PersistentEvent event) {
        List<PersistentEvent> events = store.getOrDefault(event.aggregateId(), new ArrayList<>());
        events.add(event);
        store.put(event.aggregateId(), events);
    }

    private void save(List<PersistentEvent> pendingEvents) {
        pendingEvents.forEach(this::save);
    }
}
