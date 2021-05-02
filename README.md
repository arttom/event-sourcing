# Event Sourcing example project
Presentation of basic idea and implementation for Event Sourced entities

It uses Spring as Dependency Injection management and JPA Repositories for some examples, but these are optional and should be easy to use with any other tech stack - as seen in tests where I am using InMemoryRepositories.

### Aggregate
Aggregate is a class that represents domain unit.

It has a state and actions that can be called on it.

Aggregate is responsible for protecting business rules and producing Domain Events which change its state.

Apply method shouldn't check the rules. 

It should also be able to rebuild it's state from the events.
### Aggregate Repository
Aggregate Repository is used to persist an Aggregate

### Domain Event
Domain event is a record produced by Aggregate.

Events are saved into Event Store and are used to rebuild an Aggregate

### Persistent Event
Persistent Event is unified representation of Domain Event.

### Event Mapper
Event Mapper is a class that allows mapping DomainEvent->PersistentEvent and PersistentEvent->DomainEvent

It should be separate for each Aggregate and it's events.


### Event Store
Event Store is used to save and retrieve Domain Events.

It should be provided with EventMapper implementation.

Event Store stores events from ALL Aggregates.

# Examples
I provided two example implementation of Aggregates for easier understanding and representing two approaches.

## Bank Account
Bank Account represents long living Aggregate, it doesn't have a final action. Once opened we can either Credit or Debit it (if states allow).

Because it is long living BankAccountRepository internally uses Snapshots. 

Snapshot is used to not always fetch all events, as there might be thousands of them.
Instead from time to time (based on version - in the example each 20th version increment) it creates snapshot and fetches only newer events.

All Events are still persisted in the EventStore and Snapshot can be removed - then Aggregate will rebuild completely from Events.


## Cart
Cart is by design short living Aggregate, it has final state and doesn't use snapshot. Each time it is rebuilding from Events.