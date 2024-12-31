package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class ItemDispenserBehavior implements DispenserBehavior {
	@Override
	public final ItemStack dispense(BlockPointer pointer, ItemStack stack) {
		ItemStack itemStack = this.dispenseSilently(pointer, stack);
		this.playSound(pointer);
		this.spawnParticles(pointer, pointer.getBlockState().get(DispenserBlock.FACING));
		return itemStack;
	}

	protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
		Position position = DispenserBlock.getPosition(pointer);
		ItemStack itemStack = stack.split(1);
		spawnItem(pointer.getWorld(), itemStack, 6, direction, position);
		return stack;
	}

	public static void spawnItem(World world, ItemStack stack, int offset, Direction side, Position pos) {
		double d = pos.getX();
		double e = pos.getY();
		double f = pos.getZ();
		if (side.getAxis() == Direction.Axis.Y) {
			e -= 0.125;
		} else {
			e -= 0.15625;
		}

		ItemEntity itemEntity = new ItemEntity(world, d, e, f, stack);
		double g = world.random.nextDouble() * 0.1 + 0.2;
		itemEntity.velocityX = (double)side.getOffsetX() * g;
		itemEntity.velocityY = 0.2F;
		itemEntity.velocityZ = (double)side.getOffsetZ() * g;
		itemEntity.velocityX = itemEntity.velocityX + world.random.nextGaussian() * 0.0075F * (double)offset;
		itemEntity.velocityY = itemEntity.velocityY + world.random.nextGaussian() * 0.0075F * (double)offset;
		itemEntity.velocityZ = itemEntity.velocityZ + world.random.nextGaussian() * 0.0075F * (double)offset;
		world.spawnEntity(itemEntity);
	}

	protected void playSound(BlockPointer pointer) {
		pointer.getWorld().syncGlobalEvent(1000, pointer.getBlockPos(), 0);
	}

	protected void spawnParticles(BlockPointer pointer, Direction side) {
		pointer.getWorld().syncGlobalEvent(2000, pointer.getBlockPos(), this.getDataFromDirection(side));
	}

	private int getDataFromDirection(Direction dir) {
		return dir.getOffsetX() + 1 + (dir.getOffsetZ() + 1) * 3;
	}
}
