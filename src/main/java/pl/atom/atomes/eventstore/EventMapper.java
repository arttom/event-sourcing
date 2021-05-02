package pl.atom.atomes.eventstore;

import pl.atom.atomes.aggregate.DomainEvent;

public interface EventMapper {

    DomainEvent toDomainEvent(PersistentEvent event);

    PersistentEvent toPersistentEvent(DomainEvent event);
}
