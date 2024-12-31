package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;

public interface class_4235 {
	class_4235 TRUE = stateManager -> blockState -> true;
	class_4235 FALSE = stateManager -> blockState -> false;

	Predicate<BlockState> getPredicate(StateManager<Block, BlockState> stateManager);
}
