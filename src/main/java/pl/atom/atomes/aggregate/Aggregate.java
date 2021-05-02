package pl.atom.atomes.aggregate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public abstract class Aggregate {

    private final UUID id;
    private Long version = 0L;
    private final List<DomainEvent> pendingEvents = new ArrayList<>();

    public Aggregate(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<DomainEvent> getPendingEvents() {
        return pendingEvents;
    }

    protected void handle(DomainEvent domainEvent) {
        apply(domainEvent);
        pendingEvents.add(domainEvent);
    }

    protected abstract void apply(DomainEvent domainEvent);

    protected void rebuild(List<DomainEvent> aggregateEvents) {
        aggregateEvents.stream()
                .sorted(Comparator.comparing(DomainEvent::version))
                .forEach(this::apply);
    }
}
