package pl.atom.atomes.cart.domain;

import pl.atom.atomes.aggregate.DomainEvent;
import pl.atom.atomes.eventstore.EventStore;

import java.util.List;
import java.util.UUID;

class CartRepository {

    private final EventStore eventStore;
    private final CartEventMapper eventMapper;

    CartRepository(EventStore eventStore, CartEventMapper eventMapper) {
        this.eventStore = eventStore;
        this.eventMapper = eventMapper;
    }

    public Cart get(UUID id) {
        List<DomainEvent> events = eventStore.getAggregateDomainEvents(id, eventMapper);
        return Cart.rebuild(id, events);
    }

    public void save(Cart cart) {
        List<DomainEvent> pendingEvents = cart.getPendingEvents();
        eventStore.save(pendingEvents, eventMapper);
    }
}
