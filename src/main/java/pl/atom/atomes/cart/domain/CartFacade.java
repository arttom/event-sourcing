package pl.atom.atomes.cart.domain;

import pl.atom.atomes.cart.dto.CartDto;

import java.util.UUID;

public class CartFacade {

    private final CartRepository repository;

    public CartFacade(CartRepository repository) {
        this.repository = repository;
    }

    CartDto get(UUID id) {
        return repository.get(id).dto();
    }

    void create(UUID id) {
        Cart cart = repository.get(id);
        cart.create();
        repository.save(cart);
    }

    void addItem(UUID cartId, UUID itemId) {
        Cart cart = repository.get(cartId);
        cart.addItem(itemId);
        repository.save(cart);
    }

    void subtractItem(UUID cartId, UUID itemId) {
        Cart cart = repository.get(cartId);
        cart.subtractItem(itemId);
        repository.save(cart);
    }

    void changeAmountOfItems(UUID cartId, UUID itemId, Long newCount) {
        Cart cart = repository.get(cartId);
        cart.changeItemsCount(itemId, newCount);
        repository.save(cart);
    }

    void submit(UUID id) {
        Cart cart = repository.get(id);
        cart.submit();
        repository.save(cart);
    }
}
