package pl.atom.atomes.bankAccount.domain;

import pl.atom.atomes.aggregate.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

enum BankAccountEvent {

    OPENED(BankAccountOpened.class), CREDITED(BankAccountCredited.class), DEBITED(BankAccountDebited.class);

    private final Class<? extends DomainEvent> eventClass;

    BankAccountEvent(Class<? extends DomainEvent> eventClass) {
        this.eventClass = eventClass;
    }

    public Class<? extends DomainEvent> getEventClass() {
        return eventClass;
    }

    static record BankAccountOpened(UUID aggregateId, Long version, Instant when) implements DomainEvent {

        @Override
        public String type() {
            return OPENED.name();
        }
    }

    static record BankAccountCredited(UUID aggregateId, Long version, Instant when, BigDecimal amount) implements DomainEvent {

        @Override
        public String type() {
            return CREDITED.name();
        }
    }

    static record BankAccountDebited(UUID aggregateId, Long version, Instant when, BigDecimal amount) implements DomainEvent {

        @Override
        public String type() {
            return DEBITED.name();
        }
    }
}
