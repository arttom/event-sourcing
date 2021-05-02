package pl.atom.atomes.bankAccount.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class InMemorySnapshotRepository implements BankAccountSnapshotRepository {

    private final Map<UUID, BankAccountSnapshot> values = new HashMap<>();

    @Override
    public Optional<BankAccountSnapshot> findSnapshot(UUID id) {
        return Optional.ofNullable(values.get(id));
    }

    @Override
    public void save(BankAccountSnapshot snapshot) {
        values.put(snapshot.getId(), snapshot);
    }
}
