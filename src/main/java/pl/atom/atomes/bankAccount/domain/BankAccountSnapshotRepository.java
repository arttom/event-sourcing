package pl.atom.atomes.bankAccount.domain;

import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

interface BankAccountSnapshotRepository extends Repository<BankAccountSnapshot, UUID> {

    Optional<BankAccountSnapshot> findSnapshot(UUID id);

    void save(BankAccountSnapshot snapshot);
}
