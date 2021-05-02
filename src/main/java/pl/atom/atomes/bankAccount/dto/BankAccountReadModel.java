package pl.atom.atomes.bankAccount.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BankAccountReadModel(UUID id, BigDecimal balance) {
}
