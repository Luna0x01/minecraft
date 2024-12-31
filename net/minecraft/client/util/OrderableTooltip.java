package net.minecraft.client.util;

import java.util.List;
import java.util.Optional;
import net.minecraft.text.OrderedText;

public interface OrderableTooltip {
	Optional<List<OrderedText>> getOrderedTooltip();
}
