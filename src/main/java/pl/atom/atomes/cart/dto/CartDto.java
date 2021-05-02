package pl.atom.atomes.cart.dto;

import java.util.Map;
import java.util.UUID;

public record CartDto(UUID id, Map<UUID, Long> items) {
}
