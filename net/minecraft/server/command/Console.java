package net.minecraft.server.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Console implements CommandSource {
	private final StringBuffer text = new StringBuffer();
	private final MinecraftServer field_13901;

	public Console(MinecraftServer minecraftServer) {
		this.field_13901 = minecraftServer;
	}

	@Override
	public String getTranslationKey() {
		return "Rcon";
	}

	@Override
	public Text getName() {
		return new LiteralText(this.getTranslationKey());
	}

	@Override
	public void sendMessage(Text text) {
		this.text.append(text.asUnformattedString());
	}

	@Override
	public boolean canUseCommand(int permissionLevel, String commandLiteral) {
		return true;
	}

	@Override
	public BlockPos getBlockPos() {
		return BlockPos.ORIGIN;
	}

	@Override
	public Vec3d getPos() {
		return Vec3d.ZERO;
	}

	@Override
	public World getWorld() {
		return this.field_13901.getWorld();
	}

	@Override
	public Entity getEntity() {
		return null;
	}

	@Override
	public boolean sendCommandFeedback() {
		return true;
	}

	@Override
	public void setStat(CommandStats.Type statsType, int value) {
	}

	@Override
	public MinecraftServer getMinecraftServer() {
		return this.field_13901;
	}
}
