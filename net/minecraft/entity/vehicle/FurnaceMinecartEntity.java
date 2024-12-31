package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FurnaceMinecartEntity extends AbstractMinecartEntity {
	private int fuel;
	public double pushX;
	public double pushZ;

	public FurnaceMinecartEntity(World world) {
		super(world);
	}

	public FurnaceMinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.FURNACE;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(16, new Byte((byte)0));
	}

	@Override
	public void tick() {
		super.tick();
		if (this.fuel > 0) {
			this.fuel--;
		}

		if (this.fuel <= 0) {
			this.pushX = this.pushZ = 0.0;
		}

		this.setLit(this.fuel > 0);
		if (this.isLit() && this.random.nextInt(4) == 0) {
			this.world.addParticle(ParticleType.SMOKE_LARGE, this.x, this.y + 0.8, this.z, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected double getMaxOffRailSpeed() {
		return 0.2;
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.dropItem(new ItemStack(Blocks.FURNACE, 1), 0.0F);
		}
	}

	@Override
	protected void moveOnRail(BlockPos pos, BlockState state) {
		super.moveOnRail(pos, state);
		double d = this.pushX * this.pushX + this.pushZ * this.pushZ;
		if (d > 1.0E-4 && this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 0.001) {
			d = (double)MathHelper.sqrt(d);
			this.pushX /= d;
			this.pushZ /= d;
			if (this.pushX * this.velocityX + this.pushZ * this.velocityZ < 0.0) {
				this.pushX = 0.0;
				this.pushZ = 0.0;
			} else {
				double e = d / this.getMaxOffRailSpeed();
				this.pushX *= e;
				this.pushZ *= e;
			}
		}
	}

	@Override
	protected void applySlowdown() {
		double d = this.pushX * this.pushX + this.pushZ * this.pushZ;
		if (d > 1.0E-4) {
			d = (double)MathHelper.sqrt(d);
			this.pushX /= d;
			this.pushZ /= d;
			double e = 1.0;
			this.velocityX *= 0.8F;
			this.velocityY *= 0.0;
			this.velocityZ *= 0.8F;
			this.velocityX = this.velocityX + this.pushX * e;
			this.velocityZ = this.velocityZ + this.pushZ * e;
		} else {
			this.velocityX *= 0.98F;
			this.velocityY *= 0.0;
			this.velocityZ *= 0.98F;
		}

		super.applySlowdown();
	}

	@Override
	public boolean openInventory(PlayerEntity player) {
		ItemStack itemStack = player.inventory.getMainHandStack();
		if (itemStack != null && itemStack.getItem() == Items.COAL) {
			if (!player.abilities.creativeMode && --itemStack.count == 0) {
				player.inventory.setInvStack(player.inventory.selectedSlot, null);
			}

			this.fuel += 3600;
		}

		this.pushX = this.x - player.x;
		this.pushZ = this.z - player.z;
		return true;
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putDouble("PushX", this.pushX);
		nbt.putDouble("PushZ", this.pushZ);
		nbt.putShort("Fuel", (short)this.fuel);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.pushX = nbt.getDouble("PushX");
		this.pushZ = nbt.getDouble("PushZ");
		this.fuel = nbt.getShort("Fuel");
	}

	protected boolean isLit() {
		return (this.dataTracker.getByte(16) & 1) != 0;
	}

	protected void setLit(boolean lit) {
		if (lit) {
			this.dataTracker.setProperty(16, (byte)(this.dataTracker.getByte(16) | 1));
		} else {
			this.dataTracker.setProperty(16, (byte)(this.dataTracker.getByte(16) & -2));
		}
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return (this.isLit() ? Blocks.LIT_FURNACE : Blocks.FURNACE).getDefaultState().with(FurnaceBlock.FACING, Direction.NORTH);
	}
}
