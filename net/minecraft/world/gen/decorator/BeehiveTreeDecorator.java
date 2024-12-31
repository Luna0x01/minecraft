package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class BeehiveTreeDecorator extends TreeDecorator {
	private final float chance;

	public BeehiveTreeDecorator(float f) {
		super(TreeDecoratorType.field_21323);
		this.chance = f;
	}

	public <T> BeehiveTreeDecorator(Dynamic<T> dynamic) {
		this(dynamic.get("probability").asFloat(0.0F));
	}

	@Override
	public void generate(IWorld iWorld, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox blockBox) {
		if (!(random.nextFloat() >= this.chance)) {
			Direction direction = BeehiveBlock.GENERATE_DIRECTIONS[random.nextInt(BeehiveBlock.GENERATE_DIRECTIONS.length)];
			int i = !list2.isEmpty()
				? Math.max(((BlockPos)list2.get(0)).getY() - 1, ((BlockPos)list.get(0)).getY())
				: Math.min(((BlockPos)list.get(0)).getY() + 1 + random.nextInt(3), ((BlockPos)list.get(list.size() - 1)).getY());
			List<BlockPos> list3 = (List<BlockPos>)list.stream().filter(blockPosx -> blockPosx.getY() == i).collect(Collectors.toList());
			if (!list3.isEmpty()) {
				BlockPos blockPos = (BlockPos)list3.get(random.nextInt(list3.size()));
				BlockPos blockPos2 = blockPos.offset(direction);
				if (AbstractTreeFeature.isAir(iWorld, blockPos2) && AbstractTreeFeature.isAir(iWorld, blockPos2.offset(Direction.field_11035))) {
					BlockState blockState = Blocks.field_20421.getDefaultState().with(BeehiveBlock.FACING, Direction.field_11035);
					this.method_23470(iWorld, blockPos2, blockState, set, blockBox);
					BlockEntity blockEntity = iWorld.getBlockEntity(blockPos2);
					if (blockEntity instanceof BeehiveBlockEntity) {
						BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
						int j = 2 + random.nextInt(2);

						for (int k = 0; k < j; k++) {
							BeeEntity beeEntity = new BeeEntity(EntityType.field_20346, iWorld.getWorld());
							beehiveBlockEntity.tryEnterHive(beeEntity, false, random.nextInt(599));
						}
					}
				}
			}
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return (T)new Dynamic(
				dynamicOps,
				dynamicOps.createMap(
					ImmutableMap.of(
						dynamicOps.createString("type"),
						dynamicOps.createString(Registry.field_21448.getId(this.field_21319).toString()),
						dynamicOps.createString("probability"),
						dynamicOps.createFloat(this.chance)
					)
				)
			)
			.getValue();
	}
}
