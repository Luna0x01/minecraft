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
	private static final Console INSTANCE = new Console();
	private StringBuffer text = new StringBuffer();

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
		return new BlockPos(0, 0, 0);
	}

	@Override
	public Vec3d getPos() {
		return new Vec3d(0.0, 0.0, 0.0);
	}

	@Override
	public World getWorld() {
		return MinecraftServer.getServer().getWorld();
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
}
