package pl.atom.atomes.bankAccount.domain;

import pl.atom.atomes.bankAccount.dto.BankAccountReadModel;
import pl.atom.atomes.bankAccount.dto.Result;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;

public class BankFacade {

    private final BankAccountRepository repository;

    public BankFacade(BankAccountRepository repository) {
        this.repository = repository;
    }

    public BankAccountReadModel get(UUID id) {
        return repository.get(id).readModel();
    }

    public Result open(UUID id) {
        return with(id, BankAccount::open);
    }

    public Result credit(UUID id, BigDecimal amount) {
        return with(id, account -> account.credit(amount));
    }

    public Result debit(UUID id, BigDecimal amount) {
        return with(id, account -> account.debit(amount));
    }

    private Result with(UUID id, Function<BankAccount, Result> function) {
        BankAccount account = repository.get(id);
        Result result = function.apply(account);
        if (result.success()) {
            repository.save(account);
        }
        return result;
    }
}
