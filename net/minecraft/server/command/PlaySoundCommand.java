package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundNameS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.Sound;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException(this.getUsageTranslationKey(commandSource));
		} else {
			int i = 0;
			String string = args[i++];
			String string2 = args[i++];
			SoundCategory soundCategory = SoundCategory.byName(string2);
			if (soundCategory == null) {
				throw new CommandException("commands.playsound.unknownSoundSource", string2);
			} else {
				ServerPlayerEntity serverPlayerEntity = method_4639(minecraftServer, commandSource, args[i++]);
				Vec3d vec3d = commandSource.getPos();
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

				serverPlayerEntity.networkHandler.sendPacket(new PlaySoundNameS2CPacket(string, soundCategory, d, e, f, (float)g, (float)h));
				run(commandSource, this, "commands.playsound.success", new Object[]{string, serverPlayerEntity.getTranslationKey()});
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_10708(strings, Sound.REGISTRY.getKeySet());
		} else if (strings.length == 2) {
			return method_10708(strings, SoundCategory.method_12844());
		} else if (strings.length == 3) {
			return method_2894(strings, server.getPlayerNames());
		} else {
			return strings.length > 3 && strings.length <= 6 ? method_10707(strings, 2, pos) : Collections.emptyList();
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 2;
	}
}
