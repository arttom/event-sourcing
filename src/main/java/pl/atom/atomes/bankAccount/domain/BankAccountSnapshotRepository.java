package pl.atom.atomes.bankAccount.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface BankAccountSnapshotRepository extends Repository<BankAccountSnapshot, UUID> {

    @Query("""
        SELECT bas FROM BankAccountSnapshot bas
        WHERE bas.id = :id
        """)
    Optional<BankAccountSnapshot> findSnapshot(@Param("id") UUID id);

    void save(BankAccountSnapshot snapshot);
}
