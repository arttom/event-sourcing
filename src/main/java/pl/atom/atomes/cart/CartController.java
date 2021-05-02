package pl.atom.atomes.cart;

import org.springframework.web.bind.annotation.*;
import pl.atom.atomes.cart.domain.CartFacade;
import pl.atom.atomes.cart.dto.CartDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartFacade cartFacade;

    public CartController(CartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    @GetMapping("{id}")
    CartDto get(@PathVariable("id") String id) {
        return cartFacade.get(UUID.fromString(id));
    }

    @PostMapping("{id}")
    void create(@PathVariable("id") String id) {
        cartFacade.create(UUID.fromString(id));
    }

    @PostMapping("{cartId}/item/{itemId}")
    void addItem(@PathVariable("cartId") String cartId, @PathVariable("itemId") String itemId) {
        cartFacade.addItem(UUID.fromString(cartId), UUID.fromString(itemId));
    }

    @DeleteMapping("{cartId}/item/{itemId}")
    void subtractItem(@PathVariable("cartId") String cartId, @PathVariable("itemId") String itemId) {
        cartFacade.subtractItem(UUID.fromString(cartId), UUID.fromString(itemId));
    }

    @PutMapping("{cartId}/item/{itemId}")
    void setItemCount(@PathVariable("cartId") String cartId, @PathVariable("itemId") String itemId, @RequestBody int count) {
        cartFacade.changeAmountOfItems(UUID.fromString(cartId), UUID.fromString(itemId), (long) count);
    }

    @PostMapping("{id}/submit")
    void submit(@PathVariable("id") String id) {
        cartFacade.submit(UUID.fromString(id));
    }
}
