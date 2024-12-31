package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ChestScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChestMinecartEntity extends StorageMinecartEntity {
	public ChestMinecartEntity(World world) {
		super(EntityType.CHEST_MINECART, world);
	}

	public ChestMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.CHEST_MINECART, d, e, f, world);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.method_15560(Blocks.CHEST);
		}
	}

	@Override
	public int getInvSize() {
		return 27;
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.CHEST;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.CHEST.getDefaultState().withProperty(ChestBlock.FACING, Direction.NORTH);
	}

	@Override
	public int getDefaultBlockOffset() {
		return 8;
	}

	@Override
	public String getId() {
		return "minecraft:chest";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		this.generateLoot(player);
		return new ChestScreenHandler(inventory, this, player);
	}
}
