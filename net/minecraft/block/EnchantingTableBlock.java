package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EnchantingTableBlock extends BlockWithEntity {
	protected static final Box field_12653 = new Box(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);

	protected EnchantingTableBlock() {
		super(Material.STONE, MaterialColor.RED);
		this.setOpacity(0);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12653;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		super.randomDisplayTick(state, world, pos, random);

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i > -2 && i < 2 && j == -1) {
					j = 2;
				}

				if (random.nextInt(16) == 0) {
					for (int k = 0; k <= 1; k++) {
						BlockPos blockPos = pos.add(i, k, j);
						if (world.getBlockState(blockPos).getBlock() == Blocks.BOOKSHELF) {
							if (!world.isAir(pos.add(i / 2, 0, j / 2))) {
								break;
							}

							world.addParticle(
								ParticleType.ENCHANTMENT_TABLE,
								(double)pos.getX() + 0.5,
								(double)pos.getY() + 2.0,
								(double)pos.getZ() + 0.5,
								(double)((float)i + random.nextFloat()) - 0.5,
								(double)((float)k - random.nextFloat() - 1.0F),
								(double)((float)j + random.nextFloat()) - 0.5
							);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new EnchantingTableBlockEntity();
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof EnchantingTableBlockEntity) {
				playerEntity.openHandledScreen((EnchantingTableBlockEntity)blockEntity);
			}

			return true;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof EnchantingTableBlockEntity) {
				((EnchantingTableBlockEntity)blockEntity).setCustomName(itemStack.getCustomName());
			}
		}
	}
}
