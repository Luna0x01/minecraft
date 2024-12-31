package net.minecraft.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class LightningBoltEntity extends WeatherEntity {
	private int ambientTick;
	public long seed;
	private int remainingActions;
	private final boolean cosmetic;
	@Nullable
	private ServerPlayerEntity field_17023;

	public LightningBoltEntity(World world, double d, double e, double f, boolean bl) {
		super(EntityType.LIGHTNING_BOLT, world);
		this.refreshPositionAndAngles(d, e, f, 0.0F, 0.0F);
		this.ambientTick = 2;
		this.seed = this.random.nextLong();
		this.remainingActions = this.random.nextInt(3) + 1;
		this.cosmetic = bl;
		Difficulty difficulty = world.method_16346();
		if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
			this.method_15845(4);
		}
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.WEATHER;
	}

	public void method_15846(@Nullable ServerPlayerEntity serverPlayerEntity) {
		this.field_17023 = serverPlayerEntity;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.ambientTick == 2) {
			this.world
				.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
			this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
		}

		this.ambientTick--;
		if (this.ambientTick < 0) {
			if (this.remainingActions == 0) {
				this.remove();
			} else if (this.ambientTick < -this.random.nextInt(10)) {
				this.remainingActions--;
				this.ambientTick = 1;
				this.seed = this.random.nextLong();
				this.method_15845(0);
			}
		}

		if (this.ambientTick >= 0) {
			if (this.world.isClient) {
				this.world.setLightningTicksLeft(2);
			} else if (!this.cosmetic) {
				double d = 3.0;
				List<Entity> list = this.world.getEntities(this, new Box(this.x - 3.0, this.y - 3.0, this.z - 3.0, this.x + 3.0, this.y + 6.0 + 3.0, this.z + 3.0));

				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity)list.get(i);
					entity.onLightningStrike(this);
				}

				if (this.field_17023 != null) {
					AchievementsAndCriterions.field_21657.method_15320(this.field_17023, list);
				}
			}
		}
	}

	private void method_15845(int i) {
		if (!this.cosmetic && !this.world.isClient && this.world.getGameRules().getBoolean("doFireTick")) {
			BlockState blockState = Blocks.FIRE.getDefaultState();
			BlockPos blockPos = new BlockPos(this);
			if (this.world.method_16391(blockPos, 10) && this.world.getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.world, blockPos)) {
				this.world.setBlockState(blockPos, blockState);
			}

			for (int j = 0; j < i; j++) {
				BlockPos blockPos2 = blockPos.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
				if (this.world.getBlockState(blockPos2).isAir() && blockState.canPlaceAt(this.world, blockPos2)) {
					this.world.setBlockState(blockPos2, blockState);
				}
			}
		}
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
	}
}
