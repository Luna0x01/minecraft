package net.minecraft.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;

public class EndCrystalEntity extends Entity {
	public int endCrystalAge;
	public int field_3738;

	public EndCrystalEntity(World world) {
		super(world);
		this.inanimate = true;
		this.setBounds(2.0F, 2.0F);
		this.field_3738 = 5;
		this.endCrystalAge = this.random.nextInt(100000);
	}

	public EndCrystalEntity(World world, double d, double e, double f) {
		this(world);
		this.updatePosition(d, e, f);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.track(8, this.field_3738);
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.endCrystalAge++;
		this.dataTracker.setProperty(8, this.field_3738);
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y);
		int k = MathHelper.floor(this.z);
		if (this.world.dimension instanceof TheEndDimension && this.world.getBlockState(new BlockPos(i, j, k)).getBlock() != Blocks.FIRE) {
			this.world.setBlockState(new BlockPos(i, j, k), Blocks.FIRE.getDefaultState());
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (!this.removed && !this.world.isClient) {
				this.field_3738 = 0;
				if (this.field_3738 <= 0) {
					this.remove();
					if (!this.world.isClient) {
						this.world.createExplosion(null, this.x, this.y, this.z, 6.0F, true);
					}
				}
			}

			return true;
		}
	}
}
