package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class class_4317 {
	private final int field_21175;
	private final boolean field_21176;
	private final boolean field_21177;
	private final Predicate<Entity> field_21178;
	private final class_3638.class_3641 field_21179;
	private final Function<Vec3d, Vec3d> field_21180;
	@Nullable
	private final Box field_21181;
	private final BiConsumer<Vec3d, List<? extends Entity>> field_21182;
	private final boolean field_21183;
	@Nullable
	private final String field_21184;
	@Nullable
	private final UUID field_21185;
	private final Class<? extends Entity> field_21186;
	private final boolean field_21187;

	public class_4317(
		int i,
		boolean bl,
		boolean bl2,
		Predicate<Entity> predicate,
		class_3638.class_3641 arg,
		Function<Vec3d, Vec3d> function,
		@Nullable Box box,
		BiConsumer<Vec3d, List<? extends Entity>> biConsumer,
		boolean bl3,
		@Nullable String string,
		@Nullable UUID uUID,
		Class<? extends Entity> class_,
		boolean bl4
	) {
		this.field_21175 = i;
		this.field_21176 = bl;
		this.field_21177 = bl2;
		this.field_21178 = predicate;
		this.field_21179 = arg;
		this.field_21180 = function;
		this.field_21181 = box;
		this.field_21182 = biConsumer;
		this.field_21183 = bl3;
		this.field_21184 = string;
		this.field_21185 = uUID;
		this.field_21186 = class_;
		this.field_21187 = bl4;
	}

	public int method_19726() {
		return this.field_21175;
	}

	public boolean method_19734() {
		return this.field_21176;
	}

	public boolean method_19736() {
		return this.field_21183;
	}

	public boolean method_19738() {
		return this.field_21177;
	}

	private void method_19740(class_3915 arg) throws CommandSyntaxException {
		if (this.field_21187 && !arg.method_17575(2)) {
			throw class_4062.field_19709.create();
		}
	}

	public Entity method_19727(class_3915 arg) throws CommandSyntaxException {
		this.method_19740(arg);
		List<? extends Entity> list = this.method_19735(arg);
		if (list.isEmpty()) {
			throw class_4062.field_19707.create();
		} else if (list.size() > 1) {
			throw class_4062.field_19704.create();
		} else {
			return (Entity)list.get(0);
		}
	}

	public List<? extends Entity> method_19735(class_3915 arg) throws CommandSyntaxException {
		this.method_19740(arg);
		if (!this.field_21176) {
			return this.method_19739(arg);
		} else if (this.field_21184 != null) {
			ServerPlayerEntity serverPlayerEntity = arg.method_17473().getPlayerManager().getPlayer(this.field_21184);
			return (List<? extends Entity>)(serverPlayerEntity == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayerEntity[]{serverPlayerEntity}));
		} else if (this.field_21185 != null) {
			for (ServerWorld serverWorld : arg.method_17473().method_20351()) {
				Entity entity = serverWorld.getEntity(this.field_21185);
				if (entity != null) {
					return Lists.newArrayList(new Entity[]{entity});
				}
			}

			return Collections.emptyList();
		} else {
			Vec3d vec3d = (Vec3d)this.field_21180.apply(arg.method_17467());
			Predicate<Entity> predicate = this.method_19729(vec3d);
			if (this.field_21183) {
				return (List<? extends Entity>)(arg.method_17469() != null && predicate.test(arg.method_17469())
					? Lists.newArrayList(new Entity[]{arg.method_17469()})
					: Collections.emptyList());
			} else {
				List<Entity> list = Lists.newArrayList();
				if (this.method_19738()) {
					this.method_19733(list, arg.method_17468(), vec3d, predicate);
				} else {
					for (ServerWorld serverWorld2 : arg.method_17473().method_20351()) {
						this.method_19733(list, serverWorld2, vec3d, predicate);
					}
				}

				return this.method_19731(vec3d, list);
			}
		}
	}

	private void method_19733(List<Entity> list, ServerWorld serverWorld, Vec3d vec3d, Predicate<Entity> predicate) {
		if (this.field_21181 != null) {
			list.addAll(serverWorld.method_16325(this.field_21186, this.field_21181.offset(vec3d), predicate::test));
		} else {
			list.addAll(serverWorld.method_16326(this.field_21186, predicate::test));
		}
	}

	public ServerPlayerEntity method_19737(class_3915 arg) throws CommandSyntaxException {
		this.method_19740(arg);
		List<ServerPlayerEntity> list = this.method_19739(arg);
		if (list.size() != 1) {
			throw class_4062.field_19708.create();
		} else {
			return (ServerPlayerEntity)list.get(0);
		}
	}

	public List<ServerPlayerEntity> method_19739(class_3915 arg) throws CommandSyntaxException {
		this.method_19740(arg);
		if (this.field_21184 != null) {
			ServerPlayerEntity serverPlayerEntity = arg.method_17473().getPlayerManager().getPlayer(this.field_21184);
			return (List<ServerPlayerEntity>)(serverPlayerEntity == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayerEntity[]{serverPlayerEntity}));
		} else if (this.field_21185 != null) {
			ServerPlayerEntity serverPlayerEntity2 = arg.method_17473().getPlayerManager().getPlayer(this.field_21185);
			return (List<ServerPlayerEntity>)(serverPlayerEntity2 == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayerEntity[]{serverPlayerEntity2}));
		} else {
			Vec3d vec3d = (Vec3d)this.field_21180.apply(arg.method_17467());
			Predicate<Entity> predicate = this.method_19729(vec3d);
			if (this.field_21183) {
				if (arg.method_17469() instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity3 = (ServerPlayerEntity)arg.method_17469();
					if (predicate.test(serverPlayerEntity3)) {
						return Lists.newArrayList(new ServerPlayerEntity[]{serverPlayerEntity3});
					}
				}

				return Collections.emptyList();
			} else {
				List<ServerPlayerEntity> list;
				if (this.method_19738()) {
					list = arg.method_17468().method_16334(ServerPlayerEntity.class, predicate::test);
				} else {
					list = Lists.newArrayList();

					for (ServerPlayerEntity serverPlayerEntity4 : arg.method_17473().getPlayerManager().getPlayers()) {
						if (predicate.test(serverPlayerEntity4)) {
							list.add(serverPlayerEntity4);
						}
					}
				}

				return this.method_19731(vec3d, list);
			}
		}
	}

	private Predicate<Entity> method_19729(Vec3d vec3d) {
		Predicate<Entity> predicate = this.field_21178;
		if (this.field_21181 != null) {
			Box box = this.field_21181.offset(vec3d);
			predicate = predicate.and(entity -> box.intersects(entity.getBoundingBox()));
		}

		if (!this.field_21179.method_16512()) {
			predicate = predicate.and(entity -> this.field_21179.method_16514(entity.method_15564(vec3d)));
		}

		return predicate;
	}

	private <T extends Entity> List<T> method_19731(Vec3d vec3d, List<T> list) {
		if (list.size() > 1) {
			this.field_21182.accept(vec3d, list);
		}

		return list.subList(0, Math.min(this.field_21175, list.size()));
	}

	public static Text method_19732(List<? extends Entity> list) {
		return ChatSerializer.method_20193(list, Entity::getName);
	}
}
