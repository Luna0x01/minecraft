package net.minecraft.item;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MinecartItem extends Item {
	private static final DispenserBehavior MINECART_BEHAVIOR = new ItemDispenserBehavior() {
		private final ItemDispenserBehavior behavior = new ItemDispenserBehavior();

		@Override
		public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			Direction direction = DispenserBlock.getDirection(pointer.getBlockStateData());
			World world = pointer.getWorld();
			double d = pointer.getX() + (double)direction.getOffsetX() * 1.125;
			double e = Math.floor(pointer.getY()) + (double)direction.getOffsetY();
			double f = pointer.getZ() + (double)direction.getOffsetZ() * 1.125;
			BlockPos blockPos = pointer.getBlockPos().offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			AbstractRailBlock.RailShapeType railShapeType = blockState.getBlock() instanceof AbstractRailBlock
				? blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty())
				: AbstractRailBlock.RailShapeType.NORTH_SOUTH;
			double g;
			if (AbstractRailBlock.isRail(blockState)) {
				if (railShapeType.isAscending()) {
					g = 0.6;
				} else {
					g = 0.1;
				}
			} else {
				if (blockState.getBlock().getMaterial() != Material.AIR || !AbstractRailBlock.isRail(world.getBlockState(blockPos.down()))) {
					return this.behavior.dispense(pointer, stack);
				}

				BlockState blockState2 = world.getBlockState(blockPos.down());
				AbstractRailBlock.RailShapeType railShapeType2 = blockState2.getBlock() instanceof AbstractRailBlock
					? blockState2.get(((AbstractRailBlock)blockState2.getBlock()).getShapeProperty())
					: AbstractRailBlock.RailShapeType.NORTH_SOUTH;
				if (direction != Direction.DOWN && railShapeType2.isAscending()) {
					g = -0.4;
				} else {
					g = -0.9;
				}
			}

			AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.createMinecart(world, d, e + g, f, ((MinecartItem)stack.getItem()).minecartType);
			if (stack.hasCustomName()) {
				abstractMinecartEntity.setCustomName(stack.getCustomName());
			}

			world.spawnEntity(abstractMinecartEntity);
			stack.split(1);
			return stack;
		}

		@Override
		protected void playSound(BlockPointer pointer) {
			pointer.getWorld().syncGlobalEvent(1000, pointer.getBlockPos(), 0);
		}
	};
	private final AbstractMinecartEntity.Type minecartType;

	public MinecartItem(AbstractMinecartEntity.Type type) {
		this.maxCount = 1;
		this.minecartType = type;
		this.setItemGroup(ItemGroup.TRANSPORTATION);
		DispenserBlock.SPECIAL_ITEMS.put(this, MINECART_BEHAVIOR);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		BlockState blockState = world.getBlockState(pos);
		if (AbstractRailBlock.isRail(blockState)) {
			if (!world.isClient) {
				AbstractRailBlock.RailShapeType railShapeType = blockState.getBlock() instanceof AbstractRailBlock
					? blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty())
					: AbstractRailBlock.RailShapeType.NORTH_SOUTH;
				double d = 0.0;
				if (railShapeType.isAscending()) {
					d = 0.5;
				}

				AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.createMinecart(
					world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.0625 + d, (double)pos.getZ() + 0.5, this.minecartType
				);
				if (itemStack.hasCustomName()) {
					abstractMinecartEntity.setCustomName(itemStack.getCustomName());
				}

				world.spawnEntity(abstractMinecartEntity);
			}

			itemStack.count--;
			return true;
		} else {
			return false;
		}
	}
}
