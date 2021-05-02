package pl.atom.atomes.eventstore;

import java.util.UUID;

public record PersistentEvent(UUID aggregateId, String type, Long version, String payload) {
}
