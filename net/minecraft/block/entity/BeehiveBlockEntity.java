package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BeehiveBlockEntity extends BlockEntity implements Tickable {
	private final List<BeehiveBlockEntity.Bee> bees = Lists.newArrayList();
	@Nullable
	private BlockPos flowerPos = null;

	public BeehiveBlockEntity() {
		super(BlockEntityType.field_20431);
	}

	@Override
	public void markDirty() {
		if (this.isNearFire()) {
			this.angerBees(null, this.world.getBlockState(this.getPos()), BeehiveBlockEntity.BeeState.field_21052);
		}

		super.markDirty();
	}

	public boolean isNearFire() {
		if (this.world == null) {
			return false;
		} else {
			for (BlockPos blockPos : BlockPos.iterate(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1))) {
				if (this.world.getBlockState(blockPos).getBlock() instanceof FireBlock) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean hasNoBees() {
		return this.bees.isEmpty();
	}

	public boolean isFullOfBees() {
		return this.bees.size() == 3;
	}

	public void angerBees(@Nullable PlayerEntity playerEntity, BlockState blockState, BeehiveBlockEntity.BeeState beeState) {
		List<Entity> list = this.tryReleaseBee(blockState, beeState);
		if (playerEntity != null) {
			for (Entity entity : list) {
				if (entity instanceof BeeEntity) {
					BeeEntity beeEntity = (BeeEntity)entity;
					if (playerEntity.getPos().squaredDistanceTo(entity.getPos()) <= 16.0) {
						if (!this.isSmoked()) {
							beeEntity.setBeeAttacker(playerEntity);
						} else {
							beeEntity.setCannotEnterHiveTicks(400);
						}
					}
				}
			}
		}
	}

	private List<Entity> tryReleaseBee(BlockState blockState, BeehiveBlockEntity.BeeState beeState) {
		List<Entity> list = Lists.newArrayList();
		this.bees.removeIf(bee -> this.releaseBee(blockState, bee.entityData, list, beeState));
		return list;
	}

	public void tryEnterHive(Entity entity, boolean bl) {
		this.tryEnterHive(entity, bl, 0);
	}

	public int getBeeCount() {
		return this.bees.size();
	}

	public static int getHoneyLevel(BlockState blockState) {
		return (Integer)blockState.get(BeehiveBlock.HONEY_LEVEL);
	}

	public boolean isSmoked() {
		return CampfireBlock.isLitCampfireInRange(this.world, this.getPos(), 5);
	}

	protected void sendDebugData() {
		DebugRendererInfoManager.sendBeehiveDebugData(this);
	}

	public void tryEnterHive(Entity entity, boolean bl, int i) {
		if (this.bees.size() < 3) {
			entity.stopRiding();
			entity.removeAllPassengers();
			CompoundTag compoundTag = new CompoundTag();
			entity.saveToTag(compoundTag);
			this.bees.add(new BeehiveBlockEntity.Bee(compoundTag, i, bl ? 2400 : 600));
			if (this.world != null) {
				if (entity instanceof BeeEntity) {
					BeeEntity beeEntity = (BeeEntity)entity;
					if (beeEntity.hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
						this.flowerPos = beeEntity.getFlowerPos();
					}
				}

				BlockPos blockPos = this.getPos();
				this.world
					.playSound(null, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.field_20609, SoundCategory.field_15245, 1.0F, 1.0F);
			}

			entity.remove();
		}
	}

	private boolean releaseBee(BlockState blockState, CompoundTag compoundTag, @Nullable List<Entity> list, BeehiveBlockEntity.BeeState beeState) {
		BlockPos blockPos = this.getPos();
		if ((this.world.isNight() || this.world.isRaining()) && beeState != BeehiveBlockEntity.BeeState.field_21052) {
			return false;
		} else {
			compoundTag.remove("Passengers");
			compoundTag.remove("Leash");
			compoundTag.removeUuid("UUID");
			Direction direction = blockState.get(BeehiveBlock.FACING);
			BlockPos blockPos2 = blockPos.offset(direction);
			boolean bl = !this.world.getBlockState(blockPos2).getCollisionShape(this.world, blockPos2).isEmpty();
			if (bl && beeState != BeehiveBlockEntity.BeeState.field_21052) {
				return false;
			} else {
				Entity entity = EntityType.loadEntityWithPassengers(compoundTag, this.world, entityx -> entityx);
				if (entity != null) {
					float f = entity.getWidth();
					double d = bl ? 0.0 : 0.55 + (double)(f / 2.0F);
					double e = (double)blockPos.getX() + 0.5 + d * (double)direction.getOffsetX();
					double g = (double)blockPos.getY() + 0.5 - (double)(entity.getHeight() / 2.0F);
					double h = (double)blockPos.getZ() + 0.5 + d * (double)direction.getOffsetZ();
					entity.refreshPositionAndAngles(e, g, h, entity.yaw, entity.pitch);
					if (!entity.getType().isTaggedWith(EntityTypeTags.field_20631)) {
						return false;
					} else {
						if (entity instanceof BeeEntity) {
							BeeEntity beeEntity = (BeeEntity)entity;
							if (this.hasFlowerPos() && !beeEntity.hasFlower() && this.world.random.nextFloat() < 0.9F) {
								beeEntity.setFlowerPos(this.flowerPos);
							}

							if (beeState == BeehiveBlockEntity.BeeState.field_20428) {
								beeEntity.onHoneyDelivered();
								if (blockState.getBlock().matches(BlockTags.field_20340)) {
									int i = getHoneyLevel(blockState);
									if (i < 5) {
										int j = this.world.random.nextInt(100) == 0 ? 2 : 1;
										if (i + j > 5) {
											j--;
										}

										this.world.setBlockState(this.getPos(), blockState.with(BeehiveBlock.HONEY_LEVEL, Integer.valueOf(i + j)));
									}
								}
							}

							beeEntity.resetPollinationTicks();
							if (list != null) {
								list.add(beeEntity);
							}
						}

						BlockPos blockPos3 = this.getPos();
						this.world
							.playSound(
								null, (double)blockPos3.getX(), (double)blockPos3.getY(), (double)blockPos3.getZ(), SoundEvents.field_20610, SoundCategory.field_15245, 1.0F, 1.0F
							);
						return this.world.spawnEntity(entity);
					}
				} else {
					return false;
				}
			}
		}
	}

	private boolean hasFlowerPos() {
		return this.flowerPos != null;
	}

	private void tickBees() {
		Iterator<BeehiveBlockEntity.Bee> iterator = this.bees.iterator();
		BlockState blockState = this.getCachedState();

		while (iterator.hasNext()) {
			BeehiveBlockEntity.Bee bee = (BeehiveBlockEntity.Bee)iterator.next();
			if (bee.ticksInHive > bee.minOccupationTIcks) {
				CompoundTag compoundTag = bee.entityData;
				BeehiveBlockEntity.BeeState beeState = compoundTag.getBoolean("HasNectar")
					? BeehiveBlockEntity.BeeState.field_20428
					: BeehiveBlockEntity.BeeState.field_20429;
				if (this.releaseBee(blockState, compoundTag, null, beeState)) {
					iterator.remove();
				}
			} else {
				bee.ticksInHive++;
			}
		}
	}

	@Override
	public void tick() {
		if (!this.world.isClient) {
			this.tickBees();
			BlockPos blockPos = this.getPos();
			if (this.bees.size() > 0 && this.world.getRandom().nextDouble() < 0.005) {
				double d = (double)blockPos.getX() + 0.5;
				double e = (double)blockPos.getY();
				double f = (double)blockPos.getZ() + 0.5;
				this.world.playSound(null, d, e, f, SoundEvents.field_20612, SoundCategory.field_15245, 1.0F, 1.0F);
			}

			this.sendDebugData();
		}
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.bees.clear();
		ListTag listTag = compoundTag.getList("Bees", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompound(i);
			BeehiveBlockEntity.Bee bee = new BeehiveBlockEntity.Bee(
				compoundTag2.getCompound("EntityData"), compoundTag2.getInt("TicksInHive"), compoundTag2.getInt("MinOccupationTicks")
			);
			this.bees.add(bee);
		}

		this.flowerPos = null;
		if (compoundTag.contains("FlowerPos")) {
			this.flowerPos = NbtHelper.toBlockPos(compoundTag.getCompound("FlowerPos"));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.put("Bees", this.getBees());
		if (this.hasFlowerPos()) {
			compoundTag.put("FlowerPos", NbtHelper.fromBlockPos(this.flowerPos));
		}

		return compoundTag;
	}

	public ListTag getBees() {
		ListTag listTag = new ListTag();

		for (BeehiveBlockEntity.Bee bee : this.bees) {
			bee.entityData.removeUuid("UUID");
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.put("EntityData", bee.entityData);
			compoundTag.putInt("TicksInHive", bee.ticksInHive);
			compoundTag.putInt("MinOccupationTicks", bee.minOccupationTIcks);
			listTag.add(compoundTag);
		}

		return listTag;
	}

	static class Bee {
		private final CompoundTag entityData;
		private int ticksInHive;
		private final int minOccupationTIcks;

		private Bee(CompoundTag compoundTag, int i, int j) {
			compoundTag.removeUuid("UUID");
			this.entityData = compoundTag;
			this.ticksInHive = i;
			this.minOccupationTIcks = j;
		}
	}

	public static enum BeeState {
		field_20428,
		field_20429,
		field_21052;
	}
}
