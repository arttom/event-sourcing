package pl.atom.atomes.bankAccount;

import org.springframework.web.bind.annotation.*;
import pl.atom.atomes.bankAccount.domain.BankFacade;
import pl.atom.atomes.bankAccount.dto.BankAccountReadModel;
import pl.atom.atomes.bankAccount.dto.Result;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank-account")
public class BankAccountController {

    private final BankFacade facade;

    public BankAccountController(BankFacade facade) {
        this.facade = facade;
    }

    @GetMapping("{id}")
    BankAccountReadModel get(@PathVariable("id") String id) {
        return facade.get(UUID.fromString(id));
    }

    @PostMapping("{id}")
    Result create(@PathVariable("id") String id) {
        return facade.open(UUID.fromString(id));
    }

    @PostMapping("{id}/credit")
    Result credit(@PathVariable("id") String id, @RequestBody float amount) {
        return facade.credit(UUID.fromString(id), BigDecimal.valueOf(amount));
    }

    @PostMapping("{id}/debit")
    Result debit(@PathVariable("id") String id, @RequestBody float amount) {
        return facade.debit(UUID.fromString(id), BigDecimal.valueOf(amount));
    }
}
