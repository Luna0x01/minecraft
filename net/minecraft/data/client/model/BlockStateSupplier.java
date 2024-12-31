package net.minecraft.data.client.model;

import com.google.gson.JsonElement;
import java.util.function.Supplier;
import net.minecraft.block.Block;

public interface BlockStateSupplier extends Supplier<JsonElement> {
	Block getBlock();
}
