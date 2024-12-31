package net.minecraft.world;

import io.netty.buffer.ByteBuf;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandRegistryProvider;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public abstract class CommandBlockExecutor implements CommandSource {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private int successCount;
	private boolean trackOutput = true;
	private Text lastOutput = null;
	private String command = "";
	private String name = "@";
	private final CommandStats commandStats = new CommandStats();

	public int getSuccessCount() {
		return this.successCount;
	}

	public Text getLastOutput() {
		return this.lastOutput;
	}

	public void toNbt(NbtCompound nbt) {
		nbt.putString("Command", this.command);
		nbt.putInt("SuccessCount", this.successCount);
		nbt.putString("CustomName", this.name);
		nbt.putBoolean("TrackOutput", this.trackOutput);
		if (this.lastOutput != null && this.trackOutput) {
			nbt.putString("LastOutput", Text.Serializer.serialize(this.lastOutput));
		}

		this.commandStats.toNbt(nbt);
	}

	public void fromNbt(NbtCompound nbt) {
		this.command = nbt.getString("Command");
		this.successCount = nbt.getInt("SuccessCount");
		if (nbt.contains("CustomName", 8)) {
			this.name = nbt.getString("CustomName");
		}

		if (nbt.contains("TrackOutput", 1)) {
			this.trackOutput = nbt.getBoolean("TrackOutput");
		}

		if (nbt.contains("LastOutput", 8) && this.trackOutput) {
			this.lastOutput = Text.Serializer.deserialize(nbt.getString("LastOutput"));
		}

		this.commandStats.fromNbt(nbt);
	}

	@Override
	public boolean canUseCommand(int permissionLevel, String commandLiteral) {
		return permissionLevel <= 2;
	}

	public void setCommand(String command) {
		this.command = command;
		this.successCount = 0;
	}

	public String getCommand() {
		return this.command;
	}

	public void execute(World world) {
		if (world.isClient) {
			this.successCount = 0;
		}

		MinecraftServer minecraftServer = MinecraftServer.getServer();
		if (minecraftServer != null && minecraftServer.hasGameDir() && minecraftServer.areCommandBlocksEnabled()) {
			CommandRegistryProvider commandRegistryProvider = minecraftServer.getCommandManager();

			try {
				this.lastOutput = null;
				this.successCount = commandRegistryProvider.execute(this, this.command);
			} catch (Throwable var7) {
				CrashReport crashReport = CrashReport.create(var7, "Executing command block");
				CrashReportSection crashReportSection = crashReport.addElement("Command to be executed");
				crashReportSection.add("Command", new Callable<String>() {
					public String call() throws Exception {
						return CommandBlockExecutor.this.getCommand();
					}
				});
				crashReportSection.add("Name", new Callable<String>() {
					public String call() throws Exception {
						return CommandBlockExecutor.this.getTranslationKey();
					}
				});
				throw new CrashException(crashReport);
			}
		} else {
			this.successCount = 0;
		}
	}

	@Override
	public String getTranslationKey() {
		return this.name;
	}

	@Override
	public Text getName() {
		return new LiteralText(this.getTranslationKey());
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void sendMessage(Text text) {
		if (this.trackOutput && this.getWorld() != null && !this.getWorld().isClient) {
			this.lastOutput = new LiteralText("[" + DATE_FORMAT.format(new Date()) + "] ").append(text);
			this.markDirty();
		}
	}

	@Override
	public boolean sendCommandFeedback() {
		MinecraftServer minecraftServer = MinecraftServer.getServer();
		return minecraftServer == null || !minecraftServer.hasGameDir() || minecraftServer.worlds[0].getGameRules().getBoolean("commandBlockOutput");
	}

	@Override
	public void setStat(CommandStats.Type statsType, int value) {
		this.commandStats.execute(this, statsType, value);
	}

	public abstract void markDirty();

	public abstract int getType();

	public abstract void writeEntityId(ByteBuf byteBuf);

	public void setLastOutput(Text lastOutput) {
		this.lastOutput = lastOutput;
	}

	public void setTrackOutput(boolean trackOutput) {
		this.trackOutput = trackOutput;
	}

	public boolean isTrackingOutput() {
		return this.trackOutput;
	}

	public boolean interact(PlayerEntity player) {
		if (!player.abilities.creativeMode) {
			return false;
		} else {
			if (player.getWorld().isClient) {
				player.openCommandBlockScreen(this);
			}

			return true;
		}
	}

	public CommandStats getCommandStats() {
		return this.commandStats;
	}
}
