package pl.atom.atomes.cart.error;


public class CartOperationException extends RuntimeException {

    private CartOperationException(String message) {
        super(message);
    }

    public static CartOperationException alreadyCreated() {
        return new CartOperationException("Cart is already created");
    }

    public static CartOperationException itemExceedsMax(int maxItems) {
        return new CartOperationException("Cannot add item to cart. It would exceed max: " + maxItems);
    }

    public static CartOperationException cannotRemoveEmptyItem() {
        return new CartOperationException("Cannot remove item from cart. It is not there");
    }

    public static CartOperationException negativeCount() {
        return new CartOperationException("Cannot set negative item count");
    }

    public static CartOperationException cannotEditSubmitted() {
        return new CartOperationException("Cannot edit Submitted cart");
    }

    public static CartOperationException cannotSubmitEmpty() {
        return new CartOperationException("Cannot submit empty cart");
    }

    public static CartOperationException notCreated() {
        return new CartOperationException("Cart is not created");
    }
}
