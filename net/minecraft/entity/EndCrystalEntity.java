package net.minecraft.entity;

import com.google.common.base.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.DragonRespawnAnimation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;

public class EndCrystalEntity extends Entity {
	private static final TrackedData<Optional<BlockPos>> BEAM_TARGET = DataTracker.registerData(
		EndCrystalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS
	);
	private static final TrackedData<Boolean> SHOW_BOTTOM = DataTracker.registerData(EndCrystalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public int endCrystalAge;

	public EndCrystalEntity(World world) {
		super(world);
		this.inanimate = true;
		this.setBounds(2.0F, 2.0F);
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
		this.getDataTracker().startTracking(BEAM_TARGET, Optional.absent());
		this.getDataTracker().startTracking(SHOW_BOTTOM, true);
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.endCrystalAge++;
		if (!this.world.isClient) {
			BlockPos blockPos = new BlockPos(this);
			if (this.world.dimension instanceof TheEndDimension && this.world.getBlockState(blockPos).getBlock() != Blocks.FIRE) {
				this.world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
			}
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (this.getBeamTarget() != null) {
			nbt.put("BeamTarget", NbtHelper.fromBlockPos(this.getBeamTarget()));
		}

		nbt.putBoolean("ShowBottom", this.shouldShowBottom());
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("BeamTarget", 10)) {
			this.setBeamTarget(NbtHelper.toBlockPos(nbt.getCompound("BeamTarget")));
		}

		if (nbt.contains("ShowBottom", 1)) {
			this.setShowBottom(nbt.getBoolean("ShowBottom"));
		}
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (source.getAttacker() instanceof EnderDragonEntity) {
			return false;
		} else {
			if (!this.removed && !this.world.isClient) {
				this.remove();
				if (!this.world.isClient) {
					if (!source.isExplosive()) {
						this.world.createExplosion(null, this.x, this.y, this.z, 6.0F, true);
					}

					this.crystalDestroyed(source);
				}
			}

			return true;
		}
	}

	@Override
	public void kill() {
		this.crystalDestroyed(DamageSource.GENERIC);
		super.kill();
	}

	private void crystalDestroyed(DamageSource source) {
		if (this.world.dimension instanceof TheEndDimension) {
			TheEndDimension theEndDimension = (TheEndDimension)this.world.dimension;
			DragonRespawnAnimation dragonRespawnAnimation = theEndDimension.method_11818();
			if (dragonRespawnAnimation != null) {
				dragonRespawnAnimation.onEndCrystalDestroyed(this, source);
			}
		}
	}

	public void setBeamTarget(@Nullable BlockPos beamTarget) {
		this.getDataTracker().set(BEAM_TARGET, Optional.fromNullable(beamTarget));
	}

	@Nullable
	public BlockPos getBeamTarget() {
		return (BlockPos)this.getDataTracker().get(BEAM_TARGET).orNull();
	}

	public void setShowBottom(boolean showBottom) {
		this.getDataTracker().set(SHOW_BOTTOM, showBottom);
	}

	public boolean shouldShowBottom() {
		return this.getDataTracker().get(SHOW_BOTTOM);
	}

	@Override
	public boolean shouldRender(double distance) {
		return super.shouldRender(distance) || this.getBeamTarget() != null;
	}
}
