package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.block.BlockMaterialPredicate;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class class_3737 extends SkullBlock {
	private static BlockPattern field_18586;
	private static BlockPattern field_18587;

	protected class_3737(Block.Builder builder) {
		super(SkullBlock.class_3723.WITHER_SKELETON, builder);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof SkullBlockEntity) {
			method_16769(world, pos, (SkullBlockEntity)blockEntity);
		}
	}

	public static void method_16769(World world, BlockPos blockPos, SkullBlockEntity skullBlockEntity) {
		Block block = skullBlockEntity.method_16783().getBlock();
		boolean bl = block == Blocks.WITHER_SKELETON_SKULL || block == Blocks.WITHER_SKELETON_WALL_SKULL;
		if (bl && blockPos.getY() >= 2 && world.method_16346() != Difficulty.PEACEFUL && !world.isClient) {
			BlockPattern blockPattern = method_16771();
			BlockPattern.Result result = blockPattern.method_16938(world, blockPos);
			if (result != null) {
				for (int i = 0; i < 3; i++) {
					SkullBlockEntity.method_16840(world, result.translate(i, 0, 0).getPos());
				}

				for (int j = 0; j < blockPattern.getWidth(); j++) {
					for (int k = 0; k < blockPattern.getHeight(); k++) {
						world.setBlockState(result.translate(j, k, 0).getPos(), Blocks.AIR.getDefaultState(), 2);
					}
				}

				BlockPos blockPos2 = result.translate(1, 0, 0).getPos();
				WitherEntity witherEntity = new WitherEntity(world);
				BlockPos blockPos3 = result.translate(1, 2, 0).getPos();
				witherEntity.refreshPositionAndAngles(
					(double)blockPos3.getX() + 0.5,
					(double)blockPos3.getY() + 0.55,
					(double)blockPos3.getZ() + 0.5,
					result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F,
					0.0F
				);
				witherEntity.bodyYaw = result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
				witherEntity.onSummoned();

				for (ServerPlayerEntity serverPlayerEntity : world.getEntitiesInBox(ServerPlayerEntity.class, witherEntity.getBoundingBox().expand(50.0))) {
					AchievementsAndCriterions.field_16341.method_14397(serverPlayerEntity, witherEntity);
				}

				world.method_3686(witherEntity);

				for (int l = 0; l < 120; l++) {
					world.method_16343(
						class_4342.field_21355,
						(double)blockPos2.getX() + world.random.nextDouble(),
						(double)(blockPos2.getY() - 2) + world.random.nextDouble() * 3.9,
						(double)blockPos2.getZ() + world.random.nextDouble(),
						0.0,
						0.0,
						0.0
					);
				}

				for (int m = 0; m < blockPattern.getWidth(); m++) {
					for (int n = 0; n < blockPattern.getHeight(); n++) {
						world.method_16342(result.translate(m, n, 0).getPos(), Blocks.AIR);
					}
				}
			}
		}
	}

	public static boolean method_16770(World world, BlockPos blockPos, ItemStack itemStack) {
		return itemStack.getItem() == Items.WITHER_SKELETON_SKULL && blockPos.getY() >= 2 && world.method_16346() != Difficulty.PEACEFUL && !world.isClient
			? method_16772().method_16938(world, blockPos) != null
			: false;
	}

	protected static BlockPattern method_16771() {
		if (field_18586 == null) {
			field_18586 = BlockPatternBuilder.start()
				.aisle("^^^", "###", "~#~")
				.method_16940('#', CachedBlockPosition.method_16935(BlockStatePredicate.create(Blocks.SOULSAND)))
				.method_16940(
					'^',
					CachedBlockPosition.method_16935(
						BlockStatePredicate.create(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.create(Blocks.WITHER_SKELETON_WALL_SKULL))
					)
				)
				.method_16940('~', CachedBlockPosition.method_16935(BlockMaterialPredicate.create(Material.AIR)))
				.build();
		}

		return field_18586;
	}

	protected static BlockPattern method_16772() {
		if (field_18587 == null) {
			field_18587 = BlockPatternBuilder.start()
				.aisle("   ", "###", "~#~")
				.method_16940('#', CachedBlockPosition.method_16935(BlockStatePredicate.create(Blocks.SOULSAND)))
				.method_16940('~', CachedBlockPosition.method_16935(BlockMaterialPredicate.create(Material.AIR)))
				.build();
		}

		return field_18587;
	}
}
