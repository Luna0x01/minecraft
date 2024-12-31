package net.minecraft.server.command;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class TpCommand extends AbstractCommand {
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.tp.usage");
		} else {
			int i = 0;
			Entity entity2;
			if (args.length != 2 && args.length != 4 && args.length != 6) {
				entity2 = getAsPlayer(commandSource);
			} else {
				entity2 = method_10711(minecraftServer, commandSource, args[0]);
				i = 1;
			}

			if (args.length == 1 || args.length == 2) {
				Entity entity3 = method_10711(minecraftServer, commandSource, args[args.length - 1]);
				if (entity3.world != entity2.world) {
					throw new CommandException("commands.tp.notSameDimension");
				} else {
					entity2.stopRiding();
					if (entity2 instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)entity2).networkHandler.requestTeleport(entity3.x, entity3.y, entity3.z, entity3.yaw, entity3.pitch);
					} else {
						entity2.refreshPositionAndAngles(entity3.x, entity3.y, entity3.z, entity3.yaw, entity3.pitch);
					}

					run(commandSource, this, "commands.tp.success", new Object[]{entity2.getTranslationKey(), entity3.getTranslationKey()});
				}
			} else if (args.length < i + 3) {
				throw new IncorrectUsageException("commands.tp.usage");
			} else if (entity2.world != null) {
				int k = 4096;
				int j = i + 1;
				AbstractCommand.Coordinate coordinate = getCoordinate(entity2.x, args[i], true);
				AbstractCommand.Coordinate coordinate2 = getCoordinate(entity2.y, args[j++], -4096, 4096, false);
				AbstractCommand.Coordinate coordinate3 = getCoordinate(entity2.z, args[j++], true);
				AbstractCommand.Coordinate coordinate4 = getCoordinate((double)entity2.yaw, args.length > j ? args[j++] : "~", false);
				AbstractCommand.Coordinate coordinate5 = getCoordinate((double)entity2.pitch, args.length > j ? args[j] : "~", false);
				method_13463(entity2, coordinate, coordinate2, coordinate3, coordinate4, coordinate5);
				run(
					commandSource,
					this,
					"commands.tp.success.coordinates",
					new Object[]{entity2.getTranslationKey(), coordinate.getResult(), coordinate2.getResult(), coordinate3.getResult()}
				);
			}
		}
	}

	private static void method_13463(
		Entity entity,
		AbstractCommand.Coordinate coordinate,
		AbstractCommand.Coordinate coordinate2,
		AbstractCommand.Coordinate coordinate3,
		AbstractCommand.Coordinate coordinate4,
		AbstractCommand.Coordinate coordinate5
	) {
		if (entity instanceof ServerPlayerEntity) {
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

			entity.stopRiding();
			((ServerPlayerEntity)entity).networkHandler.teleportRequest(coordinate.getAmount(), coordinate2.getAmount(), coordinate3.getAmount(), f, g, set);
			entity.setHeadYaw(f);
		} else {
			float h = (float)MathHelper.wrapDegrees(coordinate4.getResult());
			float i = (float)MathHelper.wrapDegrees(coordinate5.getResult());
			i = MathHelper.clamp(i, -90.0F, 90.0F);
			entity.refreshPositionAndAngles(coordinate.getResult(), coordinate2.getResult(), coordinate3.getResult(), h, i);
			entity.setHeadYaw(h);
		}

		if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).method_13055()) {
			entity.velocityY = 0.0;
			entity.onGround = true;
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length != 1 && strings.length != 2 ? Collections.emptyList() : method_2894(strings, server.getPlayerNames());
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
