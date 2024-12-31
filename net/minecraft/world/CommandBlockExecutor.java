package net.minecraft.world;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.class_3893;
import net.minecraft.class_3915;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Vec3d;

public abstract class CommandBlockExecutor implements class_3893 {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private long field_15705 = -1L;
	private boolean field_15706 = true;
	private int successCount;
	private boolean trackOutput = true;
	private Text lastOutput;
	private String command = "";
	private Text field_17484 = new LiteralText("@");

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
		nbt.putString("CustomName", Text.Serializer.serialize(this.field_17484));
		nbt.putBoolean("TrackOutput", this.trackOutput);
		if (this.lastOutput != null && this.trackOutput) {
			nbt.putString("LastOutput", Text.Serializer.serialize(this.lastOutput));
		}

		nbt.putBoolean("UpdateLastExecution", this.field_15706);
		if (this.field_15706 && this.field_15705 > 0L) {
			nbt.putLong("LastExecution", this.field_15705);
		}

		return nbt;
	}

	public void fromNbt(NbtCompound nbt) {
		this.command = nbt.getString("Command");
		this.successCount = nbt.getInt("SuccessCount");
		if (nbt.contains("CustomName", 8)) {
			this.field_17484 = Text.Serializer.deserializeText(nbt.getString("CustomName"));
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
			this.successCount = 0;
			MinecraftServer minecraftServer = this.method_16273().getServer();
			if (minecraftServer != null && minecraftServer.hasGameDir() && minecraftServer.areCommandBlocksEnabled() && !ChatUtil.isEmpty(this.command)) {
				try {
					this.lastOutput = null;
					class_3915 lv = this.method_16276().method_17455((commandContext, bl, i) -> {
						if (bl) {
							this.successCount++;
						}
					});
					minecraftServer.method_2971().method_17519(lv, this.command);
				} catch (Throwable var6) {
					CrashReport crashReport = CrashReport.create(var6, "Executing command block");
					CrashReportSection crashReportSection = crashReport.addElement("Command to be executed");
					crashReportSection.add("Command", this::getCommand);
					crashReportSection.add("Name", (CrashCallable<String>)(() -> this.method_16277().getString()));
					throw new CrashException(crashReport);
				}
			}

			if (this.field_15706) {
				this.field_15705 = world.getLastUpdateTime();
			} else {
				this.field_15705 = -1L;
			}

			return true;
		}
	}

	public Text method_16277() {
		return this.field_17484;
	}

	public void method_16272(Text text) {
		this.field_17484 = text;
	}

	@Override
	public void method_5505(Text text) {
		if (this.trackOutput) {
			this.lastOutput = new LiteralText("[" + DATE_FORMAT.format(new Date()) + "] ").append(text);
			this.markDirty();
		}
	}

	public abstract ServerWorld method_16273();

	public abstract void markDirty();

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
		if (!player.method_15936()) {
			return false;
		} else {
			if (player.method_5506().isClient) {
				player.openCommandBlockScreen(this);
			}

			return true;
		}
	}

	public abstract Vec3d method_16274();

	public abstract class_3915 method_16276();

	@Override
	public boolean method_17413() {
		return this.method_16273().getGameRules().getBoolean("sendCommandFeedback") && this.trackOutput;
	}

	@Override
	public boolean method_17414() {
		return this.trackOutput;
	}

	@Override
	public boolean method_17412() {
		return this.method_16273().getGameRules().getBoolean("commandBlockOutput");
	}
}
