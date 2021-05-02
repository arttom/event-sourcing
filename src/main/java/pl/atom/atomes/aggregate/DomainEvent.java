package pl.atom.atomes.aggregate;

import java.util.UUID;

public interface DomainEvent {

    Long version();

    String type();

    UUID aggregateId();
}
