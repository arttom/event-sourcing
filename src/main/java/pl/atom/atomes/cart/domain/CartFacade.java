package pl.atom.atomes.cart.domain;

import org.springframework.stereotype.Component;
import pl.atom.atomes.cart.dto.CartDto;

import java.util.UUID;

@Component
public class CartFacade {

    private final CartRepository repository;

    public CartFacade(CartRepository repository) {
        this.repository = repository;
    }

    public CartDto get(UUID id) {
        return repository.get(id).dto();
    }

    public void create(UUID id) {
        Cart cart = repository.get(id);
        cart.create();
        repository.save(cart);
    }

    public void addItem(UUID cartId, UUID itemId) {
        Cart cart = repository.get(cartId);
        cart.addItem(itemId);
        repository.save(cart);
    }

    public void subtractItem(UUID cartId, UUID itemId) {
        Cart cart = repository.get(cartId);
        cart.subtractItem(itemId);
        repository.save(cart);
    }

    public void changeAmountOfItems(UUID cartId, UUID itemId, Long newCount) {
        Cart cart = repository.get(cartId);
        cart.changeItemsCount(itemId, newCount);
        repository.save(cart);
    }

    public void submit(UUID id) {
        Cart cart = repository.get(id);
        cart.submit();
        repository.save(cart);
    }
}
