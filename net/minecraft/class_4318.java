package net.minecraft;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_4318 {
	public static final SimpleCommandExceptionType field_21208 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.invalid"));
	public static final DynamicCommandExceptionType field_21209 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.entity.selector.unknown", object)
	);
	public static final SimpleCommandExceptionType field_21210 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.selector.not_allowed"));
	public static final SimpleCommandExceptionType field_21211 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.selector.missing"));
	public static final SimpleCommandExceptionType field_21212 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.options.unterminated"));
	public static final DynamicCommandExceptionType field_21213 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.entity.options.valueless", object)
	);
	public static final BiConsumer<Vec3d, List<? extends Entity>> field_21214 = (vec3d, list) -> {
	};
	public static final BiConsumer<Vec3d, List<? extends Entity>> field_21215 = (vec3d, list) -> list.sort(
			(entity, entity2) -> Doubles.compare(entity.method_15564(vec3d), entity2.method_15564(vec3d))
		);
	public static final BiConsumer<Vec3d, List<? extends Entity>> field_21216 = (vec3d, list) -> list.sort(
			(entity, entity2) -> Doubles.compare(entity2.method_15564(vec3d), entity.method_15564(vec3d))
		);
	public static final BiConsumer<Vec3d, List<? extends Entity>> field_21217 = (vec3d, list) -> Collections.shuffle(list);
	public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> field_21218 = (suggestionsBuilder, consumer) -> suggestionsBuilder.buildFuture();
	private final StringReader field_21219;
	private final boolean field_21220;
	private int field_21221;
	private boolean field_21222;
	private boolean field_21223;
	private class_3638.class_3641 field_21224 = class_3638.class_3641.field_17695;
	private class_3638.class_3642 field_21225 = class_3638.class_3642.field_17698;
	@Nullable
	private Double field_21226;
	@Nullable
	private Double field_21227;
	@Nullable
	private Double field_21228;
	@Nullable
	private Double field_21229;
	@Nullable
	private Double field_21230;
	@Nullable
	private Double field_21231;
	private class_3783 field_21232 = class_3783.field_18843;
	private class_3783 field_21233 = class_3783.field_18843;
	private Predicate<Entity> field_21188 = entity -> true;
	private BiConsumer<Vec3d, List<? extends Entity>> field_21189 = field_21214;
	private boolean field_21190;
	@Nullable
	private String field_21191;
	private int field_21192;
	@Nullable
	private UUID field_21193;
	private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> field_21194 = field_21218;
	private boolean field_21195;
	private boolean field_21196;
	private boolean field_21197;
	private boolean field_21198;
	private boolean field_21199;
	private boolean field_21200;
	private boolean field_21201;
	private boolean field_21202;
	private Class<? extends Entity> field_21203;
	private boolean field_21204;
	private boolean field_21205;
	private boolean field_21206;
	private boolean field_21207;

	public class_4318(StringReader stringReader) {
		this(stringReader, true);
	}

	public class_4318(StringReader stringReader, boolean bl) {
		this.field_21219 = stringReader;
		this.field_21220 = bl;
	}

	public class_4317 method_19748() {
		Box box2;
		if (this.field_21229 == null && this.field_21230 == null && this.field_21231 == null) {
			if (this.field_21224.method_16511() != null) {
				float f = (Float)this.field_21224.method_16511();
				box2 = new Box((double)(-f), (double)(-f), (double)(-f), (double)(f + 1.0F), (double)(f + 1.0F), (double)(f + 1.0F));
			} else {
				box2 = null;
			}
		} else {
			box2 = this.method_19750(
				this.field_21229 == null ? 0.0 : this.field_21229, this.field_21230 == null ? 0.0 : this.field_21230, this.field_21231 == null ? 0.0 : this.field_21231
			);
		}

		Function<Vec3d, Vec3d> function;
		if (this.field_21226 == null && this.field_21227 == null && this.field_21228 == null) {
			function = vec3d -> vec3d;
		} else {
			function = vec3d -> new Vec3d(
					this.field_21226 == null ? vec3d.x : this.field_21226,
					this.field_21227 == null ? vec3d.y : this.field_21227,
					this.field_21228 == null ? vec3d.z : this.field_21228
				);
		}

		return new class_4317(
			this.field_21221,
			this.field_21222,
			this.field_21223,
			this.field_21188,
			this.field_21224,
			function,
			box2,
			this.field_21189,
			this.field_21190,
			this.field_21191,
			this.field_21193,
			this.field_21203 == null ? Entity.class : this.field_21203,
			this.field_21207
		);
	}

	private Box method_19750(double d, double e, double f) {
		boolean bl = d < 0.0;
		boolean bl2 = e < 0.0;
		boolean bl3 = f < 0.0;
		double g = bl ? d : 0.0;
		double h = bl2 ? e : 0.0;
		double i = bl3 ? f : 0.0;
		double j = (bl ? 0.0 : d) + 1.0;
		double k = (bl2 ? 0.0 : e) + 1.0;
		double l = (bl3 ? 0.0 : f) + 1.0;
		return new Box(g, h, i, j, k, l);
	}

	private void method_19747() {
		if (this.field_21232 != class_3783.field_18843) {
			this.field_21188 = this.field_21188.and(this.method_19756(this.field_21232, entity -> (double)entity.pitch));
		}

		if (this.field_21233 != class_3783.field_18843) {
			this.field_21188 = this.field_21188.and(this.method_19756(this.field_21233, entity -> (double)entity.yaw));
		}

		if (!this.field_21225.method_16512()) {
			this.field_21188 = this.field_21188
				.and(entity -> !(entity instanceof ServerPlayerEntity) ? false : this.field_21225.method_16531(((ServerPlayerEntity)entity).experienceLevel));
		}
	}

	private Predicate<Entity> method_19756(class_3783 arg, ToDoubleFunction<Entity> toDoubleFunction) {
		double d = (double)MathHelper.wrapDegrees(arg.method_17032() == null ? 0.0F : arg.method_17032());
		double e = (double)MathHelper.wrapDegrees(arg.method_17035() == null ? 359.0F : arg.method_17035());
		return entity -> {
			double f = MathHelper.wrapDegrees(toDoubleFunction.applyAsDouble(entity));
			return d > e ? f >= d || f <= e : f >= d && f <= e;
		};
	}

	protected void method_19769() throws CommandSyntaxException {
		this.field_21207 = true;
		this.field_21194 = this::method_19788;
		if (!this.field_21219.canRead()) {
			throw field_21211.createWithContext(this.field_21219);
		} else {
			int i = this.field_21219.getCursor();
			char c = this.field_21219.read();
			if (c == 'p') {
				this.field_21221 = 1;
				this.field_21222 = false;
				this.field_21189 = field_21215;
				this.method_19762(ServerPlayerEntity.class);
			} else if (c == 'a') {
				this.field_21221 = Integer.MAX_VALUE;
				this.field_21222 = false;
				this.field_21189 = field_21214;
				this.method_19762(ServerPlayerEntity.class);
			} else if (c == 'r') {
				this.field_21221 = 1;
				this.field_21222 = false;
				this.field_21189 = field_21217;
				this.method_19762(ServerPlayerEntity.class);
			} else if (c == 's') {
				this.field_21221 = 1;
				this.field_21222 = true;
				this.field_21190 = true;
			} else {
				if (c != 'e') {
					this.field_21219.setCursor(i);
					throw field_21209.createWithContext(this.field_21219, '@' + String.valueOf(c));
				}

				this.field_21221 = Integer.MAX_VALUE;
				this.field_21222 = true;
				this.field_21189 = field_21214;
				this.field_21188 = Entity::isAlive;
			}

			this.field_21194 = this::method_19792;
			if (this.field_21219.canRead() && this.field_21219.peek() == '[') {
				this.field_21219.skip();
				this.field_21194 = this::method_19796;
				this.method_19784();
			}
		}
	}

	protected void method_19778() throws CommandSyntaxException {
		if (this.field_21219.canRead()) {
			this.field_21194 = this::method_19782;
		}

		int i = this.field_21219.getCursor();
		String string = this.field_21219.readString();

		try {
			this.field_21193 = UUID.fromString(string);
			this.field_21222 = true;
		} catch (IllegalArgumentException var4) {
			if (string.isEmpty() || string.length() > 16) {
				this.field_21219.setCursor(i);
				throw field_21208.createWithContext(this.field_21219);
			}

			this.field_21222 = false;
			this.field_21191 = string;
		}

		this.field_21221 = 1;
	}

	protected void method_19784() throws CommandSyntaxException {
		this.field_21194 = this::method_19799;
		this.field_21219.skipWhitespace();

		while (this.field_21219.canRead() && this.field_21219.peek() != ']') {
			this.field_21219.skipWhitespace();
			int i = this.field_21219.getCursor();
			String string = this.field_21219.readString();
			class_4319.class_4320 lv = class_4319.method_19846(this, string, i);
			this.field_21219.skipWhitespace();
			if (!this.field_21219.canRead() || this.field_21219.peek() != '=') {
				this.field_21219.setCursor(i);
				throw field_21213.createWithContext(this.field_21219, string);
			}

			this.field_21219.skip();
			this.field_21219.skipWhitespace();
			this.field_21194 = field_21218;
			lv.handle(this);
			this.field_21219.skipWhitespace();
			this.field_21194 = this::method_19802;
			if (this.field_21219.canRead()) {
				if (this.field_21219.peek() != ',') {
					if (this.field_21219.peek() != ']') {
						throw field_21212.createWithContext(this.field_21219);
					}
					break;
				}

				this.field_21219.skip();
				this.field_21194 = this::method_19799;
			}
		}

		if (this.field_21219.canRead()) {
			this.field_21219.skip();
			this.field_21194 = field_21218;
		} else {
			throw field_21212.createWithContext(this.field_21219);
		}
	}

	public boolean method_19790() {
		this.field_21219.skipWhitespace();
		if (this.field_21219.canRead() && this.field_21219.peek() == '!') {
			this.field_21219.skip();
			this.field_21219.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	public StringReader method_19794() {
		return this.field_21219;
	}

	public void method_19766(Predicate<Entity> predicate) {
		this.field_21188 = this.field_21188.and(predicate);
	}

	public void method_19798() {
		this.field_21223 = true;
	}

	public class_3638.class_3641 method_19801() {
		return this.field_21224;
	}

	public void method_19753(class_3638.class_3641 arg) {
		this.field_21224 = arg;
	}

	public class_3638.class_3642 method_19804() {
		return this.field_21225;
	}

	public void method_19754(class_3638.class_3642 arg) {
		this.field_21225 = arg;
	}

	public class_3783 method_19806() {
		return this.field_21232;
	}

	public void method_19755(class_3783 arg) {
		this.field_21232 = arg;
	}

	public class_3783 method_19809() {
		return this.field_21233;
	}

	public void method_19772(class_3783 arg) {
		this.field_21233 = arg;
	}

	@Nullable
	public Double method_19811() {
		return this.field_21226;
	}

	@Nullable
	public Double method_19813() {
		return this.field_21227;
	}

	@Nullable
	public Double method_19814() {
		return this.field_21228;
	}

	public void method_19749(double d) {
		this.field_21226 = d;
	}

	public void method_19770(double d) {
		this.field_21227 = d;
	}

	public void method_19779(double d) {
		this.field_21228 = d;
	}

	public void method_19785(double d) {
		this.field_21229 = d;
	}

	public void method_19791(double d) {
		this.field_21230 = d;
	}

	public void method_19795(double d) {
		this.field_21231 = d;
	}

	@Nullable
	public Double method_19815() {
		return this.field_21229;
	}

	@Nullable
	public Double method_19816() {
		return this.field_21230;
	}

	@Nullable
	public Double method_19817() {
		return this.field_21231;
	}

	public void method_19751(int i) {
		this.field_21221 = i;
	}

	public void method_19768(boolean bl) {
		this.field_21222 = bl;
	}

	public void method_19764(BiConsumer<Vec3d, List<? extends Entity>> biConsumer) {
		this.field_21189 = biConsumer;
	}

	public class_4317 method_19818() throws CommandSyntaxException {
		this.field_21192 = this.field_21219.getCursor();
		this.field_21194 = this::method_19776;
		if (this.field_21219.canRead() && this.field_21219.peek() == '@') {
			if (!this.field_21220) {
				throw field_21210.createWithContext(this.field_21219);
			}

			this.field_21219.skip();
			this.method_19769();
		} else {
			this.method_19778();
		}

		this.method_19747();
		return this.method_19748();
	}

	private static void method_19760(SuggestionsBuilder suggestionsBuilder) {
		suggestionsBuilder.suggest("@p", new TranslatableText("argument.entity.selector.nearestPlayer"));
		suggestionsBuilder.suggest("@a", new TranslatableText("argument.entity.selector.allPlayers"));
		suggestionsBuilder.suggest("@r", new TranslatableText("argument.entity.selector.randomPlayer"));
		suggestionsBuilder.suggest("@s", new TranslatableText("argument.entity.selector.self"));
		suggestionsBuilder.suggest("@e", new TranslatableText("argument.entity.selector.allEntities"));
	}

	private CompletableFuture<Suggestions> method_19776(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		consumer.accept(suggestionsBuilder);
		if (this.field_21220) {
			method_19760(suggestionsBuilder);
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19782(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.field_21192);
		consumer.accept(suggestionsBuilder2);
		return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
	}

	private CompletableFuture<Suggestions> method_19788(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(suggestionsBuilder.getStart() - 1);
		method_19760(suggestionsBuilder2);
		suggestionsBuilder.add(suggestionsBuilder2);
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19792(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		suggestionsBuilder.suggest(String.valueOf('['));
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19796(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		suggestionsBuilder.suggest(String.valueOf(']'));
		class_4319.method_19844(this, suggestionsBuilder);
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19799(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		class_4319.method_19844(this, suggestionsBuilder);
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_19802(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		suggestionsBuilder.suggest(String.valueOf(','));
		suggestionsBuilder.suggest(String.valueOf(']'));
		return suggestionsBuilder.buildFuture();
	}

	public boolean method_19819() {
		return this.field_21190;
	}

	public void method_19765(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> biFunction) {
		this.field_21194 = biFunction;
	}

	public CompletableFuture<Suggestions> method_19761(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		return (CompletableFuture<Suggestions>)this.field_21194.apply(suggestionsBuilder.createOffset(this.field_21219.getCursor()), consumer);
	}

	public boolean method_19820() {
		return this.field_21195;
	}

	public void method_19783(boolean bl) {
		this.field_21195 = bl;
	}

	public boolean method_19821() {
		return this.field_21196;
	}

	public void method_19789(boolean bl) {
		this.field_21196 = bl;
	}

	public boolean method_19822() {
		return this.field_21197;
	}

	public void method_19793(boolean bl) {
		this.field_21197 = bl;
	}

	public boolean method_19823() {
		return this.field_21198;
	}

	public void method_19797(boolean bl) {
		this.field_21198 = bl;
	}

	public boolean method_19824() {
		return this.field_21199;
	}

	public void method_19800(boolean bl) {
		this.field_21199 = bl;
	}

	public boolean method_19825() {
		return this.field_21200;
	}

	public void method_19803(boolean bl) {
		this.field_21200 = bl;
	}

	public boolean method_19741() {
		return this.field_21201;
	}

	public void method_19805(boolean bl) {
		this.field_21201 = bl;
	}

	public void method_19808(boolean bl) {
		this.field_21202 = bl;
	}

	public void method_19762(Class<? extends Entity> class_) {
		this.field_21203 = class_;
	}

	public void method_19742() {
		this.field_21204 = true;
	}

	public boolean method_19743() {
		return this.field_21203 != null;
	}

	public boolean method_19744() {
		return this.field_21204;
	}

	public boolean method_19745() {
		return this.field_21205;
	}

	public void method_19810(boolean bl) {
		this.field_21205 = bl;
	}

	public boolean method_19746() {
		return this.field_21206;
	}

	public void method_19812(boolean bl) {
		this.field_21206 = bl;
	}
}
