package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EnchantingTableBlock extends BlockWithEntity {
	protected EnchantingTableBlock() {
		super(Material.STONE, MaterialColor.RED);
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		this.setOpacity(0);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		super.randomDisplayTick(world, pos, state, rand);

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i > -2 && i < 2 && j == -1) {
					j = 2;
				}

				if (rand.nextInt(16) == 0) {
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
								(double)((float)i + rand.nextFloat()) - 0.5,
								(double)((float)k - rand.nextFloat() - 1.0F),
								(double)((float)j + rand.nextFloat()) - 0.5
							);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public int getBlockType() {
		return 3;
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new EnchantingTableBlockEntity();
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof EnchantingTableBlockEntity) {
				player.openHandledScreen((EnchantingTableBlockEntity)blockEntity);
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
