package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
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
import javax.annotation.Nullable;
import net.minecraft.class_2925;
import net.minecraft.class_2957;
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
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.EndBiomeDecorator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.EndExitPortalFeature;
import net.minecraft.world.gen.feature.EndGatewayFeature;
import net.minecraft.world.gen.feature.FillerBlockFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonRespawnAnimation {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Predicate<ServerPlayerEntity> PLAYER_PREDICATE = Predicates.and(
		EntityPredicate.VALID_ENTITY, EntityPredicate.method_13024(0.0, 128.0, 0.0, 192.0)
	);
	private final class_2925 field_12936 = (class_2925)new class_2925(
			new TranslatableText("entity.EnderDragon.name"), class_2957.Color.PINK, class_2957.Division.PROGRESS
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
			Collections.shuffle(this.gateways, new Random(serverWorld.getSeed()));
		}

		this.blockPattern = BlockPatternBuilder.start()
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
			.aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ")
			.aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ")
			.where('#', CachedBlockPosition.matchesBlockState(BlockPredicate.create(Blocks.BEDROCK)))
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
			nbtList.add(new NbtInt(i));
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

		if (!this.field_12936.method_12770().isEmpty()) {
			if (this.field_12947) {
				LOGGER.info("Scanning for legacy world dragon fight...");
				this.method_11813();
				this.field_12947 = false;
				boolean bl = this.method_11811();
				if (bl) {
					LOGGER.info("Found that the dragon has been killed in this world already.");
					this.previouslyKilled = true;
				} else {
					LOGGER.info("Found that the dragon has not yet been killed in this world.");
					this.previouslyKilled = false;
					this.createExitPortal(false);
				}

				List<EnderDragonEntity> list = this.world.method_8514(EnderDragonEntity.class, EntityPredicate.VALID_ENTITY);
				if (list.isEmpty()) {
					this.killed = true;
				} else {
					EnderDragonEntity enderDragonEntity = (EnderDragonEntity)list.get(0);
					this.dragonUUID = enderDragonEntity.getUuid();
					LOGGER.info("Found that there's a dragon still alive ({})", new Object[]{enderDragonEntity});
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

			if (this.status != null) {
				if (this.endCrystals == null) {
					this.status = null;
					this.tryRespawn();
				}

				this.status.play(this.world, this, this.endCrystals, this.animationTicks++, this.portalPos);
			}

			if (!this.killed) {
				if (this.dragonUUID == null || ++this.field_12940 >= 1200) {
					this.method_11813();
					List<EnderDragonEntity> list2 = this.world.method_8514(EnderDragonEntity.class, EntityPredicate.VALID_ENTITY);
					if (list2.isEmpty()) {
						LOGGER.debug("Haven't seen the dragon, respawning it");
						this.method_11817();
					} else {
						LOGGER.debug("Haven't seen our dragon, but found another one to use.");
						this.dragonUUID = ((EnderDragonEntity)list2.get(0)).getUuid();
					}

					this.field_12940 = 0;
				}

				if (++this.field_12942 >= 100) {
					this.updateAliveCrystals();
					this.field_12942 = 0;
				}
			}
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
				this.method_11817();
			} else {
				this.status = phase;
			}
		}
	}

	private boolean method_11811() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				Chunk chunk = this.world.getChunk(i, j);

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
				Chunk chunk = this.world.getChunk(i, j);

				for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
					if (blockEntity instanceof EndPortalBlockEntity) {
						BlockPattern.Result result = this.blockPattern.searchAround(this.world, blockEntity.getPos());
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

		int k = this.world.getHighestBlock(EndExitPortalFeature.ORIGIN).getY();

		for (int l = k; l >= 0; l--) {
			BlockPattern.Result result2 = this.blockPattern
				.searchAround(this.world, new BlockPos(EndExitPortalFeature.ORIGIN.getX(), l, EndExitPortalFeature.ORIGIN.getZ()));
			if (result2 != null) {
				if (this.portalPos == null) {
					this.portalPos = result2.translate(3, 3, 3).getPos();
				}

				return result2;
			}
		}

		return null;
	}

	private void method_11813() {
		for (int i = -8; i <= 8; i++) {
			for (int j = -8; j <= 8; j++) {
				this.world.getChunk(i, j);
			}
		}
	}

	private void method_11814() {
		Set<ServerPlayerEntity> set = Sets.newHashSet();

		for (ServerPlayerEntity serverPlayerEntity : this.world.method_8536(ServerPlayerEntity.class, PLAYER_PREDICATE)) {
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

		for (FillerBlockFeature.class_2756 lv : EndBiomeDecorator.method_11545(this.world)) {
			this.aliveCrystals = this.aliveCrystals + this.world.getEntitiesInBox(EndCrystalEntity.class, lv.method_11832()).size();
		}

		LOGGER.debug("Found {} end crystals still alive", new Object[]{this.aliveCrystals});
	}

	public void method_11803(EnderDragonEntity dragon) {
		if (dragon.getUuid().equals(this.dragonUUID)) {
			this.field_12936.setHealth(0.0F);
			this.field_12936.method_12771(false);
			this.createExitPortal(true);
			this.method_11816();
			if (!this.previouslyKilled) {
				this.world.setBlockState(this.world.getHighestBlock(EndExitPortalFeature.ORIGIN), Blocks.DRAGON_EGG.getDefaultState());
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
		new EndGatewayFeature().generate(this.world, new Random(), pos);
	}

	private void createExitPortal(boolean emitNeighborBlockUpdates) {
		EndExitPortalFeature endExitPortalFeature = new EndExitPortalFeature(emitNeighborBlockUpdates);
		if (this.portalPos == null) {
			this.portalPos = this.world.getTopPosition(EndExitPortalFeature.ORIGIN).down();

			while (this.world.getBlockState(this.portalPos).getBlock() == Blocks.BEDROCK && this.portalPos.getY() > this.world.getSeaLevel()) {
				this.portalPos = this.portalPos.down();
			}
		}

		endExitPortalFeature.generate(this.world, new Random(), this.portalPos);
	}

	private void method_11817() {
		this.world.getChunk(new BlockPos(0, 128, 0));
		EnderDragonEntity enderDragonEntity = new EnderDragonEntity(this.world);
		enderDragonEntity.method_13168().method_13203(class_2993.HOLDING_PATTERN);
		enderDragonEntity.refreshPositionAndAngles(0.0, 128.0, 0.0, this.world.random.nextFloat() * 360.0F, 0.0F);
		this.world.spawnEntity(enderDragonEntity);
		this.dragonUUID = enderDragonEntity.getUuid();
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
		for (FillerBlockFeature.class_2756 lv : EndBiomeDecorator.method_11545(this.world)) {
			for (EndCrystalEntity endCrystalEntity : this.world.getEntitiesInBox(EndCrystalEntity.class, lv.method_11832())) {
				endCrystalEntity.setInvulnerable(false);
				endCrystalEntity.setBeamTarget(null);
			}
		}
	}
}
