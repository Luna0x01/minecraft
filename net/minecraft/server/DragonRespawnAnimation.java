package net.minecraft.server;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_2925;
import net.minecraft.class_2957;
import net.minecraft.class_3786;
import net.minecraft.class_3798;
import net.minecraft.class_3804;
import net.minecraft.class_3843;
import net.minecraft.class_3844;
import net.minecraft.class_3845;
import net.minecraft.class_3957;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.dragon.class_2993;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.EndExitPortalFeature;
import net.minecraft.world.gen.feature.FillerBlockFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonRespawnAnimation {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Predicate<Entity> field_18961 = EntityPredicate.field_16700.and(EntityPredicate.method_15603(0.0, 128.0, 0.0, 192.0));
	private final class_2925 field_12936 = (class_2925)new class_2925(
			new TranslatableText("entity.minecraft.ender_dragon"), class_2957.Color.PINK, class_2957.Division.PROGRESS
		)
		.method_12922(true)
		.method_12923(true);
	private final ServerWorld world;
	private final List<Integer> gateways = Lists.newArrayList();
	private final BlockPattern blockPattern;
	private int field_12940;
	private int aliveCrystals;
	private int field_12942;
	private int field_12943;
	private boolean killed;
	private boolean previouslyKilled;
	private UUID dragonUUID;
	private boolean field_12947 = true;
	private BlockPos portalPos;
	private DragonRespawnAnimationStatus status;
	private int animationTicks;
	private List<EndCrystalEntity> endCrystals;

	public DragonRespawnAnimation(ServerWorld serverWorld, NbtCompound nbtCompound) {
		this.world = serverWorld;
		if (nbtCompound.contains("DragonKilled", 99)) {
			if (nbtCompound.containsUuid("DragonUUID")) {
				this.dragonUUID = nbtCompound.getUuid("DragonUUID");
			}

			this.killed = nbtCompound.getBoolean("DragonKilled");
			this.previouslyKilled = nbtCompound.getBoolean("PreviouslyKilled");
			if (nbtCompound.getBoolean("IsRespawning")) {
				this.status = DragonRespawnAnimationStatus.START;
			}

			if (nbtCompound.contains("ExitPortalLocation", 10)) {
				this.portalPos = NbtHelper.toBlockPos(nbtCompound.getCompound("ExitPortalLocation"));
			}
		} else {
			this.killed = true;
			this.previouslyKilled = true;
		}

		if (nbtCompound.contains("Gateways", 9)) {
			NbtList nbtList = nbtCompound.getList("Gateways", 3);

			for (int i = 0; i < nbtList.size(); i++) {
				this.gateways.add(nbtList.getInt(i));
			}
		} else {
			this.gateways.addAll(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
			Collections.shuffle(this.gateways, new Random(serverWorld.method_3581()));
		}

		this.blockPattern = BlockPatternBuilder.start()
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ")
			.aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ")
			.method_16940('#', CachedBlockPosition.method_16935(BlockPredicate.create(Blocks.BEDROCK)))
			.build();
	}

	public NbtCompound toTag() {
		NbtCompound nbtCompound = new NbtCompound();
		if (this.dragonUUID != null) {
			nbtCompound.putUuid("DragonUUID", this.dragonUUID);
		}

		nbtCompound.putBoolean("DragonKilled", this.killed);
		nbtCompound.putBoolean("PreviouslyKilled", this.previouslyKilled);
		if (this.portalPos != null) {
			nbtCompound.put("ExitPortalLocation", NbtHelper.fromBlockPos(this.portalPos));
		}

		NbtList nbtList = new NbtList();

		for (int i : this.gateways) {
			nbtList.add((NbtElement)(new NbtInt(i)));
		}

		nbtCompound.put("Gateways", nbtList);
		return nbtCompound;
	}

	public void method_11805() {
		this.field_12936.method_12771(!this.killed);
		if (++this.field_12943 >= 20) {
			this.method_11814();
			this.field_12943 = 0;
		}

		DragonRespawnAnimation.class_3796 lv = new DragonRespawnAnimation.class_3796();
		if (!this.field_12936.method_12770().isEmpty()) {
			if (this.field_12947 && lv.method_17210()) {
				this.method_17208();
				this.field_12947 = false;
			}

			if (this.status != null) {
				if (this.endCrystals == null && lv.method_17210()) {
					this.status = null;
					this.tryRespawn();
				}

				this.status.play(this.world, this, this.endCrystals, this.animationTicks++, this.portalPos);
			}

			if (!this.killed) {
				if ((this.dragonUUID == null || ++this.field_12940 >= 1200) && lv.method_17210()) {
					this.method_17209();
					this.field_12940 = 0;
				}

				if (++this.field_12942 >= 100 && lv.method_17210()) {
					this.updateAliveCrystals();
					this.field_12942 = 0;
				}
			}
		}
	}

	private void method_17208() {
		LOGGER.info("Scanning for legacy world dragon fight...");
		boolean bl = this.method_11811();
		if (bl) {
			LOGGER.info("Found that the dragon has been killed in this world already.");
			this.previouslyKilled = true;
		} else {
			LOGGER.info("Found that the dragon has not yet been killed in this world.");
			this.previouslyKilled = false;
			this.createExitPortal(false);
		}

		List<EnderDragonEntity> list = this.world.method_16326(EnderDragonEntity.class, EntityPredicate.field_16700);
		if (list.isEmpty()) {
			this.killed = true;
		} else {
			EnderDragonEntity enderDragonEntity = (EnderDragonEntity)list.get(0);
			this.dragonUUID = enderDragonEntity.getUuid();
			LOGGER.info("Found that there's a dragon still alive ({})", enderDragonEntity);
			this.killed = false;
			if (!bl) {
				LOGGER.info("But we didn't have a portal, let's remove it.");
				enderDragonEntity.remove();
				this.dragonUUID = null;
			}
		}

		if (!this.previouslyKilled && this.killed) {
			this.killed = false;
		}
	}

	private void method_17209() {
		List<EnderDragonEntity> list = this.world.method_16326(EnderDragonEntity.class, EntityPredicate.field_16700);
		if (list.isEmpty()) {
			LOGGER.debug("Haven't seen the dragon, respawning it");
			this.createDragon();
		} else {
			LOGGER.debug("Haven't seen our dragon, but found another one to use.");
			this.dragonUUID = ((EnderDragonEntity)list.get(0)).getUuid();
		}
	}

	protected void skipTo(DragonRespawnAnimationStatus phase) {
		if (this.status == null) {
			throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
		} else {
			this.animationTicks = 0;
			if (phase == DragonRespawnAnimationStatus.END) {
				this.status = null;
				this.killed = false;
				EnderDragonEntity enderDragonEntity = this.createDragon();

				for (ServerPlayerEntity serverPlayerEntity : this.field_12936.method_12770()) {
					AchievementsAndCriterions.field_16341.method_14397(serverPlayerEntity, enderDragonEntity);
				}
			} else {
				this.status = phase;
			}
		}
	}

	private boolean method_11811() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				Chunk chunk = this.world.method_16347(i, j);

				for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
					if (blockEntity instanceof EndPortalBlockEntity) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Nullable
	private BlockPattern.Result findExitPortal() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				Chunk chunk = this.world.method_16347(i, j);

				for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
					if (blockEntity instanceof EndPortalBlockEntity) {
						BlockPattern.Result result = this.blockPattern.method_16938(this.world, blockEntity.getPos());
						if (result != null) {
							BlockPos blockPos = result.translate(3, 3, 3).getPos();
							if (this.portalPos == null && blockPos.getX() == 0 && blockPos.getZ() == 0) {
								this.portalPos = blockPos;
							}

							return result;
						}
					}
				}
			}
		}

		int k = this.world.method_16373(class_3804.class_3805.MOTION_BLOCKING, EndExitPortalFeature.ORIGIN).getY();

		for (int l = k; l >= 0; l--) {
			BlockPattern.Result result2 = this.blockPattern
				.method_16938(this.world, new BlockPos(EndExitPortalFeature.ORIGIN.getX(), l, EndExitPortalFeature.ORIGIN.getZ()));
			if (result2 != null) {
				if (this.portalPos == null) {
					this.portalPos = result2.translate(3, 3, 3).getPos();
				}

				return result2;
			}
		}

		return null;
	}

	private boolean method_17204(int i, int j, int k, int l) {
		if (this.method_17206(i, j, k, l)) {
			return true;
		} else {
			this.method_17207(i, j, k, l);
			return false;
		}
	}

	private boolean method_17206(int i, int j, int k, int l) {
		boolean bl = true;

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				Chunk chunk = this.world.method_16347(m, n);
				bl &= chunk.method_17009() == class_3786.POSTPROCESSED;
			}
		}

		return bl;
	}

	private void method_17207(int i, int j, int k, int l) {
		for (int m = i - 1; m <= j + 1; m++) {
			this.world.method_16347(m, k - 1);
			this.world.method_16347(m, l + 1);
		}

		for (int n = k - 1; n <= l + 1; n++) {
			this.world.method_16347(i - 1, n);
			this.world.method_16347(j + 1, n);
		}
	}

	private void method_11814() {
		Set<ServerPlayerEntity> set = Sets.newHashSet();

		for (ServerPlayerEntity serverPlayerEntity : this.world.method_16334(ServerPlayerEntity.class, field_18961)) {
			this.field_12936.method_12768(serverPlayerEntity);
			set.add(serverPlayerEntity);
		}

		Set<ServerPlayerEntity> set2 = Sets.newHashSet(this.field_12936.method_12770());
		set2.removeAll(set);

		for (ServerPlayerEntity serverPlayerEntity2 : set2) {
			this.field_12936.method_12769(serverPlayerEntity2);
		}
	}

	private void updateAliveCrystals() {
		this.field_12942 = 0;
		this.aliveCrystals = 0;

		for (FillerBlockFeature.class_2756 lv : class_3957.method_17546(this.world)) {
			this.aliveCrystals = this.aliveCrystals + this.world.getEntitiesInBox(EndCrystalEntity.class, lv.method_11832()).size();
		}

		LOGGER.debug("Found {} end crystals still alive", this.aliveCrystals);
	}

	public void method_11803(EnderDragonEntity dragon) {
		if (dragon.getUuid().equals(this.dragonUUID)) {
			this.field_12936.setHealth(0.0F);
			this.field_12936.method_12771(false);
			this.createExitPortal(true);
			this.method_11816();
			if (!this.previouslyKilled) {
				this.world.setBlockState(this.world.method_16373(class_3804.class_3805.MOTION_BLOCKING, EndExitPortalFeature.ORIGIN), Blocks.DRAGON_EGG.getDefaultState());
			}

			this.previouslyKilled = true;
			this.killed = true;
		}
	}

	private void method_11816() {
		if (!this.gateways.isEmpty()) {
			int i = (Integer)this.gateways.remove(this.gateways.size() - 1);
			int j = (int)(96.0 * Math.cos(2.0 * (-Math.PI + (Math.PI / 20) * (double)i)));
			int k = (int)(96.0 * Math.sin(2.0 * (-Math.PI + (Math.PI / 20) * (double)i)));
			this.generateEndGateway(new BlockPos(j, 75, k));
		}
	}

	private void generateEndGateway(BlockPos pos) {
		this.world.syncGlobalEvent(3000, pos, 0);
		class_3844.field_19178
			.method_17343(this.world, (ChunkGenerator<? extends class_3798>)this.world.method_3586().method_17046(), new Random(), pos, new class_3843(false));
	}

	private void createExitPortal(boolean emitNeighborBlockUpdates) {
		EndExitPortalFeature endExitPortalFeature = new EndExitPortalFeature(emitNeighborBlockUpdates);
		if (this.portalPos == null) {
			this.portalPos = this.world.method_16373(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES, EndExitPortalFeature.ORIGIN).down();

			while (this.world.getBlockState(this.portalPos).getBlock() == Blocks.BEDROCK && this.portalPos.getY() > this.world.method_8483()) {
				this.portalPos = this.portalPos.down();
			}
		}

		endExitPortalFeature.method_17343(
			this.world, (ChunkGenerator<? extends class_3798>)this.world.method_3586().method_17046(), new Random(), this.portalPos, class_3845.field_19203
		);
	}

	private EnderDragonEntity createDragon() {
		this.world.getChunk(new BlockPos(0, 128, 0));
		EnderDragonEntity enderDragonEntity = new EnderDragonEntity(this.world);
		enderDragonEntity.method_13168().method_13203(class_2993.HOLDING_PATTERN);
		enderDragonEntity.refreshPositionAndAngles(0.0, 128.0, 0.0, this.world.random.nextFloat() * 360.0F, 0.0F);
		this.world.method_3686(enderDragonEntity);
		this.dragonUUID = enderDragonEntity.getUuid();
		return enderDragonEntity;
	}

	public void method_11806(EnderDragonEntity dragon) {
		if (dragon.getUuid().equals(this.dragonUUID)) {
			this.field_12936.setHealth(dragon.getHealth() / dragon.getMaxHealth());
			this.field_12940 = 0;
			if (dragon.hasCustomName()) {
				this.field_12936.setTitle(dragon.getName());
			}
		}
	}

	public int getAliveCrystals() {
		return this.aliveCrystals;
	}

	public void onEndCrystalDestroyed(EndCrystalEntity crystal, DamageSource source) {
		if (this.status != null && this.endCrystals.contains(crystal)) {
			LOGGER.debug("Aborting respawn sequence");
			this.status = null;
			this.animationTicks = 0;
			this.method_11810();
			this.createExitPortal(true);
		} else {
			this.updateAliveCrystals();
			Entity entity = this.world.getEntity(this.dragonUUID);
			if (entity instanceof EnderDragonEntity) {
				((EnderDragonEntity)entity).method_13167(crystal, new BlockPos(crystal), source);
			}
		}
	}

	public boolean wasDragonKilled() {
		return this.previouslyKilled;
	}

	public void tryRespawn() {
		if (this.killed && this.status == null) {
			BlockPos blockPos = this.portalPos;
			if (blockPos == null) {
				LOGGER.debug("Tried to respawn, but need to find the portal first.");
				BlockPattern.Result result = this.findExitPortal();
				if (result == null) {
					LOGGER.debug("Couldn't find a portal, so we made one.");
					this.createExitPortal(true);
				} else {
					LOGGER.debug("Found the exit portal & temporarily using it.");
				}

				blockPos = this.portalPos;
			}

			List<EndCrystalEntity> list = Lists.newArrayList();
			BlockPos blockPos2 = blockPos.up(1);

			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				List<EndCrystalEntity> list2 = this.world.getEntitiesInBox(EndCrystalEntity.class, new Box(blockPos2.offset(direction, 2)));
				if (list2.isEmpty()) {
					return;
				}

				list.addAll(list2);
			}

			LOGGER.debug("Found all crystals, respawning dragon.");
			this.startAnimation(list);
		}
	}

	private void startAnimation(List<EndCrystalEntity> crystals) {
		if (this.killed && this.status == null) {
			for (BlockPattern.Result result = this.findExitPortal(); result != null; result = this.findExitPortal()) {
				for (int i = 0; i < this.blockPattern.getWidth(); i++) {
					for (int j = 0; j < this.blockPattern.getHeight(); j++) {
						for (int k = 0; k < this.blockPattern.method_11746(); k++) {
							CachedBlockPosition cachedBlockPosition = result.translate(i, j, k);
							if (cachedBlockPosition.getBlockState().getBlock() == Blocks.BEDROCK || cachedBlockPosition.getBlockState().getBlock() == Blocks.END_PORTAL) {
								this.world.setBlockState(cachedBlockPosition.getPos(), Blocks.END_STONE.getDefaultState());
							}
						}
					}
				}
			}

			this.status = DragonRespawnAnimationStatus.START;
			this.animationTicks = 0;
			this.createExitPortal(false);
			this.endCrystals = crystals;
		}
	}

	public void method_11810() {
		for (FillerBlockFeature.class_2756 lv : class_3957.method_17546(this.world)) {
			for (EndCrystalEntity endCrystalEntity : this.world.getEntitiesInBox(EndCrystalEntity.class, lv.method_11832())) {
				endCrystalEntity.setInvulnerable(false);
				endCrystalEntity.setBeamTarget(null);
			}
		}
	}

	static enum class_3795 {
		UNKNOWN,
		NOT_LOADED,
		LOADED;
	}

	class class_3796 {
		private DragonRespawnAnimation.class_3795 field_18967 = DragonRespawnAnimation.class_3795.UNKNOWN;

		private class_3796() {
		}

		private boolean method_17210() {
			if (this.field_18967 == DragonRespawnAnimation.class_3795.UNKNOWN) {
				this.field_18967 = DragonRespawnAnimation.this.method_17204(-8, 8, -8, 8)
					? DragonRespawnAnimation.class_3795.LOADED
					: DragonRespawnAnimation.class_3795.NOT_LOADED;
			}

			return this.field_18967 == DragonRespawnAnimation.class_3795.LOADED;
		}
	}
}
