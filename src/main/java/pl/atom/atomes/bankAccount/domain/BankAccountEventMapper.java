package pl.atom.atomes.bankAccount.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.atom.atomes.aggregate.DomainEvent;
import pl.atom.atomes.eventstore.EventMapper;
import pl.atom.atomes.eventstore.PersistentEvent;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

class BankAccountEventMapper implements EventMapper {

    private final ObjectMapper objectMapper;

    public BankAccountEventMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public DomainEvent toDomainEvent(PersistentEvent event) {
        BankAccountEvent eventDescriptor = BankAccountEvent.valueOf(event.type());
        try {
            return objectMapper.readValue(event.payload(), eventDescriptor.getEventClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("No valid event for: " + event.type());
        }
    }

    @Override
    public PersistentEvent toPersistentEvent(DomainEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            return new PersistentEvent(event.aggregateId(), event.type(), event.version(), payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot parse " + event.type());
        }
    }
}
