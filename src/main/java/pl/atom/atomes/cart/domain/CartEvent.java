package pl.atom.atomes.cart.domain;

import pl.atom.atomes.aggregate.DomainEvent;

import java.util.UUID;

enum CartEvent {

    CREATED(CartCreated.class),
    ITEM_ADDED(ItemAdded.class),
    ITEM_SUBTRACTED(ItemSubtracted.class),
    ITEM_COUNT_CHANGE(ItemCountChange.class),
    ITEM_REMOVED(ItemRemoved.class),
    SUBMITTED(CartSubmitted.class);

    private final Class<? extends DomainEvent> eventClass;

    CartEvent(Class<? extends DomainEvent> eventClass) {
        this.eventClass = eventClass;
    }

    public Class<? extends DomainEvent> getEventClass() {
        return eventClass;
    }

    record CartCreated(UUID aggregateId, Long version) implements DomainEvent {

        @Override
        public String type() {
            return CREATED.name();
        }
    }

    record ItemAdded(UUID aggregateId, Long version, UUID itemId) implements DomainEvent {

        @Override
        public String type() {
            return ITEM_ADDED.name();
        }
    }

    record ItemSubtracted(UUID aggregateId, Long version, UUID itemId) implements DomainEvent {

        @Override
        public String type() {
            return ITEM_SUBTRACTED.name();
        }
    }

    record ItemCountChange(UUID aggregateId, Long version, UUID itemId, Long newCount) implements DomainEvent {

        @Override
        public String type() {
            return ITEM_COUNT_CHANGE.name();
        }
    }

    record CartSubmitted(UUID aggregateId, Long version) implements DomainEvent {

        @Override
        public String type() {
            return SUBMITTED.name();
        }
    }

    record ItemRemoved(UUID aggregateId, Long version, UUID itemId) implements DomainEvent {

        @Override
        public String type() {
            return ITEM_REMOVED.name();
        }
    }
}
