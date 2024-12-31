package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_3741 extends BlockEntity implements Tickable {
	private static final Block[] field_18626 = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
	public int field_18625;
	private float field_18627;
	private boolean field_18628;
	private boolean field_18629;
	private final List<BlockPos> field_18630 = Lists.newArrayList();
	private LivingEntity field_18631;
	private UUID field_18632;
	private long field_18633;

	public class_3741() {
		this(BlockEntityType.CONDUIT);
	}

	public class_3741(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		if (nbt.contains("target_uuid")) {
			this.field_18632 = NbtHelper.toUuid(nbt.getCompound("target_uuid"));
		} else {
			this.field_18632 = null;
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (this.field_18631 != null) {
			nbt.put("target_uuid", NbtHelper.fromUuid(this.field_18631.getUuid()));
		}

		return nbt;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 5, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	@Override
	public void tick() {
		this.field_18625++;
		long l = this.world.getLastUpdateTime();
		if (l % 40L == 0L) {
			this.method_16799(this.method_16804());
			if (!this.world.isClient && this.method_16802()) {
				this.method_16805();
				this.method_16806();
			}
		}

		if (l % 80L == 0L && this.method_16802()) {
			this.method_16798(Sounds.BLOCK_CONDUIT_AMBIENT);
		}

		if (l > this.field_18633 && this.method_16802()) {
			this.field_18633 = l + 60L + (long)this.world.getRandom().nextInt(40);
			this.method_16798(Sounds.BLOCK_CONDUIT_AMBIENT_SHORT);
		}

		if (this.world.isClient) {
			this.method_16807();
			this.method_16810();
			if (this.method_16802()) {
				this.field_18627++;
			}
		}
	}

	private boolean method_16804() {
		this.field_18630.clear();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					BlockPos blockPos = this.pos.add(i, j, k);
					if (!this.world.method_16357(blockPos)) {
						return false;
					}
				}
			}
		}

		for (int l = -2; l <= 2; l++) {
			for (int m = -2; m <= 2; m++) {
				for (int n = -2; n <= 2; n++) {
					int o = Math.abs(l);
					int p = Math.abs(m);
					int q = Math.abs(n);
					if ((o > 1 || p > 1 || q > 1) && (l == 0 && (p == 2 || q == 2) || m == 0 && (o == 2 || q == 2) || n == 0 && (o == 2 || p == 2))) {
						BlockPos blockPos2 = this.pos.add(l, m, n);
						BlockState blockState = this.world.getBlockState(blockPos2);

						for (Block block : field_18626) {
							if (blockState.getBlock() == block) {
								this.field_18630.add(blockPos2);
							}
						}
					}
				}
			}
		}

		this.method_16801(this.field_18630.size() >= 42);
		return this.field_18630.size() >= 16;
	}

	private void method_16805() {
		int i = this.field_18630.size();
		int j = i / 7 * 16;
		int k = this.pos.getX();
		int l = this.pos.getY();
		int m = this.pos.getZ();
		Box box = new Box((double)k, (double)l, (double)m, (double)(k + 1), (double)(l + 1), (double)(m + 1))
			.expand((double)j)
			.stretch(0.0, (double)this.world.getMaxBuildHeight(), 0.0);
		List<PlayerEntity> list = this.world.getEntitiesInBox(PlayerEntity.class, box);
		if (!list.isEmpty()) {
			for (PlayerEntity playerEntity : list) {
				if (this.pos.method_19965(new BlockPos(playerEntity)) <= (double)j && playerEntity.tickFire()) {
					playerEntity.method_2654(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 260, 0, true, true));
				}
			}
		}
	}

	private void method_16806() {
		LivingEntity livingEntity = this.field_18631;
		int i = this.field_18630.size();
		if (i < 42) {
			this.field_18631 = null;
		} else if (this.field_18631 == null && this.field_18632 != null) {
			this.field_18631 = this.method_16809();
			this.field_18632 = null;
		} else if (this.field_18631 == null) {
			List<LivingEntity> list = this.world
				.method_16325(LivingEntity.class, this.method_16808(), livingEntityx -> livingEntityx instanceof Monster && livingEntityx.tickFire());
			if (!list.isEmpty()) {
				this.field_18631 = (LivingEntity)list.get(this.world.random.nextInt(list.size()));
			}
		} else if (!this.field_18631.isAlive() || this.pos.method_19965(new BlockPos(this.field_18631)) > 8.0) {
			this.field_18631 = null;
		}

		if (this.field_18631 != null) {
			this.world.playSound(null, this.field_18631.x, this.field_18631.y, this.field_18631.z, Sounds.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0F, 1.0F);
			this.field_18631.damage(DamageSource.MAGIC, 4.0F);
		}

		if (livingEntity != this.field_18631) {
			BlockState blockState = this.method_16783();
			this.world.method_11481(this.pos, blockState, blockState, 2);
		}
	}

	private void method_16807() {
		if (this.field_18632 == null) {
			this.field_18631 = null;
		} else if (this.field_18631 == null || !this.field_18631.getUuid().equals(this.field_18632)) {
			this.field_18631 = this.method_16809();
			if (this.field_18631 == null) {
				this.field_18632 = null;
			}
		}
	}

	private Box method_16808() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		return new Box((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1)).expand(8.0);
	}

	@Nullable
	private LivingEntity method_16809() {
		List<LivingEntity> list = this.world.method_16325(LivingEntity.class, this.method_16808(), livingEntity -> livingEntity.getUuid().equals(this.field_18632));
		return list.size() == 1 ? (LivingEntity)list.get(0) : null;
	}

	private void method_16810() {
		Random random = this.world.random;
		float f = MathHelper.sin((float)(this.field_18625 + 35) * 0.1F) / 2.0F + 0.5F;
		f = (f * f + f) * 0.3F;
		Vec3d vec3d = new Vec3d((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 1.5F + f), (double)((float)this.pos.getZ() + 0.5F));

		for (BlockPos blockPos : this.field_18630) {
			if (random.nextInt(50) == 0) {
				float g = -0.5F + random.nextFloat();
				float h = -2.0F + random.nextFloat();
				float i = -0.5F + random.nextFloat();
				BlockPos blockPos2 = blockPos.subtract(this.pos);
				Vec3d vec3d2 = new Vec3d((double)g, (double)h, (double)i).add((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				this.world.method_16343(class_4342.field_21373, vec3d.x, vec3d.y, vec3d.z, vec3d2.x, vec3d2.y, vec3d2.z);
			}
		}

		if (this.field_18631 != null) {
			Vec3d vec3d3 = new Vec3d(this.field_18631.x, this.field_18631.y + (double)this.field_18631.getEyeHeight(), this.field_18631.z);
			float j = (-0.5F + random.nextFloat()) * (3.0F + this.field_18631.width);
			float k = -1.0F + random.nextFloat() * this.field_18631.height;
			float l = (-0.5F + random.nextFloat()) * (3.0F + this.field_18631.width);
			Vec3d vec3d4 = new Vec3d((double)j, (double)k, (double)l);
			this.world.method_16343(class_4342.field_21373, vec3d3.x, vec3d3.y, vec3d3.z, vec3d4.x, vec3d4.y, vec3d4.z);
		}
	}

	public boolean method_16802() {
		return this.field_18628;
	}

	public boolean method_16803() {
		return this.field_18629;
	}

	private void method_16799(boolean bl) {
		if (bl != this.field_18628) {
			this.method_16798(bl ? Sounds.BLOCK_CONDUIT_ACTIVATE : Sounds.BLOCK_CONDUIT_DEACTIVATE);
		}

		this.field_18628 = bl;
	}

	private void method_16801(boolean bl) {
		this.field_18629 = bl;
	}

	public float method_16796(float f) {
		return (this.field_18627 + f) * -0.0375F;
	}

	public void method_16798(Sound sound) {
		this.world.playSound(null, this.pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}
}
