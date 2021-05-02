package pl.atom.atomes.bankAccount.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
class BankAccountSnapshot {

    @Id
    private UUID id;
    private Long version;
    private BigDecimal balance;

    public BankAccountSnapshot() {
    }

    BankAccountSnapshot(UUID id) {
        this.id = id;
        version = 0L;
    }

    public BankAccountSnapshot(UUID id, Long version, BigDecimal balance) {
        this.id = id;
        this.version = version;
        this.balance = balance;
    }

    static BankAccountSnapshot fromBankAccount(BankAccount account) {
        return new BankAccountSnapshot(account.getId(), account.getVersion(), account.getBalance());
    }

    public UUID getId() {
        return id;
    }

    Long getVersion() {
        return version;
    }

    BigDecimal getBalance() {
        return balance;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
