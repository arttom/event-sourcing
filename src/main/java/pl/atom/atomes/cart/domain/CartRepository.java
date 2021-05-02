package pl.atom.atomes.cart.domain;

import pl.atom.atomes.aggregate.DomainEvent;
import pl.atom.atomes.eventstore.EventStore;
import pl.atom.atomes.eventstore.PersistentEvent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class CartRepository {

    private final EventStore eventStore;
    private final CartEventMapper eventMapper;

    CartRepository(EventStore eventStore, CartEventMapper eventMapper) {
        this.eventStore = eventStore;
        this.eventMapper = eventMapper;
    }

    public Cart get(UUID id) {
        List<DomainEvent> events = eventStore.getAggregateEvents(id)
                .stream()
                .map(eventMapper::toDomainEvent)
                .collect(Collectors.toList());
        return Cart.rebuild(id, events);
    }

    public void save(Cart cart) {
        List<PersistentEvent> pendingEvents = cart.getPendingEvents()
                .stream()
                .map(eventMapper::toPersistentEvent)
                .collect(Collectors.toList());
        eventStore.save(pendingEvents);
    }
}
