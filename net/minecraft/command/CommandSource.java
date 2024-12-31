package net.minecraft.command;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface CommandSource {
	String getTranslationKey();

	Text getName();

	void sendMessage(Text text);

	boolean canUseCommand(int permissionLevel, String commandLiteral);

	BlockPos getBlockPos();

	Vec3d getPos();

	World getWorld();

	@Nullable
	Entity getEntity();

	boolean sendCommandFeedback();

	void setStat(CommandStats.Type statsType, int value);

	@Nullable
	MinecraftServer getMinecraftServer();
}
