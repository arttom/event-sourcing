package pl.atom.atomes.cart.domain;

import pl.atom.atomes.aggregate.Aggregate;
import pl.atom.atomes.aggregate.DomainEvent;
import pl.atom.atomes.cart.dto.CartDto;
import pl.atom.atomes.cart.error.CartOperationException;

import java.util.*;

class Cart extends Aggregate {

    enum Status {
        CREATED, IN_PROGRESS, SUBMITTED
    }

    private static final int MAX_ITEMS = 10;
    private Status status;
    private Map<UUID, Long> items;

    public Cart(UUID id) {
        super(id);
    }

    public static Cart rebuild(UUID id, List<DomainEvent> events) {
        Cart cart = new Cart(id);
        cart.rebuild(events);
        return cart;
    }

    public CartDto dto() {
        return new CartDto(getId(), new HashMap<>(items));
    }

    void create() {
        if (getVersion() > 0 || status != null) {
            throw CartOperationException.alreadyCreated();
        }
        handle(new CartEvent.CartCreated(getId(), getVersion() + 1));
    }

    void addItem(UUID itemId) {
        if (status == null) {
            throw CartOperationException.notCreated();
        }
        if (status == Status.SUBMITTED) {
            throw CartOperationException.cannotEditSubmitted();
        }
        if (items.getOrDefault(itemId, 0L) >= MAX_ITEMS) {
            throw CartOperationException.itemExceedsMax(MAX_ITEMS);
        }
        handle(new CartEvent.ItemAdded(getId(), getVersion() + 1, itemId));
    }

    public void subtractItem(UUID itemId) {
        if (status == Status.SUBMITTED) {
            throw CartOperationException.cannotEditSubmitted();
        }

        if (items.getOrDefault(itemId, 0L) < 1) {
            throw CartOperationException.cannotRemoveEmptyItem();
        }

        if (items.get(itemId) == 1) {
            handle(new CartEvent.ItemRemoved(getId(), getVersion() + 1, itemId));
        } else {
            handle(new CartEvent.ItemSubtracted(getId(), getVersion() + 1, itemId));
        }
    }

    public void changeItemsCount(UUID itemId, Long newCount) {
        if (status == Status.SUBMITTED) {
            throw CartOperationException.cannotEditSubmitted();
        }

        if (newCount > MAX_ITEMS) {
            throw CartOperationException.itemExceedsMax(MAX_ITEMS);
        }
        if (newCount < 0) {
            throw CartOperationException.negativeCount();
        }

        if (newCount == 0 && !items.containsKey(itemId)) {
            throw CartOperationException.cannotRemoveEmptyItem();
        }
        if (newCount == 0) {
            handle(new CartEvent.ItemRemoved(getId(), getVersion() + 1, itemId));
        } else {
            handle(new CartEvent.ItemCountChange(getId(), getVersion() + 1, itemId, newCount));
        }
    }

    public void submit() {
        if (status == Status.SUBMITTED) {
            throw CartOperationException.cannotEditSubmitted();
        }

        if (items.isEmpty()) {
            throw CartOperationException.cannotSubmitEmpty();
        }
        handle(new CartEvent.CartSubmitted(getId(), getVersion() + 1));
    }

    protected void apply(DomainEvent domainEvent) {
        if (domainEvent instanceof CartEvent.CartCreated created) {
            applyCreated(created);
        } else if (domainEvent instanceof CartEvent.ItemAdded itemAdded) {
            applyItemAdded(itemAdded);
        } else if (domainEvent instanceof CartEvent.ItemSubtracted itemSubtracted) {
            applyItemSubtracted(itemSubtracted);
        } else if (domainEvent instanceof CartEvent.ItemCountChange countChange) {
            applyItemCountChange(countChange);
        } else if (domainEvent instanceof CartEvent.ItemRemoved itemRemoved) {
            applyItemRemoved(itemRemoved);
        } else if (domainEvent instanceof CartEvent.CartSubmitted submitted) {
            applySubmitted(submitted);
        }
    }

    private void applySubmitted(CartEvent.CartSubmitted event) {
        setVersion(event.version());
        status = Status.SUBMITTED;
    }

    private void applyItemRemoved(CartEvent.ItemRemoved event) {
        setVersion(event.version());
        items.remove(event.itemId());
    }

    private void applyItemCountChange(CartEvent.ItemCountChange event) {
        setVersion(event.version());
        items.put(event.itemId(), event.newCount());
    }

    private void applyItemSubtracted(CartEvent.ItemSubtracted event) {
        setVersion(event.version());
        Long itemCount = items.get(event.itemId());
        items.put(event.itemId(), itemCount - 1);
    }

    private void applyItemAdded(CartEvent.ItemAdded event) {
        setVersion(event.version());
        status = Status.IN_PROGRESS;
        Long itemCount = items.getOrDefault(event.itemId(), 0L);
        items.put(event.itemId(), itemCount + 1);
    }

    private void applyCreated(CartEvent.CartCreated event) {
        setVersion(event.version());
        this.status = Status.CREATED;
        items = new HashMap<>();
    }
}
