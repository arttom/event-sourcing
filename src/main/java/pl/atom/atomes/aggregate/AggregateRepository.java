package pl.atom.atomes.aggregate;

import java.util.UUID;

public interface AggregateRepository<T extends Aggregate> {

    void save(T aggregate);

    T get(UUID id);
}
