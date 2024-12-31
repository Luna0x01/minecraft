package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemUsageContext {
	protected final PlayerEntity field_17400;
	protected final float field_17401;
	protected final float field_17402;
	protected final float field_17403;
	protected final Direction field_17404;
	protected final World field_17405;
	protected final ItemStack field_17406;
	protected final BlockPos field_17407;

	public ItemUsageContext(PlayerEntity playerEntity, ItemStack itemStack, BlockPos blockPos, Direction direction, float f, float g, float h) {
		this(playerEntity.world, playerEntity, itemStack, blockPos, direction, f, g, h);
	}

	protected ItemUsageContext(
		World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockPos blockPos, Direction direction, float f, float g, float h
	) {
		this.field_17400 = playerEntity;
		this.field_17404 = direction;
		this.field_17401 = f;
		this.field_17402 = g;
		this.field_17403 = h;
		this.field_17407 = blockPos;
		this.field_17406 = itemStack;
		this.field_17405 = world;
	}

	public BlockPos getBlockPos() {
		return this.field_17407;
	}

	public ItemStack getItemStack() {
		return this.field_17406;
	}

	@Nullable
	public PlayerEntity getPlayer() {
		return this.field_17400;
	}

	public World getWorld() {
		return this.field_17405;
	}

	public Direction method_16151() {
		return this.field_17404;
	}

	public float method_16152() {
		return this.field_17401;
	}

	public float method_16153() {
		return this.field_17402;
	}

	public float method_16154() {
		return this.field_17403;
	}

	public Direction method_16145() {
		return this.field_17400 == null ? Direction.NORTH : this.field_17400.getHorizontalDirection();
	}

	public boolean method_16146() {
		return this.field_17400 != null && this.field_17400.isSneaking();
	}

	public float method_16147() {
		return this.field_17400 == null ? 0.0F : this.field_17400.yaw;
	}
}
