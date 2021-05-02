package pl.atom.atomes.cart.domain

import pl.atom.atomes.cart.error.CartOperationException
import pl.atom.atomes.eventstore.memory.InMemoryEventStore
import spock.lang.Specification

class CartFacadeSpec extends Specification {

    CartFacade facade = new CartFacade(new CartRepository(new InMemoryEventStore()))

    def "should create"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            facade.create(id)
        and:
            def cart = facade.get(id)
        then:
            cart.items().isEmpty()
    }

    def "cannot create twice"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            facade.create(id)
        then:
            facade.get(id).items().isEmpty()
        when:
            facade.create(id)
        then:
            CartOperationException ex = thrown()
            ex.message == "Cart is already created"
    }

    def "cannot add to not created"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        when:
            facade.addItem(cartId, itemId)
        then:
            CartOperationException ex = thrown()
            ex.message == "Cart is not created"
    }

    def "should add item"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        when:
            facade.addItem(cartId, itemId)
        then:
            with(facade.get(cartId).items()) {
                size() == 1
                containsKey(itemId)
                get(itemId) == 1
            }
    }

    def "should add twice"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        when:
            facade.addItem(cartId, itemId)
        then:
            with(facade.get(cartId).items()) {
                containsKey(itemId)
                size() == 1
                get(itemId) == 1
            }
        when:
            facade.addItem(cartId, itemId)
        then:
            with(facade.get(cartId).items()) {
                containsKey(itemId)
                size() == 1
                get(itemId) == 2
            }
    }

    def "should submit"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
        when:
            facade.submit(cartId)
        then:
            noExceptionThrown()
        and:
            with(facade.get(cartId).items()) {
                containsKey(itemId)
                size() == 1
                get(itemId) == 1
            }
    }

    def "should reject adding more than 10"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        when:
            for (int i = 0; i < 10; i ++) facade.addItem(cartId, itemId)
        then:
            with(facade.get(cartId).items()) {
                containsKey(itemId)
                size() == 1
                get(itemId) == 10
            }
        when:
            facade.addItem(cartId, itemId)
        then:
            CartOperationException ex = thrown()
            ex.message.startsWith("Cannot add item to cart. It would exceed max: 10")
    }

    def "should reject adding to submitted"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
        and:
            facade.submit(cartId)
        when:
            facade.addItem(cartId, itemId)
        then:
            CartOperationException ex = thrown()
            ex.message.startsWith("Cannot edit Submitted cart")
    }

    def "should reject submit submitted"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
        and:
            facade.submit(cartId)
        when:
            facade.submit(cartId)
        then:
            CartOperationException ex = thrown()
            ex.message.startsWith("Cannot edit Submitted cart")
    }

    def "should subtract item"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
            facade.addItem(cartId, itemId)
        when:
            facade.subtractItem(cartId, itemId)
        then:
            with(facade.get(cartId).items()) {
                size() == 1
                containsKey(itemId)
                get(itemId) == 1
            }
    }

    def "should remove if would be empty"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
        when:
            facade.subtractItem(cartId, itemId)
        then:
            with(facade.get(cartId).items()) {
                isEmpty()
            }
    }

    def "should remove item if would be empty"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
            UUID item2Id = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
            facade.addItem(cartId, item2Id)
        when:
            facade.subtractItem(cartId, itemId)
        then:
            with(facade.get(cartId).items()) {
                size() == 1
                containsKey(item2Id)
                !containsKey(itemId)
                get(item2Id) == 1
            }
    }

    def "should remove when set to 0"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
        when:
            facade.changeAmountOfItems(cartId, itemId, 0)
        then:
            with(facade.get(cartId).items()) {
                isEmpty()
            }
    }

    def "should remove when set to 0 from 2"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
            facade.addItem(cartId, itemId)
        when:
            facade.changeAmountOfItems(cartId, itemId, 0)
        then:
            with(facade.get(cartId).items()) {
                isEmpty()
            }
    }

    def "should remove position when set to 0"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
            UUID item2Id = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
            facade.addItem(cartId, itemId)
            facade.addItem(cartId, item2Id)
        when:
            facade.changeAmountOfItems(cartId, itemId, 0)
        then:
            with(facade.get(cartId).items()) {
                size() == 1
                containsKey(item2Id)
                !containsKey(itemId)
                get(item2Id) == 1
            }
    }

    def "should reject subtract without add"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        when:
            facade.subtractItem(cartId, itemId)
        then:
            CartOperationException ex = thrown()
            ex.message == "Cannot remove item from cart. It is not there"
    }

    def "should reject subtract item not in cart"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
            UUID item2Id = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
        when:
            facade.subtractItem(cartId, item2Id)
        then:
            CartOperationException ex = thrown()
            ex.message == "Cannot remove item from cart. It is not there"
    }

    def "should change item count"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
            UUID item2Id = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
            facade.addItem(cartId, itemId)
            facade.addItem(cartId, item2Id)
        when:
            facade.changeAmountOfItems(cartId, item2Id, 5)
        then:
            with(facade.get(cartId).items()) {
                size() == 2
                containsKey(itemId)
                containsKey(item2Id)
                get(itemId) == 2
                get(item2Id) == 5
            }
    }

    def "should reject set negative"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
            facade.addItem(cartId, itemId)
        when:
            facade.changeAmountOfItems(cartId, itemId, -5)
        then:
            CartOperationException ex = thrown()
            ex.message == "Cannot set negative item count"
    }

    def "should reject set empty when not in cart"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
            UUID item2Id = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
            facade.addItem(cartId, itemId)
        when:
            facade.changeAmountOfItems(cartId, item2Id, 0)
        then:
            CartOperationException ex = thrown()
            ex.message == "Cannot remove item from cart. It is not there"
    }

    def "should add with set"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
            UUID item2Id = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
        when:
            facade.changeAmountOfItems(cartId, item2Id, 3)
        then:
            with(facade.get(cartId).items()) {
                size() == 2
                containsKey(itemId)
                containsKey(item2Id)
                get(itemId) == 1
                get(item2Id) == 3
            }
    }
    def "should reject set more than max"() {
        given:
            UUID cartId = UUID.randomUUID()
            UUID itemId = UUID.randomUUID()
        and:
            facade.create(cartId)
        and:
            facade.addItem(cartId, itemId)
        when:
            facade.changeAmountOfItems(cartId, itemId, 11)
        then:
            CartOperationException ex = thrown()
            ex.message.startsWith("Cannot add item to cart. It would exceed max: 10")
    }

    def "should reject empty submit"() {
        given:
            UUID cartId = UUID.randomUUID()
        and:
            facade.create(cartId)
        when:
            facade.submit(cartId)
        then:
            CartOperationException ex = thrown()
            ex.message.startsWith("Cannot submit empty cart")
    }
}
