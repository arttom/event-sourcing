package pl.atom.atomes.cart.domain;

import org.springframework.stereotype.Component;
import pl.atom.atomes.aggregate.AggregateRepository;
import pl.atom.atomes.aggregate.DomainEvent;
import pl.atom.atomes.eventstore.EventStore;

import java.util.List;
import java.util.UUID;

@Component
class CartRepository implements AggregateRepository<Cart> {

    private final EventStore eventStore;
    private final CartEventMapper eventMapper = new CartEventMapper();

    CartRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public Cart get(UUID id) {
        List<DomainEvent> events = eventStore.getAggregateDomainEvents(id, eventMapper);
        return Cart.rebuild(id, events);
    }

    @Override
    public void save(Cart cart) {
        List<DomainEvent> pendingEvents = cart.getPendingEvents();
        eventStore.save(pendingEvents, eventMapper);
    }
}
