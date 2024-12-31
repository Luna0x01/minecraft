package net.minecraft.command;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface CommandSource {
	String getTranslationKey();

	default Text getName() {
		return new LiteralText(this.getTranslationKey());
	}

	default void sendMessage(Text text) {
	}

	boolean canUseCommand(int permissionLevel, String commandLiteral);

	default BlockPos getBlockPos() {
		return BlockPos.ORIGIN;
	}

	default Vec3d getPos() {
		return Vec3d.ZERO;
	}

	World getWorld();

	@Nullable
	default Entity getEntity() {
		return null;
	}

	default boolean sendCommandFeedback() {
		return false;
	}

	default void setStat(CommandStats.Type statsType, int value) {
	}

	@Nullable
	MinecraftServer getMinecraftServer();
}
