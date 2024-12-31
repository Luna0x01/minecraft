package net.minecraft.client;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.class_4235;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;

public class class_2884 implements class_4235 {
	private final Iterable<? extends class_4235> field_13590;

	public class_2884(Iterable<? extends class_4235> iterable) {
		this.field_13590 = iterable;
	}

	@Override
	public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> stateManager) {
		List<Predicate<BlockState>> list = (List<Predicate<BlockState>>)Streams.stream(this.field_13590)
			.map(arg -> arg.getPredicate(stateManager))
			.collect(Collectors.toList());
		return blockState -> list.stream().anyMatch(predicate -> predicate.test(blockState));
	}
}
