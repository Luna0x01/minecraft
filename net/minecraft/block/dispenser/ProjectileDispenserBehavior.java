package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public abstract class ProjectileDispenserBehavior extends ItemDispenserBehavior {
	@Override
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		World world = pointer.getWorld();
		Position position = DispenserBlock.getPosition(pointer);
		Direction direction = DispenserBlock.getDirection(pointer.getBlockStateData());
		Projectile projectile = this.createProjectile(world, position, stack);
		projectile.setVelocity(
			(double)direction.getOffsetX(), (double)((float)direction.getOffsetY() + 0.1F), (double)direction.getOffsetZ(), this.getForce(), this.getVariation()
		);
		world.spawnEntity((Entity)projectile);
		stack.split(1);
		return stack;
	}

	@Override
	protected void playSound(BlockPointer pointer) {
		pointer.getWorld().syncGlobalEvent(1002, pointer.getBlockPos(), 0);
	}

	protected abstract Projectile createProjectile(World world, Position pos, ItemStack stack);

	protected float getVariation() {
		return 6.0F;
	}

	protected float getForce() {
		return 1.1F;
	}
}
