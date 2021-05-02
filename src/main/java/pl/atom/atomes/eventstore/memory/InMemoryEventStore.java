package pl.atom.atomes.eventstore.memory;

import pl.atom.atomes.eventstore.EventStore;
import pl.atom.atomes.eventstore.PersistentEvent;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryEventStore implements EventStore {

    private final Map<UUID, List<PersistentEvent>> store = new HashMap<>();

    @Override
    public List<PersistentEvent> getAggregateEvents(UUID aggregateId) {
        return store.getOrDefault(aggregateId, new ArrayList<>());
    }

    @Override
    public List<PersistentEvent> getAggregateEvents(UUID aggregateId, Long fromVersion) {
        return getAggregateEvents(aggregateId)
                .stream()
                .filter(event -> event.version() > fromVersion)
                .collect(Collectors.toList());
    }

    @Override
    public void save(PersistentEvent event) {
        List<PersistentEvent> events = getAggregateEvents(event.aggregateId());
        events.add(event);
        store.put(event.aggregateId(), events);
    }

    @Override
    public void save(List<PersistentEvent> pendingEvents) {
        pendingEvents.forEach(this::save);
    }
}
