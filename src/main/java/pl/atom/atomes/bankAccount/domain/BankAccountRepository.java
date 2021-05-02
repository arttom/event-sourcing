package pl.atom.atomes.bankAccount.domain;

import pl.atom.atomes.aggregate.DomainEvent;
import pl.atom.atomes.eventstore.EventStore;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class BankAccountRepository {

    private final EventStore eventStore;
    private final BankAccountEventMapper eventMapper;
    private final BankAccountSnapshotRepository snapshotRepository;

    public BankAccountRepository(EventStore eventStore, BankAccountEventMapper eventMapper, BankAccountSnapshotRepository snapshotRepository) {
        this.eventStore = eventStore;
        this.eventMapper = eventMapper;
        this.snapshotRepository = snapshotRepository;
    }

    BankAccount get(UUID id) {
        BankAccountSnapshot snapshot = snapshotRepository.findSnapshot(id)
                .orElse(new BankAccountSnapshot(id));
        List<DomainEvent> aggregateEvents = eventStore
                .getAggregateEvents(id, snapshot.getVersion())
                .stream()
                .map(eventMapper::toDomainEvent)
                .collect(Collectors.toList());
        BankAccount bankAccount = new BankAccount(id);
        bankAccount.rebuild(snapshot, aggregateEvents);
        return bankAccount;
    }

    void save(BankAccount bankAccount) {
        if (bankAccount.getVersion() % 20 == 0) {
            BankAccountSnapshot snapshot = BankAccountSnapshot.fromBankAccount(bankAccount);
            snapshotRepository.save(snapshot);
        }
        bankAccount.getPendingEvents()
                .stream()
                .map(eventMapper::toPersistentEvent)
                .forEach(eventStore::save);
    }
}
