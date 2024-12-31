package net.minecraft.entity.vehicle;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.HopperProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HopperMinecartEntity extends StorageMinecartEntity implements HopperProvider {
	private boolean enabled = true;
	private int transferCooldown = -1;
	private final BlockPos currentBlockPos = BlockPos.ORIGIN;

	public HopperMinecartEntity(World world) {
		super(world);
	}

	public HopperMinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.HOPPER;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.HOPPER.getDefaultState();
	}

	@Override
	public int getDefaultBlockOffset() {
		return 1;
	}

	@Override
	public int getInvSize() {
		return 5;
	}

	@Override
	public boolean method_6100(PlayerEntity playerEntity, @Nullable ItemStack itemStack, Hand hand) {
		if (!this.world.isClient) {
			playerEntity.openInventory(this);
		}

		return true;
	}

	@Override
	public void onActivatorRail(int x, int y, int z, boolean powered) {
		boolean bl = !powered;
		if (bl != this.isEnabled()) {
			this.setEnabled(bl);
		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public World getEntityWorld() {
		return this.world;
	}

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y + 0.5;
	}

	@Override
	public double getZ() {
		return this.z;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient && this.isAlive() && this.isEnabled()) {
			BlockPos blockPos = new BlockPos(this);
			if (blockPos.equals(this.currentBlockPos)) {
				this.transferCooldown--;
			} else {
				this.setTransferCooldown(0);
			}

			if (!this.isCoolingDown()) {
				this.setTransferCooldown(0);
				if (this.canOperate()) {
					this.setTransferCooldown(4);
					this.markDirty();
				}
			}
		}
	}

	public boolean canOperate() {
		if (HopperBlockEntity.extract(this)) {
			return true;
		} else {
			List<ItemEntity> list = this.world.getEntitiesInBox(ItemEntity.class, this.getBoundingBox().expand(0.25, 0.0, 0.25), EntityPredicate.VALID_ENTITY);
			if (!list.isEmpty()) {
				HopperBlockEntity.extract(this, (ItemEntity)list.get(0));
			}

			return false;
		}
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.dropItem(Item.fromBlock(Blocks.HOPPER), 1, 0.0F);
		}
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		StorageMinecartEntity.method_13305(dataFixer, "MinecartHopper");
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("TransferCooldown", this.transferCooldown);
		nbt.putBoolean("Enabled", this.enabled);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.transferCooldown = nbt.getInt("TransferCooldown");
		this.enabled = nbt.contains("Enabled") ? nbt.getBoolean("Enabled") : true;
	}

	public void setTransferCooldown(int cooldown) {
		this.transferCooldown = cooldown;
	}

	public boolean isCoolingDown() {
		return this.transferCooldown > 0;
	}

	@Override
	public String getId() {
		return "minecraft:hopper";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		return new HopperScreenHandler(inventory, this, player);
	}
}
