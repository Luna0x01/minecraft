package net.minecraft.world;

import io.netty.buffer.ByteBuf;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public abstract class CommandBlockExecutor implements CommandSource {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private long field_15705 = -1L;
	private boolean field_15706 = true;
	private int successCount;
	private boolean trackOutput = true;
	private Text lastOutput;
	private String command = "";
	private String name = "@";
	private final CommandStats commandStats = new CommandStats();

	public int getSuccessCount() {
		return this.successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public Text getLastOutput() {
		return (Text)(this.lastOutput == null ? new LiteralText("") : this.lastOutput);
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		nbt.putString("Command", this.command);
		nbt.putInt("SuccessCount", this.successCount);
		nbt.putString("CustomName", this.name);
		nbt.putBoolean("TrackOutput", this.trackOutput);
		if (this.lastOutput != null && this.trackOutput) {
			nbt.putString("LastOutput", Text.Serializer.serialize(this.lastOutput));
		}

		nbt.putBoolean("UpdateLastExecution", this.field_15706);
		if (this.field_15706 && this.field_15705 > 0L) {
			nbt.putLong("LastExecution", this.field_15705);
		}

		this.commandStats.toNbt(nbt);
		return nbt;
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
			try {
				this.lastOutput = Text.Serializer.deserializeText(nbt.getString("LastOutput"));
			} catch (Throwable var3) {
				this.lastOutput = new LiteralText(var3.getMessage());
			}
		} else {
			this.lastOutput = null;
		}

		if (nbt.contains("UpdateLastExecution")) {
			this.field_15706 = nbt.getBoolean("UpdateLastExecution");
		}

		if (this.field_15706 && nbt.contains("LastExecution")) {
			this.field_15705 = nbt.getLong("LastExecution");
		} else {
			this.field_15705 = -1L;
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

	public boolean execute(World world) {
		if (world.isClient || world.getLastUpdateTime() == this.field_15705) {
			return false;
		} else if ("Searge".equalsIgnoreCase(this.command)) {
			this.lastOutput = new LiteralText("#itzlipofutzli");
			this.successCount = 1;
			return true;
		} else {
			MinecraftServer minecraftServer = this.getMinecraftServer();
			if (minecraftServer != null && minecraftServer.hasGameDir() && minecraftServer.areCommandBlocksEnabled()) {
				try {
					this.lastOutput = null;
					this.successCount = minecraftServer.getCommandManager().execute(this, this.command);
				} catch (Throwable var6) {
					CrashReport crashReport = CrashReport.create(var6, "Executing command block");
					CrashReportSection crashReportSection = crashReport.addElement("Command to be executed");
					crashReportSection.add("Command", new CrashCallable<String>() {
						public String call() throws Exception {
							return CommandBlockExecutor.this.getCommand();
						}
					});
					crashReportSection.add("Name", new CrashCallable<String>() {
						public String call() throws Exception {
							return CommandBlockExecutor.this.getTranslationKey();
						}
					});
					throw new CrashException(crashReport);
				}
			} else {
				this.successCount = 0;
			}

			if (this.field_15706) {
				this.field_15705 = world.getLastUpdateTime();
			} else {
				this.field_15705 = -1L;
			}

			return true;
		}
	}

	@Override
	public String getTranslationKey() {
		return this.name;
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
		MinecraftServer minecraftServer = this.getMinecraftServer();
		return minecraftServer == null || !minecraftServer.hasGameDir() || minecraftServer.worlds[0].getGameRules().getBoolean("commandBlockOutput");
	}

	@Override
	public void setStat(CommandStats.Type statsType, int value) {
		this.commandStats.method_10792(this.getMinecraftServer(), this, statsType, value);
	}

	public abstract void markDirty();

	public abstract int getType();

	public abstract void writeEntityId(ByteBuf byteBuf);

	public void setLastOutput(@Nullable Text lastOutput) {
		this.lastOutput = lastOutput;
	}

	public void setTrackOutput(boolean trackOutput) {
		this.trackOutput = trackOutput;
	}

	public boolean isTrackingOutput() {
		return this.trackOutput;
	}

	public boolean interact(PlayerEntity player) {
		if (!player.method_13567()) {
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
