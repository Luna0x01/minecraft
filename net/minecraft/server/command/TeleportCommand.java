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
import net.minecraft.util.math.Vec3d;

public class TeleportCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "teleport";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.teleport.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 4) {
			throw new IncorrectUsageException("commands.teleport.usage");
		} else {
			Entity entity = method_10711(minecraftServer, commandSource, args[0]);
			if (entity.world != null) {
				int i = 4096;
				Vec3d vec3d = commandSource.getPos();
				int j = 1;
				AbstractCommand.Coordinate coordinate = getCoordinate(vec3d.x, args[j++], true);
				AbstractCommand.Coordinate coordinate2 = getCoordinate(vec3d.y, args[j++], -4096, 4096, false);
				AbstractCommand.Coordinate coordinate3 = getCoordinate(vec3d.z, args[j++], true);
				Entity entity2 = commandSource.getEntity() == null ? entity : commandSource.getEntity();
				AbstractCommand.Coordinate coordinate4 = getCoordinate(args.length > j ? (double)entity2.yaw : (double)entity.yaw, args.length > j ? args[j] : "~", false);
				j++;
				AbstractCommand.Coordinate coordinate5 = getCoordinate(
					args.length > j ? (double)entity2.pitch : (double)entity.pitch, args.length > j ? args[j] : "~", false
				);
				method_13428(entity, coordinate, coordinate2, coordinate3, coordinate4, coordinate5);
				run(
					commandSource,
					this,
					"commands.teleport.success.coordinates",
					new Object[]{entity.getTranslationKey(), coordinate.getResult(), coordinate2.getResult(), coordinate3.getResult()}
				);
			}
		}
	}

	private static void method_13428(
		Entity entity,
		AbstractCommand.Coordinate coordinate,
		AbstractCommand.Coordinate coordinate2,
		AbstractCommand.Coordinate coordinate3,
		AbstractCommand.Coordinate coordinate4,
		AbstractCommand.Coordinate coordinate5
	) {
		if (entity instanceof ServerPlayerEntity) {
			Set<PlayerPositionLookS2CPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class);
			float f = (float)coordinate4.getAmount();
			if (coordinate4.isRelative()) {
				set.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
			} else {
				f = MathHelper.wrapDegrees(f);
			}

			float g = (float)coordinate5.getAmount();
			if (coordinate5.isRelative()) {
				set.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
			} else {
				g = MathHelper.wrapDegrees(g);
			}

			entity.stopRiding();
			((ServerPlayerEntity)entity).networkHandler.teleportRequest(coordinate.getResult(), coordinate2.getResult(), coordinate3.getResult(), f, g, set);
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
		if (strings.length == 1) {
			return method_2894(strings, server.getPlayerNames());
		} else {
			return strings.length > 1 && strings.length <= 4 ? method_10707(strings, 1, pos) : Collections.emptyList();
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
