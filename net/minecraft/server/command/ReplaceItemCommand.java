package net.minecraft.server.command;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
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
import net.minecraft.entity.EquipmentSlot;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.replaceitem.usage");
		} else {
			boolean bl;
			if ("entity".equals(args[0])) {
				bl = false;
			} else {
				if (!"block".equals(args[0])) {
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

			String string = args[i];
			int k = this.method_9549(args[i++]);

			Item item;
			try {
				item = getItem(commandSource, args[i]);
			} catch (InvalidNumberException var17) {
				if (Block.get(args[i]) != Blocks.AIR) {
					throw var17;
				}

				item = null;
			}

			i++;
			int l = args.length > i ? parseClampedInt(args[i++], 1, 64) : 1;
			int m = args.length > i ? parseInt(args[i++]) : 0;
			ItemStack itemStack = new ItemStack(item, l, m);
			if (args.length > i) {
				String string2 = method_4635(commandSource, args, i).asUnformattedString();

				try {
					itemStack.setNbt(StringNbtReader.parse(string2));
				} catch (NbtException var16) {
					throw new CommandException("commands.replaceitem.tagError", var16.getMessage());
				}
			}

			if (bl) {
				commandSource.setStat(CommandStats.Type.AFFECTED_ITEMS, 0);
				BlockPos blockPos = getBlockPos(commandSource, args, 1, false);
				World world = commandSource.getWorld();
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity == null || !(blockEntity instanceof Inventory)) {
					throw new CommandException("commands.replaceitem.noContainer", blockPos.getX(), blockPos.getY(), blockPos.getZ());
				}

				Inventory inventory = (Inventory)blockEntity;
				if (k >= 0 && k < inventory.getInvSize()) {
					inventory.setInvStack(k, itemStack);
				}
			} else {
				Entity entity = method_10711(minecraftServer, commandSource, args[1]);
				commandSource.setStat(CommandStats.Type.AFFECTED_ITEMS, 0);
				if (entity instanceof PlayerEntity) {
					((PlayerEntity)entity).playerScreenHandler.sendContentUpdates();
				}

				if (!entity.equip(k, itemStack)) {
					throw new CommandException("commands.replaceitem.failed", string, l, itemStack.isEmpty() ? "Air" : itemStack.toHoverableText());
				}

				if (entity instanceof PlayerEntity) {
					((PlayerEntity)entity).playerScreenHandler.sendContentUpdates();
				}
			}

			commandSource.setStat(CommandStats.Type.AFFECTED_ITEMS, l);
			run(commandSource, this, "commands.replaceitem.success", new Object[]{string, l, itemStack.isEmpty() ? "Air" : itemStack.toHoverableText()});
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
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, new String[]{"entity", "block"});
		} else if (strings.length == 2 && "entity".equals(strings[0])) {
			return method_2894(strings, server.getPlayerNames());
		} else if (strings.length >= 2 && strings.length <= 4 && "block".equals(strings[0])) {
			return method_10707(strings, 1, pos);
		} else if ((strings.length != 3 || !"entity".equals(strings[0])) && (strings.length != 5 || !"block".equals(strings[0]))) {
			return (strings.length != 4 || !"entity".equals(strings[0])) && (strings.length != 6 || !"block".equals(strings[0]))
				? Collections.emptyList()
				: method_10708(strings, Item.REGISTRY.getKeySet());
		} else {
			return method_10708(strings, field_10417.keySet());
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return args.length > 0 && "entity".equals(args[0]) && index == 1;
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

		field_10417.put("slot.weapon", 98);
		field_10417.put("slot.weapon.mainhand", 98);
		field_10417.put("slot.weapon.offhand", 99);
		field_10417.put("slot.armor.head", 100 + EquipmentSlot.HEAD.method_13032());
		field_10417.put("slot.armor.chest", 100 + EquipmentSlot.CHEST.method_13032());
		field_10417.put("slot.armor.legs", 100 + EquipmentSlot.LEGS.method_13032());
		field_10417.put("slot.armor.feet", 100 + EquipmentSlot.FEET.method_13032());
		field_10417.put("slot.horse.saddle", 400);
		field_10417.put("slot.horse.armor", 401);
		field_10417.put("slot.horse.chest", 499);
	}
}
