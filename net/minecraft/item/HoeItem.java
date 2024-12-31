package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HoeItem extends Item {
	protected Item.ToolMaterialType material;

	public HoeItem(Item.ToolMaterialType toolMaterialType) {
		this.material = toolMaterialType;
		this.maxCount = 1;
		this.setMaxDamage(toolMaterialType.getMaxDurability());
		this.setItemGroup(ItemGroup.TOOLS);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (!player.canModify(pos.offset(direction), direction, itemStack)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (direction != Direction.DOWN && world.getBlockState(pos.up()).getBlock().getMaterial() == Material.AIR) {
				if (block == Blocks.GRASS) {
					return this.use(itemStack, player, world, pos, Blocks.FARMLAND.getDefaultState());
				}

				if (block == Blocks.DIRT) {
					switch ((DirtBlock.DirtType)blockState.get(DirtBlock.VARIANT)) {
						case DIRT:
							return this.use(itemStack, player, world, pos, Blocks.FARMLAND.getDefaultState());
						case COARSE_DIRT:
							return this.use(itemStack, player, world, pos, Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.DIRT));
					}
				}
			}

			return false;
		}
	}

	protected boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, BlockState state) {
		world.playSound(
			(double)((float)pos.getX() + 0.5F),
			(double)((float)pos.getY() + 0.5F),
			(double)((float)pos.getZ() + 0.5F),
			state.getBlock().sound.getStepSound(),
			(state.getBlock().sound.getVolume() + 1.0F) / 2.0F,
			state.getBlock().sound.getPitch() * 0.8F
		);
		if (world.isClient) {
			return true;
		} else {
			world.setBlockState(pos, state);
			stack.damage(1, player);
			return true;
		}
	}

	@Override
	public boolean isHandheld() {
		return true;
	}

	public String getAsString() {
		return this.material.toString();
	}
}
