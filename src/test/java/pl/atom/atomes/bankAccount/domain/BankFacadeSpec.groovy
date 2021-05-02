package pl.atom.atomes.bankAccount.domain


import pl.atom.atomes.eventstore.memory.InMemoryEventStore
import spock.lang.Specification
import spock.lang.Subject

class BankFacadeSpec extends Specification {

    BankAccountSnapshotRepository snapshotRepository = new InMemorySnapshotRepository()

    @Subject
    BankFacade facade = new BankFacade(new BankAccountRepository(new InMemoryEventStore(), new BankAccountEventMapper(), snapshotRepository))


    def "should open"() {
        given: "we want to open new"
            UUID id = UUID.randomUUID()
        when:
            facade.open(id)
            def account = facade.get(id)
        then:
            account.balance() == 0.0
    }

    def "should credit"() {
        given: "we open new"
            UUID id = UUID.randomUUID()
            facade.open(id)
        when: "we credit it"
            facade.credit(id, 100.0)
            def account = facade.get(id)
        then:
            account.balance() == 100.0
    }

    def "shouldn't debit"() {
        given: "we open new"
            UUID id = UUID.randomUUID()
            facade.open(id)
        when: "we debit it"
            def result = facade.debit(id, 100.0)
        then: "rejected"
            !result.success()
        when: "we get it"
            def account = facade.get(id)
        then:
            account.balance() == 0.0
    }

    def "should debit credited"() {
        given: "we open new"
            UUID id = UUID.randomUUID()
            facade.open(id)
        and: "we credit it"
            facade.credit(id, 100.0)
        when: "we debit it"
            facade.debit(id, 60.0)

        and: "we get it"
            def account = facade.get(id)
        then:
            account.balance() == 40.0
    }


    def "shouldn't credit not open"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            facade.credit(id, 100.0)
        then:
            thrown(RuntimeException)
    }

    def "should go with story"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            facade.open(id)
        and:
            facade.credit(id, 100.0)
        and:
            facade.credit(id, 80.0)
        and:
            facade.debit(id, 60.0)
        and:
            def account = facade.get(id)
        then:
            account.balance() == 120.0
        when:
            facade.credit(id, 5.0)
        and:
            account = facade.get(id)
        then:
            account.balance() == 125.0
    }

    def "should go with problematic story"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            facade.open(id)
        and:
            facade.credit(id, 100.0)
        and:
            def result = facade.debit(id, 150.0)
        then:
            !result.success()
        when:
            facade.credit(id, 80.0)
        and:
            facade.debit(id, 60.0)
        and:
            def account = facade.get(id)
        then:
            account.balance() == 120.0
        when:
            facade.credit(id, 5.0)
        and:
            account = facade.get(id)
        then:
            account.balance() == 125.0
        when:
            result = facade.debit(id, 200.0)
        then:
            !result.success()
        when:
            facade.credit(id, 80.0)
        and:
            facade.debit(id, 200.0)
        and:
            account = facade.get(id)
        then:
            account.balance() == 5.0
    }

    def "should use snapshot story"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            facade.open(id)
        and:
            for (int i = 0; i < 18; i++) facade.credit(id, 10.0)
        and:
            def account = facade.get(id)
        then:
            snapshotRepository.findSnapshot(id).isEmpty()
            account.balance() == 180.0
        when:
            facade.credit(id, 20.0)
            def snapshot = snapshotRepository.findSnapshot(id)
        then:
            snapshot.isPresent()
            snapshot.get().balance == 200.0
            snapshot.get().version == 20
        when:
            account = facade.get(id)
        then:
            account.balance() == 200.0
        when:
            facade.credit(id, 50.0)
            snapshot = snapshotRepository.findSnapshot(id)
            account = facade.get(id)
        then:
            snapshot.isPresent()
            snapshot.get().version == 20
            snapshot.get().balance == 200.0
            account.balance() == 250.0
        when:
            facade.credit(id, 50.0)
            snapshot = snapshotRepository.findSnapshot(id)
            account = facade.get(id)
        then:
            snapshot.isPresent()
            snapshot.get().version == 20
            snapshot.get().balance == 200.0
            account.balance() == 300.0

    }
}
