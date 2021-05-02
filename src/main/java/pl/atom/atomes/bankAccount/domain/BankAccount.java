package pl.atom.atomes.bankAccount.domain;

import pl.atom.atomes.aggregate.Aggregate;
import pl.atom.atomes.aggregate.DomainEvent;
import pl.atom.atomes.bankAccount.dto.BankAccountReadModel;
import pl.atom.atomes.bankAccount.dto.Result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

class BankAccount extends Aggregate {

    private BigDecimal balance;

    public BankAccount(UUID id) {
        super(id);
    }

    BigDecimal getBalance() {
        return balance;
    }

    Result open() {
        if (getVersion() > 0) {
            return new Result(false, "Cannot open not new account");
        }
        handle(new BankAccountEvent.BankAccountOpened(getId(), getVersion() + 1, Instant.now()));
        return new Result(true, "");
    }

    Result credit(BigDecimal amount) {
        handle(new BankAccountEvent.BankAccountCredited(getId(), getVersion() + 1, Instant.now(), amount));
        return new Result(true, "");
    }

    Result debit(BigDecimal amount) {
        if (amount.compareTo(balance) > 0) {
            return new Result(false, "No enough balance");
        }
        handle(new BankAccountEvent.BankAccountDebited(getId(), getVersion() + 1, Instant.now(), amount));
        return new Result(true, "");
    }

    protected void apply(DomainEvent event) {
        if (event instanceof BankAccountEvent.BankAccountOpened opened) {
            applyOpened(opened);
        } else if (event instanceof BankAccountEvent.BankAccountCredited credited) {
            applyCredited(credited);
        } else if (event instanceof BankAccountEvent.BankAccountDebited debited){
            applyDebited(debited);
        } else {
            throw new RuntimeException("No handler for " + event.type());
        }
    }

    private void applyOpened(BankAccountEvent.BankAccountOpened opened) {
        setVersion(opened.version());
        this.balance = new BigDecimal(0);
    }

    private void applyDebited(BankAccountEvent.BankAccountDebited event) {
        setVersion(event.version());
        this.balance = this.balance.subtract(event.amount());
    }

    private void applyCredited(BankAccountEvent.BankAccountCredited event) {
        setVersion(event.version());
        this.balance = this.balance.add(event.amount());
    }

    void rebuild(BankAccountSnapshot snapshot, List<DomainEvent> aggregateEvents) {
        setVersion(snapshot.getVersion());
        this.balance = snapshot.getBalance();
        rebuild(aggregateEvents);
    }

    BankAccountReadModel readModel() {
        return new BankAccountReadModel(getId(), balance);
    }


}
