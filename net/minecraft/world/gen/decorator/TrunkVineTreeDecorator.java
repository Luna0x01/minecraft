package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class TrunkVineTreeDecorator extends TreeDecorator {
	public TrunkVineTreeDecorator() {
		super(TreeDecoratorType.field_21320);
	}

	public <T> TrunkVineTreeDecorator(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public void generate(IWorld iWorld, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox blockBox) {
		list.forEach(blockPos -> {
			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.west();
				if (AbstractTreeFeature.isAir(iWorld, blockPos2)) {
					this.method_23471(iWorld, blockPos2, VineBlock.EAST, set, blockBox);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos3 = blockPos.east();
				if (AbstractTreeFeature.isAir(iWorld, blockPos3)) {
					this.method_23471(iWorld, blockPos3, VineBlock.WEST, set, blockBox);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos4 = blockPos.north();
				if (AbstractTreeFeature.isAir(iWorld, blockPos4)) {
					this.method_23471(iWorld, blockPos4, VineBlock.SOUTH, set, blockBox);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos5 = blockPos.south();
				if (AbstractTreeFeature.isAir(iWorld, blockPos5)) {
					this.method_23471(iWorld, blockPos5, VineBlock.NORTH, set, blockBox);
				}
			}
		});
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return (T)new Dynamic(
				dynamicOps,
				dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString(Registry.field_21448.getId(this.field_21319).toString())))
			)
			.getValue();
	}
}
