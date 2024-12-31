package net.minecraft.server.command;

import java.util.List;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 8) {
			throw new IncorrectUsageException("commands.particle.usage");
		} else {
			boolean bl = false;
			ParticleType particleType = null;

			for (ParticleType particleType2 : ParticleType.values()) {
				if (particleType2.hasArguments()) {
					if (args[0].startsWith(particleType2.getName())) {
						bl = true;
						particleType = particleType2;
						break;
					}
				} else if (args[0].equals(particleType2.getName())) {
					bl = true;
					particleType = particleType2;
					break;
				}
			}

			if (!bl) {
				throw new CommandException("commands.particle.notFound", args[0]);
			} else {
				String string = args[0];
				Vec3d vec3d = source.getPos();
				double d = (double)((float)parseDouble(vec3d.x, args[1], true));
				double e = (double)((float)parseDouble(vec3d.y, args[2], true));
				double f = (double)((float)parseDouble(vec3d.z, args[3], true));
				double g = (double)((float)parseDouble(args[4]));
				double h = (double)((float)parseDouble(args[5]));
				double k = (double)((float)parseDouble(args[6]));
				double l = (double)((float)parseDouble(args[7]));
				int m = 0;
				if (args.length > 8) {
					m = parseClampedInt(args[8], 0);
				}

				boolean bl2 = false;
				if (args.length > 9 && "force".equals(args[9])) {
					bl2 = true;
				}

				World world = source.getWorld();
				if (world instanceof ServerWorld) {
					ServerWorld serverWorld = (ServerWorld)world;
					int[] is = new int[particleType.getArgs()];
					if (particleType.hasArguments()) {
						String[] strings = args[0].split("_", 3);

						for (int n = 1; n < strings.length; n++) {
							try {
								is[n - 1] = Integer.parseInt(strings[n]);
							} catch (NumberFormatException var29) {
								throw new CommandException("commands.particle.notFound", args[0]);
							}
						}
					}

					serverWorld.addParticle(particleType, bl2, d, e, f, m, g, h, k, l, is);
					run(source, this, "commands.particle.success", new Object[]{string, Math.max(m, 1)});
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, ParticleType.getNames());
		} else if (args.length > 1 && args.length <= 4) {
			return method_10707(args, 1, pos);
		} else {
			return args.length == 10 ? method_2894(args, new String[]{"normal", "force"}) : null;
		}
	}
}
