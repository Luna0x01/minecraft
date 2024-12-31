package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_4203 implements class_3965 {
	private final ClientPlayNetworkHandler field_20622;
	private final MinecraftClient field_20623;
	private int field_20624 = -1;
	private CompletableFuture<Suggestions> field_20625;

	public class_4203(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
		this.field_20622 = clientPlayNetworkHandler;
		this.field_20623 = minecraftClient;
	}

	@Override
	public Collection<String> method_17576() {
		List<String> list = Lists.newArrayList();

		for (PlayerListEntry playerListEntry : this.field_20622.getPlayerList()) {
			list.add(playerListEntry.getProfile().getName());
		}

		return list;
	}

	@Override
	public Collection<String> method_17580() {
		return (Collection<String>)(this.field_20623.result != null && this.field_20623.result.type == BlockHitResult.Type.ENTITY
			? Collections.singleton(this.field_20623.result.entity.getEntityName())
			: Collections.emptyList());
	}

	@Override
	public Collection<String> method_17577() {
		return this.field_20622.method_18964().getScoreboard().getTeamNames();
	}

	@Override
	public Collection<Identifier> method_17578() {
		return this.field_20623.getSoundManager().method_19628();
	}

	@Override
	public Collection<Identifier> method_17579() {
		return this.field_20622.method_18962().method_16210();
	}

	@Override
	public boolean method_17575(int i) {
		ClientPlayerEntity clientPlayerEntity = this.field_20623.player;
		return clientPlayerEntity != null ? clientPlayerEntity.method_15592(i) : i == 0;
	}

	@Override
	public CompletableFuture<Suggestions> method_17555(CommandContext<class_3965> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (this.field_20625 != null) {
			this.field_20625.cancel(false);
		}

		this.field_20625 = new CompletableFuture();
		int i = ++this.field_20624;
		this.field_20622.sendPacket(new RequestCommandCompletionsC2SPacket(i, commandContext.getInput()));
		return this.field_20625;
	}

	private static String method_18967(double d) {
		return String.format(Locale.ROOT, "%.2f", d);
	}

	private static String method_18968(int i) {
		return Integer.toString(i);
	}

	@Override
	public Collection<class_3965.class_3966> method_17569(boolean bl) {
		if (this.field_20623.result == null || this.field_20623.result.type != BlockHitResult.Type.BLOCK) {
			return Collections.singleton(class_3965.class_3966.field_19335);
		} else if (bl) {
			Vec3d vec3d = this.field_20623.result.pos;
			return Collections.singleton(new class_3965.class_3966(method_18967(vec3d.x), method_18967(vec3d.y), method_18967(vec3d.z)));
		} else {
			BlockPos blockPos = this.field_20623.result.getBlockPos();
			return Collections.singleton(new class_3965.class_3966(method_18968(blockPos.getX()), method_18968(blockPos.getY()), method_18968(blockPos.getZ())));
		}
	}

	public void method_18969(int i, Suggestions suggestions) {
		if (i == this.field_20624) {
			this.field_20625.complete(suggestions);
			this.field_20625 = null;
			this.field_20624 = -1;
		}
	}
}
