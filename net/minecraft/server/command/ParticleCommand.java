package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "particle";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.particle.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 8) {
			throw new IncorrectUsageException("commands.particle.usage");
		} else {
			boolean bl = false;
			ParticleType particleType = ParticleType.method_12582(args[0]);
			if (particleType == null) {
				throw new CommandException("commands.particle.notFound", args[0]);
			} else {
				String string = args[0];
				Vec3d vec3d = commandSource.getPos();
				double d = (double)((float)parseDouble(vec3d.x, args[1], true));
				double e = (double)((float)parseDouble(vec3d.y, args[2], true));
				double f = (double)((float)parseDouble(vec3d.z, args[3], true));
				double g = (double)((float)parseDouble(args[4]));
				double h = (double)((float)parseDouble(args[5]));
				double i = (double)((float)parseDouble(args[6]));
				double j = (double)((float)parseDouble(args[7]));
				int k = 0;
				if (args.length > 8) {
					k = parseClampedInt(args[8], 0);
				}

				boolean bl2 = false;
				if (args.length > 9 && "force".equals(args[9])) {
					bl2 = true;
				}

				ServerPlayerEntity serverPlayerEntity;
				if (args.length > 10) {
					serverPlayerEntity = method_4639(minecraftServer, commandSource, args[10]);
				} else {
					serverPlayerEntity = null;
				}

				int[] is = new int[particleType.getArgs()];

				for (int l = 0; l < is.length; l++) {
					if (args.length > 11 + l) {
						try {
							is[l] = Integer.parseInt(args[11 + l]);
						} catch (NumberFormatException var28) {
							throw new CommandException("commands.particle.invalidParam", args[11 + l]);
						}
					}
				}

				World world = commandSource.getWorld();
				if (world instanceof ServerWorld) {
					ServerWorld serverWorld = (ServerWorld)world;
					if (serverPlayerEntity == null) {
						serverWorld.addParticle(particleType, bl2, d, e, f, k, g, h, i, j, is);
					} else {
						serverWorld.method_12778(serverPlayerEntity, particleType, bl2, d, e, f, k, g, h, i, j, is);
					}

					run(commandSource, this, "commands.particle.success", new Object[]{string, Math.max(k, 1)});
				}
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_10708(strings, ParticleType.method_12581());
		} else if (strings.length > 1 && strings.length <= 4) {
			return method_10707(strings, 1, pos);
		} else if (strings.length == 10) {
			return method_2894(strings, new String[]{"normal", "force"});
		} else {
			return strings.length == 11 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 10;
	}
}
