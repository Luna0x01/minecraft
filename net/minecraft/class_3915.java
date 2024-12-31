package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class class_3915 implements class_3965 {
	public static final SimpleCommandExceptionType field_19274 = new SimpleCommandExceptionType(new TranslatableText("permissions.requires.player"));
	public static final SimpleCommandExceptionType field_19275 = new SimpleCommandExceptionType(new TranslatableText("permissions.requires.entity"));
	private final class_3893 field_19276;
	private final Vec3d field_19277;
	private final ServerWorld field_19278;
	private final int field_19279;
	private final String field_19280;
	private final Text field_19281;
	private final MinecraftServer field_19282;
	private final boolean field_19283;
	@Nullable
	private final Entity field_19284;
	private final ResultConsumer<class_3915> field_19285;
	private final class_4048.class_4049 field_19286;
	private final Vec2f field_19287;

	public class_3915(
		class_3893 arg, Vec3d vec3d, Vec2f vec2f, ServerWorld serverWorld, int i, String string, Text text, MinecraftServer minecraftServer, @Nullable Entity entity
	) {
		this(arg, vec3d, vec2f, serverWorld, i, string, text, minecraftServer, entity, false, (commandContext, bl, ix) -> {
		}, class_4048.class_4049.FEET);
	}

	protected class_3915(
		class_3893 arg,
		Vec3d vec3d,
		Vec2f vec2f,
		ServerWorld serverWorld,
		int i,
		String string,
		Text text,
		MinecraftServer minecraftServer,
		@Nullable Entity entity,
		boolean bl,
		ResultConsumer<class_3915> resultConsumer,
		class_4048.class_4049 arg2
	) {
		this.field_19276 = arg;
		this.field_19277 = vec3d;
		this.field_19278 = serverWorld;
		this.field_19283 = bl;
		this.field_19284 = entity;
		this.field_19279 = i;
		this.field_19280 = string;
		this.field_19281 = text;
		this.field_19282 = minecraftServer;
		this.field_19285 = resultConsumer;
		this.field_19286 = arg2;
		this.field_19287 = vec2f;
	}

	public class_3915 method_17450(Entity entity) {
		return this.field_19284 == entity
			? this
			: new class_3915(
				this.field_19276,
				this.field_19277,
				this.field_19287,
				this.field_19278,
				this.field_19279,
				entity.method_15540().getString(),
				entity.getName(),
				this.field_19282,
				entity,
				this.field_19283,
				this.field_19285,
				this.field_19286
			);
	}

	public class_3915 method_17454(Vec3d vec3d) {
		return this.field_19277.equals(vec3d)
			? this
			: new class_3915(
				this.field_19276,
				vec3d,
				this.field_19287,
				this.field_19278,
				this.field_19279,
				this.field_19280,
				this.field_19281,
				this.field_19282,
				this.field_19284,
				this.field_19283,
				this.field_19285,
				this.field_19286
			);
	}

	public class_3915 method_17453(Vec2f vec2f) {
		return this.field_19287.method_18011(vec2f)
			? this
			: new class_3915(
				this.field_19276,
				this.field_19277,
				vec2f,
				this.field_19278,
				this.field_19279,
				this.field_19280,
				this.field_19281,
				this.field_19282,
				this.field_19284,
				this.field_19283,
				this.field_19285,
				this.field_19286
			);
	}

	public class_3915 method_17455(ResultConsumer<class_3915> resultConsumer) {
		return this.field_19285.equals(resultConsumer)
			? this
			: new class_3915(
				this.field_19276,
				this.field_19277,
				this.field_19287,
				this.field_19278,
				this.field_19279,
				this.field_19280,
				this.field_19281,
				this.field_19282,
				this.field_19284,
				this.field_19283,
				resultConsumer,
				this.field_19286
			);
	}

	public class_3915 method_17456(ResultConsumer<class_3915> resultConsumer, BinaryOperator<ResultConsumer<class_3915>> binaryOperator) {
		ResultConsumer<class_3915> resultConsumer2 = (ResultConsumer<class_3915>)binaryOperator.apply(this.field_19285, resultConsumer);
		return this.method_17455(resultConsumer2);
	}

	public class_3915 method_17448() {
		return this.field_19283
			? this
			: new class_3915(
				this.field_19276,
				this.field_19277,
				this.field_19287,
				this.field_19278,
				this.field_19279,
				this.field_19280,
				this.field_19281,
				this.field_19282,
				this.field_19284,
				true,
				this.field_19285,
				this.field_19286
			);
	}

	public class_3915 method_17449(int i) {
		return i == this.field_19279
			? this
			: new class_3915(
				this.field_19276,
				this.field_19277,
				this.field_19287,
				this.field_19278,
				i,
				this.field_19280,
				this.field_19281,
				this.field_19282,
				this.field_19284,
				this.field_19283,
				this.field_19285,
				this.field_19286
			);
	}

	public class_3915 method_17462(int i) {
		return i <= this.field_19279
			? this
			: new class_3915(
				this.field_19276,
				this.field_19277,
				this.field_19287,
				this.field_19278,
				i,
				this.field_19280,
				this.field_19281,
				this.field_19282,
				this.field_19284,
				this.field_19283,
				this.field_19285,
				this.field_19286
			);
	}

	public class_3915 method_17452(class_4048.class_4049 arg) {
		return arg == this.field_19286
			? this
			: new class_3915(
				this.field_19276,
				this.field_19277,
				this.field_19287,
				this.field_19278,
				this.field_19279,
				this.field_19280,
				this.field_19281,
				this.field_19282,
				this.field_19284,
				this.field_19283,
				this.field_19285,
				arg
			);
	}

	public class_3915 method_17460(ServerWorld serverWorld) {
		return serverWorld == this.field_19278
			? this
			: new class_3915(
				this.field_19276,
				this.field_19277,
				this.field_19287,
				serverWorld,
				this.field_19279,
				this.field_19280,
				this.field_19281,
				this.field_19282,
				this.field_19284,
				this.field_19283,
				this.field_19285,
				this.field_19286
			);
	}

	public class_3915 method_17451(Entity entity, class_4048.class_4049 arg) throws CommandSyntaxException {
		return this.method_17463(arg.method_17870(entity));
	}

	public class_3915 method_17463(Vec3d vec3d) throws CommandSyntaxException {
		Vec3d vec3d2 = this.field_19286.method_17871(this);
		double d = vec3d.x - vec3d2.x;
		double e = vec3d.y - vec3d2.y;
		double f = vec3d.z - vec3d2.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		float h = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)));
		float i = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F);
		return this.method_17453(new Vec2f(h, i));
	}

	public Text method_17461() {
		return this.field_19281;
	}

	public String method_17466() {
		return this.field_19280;
	}

	@Override
	public boolean method_17575(int i) {
		return this.field_19279 >= i;
	}

	public Vec3d method_17467() {
		return this.field_19277;
	}

	public ServerWorld method_17468() {
		return this.field_19278;
	}

	@Nullable
	public Entity method_17469() {
		return this.field_19284;
	}

	public Entity method_17470() throws CommandSyntaxException {
		if (this.field_19284 == null) {
			throw field_19275.create();
		} else {
			return this.field_19284;
		}
	}

	public ServerPlayerEntity method_17471() throws CommandSyntaxException {
		if (!(this.field_19284 instanceof ServerPlayerEntity)) {
			throw field_19274.create();
		} else {
			return (ServerPlayerEntity)this.field_19284;
		}
	}

	public Vec2f method_17472() {
		return this.field_19287;
	}

	public MinecraftServer method_17473() {
		return this.field_19282;
	}

	public class_4048.class_4049 method_17474() {
		return this.field_19286;
	}

	public void method_17459(Text text, boolean bl) {
		if (this.field_19276.method_17413() && !this.field_19283) {
			this.field_19276.method_5505(text);
		}

		if (bl && this.field_19276.method_17412() && !this.field_19283) {
			this.method_17465(text);
		}
	}

	private void method_17465(Text text) {
		Text text2 = new TranslatableText("chat.type.admin", this.method_17461(), text).formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC});
		if (this.field_19282.method_20335().getBoolean("sendCommandFeedback")) {
			for (ServerPlayerEntity serverPlayerEntity : this.field_19282.getPlayerManager().getPlayers()) {
				if (serverPlayerEntity != this.field_19276 && this.field_19282.getPlayerManager().isOperator(serverPlayerEntity.getGameProfile())) {
					serverPlayerEntity.method_5505(text2);
				}
			}
		}

		if (this.field_19276 != this.field_19282 && this.field_19282.method_20335().getBoolean("logAdminCommands")) {
			this.field_19282.method_5505(text2);
		}
	}

	public void method_17458(Text text) {
		if (this.field_19276.method_17414() && !this.field_19283) {
			this.field_19276.method_5505(new LiteralText("").append(text).formatted(Formatting.RED));
		}
	}

	public void method_17457(CommandContext<class_3915> commandContext, boolean bl, int i) {
		if (this.field_19285 != null) {
			this.field_19285.onCommandComplete(commandContext, bl, i);
		}
	}

	@Override
	public Collection<String> method_17576() {
		return Lists.newArrayList(this.field_19282.getPlayerNames());
	}

	@Override
	public Collection<String> method_17577() {
		return this.field_19282.method_20333().getTeamNames();
	}

	@Override
	public Collection<Identifier> method_17578() {
		return Registry.SOUND_EVENT.getKeySet();
	}

	@Override
	public Collection<Identifier> method_17579() {
		return this.field_19282.method_20331().method_16210();
	}

	@Override
	public CompletableFuture<Suggestions> method_17555(CommandContext<class_3965> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return null;
	}

	@Override
	public Collection<class_3965.class_3966> method_17569(boolean bl) {
		return Collections.singleton(class_3965.class_3966.field_19335);
	}
}
