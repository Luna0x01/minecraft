package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class EntitySelector {
	public static final int field_33068 = Integer.MAX_VALUE;
	private static final TypeFilter<Entity, ?> PASSTHROUGH_FILTER = new TypeFilter<Entity, Entity>() {
		public Entity downcast(Entity entity) {
			return entity;
		}

		@Override
		public Class<? extends Entity> getBaseClass() {
			return Entity.class;
		}
	};
	private final int limit;
	private final boolean includesNonPlayers;
	private final boolean localWorldOnly;
	private final Predicate<Entity> basePredicate;
	private final NumberRange.FloatRange distance;
	private final Function<Vec3d, Vec3d> positionOffset;
	@Nullable
	private final Box box;
	private final BiConsumer<Vec3d, List<? extends Entity>> sorter;
	private final boolean senderOnly;
	@Nullable
	private final String playerName;
	@Nullable
	private final UUID uuid;
	private TypeFilter<Entity, ?> entityFilter;
	private final boolean usesAt;

	public EntitySelector(
		int count,
		boolean includesNonPlayers,
		boolean localWorldOnly,
		Predicate<Entity> basePredicate,
		NumberRange.FloatRange distance,
		Function<Vec3d, Vec3d> positionOffset,
		@Nullable Box box,
		BiConsumer<Vec3d, List<? extends Entity>> sorter,
		boolean senderOnly,
		@Nullable String playerName,
		@Nullable UUID uuid,
		@Nullable EntityType<?> type,
		boolean usesAt
	) {
		this.limit = count;
		this.includesNonPlayers = includesNonPlayers;
		this.localWorldOnly = localWorldOnly;
		this.basePredicate = basePredicate;
		this.distance = distance;
		this.positionOffset = positionOffset;
		this.box = box;
		this.sorter = sorter;
		this.senderOnly = senderOnly;
		this.playerName = playerName;
		this.uuid = uuid;
		this.entityFilter = (TypeFilter<Entity, ?>)(type == null ? PASSTHROUGH_FILTER : type);
		this.usesAt = usesAt;
	}

	public int getLimit() {
		return this.limit;
	}

	public boolean includesNonPlayers() {
		return this.includesNonPlayers;
	}

	public boolean isSenderOnly() {
		return this.senderOnly;
	}

	public boolean isLocalWorldOnly() {
		return this.localWorldOnly;
	}

	public boolean usesAt() {
		return this.usesAt;
	}

	private void checkSourcePermission(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		if (this.usesAt && !serverCommandSource.hasPermissionLevel(2)) {
			throw EntityArgumentType.NOT_ALLOWED_EXCEPTION.create();
		}
	}

	public Entity getEntity(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		this.checkSourcePermission(serverCommandSource);
		List<? extends Entity> list = this.getEntities(serverCommandSource);
		if (list.isEmpty()) {
			throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
		} else if (list.size() > 1) {
			throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
		} else {
			return (Entity)list.get(0);
		}
	}

	public List<? extends Entity> getEntities(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		this.checkSourcePermission(serverCommandSource);
		if (!this.includesNonPlayers) {
			return this.getPlayers(serverCommandSource);
		} else if (this.playerName != null) {
			ServerPlayerEntity serverPlayerEntity = serverCommandSource.getServer().getPlayerManager().getPlayer(this.playerName);
			return (List<? extends Entity>)(serverPlayerEntity == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayerEntity[]{serverPlayerEntity}));
		} else if (this.uuid != null) {
			for (ServerWorld serverWorld : serverCommandSource.getServer().getWorlds()) {
				Entity entity = serverWorld.getEntity(this.uuid);
				if (entity != null) {
					return Lists.newArrayList(new Entity[]{entity});
				}
			}

			return Collections.emptyList();
		} else {
			Vec3d vec3d = (Vec3d)this.positionOffset.apply(serverCommandSource.getPosition());
			Predicate<Entity> predicate = this.getPositionPredicate(vec3d);
			if (this.senderOnly) {
				return (List<? extends Entity>)(serverCommandSource.getEntity() != null && predicate.test(serverCommandSource.getEntity())
					? Lists.newArrayList(new Entity[]{serverCommandSource.getEntity()})
					: Collections.emptyList());
			} else {
				List<Entity> list = Lists.newArrayList();
				if (this.isLocalWorldOnly()) {
					this.appendEntitiesFromWorld(list, serverCommandSource.getWorld(), vec3d, predicate);
				} else {
					for (ServerWorld serverWorld2 : serverCommandSource.getServer().getWorlds()) {
						this.appendEntitiesFromWorld(list, serverWorld2, vec3d, predicate);
					}
				}

				return this.getEntities(vec3d, list);
			}
		}
	}

	private void appendEntitiesFromWorld(List<Entity> list, ServerWorld serverWorld, Vec3d vec3d, Predicate<Entity> predicate) {
		if (this.box != null) {
			list.addAll(serverWorld.getEntitiesByType(this.entityFilter, this.box.offset(vec3d), predicate));
		} else {
			list.addAll(serverWorld.getEntitiesByType(this.entityFilter, predicate));
		}
	}

	public ServerPlayerEntity getPlayer(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		this.checkSourcePermission(serverCommandSource);
		List<ServerPlayerEntity> list = this.getPlayers(serverCommandSource);
		if (list.size() != 1) {
			throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
		} else {
			return (ServerPlayerEntity)list.get(0);
		}
	}

	public List<ServerPlayerEntity> getPlayers(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		this.checkSourcePermission(serverCommandSource);
		if (this.playerName != null) {
			ServerPlayerEntity serverPlayerEntity = serverCommandSource.getServer().getPlayerManager().getPlayer(this.playerName);
			return (List<ServerPlayerEntity>)(serverPlayerEntity == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayerEntity[]{serverPlayerEntity}));
		} else if (this.uuid != null) {
			ServerPlayerEntity serverPlayerEntity2 = serverCommandSource.getServer().getPlayerManager().getPlayer(this.uuid);
			return (List<ServerPlayerEntity>)(serverPlayerEntity2 == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayerEntity[]{serverPlayerEntity2}));
		} else {
			Vec3d vec3d = (Vec3d)this.positionOffset.apply(serverCommandSource.getPosition());
			Predicate<Entity> predicate = this.getPositionPredicate(vec3d);
			if (this.senderOnly) {
				if (serverCommandSource.getEntity() instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity3 = (ServerPlayerEntity)serverCommandSource.getEntity();
					if (predicate.test(serverPlayerEntity3)) {
						return Lists.newArrayList(new ServerPlayerEntity[]{serverPlayerEntity3});
					}
				}

				return Collections.emptyList();
			} else {
				List<ServerPlayerEntity> list;
				if (this.isLocalWorldOnly()) {
					list = serverCommandSource.getWorld().getPlayers(predicate);
				} else {
					list = Lists.newArrayList();

					for (ServerPlayerEntity serverPlayerEntity4 : serverCommandSource.getServer().getPlayerManager().getPlayerList()) {
						if (predicate.test(serverPlayerEntity4)) {
							list.add(serverPlayerEntity4);
						}
					}
				}

				return this.getEntities(vec3d, list);
			}
		}
	}

	private Predicate<Entity> getPositionPredicate(Vec3d vec3d) {
		Predicate<Entity> predicate = this.basePredicate;
		if (this.box != null) {
			Box box = this.box.offset(vec3d);
			predicate = predicate.and(entity -> box.intersects(entity.getBoundingBox()));
		}

		if (!this.distance.isDummy()) {
			predicate = predicate.and(entity -> this.distance.testSqrt(entity.squaredDistanceTo(vec3d)));
		}

		return predicate;
	}

	private <T extends Entity> List<T> getEntities(Vec3d vec3d, List<T> list) {
		if (list.size() > 1) {
			this.sorter.accept(vec3d, list);
		}

		return list.subList(0, Math.min(this.limit, list.size()));
	}

	public static Text getNames(List<? extends Entity> list) {
		return Texts.join(list, Entity::getDisplayName);
	}
}
