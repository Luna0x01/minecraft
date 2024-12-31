package net.minecraft.server.command;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.InvalidNumberException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReplaceItemCommand extends AbstractCommand {
	private static final Map<String, Integer> field_10417 = Maps.newHashMap();

	@Override
	public String getCommandName() {
		return "replaceitem";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.replaceitem.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.replaceitem.usage");
		} else {
			boolean bl;
			if (args[0].equals("entity")) {
				bl = false;
			} else {
				if (!args[0].equals("block")) {
					throw new IncorrectUsageException("commands.replaceitem.usage");
				}

				bl = true;
			}

			int i;
			if (bl) {
				if (args.length < 6) {
					throw new IncorrectUsageException("commands.replaceitem.block.usage");
				}

				i = 4;
			} else {
				if (args.length < 4) {
					throw new IncorrectUsageException("commands.replaceitem.entity.usage");
				}

				i = 2;
			}

			int k = this.method_9549(args[i++]);

			Item item;
			try {
				item = getItem(source, args[i]);
			} catch (InvalidNumberException var15) {
				if (Block.get(args[i]) != Blocks.AIR) {
					throw var15;
				}

				item = null;
			}

			i++;
			int l = args.length > i ? parseClampedInt(args[i++], 1, 64) : 1;
			int m = args.length > i ? parseInt(args[i++]) : 0;
			ItemStack itemStack = new ItemStack(item, l, m);
			if (args.length > i) {
				String string = method_4635(source, args, i).asUnformattedString();

				try {
					itemStack.setNbt(StringNbtReader.parse(string));
				} catch (NbtException var14) {
					throw new CommandException("commands.replaceitem.tagError", var14.getMessage());
				}
			}

			if (itemStack.getItem() == null) {
				itemStack = null;
			}

			if (bl) {
				source.setStat(CommandStats.Type.AFFECTED_ITEMS, 0);
				BlockPos blockPos = getBlockPos(source, args, 1, false);
				World world = source.getWorld();
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity == null || !(blockEntity instanceof Inventory)) {
					throw new CommandException("commands.replaceitem.noContainer", blockPos.getX(), blockPos.getY(), blockPos.getZ());
				}

				Inventory inventory = (Inventory)blockEntity;
				if (k >= 0 && k < inventory.getInvSize()) {
					inventory.setInvStack(k, itemStack);
				}
			} else {
				Entity entity = getEntity(source, args[1]);
				source.setStat(CommandStats.Type.AFFECTED_ITEMS, 0);
				if (entity instanceof PlayerEntity) {
					((PlayerEntity)entity).playerScreenHandler.sendContentUpdates();
				}

				if (!entity.equip(k, itemStack)) {
					throw new CommandException("commands.replaceitem.failed", k, l, itemStack == null ? "Air" : itemStack.toHoverableText());
				}

				if (entity instanceof PlayerEntity) {
					((PlayerEntity)entity).playerScreenHandler.sendContentUpdates();
				}
			}

			source.setStat(CommandStats.Type.AFFECTED_ITEMS, l);
			run(source, this, "commands.replaceitem.success", new Object[]{k, l, itemStack == null ? "Air" : itemStack.toHoverableText()});
		}
	}

	private int method_9549(String string) throws CommandException {
		if (!field_10417.containsKey(string)) {
			throw new CommandException("commands.generic.parameter.invalid", string);
		} else {
			return (Integer)field_10417.get(string);
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, new String[]{"entity", "block"});
		} else if (args.length == 2 && args[0].equals("entity")) {
			return method_2894(args, this.method_9548());
		} else if (args.length >= 2 && args.length <= 4 && args[0].equals("block")) {
			return method_10707(args, 1, pos);
		} else if ((args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block"))) {
			return (args.length != 4 || !args[0].equals("entity")) && (args.length != 6 || !args[0].equals("block")) ? null : method_10708(args, Item.REGISTRY.keySet());
		} else {
			return method_10708(args, field_10417.keySet());
		}
	}

	protected String[] method_9548() {
		return MinecraftServer.getServer().getPlayerNames();
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return args.length > 0 && args[0].equals("entity") && index == 1;
	}

	static {
		for (int i = 0; i < 54; i++) {
			field_10417.put("slot.container." + i, i);
		}

		for (int j = 0; j < 9; j++) {
			field_10417.put("slot.hotbar." + j, j);
		}

		for (int k = 0; k < 27; k++) {
			field_10417.put("slot.inventory." + k, 9 + k);
		}

		for (int l = 0; l < 27; l++) {
			field_10417.put("slot.enderchest." + l, 200 + l);
		}

		for (int m = 0; m < 8; m++) {
			field_10417.put("slot.villager." + m, 300 + m);
		}

		for (int n = 0; n < 15; n++) {
			field_10417.put("slot.horse." + n, 500 + n);
		}

		field_10417.put("slot.weapon", 99);
		field_10417.put("slot.armor.head", 103);
		field_10417.put("slot.armor.chest", 102);
		field_10417.put("slot.armor.legs", 101);
		field_10417.put("slot.armor.feet", 100);
		field_10417.put("slot.horse.saddle", 400);
		field_10417.put("slot.horse.armor", 401);
		field_10417.put("slot.horse.chest", 499);
	}
}
