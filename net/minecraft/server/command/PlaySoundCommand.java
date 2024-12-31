package net.minecraft.server.command;

import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlaySoundCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "playsound";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.playsound.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException(this.getUsageTranslationKey(source));
		} else {
			int i = 0;
			String string = args[i++];
			ServerPlayerEntity serverPlayerEntity = getPlayer(source, args[i++]);
			Vec3d vec3d = source.getPos();
			double d = vec3d.x;
			if (args.length > i) {
				d = parseDouble(d, args[i++], true);
			}

			double e = vec3d.y;
			if (args.length > i) {
				e = parseDouble(e, args[i++], 0, 0, false);
			}

			double f = vec3d.z;
			if (args.length > i) {
				f = parseDouble(f, args[i++], true);
			}

			double g = 1.0;
			if (args.length > i) {
				g = parseClampedDouble(args[i++], 0.0, Float.MAX_VALUE);
			}

			double h = 1.0;
			if (args.length > i) {
				h = parseClampedDouble(args[i++], 0.0, 2.0);
			}

			double j = 0.0;
			if (args.length > i) {
				j = parseClampedDouble(args[i], 0.0, 1.0);
			}

			double k = g > 1.0 ? g * 16.0 : 16.0;
			double l = serverPlayerEntity.distanceTo(d, e, f);
			if (l > k) {
				if (j <= 0.0) {
					throw new CommandException("commands.playsound.playerTooFar", serverPlayerEntity.getTranslationKey());
				}

				double m = d - serverPlayerEntity.x;
				double n = e - serverPlayerEntity.y;
				double o = f - serverPlayerEntity.z;
				double p = Math.sqrt(m * m + n * n + o * o);
				if (p > 0.0) {
					d = serverPlayerEntity.x + m / p * 2.0;
					e = serverPlayerEntity.y + n / p * 2.0;
					f = serverPlayerEntity.z + o / p * 2.0;
				}

				g = j;
			}

			serverPlayerEntity.networkHandler.sendPacket(new PlaySoundIdS2CPacket(string, d, e, f, (float)g, (float)h));
			run(source, this, "commands.playsound.success", new Object[]{string, serverPlayerEntity.getTranslationKey()});
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 2) {
			return method_2894(args, MinecraftServer.getServer().getPlayerNames());
		} else {
			return args.length > 2 && args.length <= 5 ? method_10707(args, 2, pos) : null;
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 1;
	}
}
