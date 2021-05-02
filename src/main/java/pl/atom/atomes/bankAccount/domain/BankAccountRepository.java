package pl.atom.atomes.bankAccount.domain;

import org.springframework.stereotype.Component;
import pl.atom.atomes.aggregate.DomainEvent;
import pl.atom.atomes.eventstore.EventStore;

import java.util.List;
import java.util.UUID;

@Component
class BankAccountRepository {

    private final EventStore eventStore;
    private final BankAccountEventMapper eventMapper = new BankAccountEventMapper();
    private final BankAccountSnapshotRepository snapshotRepository;

    public BankAccountRepository(EventStore eventStore, BankAccountSnapshotRepository snapshotRepository) {
        this.eventStore = eventStore;
        this.snapshotRepository = snapshotRepository;
    }

    BankAccount get(UUID id) {
        BankAccountSnapshot snapshot = snapshotRepository.findSnapshot(id)
                .orElse(new BankAccountSnapshot(id));
        List<DomainEvent> aggregateEvents = eventStore
                .getAggregateDomainEvents(id, snapshot.getVersion(), eventMapper);
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
                .forEach(event -> eventStore.save(event, eventMapper));
    }
}
