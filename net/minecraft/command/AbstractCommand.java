package net.minecraft.command;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerSelector;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractCommand implements Command {
	private static CommandProvider commandProvider;

	public int getPermissionLevel() {
		return 4;
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public boolean isAccessible(CommandSource source) {
		return source.canUseCommand(this.getPermissionLevel(), this.getCommandName());
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return null;
	}

	public static int parseInt(String value) throws InvalidNumberException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException var2) {
			throw new InvalidNumberException("commands.generic.num.invalid", value);
		}
	}

	public static int parseClampedInt(String value, int min) throws InvalidNumberException {
		return parseClampedInt(value, min, Integer.MAX_VALUE);
	}

	public static int parseClampedInt(String value, int min, int max) throws InvalidNumberException {
		int i = parseInt(value);
		if (i < min) {
			throw new InvalidNumberException("commands.generic.num.tooSmall", i, min);
		} else if (i > max) {
			throw new InvalidNumberException("commands.generic.num.tooBig", i, max);
		} else {
			return i;
		}
	}

	public static long parseLong(String value) throws InvalidNumberException {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException var2) {
			throw new InvalidNumberException("commands.generic.num.invalid", value);
		}
	}

	public static long parseClampedLong(String value, long min, long max) throws InvalidNumberException {
		long l = parseLong(value);
		if (l < min) {
			throw new InvalidNumberException("commands.generic.num.tooSmall", l, min);
		} else if (l > max) {
			throw new InvalidNumberException("commands.generic.num.tooBig", l, max);
		} else {
			return l;
		}
	}

	public static BlockPos getBlockPos(CommandSource source, String[] args, int permissionLevel, boolean bl) throws InvalidNumberException {
		BlockPos blockPos = source.getBlockPos();
		return new BlockPos(
			parseDouble((double)blockPos.getX(), args[permissionLevel], -30000000, 30000000, bl),
			parseDouble((double)blockPos.getY(), args[permissionLevel + 1], 0, 256, false),
			parseDouble((double)blockPos.getZ(), args[permissionLevel + 2], -30000000, 30000000, bl)
		);
	}

	public static double parseDouble(String value) throws InvalidNumberException {
		try {
			double d = Double.parseDouble(value);
			if (!Doubles.isFinite(d)) {
				throw new InvalidNumberException("commands.generic.num.invalid", value);
			} else {
				return d;
			}
		} catch (NumberFormatException var3) {
			throw new InvalidNumberException("commands.generic.num.invalid", value);
		}
	}

	public static double parseClampedDouble(String value, double min) throws InvalidNumberException {
		return parseClampedDouble(value, min, Double.MAX_VALUE);
	}

	public static double parseClampedDouble(String value, double min, double max) throws InvalidNumberException {
		double d = parseDouble(value);
		if (d < min) {
			throw new InvalidNumberException("commands.generic.double.tooSmall", d, min);
		} else if (d > max) {
			throw new InvalidNumberException("commands.generic.double.tooBig", d, max);
		} else {
			return d;
		}
	}

	public static boolean parseBoolean(String value) throws CommandException {
		if (value.equals("true") || value.equals("1")) {
			return true;
		} else if (!value.equals("false") && !value.equals("0")) {
			throw new CommandException("commands.generic.boolean.invalid", value);
		} else {
			return false;
		}
	}

	public static ServerPlayerEntity getAsPlayer(CommandSource source) throws PlayerNotFoundException {
		if (source instanceof ServerPlayerEntity) {
			return (ServerPlayerEntity)source;
		} else {
			throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.");
		}
	}

	public static ServerPlayerEntity getPlayer(CommandSource source, String name) throws PlayerNotFoundException {
		ServerPlayerEntity serverPlayerEntity = PlayerSelector.selectPlayer(source, name);
		if (serverPlayerEntity == null) {
			try {
				serverPlayerEntity = MinecraftServer.getServer().getPlayerManager().getPlayer(UUID.fromString(name));
			} catch (IllegalArgumentException var4) {
			}
		}

		if (serverPlayerEntity == null) {
			serverPlayerEntity = MinecraftServer.getServer().getPlayerManager().getPlayer(name);
		}

		if (serverPlayerEntity == null) {
			throw new PlayerNotFoundException();
		} else {
			return serverPlayerEntity;
		}
	}

	public static Entity getEntity(CommandSource source, String uuid) throws EntityNotFoundException {
		return getEntity(source, uuid, Entity.class);
	}

	public static <T extends Entity> T getEntity(CommandSource source, String uuid, Class<? extends T> entityClass) throws EntityNotFoundException {
		Entity entity = PlayerSelector.selectEntity(source, uuid, entityClass);
		MinecraftServer minecraftServer = MinecraftServer.getServer();
		if (entity == null) {
			entity = minecraftServer.getPlayerManager().getPlayer(uuid);
		}

		if (entity == null) {
			try {
				UUID uUID = UUID.fromString(uuid);
				entity = minecraftServer.getEntity(uUID);
				if (entity == null) {
					entity = minecraftServer.getPlayerManager().getPlayer(uUID);
				}
			} catch (IllegalArgumentException var6) {
				throw new EntityNotFoundException("commands.generic.entity.invalidUuid");
			}
		}

		if (entity != null && entityClass.isAssignableFrom(entity.getClass())) {
			return (T)entity;
		} else {
			throw new EntityNotFoundException();
		}
	}

	public static List<Entity> getEntities(CommandSource source, String uuid) throws EntityNotFoundException {
		return (List<Entity>)(PlayerSelector.method_4091(uuid)
			? PlayerSelector.method_10866(source, uuid, Entity.class)
			: Lists.newArrayList(new Entity[]{getEntity(source, uuid)}));
	}

	public static String method_5468(CommandSource source, String name) throws PlayerNotFoundException {
		try {
			return getPlayer(source, name).getTranslationKey();
		} catch (PlayerNotFoundException var3) {
			if (PlayerSelector.method_4091(name)) {
				throw var3;
			} else {
				return name;
			}
		}
	}

	public static String method_10714(CommandSource source, String name) throws EntityNotFoundException {
		try {
			return getPlayer(source, name).getTranslationKey();
		} catch (PlayerNotFoundException var5) {
			try {
				return getEntity(source, name).getUuid().toString();
			} catch (EntityNotFoundException var4) {
				if (PlayerSelector.method_4091(name)) {
					throw var4;
				} else {
					return name;
				}
			}
		}
	}

	public static Text method_4635(CommandSource source, String[] strings, int i) throws PlayerNotFoundException {
		return method_8406(source, strings, i, false);
	}

	public static Text method_8406(CommandSource source, String[] strings, int i, boolean bl) throws PlayerNotFoundException {
		Text text = new LiteralText("");

		for (int j = i; j < strings.length; j++) {
			if (j > i) {
				text.append(" ");
			}

			Text text2 = new LiteralText(strings[j]);
			if (bl) {
				Text text3 = PlayerSelector.method_6362(source, strings[j]);
				if (text3 == null) {
					if (PlayerSelector.method_4091(strings[j])) {
						throw new PlayerNotFoundException();
					}
				} else {
					text2 = text3;
				}
			}

			text.append(text2);
		}

		return text;
	}

	public static String method_10706(String[] strings, int i) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int j = i; j < strings.length; j++) {
			if (j > i) {
				stringBuilder.append(" ");
			}

			String string = strings[j];
			stringBuilder.append(string);
		}

		return stringBuilder.toString();
	}

	public static AbstractCommand.Coordinate getCoordinate(double d, String value, boolean bl) throws InvalidNumberException {
		return getCoordinate(d, value, -30000000, 30000000, bl);
	}

	public static AbstractCommand.Coordinate getCoordinate(double d, String value, int min, int max, boolean bl) throws InvalidNumberException {
		boolean bl2 = value.startsWith("~");
		if (bl2 && Double.isNaN(d)) {
			throw new InvalidNumberException("commands.generic.num.invalid", d);
		} else {
			double e = 0.0;
			if (!bl2 || value.length() > 1) {
				boolean bl3 = value.contains(".");
				if (bl2) {
					value = value.substring(1);
				}

				e += parseDouble(value);
				if (!bl3 && !bl2 && bl) {
					e += 0.5;
				}
			}

			if (min != 0 || max != 0) {
				if (e < (double)min) {
					throw new InvalidNumberException("commands.generic.double.tooSmall", e, min);
				}

				if (e > (double)max) {
					throw new InvalidNumberException("commands.generic.double.tooBig", e, max);
				}
			}

			return new AbstractCommand.Coordinate(e + (bl2 ? d : 0.0), e, bl2);
		}
	}

	public static double parseDouble(double d, String value, boolean bl) throws InvalidNumberException {
		return parseDouble(d, value, -30000000, 30000000, bl);
	}

	public static double parseDouble(double d, String value, int min, int max, boolean bl) throws InvalidNumberException {
		boolean bl2 = value.startsWith("~");
		if (bl2 && Double.isNaN(d)) {
			throw new InvalidNumberException("commands.generic.num.invalid", d);
		} else {
			double e = bl2 ? d : 0.0;
			if (!bl2 || value.length() > 1) {
				boolean bl3 = value.contains(".");
				if (bl2) {
					value = value.substring(1);
				}

				e += parseDouble(value);
				if (!bl3 && !bl2 && bl) {
					e += 0.5;
				}
			}

			if (min != 0 || max != 0) {
				if (e < (double)min) {
					throw new InvalidNumberException("commands.generic.double.tooSmall", e, min);
				}

				if (e > (double)max) {
					throw new InvalidNumberException("commands.generic.double.tooBig", e, max);
				}
			}

			return e;
		}
	}

	public static Item getItem(CommandSource source, String identifier) throws InvalidNumberException {
		Identifier identifier2 = new Identifier(identifier);
		Item item = Item.REGISTRY.get(identifier2);
		if (item == null) {
			throw new InvalidNumberException("commands.give.item.notFound", identifier2);
		} else {
			return item;
		}
	}

	public static Block getBlock(CommandSource source, String identifier) throws InvalidNumberException {
		Identifier identifier2 = new Identifier(identifier);
		if (!Block.REGISTRY.containsKey(identifier2)) {
			throw new InvalidNumberException("commands.give.block.notFound", identifier2);
		} else {
			Block block = Block.REGISTRY.get(identifier2);
			if (block == null) {
				throw new InvalidNumberException("commands.give.block.notFound", identifier2);
			} else {
				return block;
			}
		}
	}

	public static String concat(Object[] args) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < args.length; i++) {
			String string = args[i].toString();
			if (i > 0) {
				if (i == args.length - 1) {
					stringBuilder.append(" and ");
				} else {
					stringBuilder.append(", ");
				}
			}

			stringBuilder.append(string);
		}

		return stringBuilder.toString();
	}

	public static Text concat(List<Text> texts) {
		Text text = new LiteralText("");

		for (int i = 0; i < texts.size(); i++) {
			if (i > 0) {
				if (i == texts.size() - 1) {
					text.append(" and ");
				} else if (i > 0) {
					text.append(", ");
				}
			}

			text.append((Text)texts.get(i));
		}

		return text;
	}

	public static String concat(Collection<String> strings) {
		return concat(strings.toArray(new String[strings.size()]));
	}

	public static List<String> method_10707(String[] strings, int i, BlockPos pos) {
		if (pos == null) {
			return null;
		} else {
			int j = strings.length - 1;
			String string;
			if (j == i) {
				string = Integer.toString(pos.getX());
			} else if (j == i + 1) {
				string = Integer.toString(pos.getY());
			} else {
				if (j != i + 2) {
					return null;
				}

				string = Integer.toString(pos.getZ());
			}

			return Lists.newArrayList(new String[]{string});
		}
	}

	public static List<String> method_10712(String[] strings, int i, BlockPos pos) {
		if (pos == null) {
			return null;
		} else {
			int j = strings.length - 1;
			String string;
			if (j == i) {
				string = Integer.toString(pos.getX());
			} else {
				if (j != i + 1) {
					return null;
				}

				string = Integer.toString(pos.getZ());
			}

			return Lists.newArrayList(new String[]{string});
		}
	}

	public static boolean method_2883(String string, String string2) {
		return string2.regionMatches(true, 0, string, 0, string.length());
	}

	public static List<String> method_2894(String[] strings, String... strings2) {
		return method_10708(strings, Arrays.asList(strings2));
	}

	public static List<String> method_10708(String[] strings, Collection<?> collection) {
		String string = strings[strings.length - 1];
		List<String> list = Lists.newArrayList();
		if (!collection.isEmpty()) {
			for (String string2 : Iterables.transform(collection, Functions.toStringFunction())) {
				if (method_2883(string, string2)) {
					list.add(string2);
				}
			}

			if (list.isEmpty()) {
				for (Object object : collection) {
					if (object instanceof Identifier && method_2883(string, ((Identifier)object).getPath())) {
						list.add(String.valueOf(object));
					}
				}
			}
		}

		return list;
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return false;
	}

	public static void run(CommandSource source, Command command, String label, Object... args) {
		run(source, command, 0, label, args);
	}

	public static void run(CommandSource source, Command command, int permissionLevel, String label, Object... args) {
		if (commandProvider != null) {
			commandProvider.run(source, command, permissionLevel, label, args);
		}
	}

	public static void setCommandProvider(CommandProvider provider) {
		commandProvider = provider;
	}

	public int compareTo(Command command) {
		return this.getCommandName().compareTo(command.getCommandName());
	}

	public static class Coordinate {
		private final double result;
		private final double amount;
		private final boolean relative;

		protected Coordinate(double d, double e, boolean bl) {
			this.result = d;
			this.amount = e;
			this.relative = bl;
		}

		public double getResult() {
			return this.result;
		}

		public double getAmount() {
			return this.amount;
		}

		public boolean isRelative() {
			return this.relative;
		}
	}
}
