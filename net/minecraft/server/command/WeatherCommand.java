package net.minecraft.server.command;

import java.util.List;
import java.util.Random;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;

public class WeatherCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "weather";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.weather.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length >= 1 && args.length <= 2) {
			int i = (300 + new Random().nextInt(600)) * 20;
			if (args.length >= 2) {
				i = parseClampedInt(args[1], 1, 1000000) * 20;
			}

			World world = MinecraftServer.getServer().worlds[0];
			LevelProperties levelProperties = world.getLevelProperties();
			if ("clear".equalsIgnoreCase(args[0])) {
				levelProperties.setClearWeatherTime(i);
				levelProperties.setRainTime(0);
				levelProperties.setThunderTime(0);
				levelProperties.setRaining(false);
				levelProperties.setThundering(false);
				run(source, this, "commands.weather.clear", new Object[0]);
			} else if ("rain".equalsIgnoreCase(args[0])) {
				levelProperties.setClearWeatherTime(0);
				levelProperties.setRainTime(i);
				levelProperties.setThunderTime(i);
				levelProperties.setRaining(true);
				levelProperties.setThundering(false);
				run(source, this, "commands.weather.rain", new Object[0]);
			} else {
				if (!"thunder".equalsIgnoreCase(args[0])) {
					throw new IncorrectUsageException("commands.weather.usage");
				}

				levelProperties.setClearWeatherTime(0);
				levelProperties.setRainTime(i);
				levelProperties.setThunderTime(i);
				levelProperties.setRaining(true);
				levelProperties.setThundering(true);
				run(source, this, "commands.weather.thunder", new Object[0]);
			}
		} else {
			throw new IncorrectUsageException("commands.weather.usage");
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? method_2894(args, new String[]{"clear", "rain", "thunder"}) : null;
	}
}
