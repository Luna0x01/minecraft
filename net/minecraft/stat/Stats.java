package net.minecraft.stat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipeRegistry;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class Stats {
	protected static final Map<String, Stat> ID_TO_STAT = Maps.newHashMap();
	public static final List<Stat> ALL = Lists.newArrayList();
	public static final List<Stat> GENERAL = Lists.newArrayList();
	public static final List<CraftingStat> ITEM = Lists.newArrayList();
	public static final List<CraftingStat> MINE = Lists.newArrayList();
	public static final Stat GAMES_LEFT = new SimpleStat("stat.leaveGame", new TranslatableText("stat.leaveGame")).localOnly().addStat();
	public static final Stat MINUTES_PLAYED = new SimpleStat("stat.playOneMinute", new TranslatableText("stat.playOneMinute"), Stat.TIME_PROVIDER)
		.localOnly()
		.addStat();
	public static final Stat TIME_SINCE_DEATH = new SimpleStat("stat.timeSinceDeath", new TranslatableText("stat.timeSinceDeath"), Stat.TIME_PROVIDER)
		.localOnly()
		.addStat();
	public static final Stat SNEAK_TIME = new SimpleStat("stat.sneakTime", new TranslatableText("stat.sneakTime"), Stat.TIME_PROVIDER).localOnly().addStat();
	public static final Stat CM_WALKED = new SimpleStat("stat.walkOneCm", new TranslatableText("stat.walkOneCm"), Stat.DISTANCE_PROVIDER).localOnly().addStat();
	public static final Stat CM_SNEAKED = new SimpleStat("stat.crouchOneCm", new TranslatableText("stat.crouchOneCm"), Stat.DISTANCE_PROVIDER)
		.localOnly()
		.addStat();
	public static final Stat CM_SPRINTED = new SimpleStat("stat.sprintOneCm", new TranslatableText("stat.sprintOneCm"), Stat.DISTANCE_PROVIDER)
		.localOnly()
		.addStat();
	public static final Stat CM_SWUM = new SimpleStat("stat.swimOneCm", new TranslatableText("stat.swimOneCm"), Stat.DISTANCE_PROVIDER).localOnly().addStat();
	public static final Stat CM_FALLEN = new SimpleStat("stat.fallOneCm", new TranslatableText("stat.fallOneCm"), Stat.DISTANCE_PROVIDER).localOnly().addStat();
	public static final Stat CM_CLIMB = new SimpleStat("stat.climbOneCm", new TranslatableText("stat.climbOneCm"), Stat.DISTANCE_PROVIDER).localOnly().addStat();
	public static final Stat CM_FLOWN = new SimpleStat("stat.flyOneCm", new TranslatableText("stat.flyOneCm"), Stat.DISTANCE_PROVIDER).localOnly().addStat();
	public static final Stat CM_DIVED = new SimpleStat("stat.diveOneCm", new TranslatableText("stat.diveOneCm"), Stat.DISTANCE_PROVIDER).localOnly().addStat();
	public static final Stat CM_MINECART = new SimpleStat("stat.minecartOneCm", new TranslatableText("stat.minecartOneCm"), Stat.DISTANCE_PROVIDER)
		.localOnly()
		.addStat();
	public static final Stat CM_SAILED = new SimpleStat("stat.boatOneCm", new TranslatableText("stat.boatOneCm"), Stat.DISTANCE_PROVIDER).localOnly().addStat();
	public static final Stat CM_PIG = new SimpleStat("stat.pigOneCm", new TranslatableText("stat.pigOneCm"), Stat.DISTANCE_PROVIDER).localOnly().addStat();
	public static final Stat CM_HORSE = new SimpleStat("stat.horseOneCm", new TranslatableText("stat.horseOneCm"), Stat.DISTANCE_PROVIDER).localOnly().addStat();
	public static final Stat AVIATE_ONE_CM = new SimpleStat("stat.aviateOneCm", new TranslatableText("stat.aviateOneCm"), Stat.DISTANCE_PROVIDER)
		.localOnly()
		.addStat();
	public static final Stat JUMPS = new SimpleStat("stat.jump", new TranslatableText("stat.jump")).localOnly().addStat();
	public static final Stat DROPS = new SimpleStat("stat.drop", new TranslatableText("stat.drop")).localOnly().addStat();
	public static final Stat DAMAGE_DEALT = new SimpleStat("stat.damageDealt", new TranslatableText("stat.damageDealt"), Stat.DAMAGE_PROVIDER).addStat();
	public static final Stat DAMAGE_TAKEN = new SimpleStat("stat.damageTaken", new TranslatableText("stat.damageTaken"), Stat.DAMAGE_PROVIDER).addStat();
	public static final Stat DEATHS = new SimpleStat("stat.deaths", new TranslatableText("stat.deaths")).addStat();
	public static final Stat MOB_KILLS = new SimpleStat("stat.mobKills", new TranslatableText("stat.mobKills")).addStat();
	public static final Stat ANIMALS_BRED = new SimpleStat("stat.animalsBred", new TranslatableText("stat.animalsBred")).addStat();
	public static final Stat PLAYERS_KILLED = new SimpleStat("stat.playerKills", new TranslatableText("stat.playerKills")).addStat();
	public static final Stat field_14357 = new SimpleStat("stat.fishCaught", new TranslatableText("stat.fishCaught")).addStat();
	public static final Stat TALKED_TO_VILLAGER = new SimpleStat("stat.talkedToVillager", new TranslatableText("stat.talkedToVillager")).addStat();
	public static final Stat TRADED_WITH_VILLAGER = new SimpleStat("stat.tradedWithVillager", new TranslatableText("stat.tradedWithVillager")).addStat();
	public static final Stat CAKE_SLICES_EATEN = new SimpleStat("stat.cakeSlicesEaten", new TranslatableText("stat.cakeSlicesEaten")).addStat();
	public static final Stat CAULDRONS_FILLED = new SimpleStat("stat.cauldronFilled", new TranslatableText("stat.cauldronFilled")).addStat();
	public static final Stat CAULDRONS_USED = new SimpleStat("stat.cauldronUsed", new TranslatableText("stat.cauldronUsed")).addStat();
	public static final Stat ARMOR_CLEANED = new SimpleStat("stat.armorCleaned", new TranslatableText("stat.armorCleaned")).addStat();
	public static final Stat BANNER_CLEANED = new SimpleStat("stat.bannerCleaned", new TranslatableText("stat.bannerCleaned")).addStat();
	public static final Stat INTERACTIONS_WITH_BREWING_STAND = new SimpleStat("stat.brewingstandInteraction", new TranslatableText("stat.brewingstandInteraction"))
		.addStat();
	public static final Stat INTERACTIONS_WITH_BEACON = new SimpleStat("stat.beaconInteraction", new TranslatableText("stat.beaconInteraction")).addStat();
	public static final Stat INTERACTIONS_WITH_DROPPER = new SimpleStat("stat.dropperInspected", new TranslatableText("stat.dropperInspected")).addStat();
	public static final Stat INTERACTIONS_WITH_HOPPER = new SimpleStat("stat.hopperInspected", new TranslatableText("stat.hopperInspected")).addStat();
	public static final Stat INTERACTIONS_WITH_DISPENSER = new SimpleStat("stat.dispenserInspected", new TranslatableText("stat.dispenserInspected")).addStat();
	public static final Stat NOTEBLOCK_PLAYED = new SimpleStat("stat.noteblockPlayed", new TranslatableText("stat.noteblockPlayed")).addStat();
	public static final Stat NOTEBLOCK_TUNED = new SimpleStat("stat.noteblockTuned", new TranslatableText("stat.noteblockTuned")).addStat();
	public static final Stat FLOWER_POTTED = new SimpleStat("stat.flowerPotted", new TranslatableText("stat.flowerPotted")).addStat();
	public static final Stat TRAPPED_CHEST_TRIGGERED = new SimpleStat("stat.trappedChestTriggered", new TranslatableText("stat.trappedChestTriggered")).addStat();
	public static final Stat ENDERCHEST_OPENED = new SimpleStat("stat.enderchestOpened", new TranslatableText("stat.enderchestOpened")).addStat();
	public static final Stat ITEM_ENCHANTED = new SimpleStat("stat.itemEnchanted", new TranslatableText("stat.itemEnchanted")).addStat();
	public static final Stat RECORD_PLAYED = new SimpleStat("stat.recordPlayed", new TranslatableText("stat.recordPlayed")).addStat();
	public static final Stat FURNACE_INTERACTION = new SimpleStat("stat.furnaceInteraction", new TranslatableText("stat.furnaceInteraction")).addStat();
	public static final Stat INTERACT_WITH_CRAFTING_TABLE = new SimpleStat("stat.craftingTableInteraction", new TranslatableText("stat.workbenchInteraction"))
		.addStat();
	public static final Stat CHEST_OPENED = new SimpleStat("stat.chestOpened", new TranslatableText("stat.chestOpened")).addStat();
	public static final Stat SLEEP_IN_BED = new SimpleStat("stat.sleepInBed", new TranslatableText("stat.sleepInBed")).addStat();
	public static final Stat OPEN_SHULKER_BOX = new SimpleStat("stat.shulkerBoxOpened", new TranslatableText("stat.shulkerBoxOpened")).addStat();
	private static final Stat[] MINED = new Stat[4096];
	private static final Stat[] CRAFTED = new Stat[32000];
	private static final Stat[] USED = new Stat[32000];
	private static final Stat[] BROKEN = new Stat[32000];
	private static final Stat[] PICKED_UP = new Stat[32000];
	private static final Stat[] DROPPED = new Stat[32000];

	@Nullable
	public static Stat mined(Block block) {
		return MINED[Block.getIdByBlock(block)];
	}

	@Nullable
	public static Stat crafted(Item item) {
		return CRAFTED[Item.getRawId(item)];
	}

	@Nullable
	public static Stat used(Item item) {
		return USED[Item.getRawId(item)];
	}

	@Nullable
	public static Stat broke(Item item) {
		return BROKEN[Item.getRawId(item)];
	}

	@Nullable
	public static Stat picked(Item item) {
		return PICKED_UP[Item.getRawId(item)];
	}

	@Nullable
	public static Stat dropped(Item item) {
		return DROPPED[Item.getRawId(item)];
	}

	public static void setup() {
		loadBlockStats();
		loadUseStats();
		loadBreakStats();
		loadCraftingStats();
		method_12851();
	}

	private static void loadCraftingStats() {
		Set<Item> set = Sets.newHashSet();

		for (RecipeType recipeType : RecipeDispatcher.REGISTRY) {
			ItemStack itemStack = recipeType.getOutput();
			if (!itemStack.isEmpty()) {
				set.add(recipeType.getOutput().getItem());
			}
		}

		for (ItemStack itemStack2 : SmeltingRecipeRegistry.getInstance().getRecipeMap().values()) {
			set.add(itemStack2.getItem());
		}

		for (Item item : set) {
			if (item != null) {
				int i = Item.getRawId(item);
				String string = method_10801(item);
				if (string != null) {
					CRAFTED[i] = new CraftingStat("stat.craftItem.", string, new TranslatableText("stat.craftItem", new ItemStack(item).toHoverableText()), item).addStat();
				}
			}
		}

		method_8293(CRAFTED);
	}

	private static void loadBlockStats() {
		for (Block block : Block.REGISTRY) {
			Item item = Item.fromBlock(block);
			if (item != Items.AIR) {
				int i = Block.getIdByBlock(block);
				String string = method_10801(item);
				if (string != null && block.hasStats()) {
					MINED[i] = new CraftingStat("stat.mineBlock.", string, new TranslatableText("stat.mineBlock", new ItemStack(block).toHoverableText()), item).addStat();
					MINE.add((CraftingStat)MINED[i]);
				}
			}
		}

		method_8293(MINED);
	}

	private static void loadUseStats() {
		for (Item item : Item.REGISTRY) {
			if (item != null) {
				int i = Item.getRawId(item);
				String string = method_10801(item);
				if (string != null) {
					USED[i] = new CraftingStat("stat.useItem.", string, new TranslatableText("stat.useItem", new ItemStack(item).toHoverableText()), item).addStat();
					if (!(item instanceof BlockItem)) {
						ITEM.add((CraftingStat)USED[i]);
					}
				}
			}
		}

		method_8293(USED);
	}

	private static void loadBreakStats() {
		for (Item item : Item.REGISTRY) {
			if (item != null) {
				int i = Item.getRawId(item);
				String string = method_10801(item);
				if (string != null && item.isDamageable()) {
					BROKEN[i] = new CraftingStat("stat.breakItem.", string, new TranslatableText("stat.breakItem", new ItemStack(item).toHoverableText()), item).addStat();
				}
			}
		}

		method_8293(BROKEN);
	}

	private static void method_12851() {
		for (Item item : Item.REGISTRY) {
			if (item != null) {
				int i = Item.getRawId(item);
				String string = method_10801(item);
				if (string != null) {
					PICKED_UP[i] = new CraftingStat("stat.pickup.", string, new TranslatableText("stat.pickup", new ItemStack(item).toHoverableText()), item).addStat();
					DROPPED[i] = new CraftingStat("stat.drop.", string, new TranslatableText("stat.drop", new ItemStack(item).toHoverableText()), item).addStat();
				}
			}
		}

		method_8293(BROKEN);
	}

	private static String method_10801(Item item) {
		Identifier identifier = Item.REGISTRY.getIdentifier(item);
		return identifier != null ? identifier.toString().replace(':', '.') : null;
	}

	private static void method_8293(Stat[] stats) {
		method_8294(stats, Blocks.WATER, Blocks.FLOWING_WATER);
		method_8294(stats, Blocks.LAVA, Blocks.FLOWING_LAVA);
		method_8294(stats, Blocks.JACK_O_LANTERN, Blocks.PUMPKIN);
		method_8294(stats, Blocks.LIT_FURNACE, Blocks.FURNACE);
		method_8294(stats, Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);
		method_8294(stats, Blocks.POWERED_REPEATER, Blocks.UNPOWERED_REPEATER);
		method_8294(stats, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_COMPARATOR);
		method_8294(stats, Blocks.REDSTONE_TORCH, Blocks.UNLIT_REDSTONE_TORCH);
		method_8294(stats, Blocks.LIT_REDSTONE_LAMP, Blocks.REDSTONE_LAMP);
		method_8294(stats, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB);
		method_8294(stats, Blocks.DOUBLE_WOODEN_SLAB, Blocks.WOODEN_SLAB);
		method_8294(stats, Blocks.DOUBLE_STONE_SLAB2, Blocks.STONE_SLAB2);
		method_8294(stats, Blocks.GRASS, Blocks.DIRT);
		method_8294(stats, Blocks.FARMLAND, Blocks.DIRT);
	}

	private static void method_8294(Stat[] stats, Block block0, Block block1) {
		int i = Block.getIdByBlock(block0);
		int j = Block.getIdByBlock(block1);
		if (stats[i] != null && stats[j] == null) {
			stats[j] = stats[i];
		} else {
			ALL.remove(stats[i]);
			MINE.remove(stats[i]);
			GENERAL.remove(stats[i]);
			stats[i] = stats[j];
		}
	}

	public static Stat createKillEntityStat(EntityType.SpawnEggData spawnEggData) {
		String string = EntityType.getEntityName(spawnEggData.identifier);
		return string == null
			? null
			: new Stat("stat.killEntity." + string, new TranslatableText("stat.entityKill", new TranslatableText("entity." + string + ".name"))).addStat();
	}

	public static Stat createKilledByEntityStat(EntityType.SpawnEggData spawnEggData) {
		String string = EntityType.getEntityName(spawnEggData.identifier);
		return string == null
			? null
			: new Stat("stat.entityKilledBy." + string, new TranslatableText("stat.entityKilledBy", new TranslatableText("entity." + string + ".name"))).addStat();
	}

	@Nullable
	public static Stat getAStat(String id) {
		return (Stat)ID_TO_STAT.get(id);
	}
}
