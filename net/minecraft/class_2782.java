package net.minecraft;

import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.class_2780;
import net.minecraft.loot.class_2787;
import net.minecraft.server.world.ServerWorld;

public class class_2782 {
	private final float field_13188;
	private final ServerWorld server;
	private final class_2787 field_13190;
	@Nullable
	private final Entity field_13191;
	@Nullable
	private final PlayerEntity field_13192;
	@Nullable
	private final DamageSource field_13193;
	private final Set<class_2780> field_13194 = Sets.newLinkedHashSet();

	public class_2782(
		float f, ServerWorld serverWorld, class_2787 arg, @Nullable Entity entity, @Nullable PlayerEntity playerEntity, @Nullable DamageSource damageSource
	) {
		this.field_13188 = f;
		this.server = serverWorld;
		this.field_13190 = arg;
		this.field_13191 = entity;
		this.field_13192 = playerEntity;
		this.field_13193 = damageSource;
	}

	@Nullable
	public Entity method_11986() {
		return this.field_13191;
	}

	@Nullable
	public Entity method_11989() {
		return this.field_13192;
	}

	@Nullable
	public Entity method_11991() {
		return this.field_13193 == null ? null : this.field_13193.getAttacker();
	}

	public boolean method_11987(class_2780 arg) {
		return this.field_13194.add(arg);
	}

	public void method_11990(class_2780 arg) {
		this.field_13194.remove(arg);
	}

	public class_2787 method_11992() {
		return this.field_13190;
	}

	public float method_11993() {
		return this.field_13188;
	}

	@Nullable
	public Entity method_11988(class_2782.class_2784 arg) {
		switch (arg) {
			case THIS:
				return this.method_11986();
			case KILLER:
				return this.method_11991();
			case KILLER_PLAYER:
				return this.method_11989();
			default:
				return null;
		}
	}

	public static class class_2783 {
		private final ServerWorld field_13196;
		private float field_13197;
		private Entity field_13198;
		private PlayerEntity field_13199;
		private DamageSource field_13200;

		public class_2783(ServerWorld serverWorld) {
			this.field_13196 = serverWorld;
		}

		public class_2782.class_2783 method_11995(float f) {
			this.field_13197 = f;
			return this;
		}

		public class_2782.class_2783 method_11997(Entity entity) {
			this.field_13198 = entity;
			return this;
		}

		public class_2782.class_2783 method_11998(PlayerEntity playerEntity) {
			this.field_13199 = playerEntity;
			return this;
		}

		public class_2782.class_2783 method_11996(DamageSource damageSource) {
			this.field_13200 = damageSource;
			return this;
		}

		public class_2782 method_11994() {
			return new class_2782(this.field_13197, this.field_13196, this.field_13196.method_11487(), this.field_13198, this.field_13199, this.field_13200);
		}
	}

	public static enum class_2784 {
		THIS("this"),
		KILLER("killer"),
		KILLER_PLAYER("killer_player");

		private final String field_13204;

		private class_2784(String string2) {
			this.field_13204 = string2;
		}

		public static class_2782.class_2784 method_12000(String string) {
			for (class_2782.class_2784 lv : values()) {
				if (lv.field_13204.equals(string)) {
					return lv;
				}
			}

			throw new IllegalArgumentException("Invalid entity target " + string);
		}

		public static class class_2785 extends TypeAdapter<class_2782.class_2784> {
			public void write(JsonWriter jsonWriter, class_2782.class_2784 arg) throws IOException {
				jsonWriter.value(arg.field_13204);
			}

			public class_2782.class_2784 read(JsonReader jsonReader) throws IOException {
				return class_2782.class_2784.method_12000(jsonReader.nextString());
			}
		}
	}
}
