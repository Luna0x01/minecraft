package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SimpleBlockFeatureConfig implements FeatureConfig {
	public final BlockState toPlace;
	public final List<BlockState> placeOn;
	public final List<BlockState> placeIn;
	public final List<BlockState> placeUnder;

	public SimpleBlockFeatureConfig(BlockState blockState, List<BlockState> list, List<BlockState> list2, List<BlockState> list3) {
		this.toPlace = blockState;
		this.placeOn = list;
		this.placeIn = list2;
		this.placeUnder = list3;
	}

	public SimpleBlockFeatureConfig(BlockState blockState, BlockState[] blockStates, BlockState[] blockStates2, BlockState[] blockStates3) {
		this(blockState, Lists.newArrayList(blockStates), Lists.newArrayList(blockStates2), Lists.newArrayList(blockStates3));
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		T object = (T)BlockState.serialize(dynamicOps, this.toPlace).getValue();
		T object2 = (T)dynamicOps.createList(this.placeOn.stream().map(blockState -> BlockState.serialize(dynamicOps, blockState).getValue()));
		T object3 = (T)dynamicOps.createList(this.placeIn.stream().map(blockState -> BlockState.serialize(dynamicOps, blockState).getValue()));
		T object4 = (T)dynamicOps.createList(this.placeUnder.stream().map(blockState -> BlockState.serialize(dynamicOps, blockState).getValue()));
		return new Dynamic(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("to_place"),
					object,
					dynamicOps.createString("place_on"),
					object2,
					dynamicOps.createString("place_in"),
					object3,
					dynamicOps.createString("place_under"),
					object4
				)
			)
		);
	}

	public static <T> SimpleBlockFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("to_place").map(BlockState::deserialize).orElse(Blocks.field_10124.getDefaultState());
		List<BlockState> list = dynamic.get("place_on").asList(BlockState::deserialize);
		List<BlockState> list2 = dynamic.get("place_in").asList(BlockState::deserialize);
		List<BlockState> list3 = dynamic.get("place_under").asList(BlockState::deserialize);
		return new SimpleBlockFeatureConfig(blockState, list, list2, list3);
	}
}
