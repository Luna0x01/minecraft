package net.minecraft.server.command;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class TeleportCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "tp";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.tp.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.tp.usage");
		} else {
			int i = 0;
			Entity entity2;
			if (args.length != 2 && args.length != 4 && args.length != 6) {
				entity2 = getAsPlayer(source);
			} else {
				entity2 = getEntity(source, args[0]);
				i = 1;
			}

			if (args.length == 1 || args.length == 2) {
				Entity entity3 = getEntity(source, args[args.length - 1]);
				if (entity3.world != entity2.world) {
					throw new CommandException("commands.tp.notSameDimension");
				} else {
					entity2.startRiding(null);
					if (entity2 instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)entity2).networkHandler.requestTeleport(entity3.x, entity3.y, entity3.z, entity3.yaw, entity3.pitch);
					} else {
						entity2.refreshPositionAndAngles(entity3.x, entity3.y, entity3.z, entity3.yaw, entity3.pitch);
					}

					run(source, this, "commands.tp.success", new Object[]{entity2.getTranslationKey(), entity3.getTranslationKey()});
				}
			} else if (args.length < i + 3) {
				throw new IncorrectUsageException("commands.tp.usage");
			} else if (entity2.world != null) {
				int j = i + 1;
				AbstractCommand.Coordinate coordinate = getCoordinate(entity2.x, args[i], true);
				AbstractCommand.Coordinate coordinate2 = getCoordinate(entity2.y, args[j++], 0, 0, false);
				AbstractCommand.Coordinate coordinate3 = getCoordinate(entity2.z, args[j++], true);
				AbstractCommand.Coordinate coordinate4 = getCoordinate((double)entity2.yaw, args.length > j ? args[j++] : "~", false);
				AbstractCommand.Coordinate coordinate5 = getCoordinate((double)entity2.pitch, args.length > j ? args[j] : "~", false);
				if (entity2 instanceof ServerPlayerEntity) {
					Set<PlayerPositionLookS2CPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class);
					if (coordinate.isRelative()) {
						set.add(PlayerPositionLookS2CPacket.Flag.X);
					}

					if (coordinate2.isRelative()) {
						set.add(PlayerPositionLookS2CPacket.Flag.Y);
					}

					if (coordinate3.isRelative()) {
						set.add(PlayerPositionLookS2CPacket.Flag.Z);
					}

					if (coordinate5.isRelative()) {
						set.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
					}

					if (coordinate4.isRelative()) {
						set.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
					}

					float f = (float)coordinate4.getAmount();
					if (!coordinate4.isRelative()) {
						f = MathHelper.wrapDegrees(f);
					}

					float g = (float)coordinate5.getAmount();
					if (!coordinate5.isRelative()) {
						g = MathHelper.wrapDegrees(g);
					}

					if (g > 90.0F || g < -90.0F) {
						g = MathHelper.wrapDegrees(180.0F - g);
						f = MathHelper.wrapDegrees(f + 180.0F);
					}

					entity2.startRiding(null);
					((ServerPlayerEntity)entity2).networkHandler.teleportRequest(coordinate.getAmount(), coordinate2.getAmount(), coordinate3.getAmount(), f, g, set);
					entity2.setHeadYaw(f);
				} else {
					float h = (float)MathHelper.wrapDegrees(coordinate4.getResult());
					float k = (float)MathHelper.wrapDegrees(coordinate5.getResult());
					if (k > 90.0F || k < -90.0F) {
						k = MathHelper.wrapDegrees(180.0F - k);
						h = MathHelper.wrapDegrees(h + 180.0F);
					}

					entity2.refreshPositionAndAngles(coordinate.getResult(), coordinate2.getResult(), coordinate3.getResult(), h, k);
					entity2.setHeadYaw(h);
				}

				run(
					source,
					this,
					"commands.tp.success.coordinates",
					new Object[]{entity2.getTranslationKey(), coordinate.getResult(), coordinate2.getResult(), coordinate3.getResult()}
				);
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length != 1 && args.length != 2 ? null : method_2894(args, MinecraftServer.getServer().getPlayerNames());
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
