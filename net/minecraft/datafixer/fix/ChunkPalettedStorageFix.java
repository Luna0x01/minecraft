package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.WordPackedArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPalettedStorageFix extends DataFix {
	private static final int field_29871 = 128;
	private static final int field_29872 = 64;
	private static final int field_29873 = 32;
	private static final int field_29874 = 16;
	private static final int field_29875 = 8;
	private static final int field_29876 = 4;
	private static final int field_29877 = 2;
	private static final int field_29878 = 1;
	static final Logger LOGGER = LogManager.getLogger();
	static final BitSet BLOCKS_NEEDING_SIDE_UPDATE = new BitSet(256);
	static final BitSet BLOCKS_NEEDING_IN_PLACE_UPDATE = new BitSet(256);
	static final Dynamic<?> PUMPKIN = BlockStateFlattening.parseState("{Name:'minecraft:pumpkin'}");
	static final Dynamic<?> PODZOL = BlockStateFlattening.parseState("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
	static final Dynamic<?> SNOWY_GRASS = BlockStateFlattening.parseState("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
	static final Dynamic<?> SNOWY_MYCELIUM = BlockStateFlattening.parseState("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
	static final Dynamic<?> SUNFLOWER_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
	static final Dynamic<?> LILAC_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
	static final Dynamic<?> GRASS_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
	static final Dynamic<?> FERN_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
	static final Dynamic<?> ROSE_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
	static final Dynamic<?> PEONY_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:peony',Properties:{half:'upper'}}");
	static final Map<String, Dynamic<?>> FLOWER_POT = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		hashMap.put("minecraft:air0", BlockStateFlattening.parseState("{Name:'minecraft:flower_pot'}"));
		hashMap.put("minecraft:red_flower0", BlockStateFlattening.parseState("{Name:'minecraft:potted_poppy'}"));
		hashMap.put("minecraft:red_flower1", BlockStateFlattening.parseState("{Name:'minecraft:potted_blue_orchid'}"));
		hashMap.put("minecraft:red_flower2", BlockStateFlattening.parseState("{Name:'minecraft:potted_allium'}"));
		hashMap.put("minecraft:red_flower3", BlockStateFlattening.parseState("{Name:'minecraft:potted_azure_bluet'}"));
		hashMap.put("minecraft:red_flower4", BlockStateFlattening.parseState("{Name:'minecraft:potted_red_tulip'}"));
		hashMap.put("minecraft:red_flower5", BlockStateFlattening.parseState("{Name:'minecraft:potted_orange_tulip'}"));
		hashMap.put("minecraft:red_flower6", BlockStateFlattening.parseState("{Name:'minecraft:potted_white_tulip'}"));
		hashMap.put("minecraft:red_flower7", BlockStateFlattening.parseState("{Name:'minecraft:potted_pink_tulip'}"));
		hashMap.put("minecraft:red_flower8", BlockStateFlattening.parseState("{Name:'minecraft:potted_oxeye_daisy'}"));
		hashMap.put("minecraft:yellow_flower0", BlockStateFlattening.parseState("{Name:'minecraft:potted_dandelion'}"));
		hashMap.put("minecraft:sapling0", BlockStateFlattening.parseState("{Name:'minecraft:potted_oak_sapling'}"));
		hashMap.put("minecraft:sapling1", BlockStateFlattening.parseState("{Name:'minecraft:potted_spruce_sapling'}"));
		hashMap.put("minecraft:sapling2", BlockStateFlattening.parseState("{Name:'minecraft:potted_birch_sapling'}"));
		hashMap.put("minecraft:sapling3", BlockStateFlattening.parseState("{Name:'minecraft:potted_jungle_sapling'}"));
		hashMap.put("minecraft:sapling4", BlockStateFlattening.parseState("{Name:'minecraft:potted_acacia_sapling'}"));
		hashMap.put("minecraft:sapling5", BlockStateFlattening.parseState("{Name:'minecraft:potted_dark_oak_sapling'}"));
		hashMap.put("minecraft:red_mushroom0", BlockStateFlattening.parseState("{Name:'minecraft:potted_red_mushroom'}"));
		hashMap.put("minecraft:brown_mushroom0", BlockStateFlattening.parseState("{Name:'minecraft:potted_brown_mushroom'}"));
		hashMap.put("minecraft:deadbush0", BlockStateFlattening.parseState("{Name:'minecraft:potted_dead_bush'}"));
		hashMap.put("minecraft:tallgrass2", BlockStateFlattening.parseState("{Name:'minecraft:potted_fern'}"));
		hashMap.put("minecraft:cactus0", BlockStateFlattening.lookupState(2240));
	});
	static final Map<String, Dynamic<?>> SKULL = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		buildSkull(hashMap, 0, "skeleton", "skull");
		buildSkull(hashMap, 1, "wither_skeleton", "skull");
		buildSkull(hashMap, 2, "zombie", "head");
		buildSkull(hashMap, 3, "player", "head");
		buildSkull(hashMap, 4, "creeper", "head");
		buildSkull(hashMap, 5, "dragon", "head");
	});
	static final Map<String, Dynamic<?>> DOOR = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		buildDoor(hashMap, "oak_door", 1024);
		buildDoor(hashMap, "iron_door", 1136);
		buildDoor(hashMap, "spruce_door", 3088);
		buildDoor(hashMap, "birch_door", 3104);
		buildDoor(hashMap, "jungle_door", 3120);
		buildDoor(hashMap, "acacia_door", 3136);
		buildDoor(hashMap, "dark_oak_door", 3152);
	});
	static final Map<String, Dynamic<?>> NOTE_BLOCK = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		for (int i = 0; i < 26; i++) {
			hashMap.put("true" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
			hashMap.put("false" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
		}
	});
	private static final Int2ObjectMap<String> COLORS = (Int2ObjectMap<String>)DataFixUtils.make(new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
		int2ObjectOpenHashMap.put(0, "white");
		int2ObjectOpenHashMap.put(1, "orange");
		int2ObjectOpenHashMap.put(2, "magenta");
		int2ObjectOpenHashMap.put(3, "light_blue");
		int2ObjectOpenHashMap.put(4, "yellow");
		int2ObjectOpenHashMap.put(5, "lime");
		int2ObjectOpenHashMap.put(6, "pink");
		int2ObjectOpenHashMap.put(7, "gray");
		int2ObjectOpenHashMap.put(8, "light_gray");
		int2ObjectOpenHashMap.put(9, "cyan");
		int2ObjectOpenHashMap.put(10, "purple");
		int2ObjectOpenHashMap.put(11, "blue");
		int2ObjectOpenHashMap.put(12, "brown");
		int2ObjectOpenHashMap.put(13, "green");
		int2ObjectOpenHashMap.put(14, "red");
		int2ObjectOpenHashMap.put(15, "black");
	});
	static final Map<String, Dynamic<?>> BED = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		ObjectIterator var1 = COLORS.int2ObjectEntrySet().iterator();

		while (var1.hasNext()) {
			Entry<String> entry = (Entry<String>)var1.next();
			if (!Objects.equals(entry.getValue(), "red")) {
				buildBed(hashMap, entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	static final Map<String, Dynamic<?>> BANNER = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		ObjectIterator var1 = COLORS.int2ObjectEntrySet().iterator();

		while (var1.hasNext()) {
			Entry<String> entry = (Entry<String>)var1.next();
			if (!Objects.equals(entry.getValue(), "white")) {
				buildBanner(hashMap, 15 - entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	static final Dynamic<?> AIR = BlockStateFlattening.lookupState(0);
	private static final int field_29870 = 4096;

	public ChunkPalettedStorageFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	private static void buildSkull(Map<String, Dynamic<?>> out, int i, String mob, String block) {
		out.put(i + "north", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'north'}}"));
		out.put(i + "east", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'east'}}"));
		out.put(i + "south", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'south'}}"));
		out.put(i + "west", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'west'}}"));

		for (int j = 0; j < 16; j++) {
			out.put("" + i + j, BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_" + block + "',Properties:{rotation:'" + j + "'}}"));
		}
	}

	private static void buildDoor(Map<String, Dynamic<?>> out, String name, int i) {
		out.put(
			"minecraft:" + name + "eastlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "eastlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "eastlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "eastlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastlowerrightfalsefalse", BlockStateFlattening.lookupState(i));
		out.put(
			"minecraft:" + name + "eastlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastlowerrighttruefalse", BlockStateFlattening.lookupState(i + 4));
		out.put(
			"minecraft:" + name + "eastlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastupperleftfalsefalse", BlockStateFlattening.lookupState(i + 8));
		out.put("minecraft:" + name + "eastupperleftfalsetrue", BlockStateFlattening.lookupState(i + 10));
		out.put(
			"minecraft:" + name + "eastupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "eastupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastupperrightfalsefalse", BlockStateFlattening.lookupState(i + 9));
		out.put("minecraft:" + name + "eastupperrightfalsetrue", BlockStateFlattening.lookupState(i + 11));
		out.put(
			"minecraft:" + name + "eastupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "eastupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "northlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 3));
		out.put(
			"minecraft:" + name + "northlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "northlowerrighttruefalse", BlockStateFlattening.lookupState(i + 7));
		out.put(
			"minecraft:" + name + "northlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northupperleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northupperleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northupperrightfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northupperrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "southlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 1));
		out.put(
			"minecraft:" + name + "southlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "southlowerrighttruefalse", BlockStateFlattening.lookupState(i + 5));
		out.put(
			"minecraft:" + name + "southlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southupperleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southupperleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southupperrightfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southupperrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "westlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 2));
		out.put(
			"minecraft:" + name + "westlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "westlowerrighttruefalse", BlockStateFlattening.lookupState(i + 6));
		out.put(
			"minecraft:" + name + "westlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westupperleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westupperleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westupperrightfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westupperrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
	}

	private static void buildBed(Map<String, Dynamic<?>> out, int i, String string) {
		out.put(
			"southfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}")
		);
		out.put("westfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
		out.put(
			"northfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}")
		);
		out.put("eastfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
		out.put(
			"southfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}")
		);
		out.put("westfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
		out.put(
			"northfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}")
		);
		out.put("eastfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
		out.put("southtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
		out.put("westtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
		out.put("northtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
		out.put("easttruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
	}

	private static void buildBanner(Map<String, Dynamic<?>> out, int i, String string) {
		for (int j = 0; j < 16; j++) {
			out.put(j + "_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_banner',Properties:{rotation:'" + j + "'}}"));
		}

		out.put("north_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'north'}}"));
		out.put("south_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'south'}}"));
		out.put("west_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'west'}}"));
		out.put("east_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'east'}}"));
	}

	public static String getName(Dynamic<?> dynamic) {
		return dynamic.get("Name").asString("");
	}

	public static String getProperty(Dynamic<?> dynamic, String string) {
		return dynamic.get("Properties").get(string).asString("");
	}

	public static int addTo(Int2ObjectBiMap<Dynamic<?>> int2ObjectBiMap, Dynamic<?> dynamic) {
		int i = int2ObjectBiMap.getRawId(dynamic);
		if (i == -1) {
			i = int2ObjectBiMap.add(dynamic);
		}

		return i;
	}

	private Dynamic<?> fixChunk(Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = dynamic.get("Level").result();
		return optional.isPresent() && ((Dynamic)optional.get()).get("Sections").asStreamOpt().result().isPresent()
			? dynamic.set("Level", new ChunkPalettedStorageFix.Level((Dynamic<?>)optional.get()).transform())
			: dynamic;
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
		return this.writeFixAndRead("ChunkPalettedStorageFix", type, type2, this::fixChunk);
	}

	public static int getSideToUpgradeFlag(boolean west, boolean east, boolean north, boolean south) {
		int i = 0;
		if (north) {
			if (east) {
				i |= 2;
			} else if (west) {
				i |= 128;
			} else {
				i |= 1;
			}
		} else if (south) {
			if (west) {
				i |= 32;
			} else if (east) {
				i |= 8;
			} else {
				i |= 16;
			}
		} else if (east) {
			i |= 4;
		} else if (west) {
			i |= 64;
		}

		return i;
	}

	static {
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(2);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(3);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(110);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(140);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(144);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(25);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(86);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(26);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(176);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(177);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(175);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(64);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(71);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(193);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(194);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(195);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(196);
		BLOCKS_NEEDING_IN_PLACE_UPDATE.set(197);
		BLOCKS_NEEDING_SIDE_UPDATE.set(54);
		BLOCKS_NEEDING_SIDE_UPDATE.set(146);
		BLOCKS_NEEDING_SIDE_UPDATE.set(25);
		BLOCKS_NEEDING_SIDE_UPDATE.set(26);
		BLOCKS_NEEDING_SIDE_UPDATE.set(51);
		BLOCKS_NEEDING_SIDE_UPDATE.set(53);
		BLOCKS_NEEDING_SIDE_UPDATE.set(67);
		BLOCKS_NEEDING_SIDE_UPDATE.set(108);
		BLOCKS_NEEDING_SIDE_UPDATE.set(109);
		BLOCKS_NEEDING_SIDE_UPDATE.set(114);
		BLOCKS_NEEDING_SIDE_UPDATE.set(128);
		BLOCKS_NEEDING_SIDE_UPDATE.set(134);
		BLOCKS_NEEDING_SIDE_UPDATE.set(135);
		BLOCKS_NEEDING_SIDE_UPDATE.set(136);
		BLOCKS_NEEDING_SIDE_UPDATE.set(156);
		BLOCKS_NEEDING_SIDE_UPDATE.set(163);
		BLOCKS_NEEDING_SIDE_UPDATE.set(164);
		BLOCKS_NEEDING_SIDE_UPDATE.set(180);
		BLOCKS_NEEDING_SIDE_UPDATE.set(203);
		BLOCKS_NEEDING_SIDE_UPDATE.set(55);
		BLOCKS_NEEDING_SIDE_UPDATE.set(85);
		BLOCKS_NEEDING_SIDE_UPDATE.set(113);
		BLOCKS_NEEDING_SIDE_UPDATE.set(188);
		BLOCKS_NEEDING_SIDE_UPDATE.set(189);
		BLOCKS_NEEDING_SIDE_UPDATE.set(190);
		BLOCKS_NEEDING_SIDE_UPDATE.set(191);
		BLOCKS_NEEDING_SIDE_UPDATE.set(192);
		BLOCKS_NEEDING_SIDE_UPDATE.set(93);
		BLOCKS_NEEDING_SIDE_UPDATE.set(94);
		BLOCKS_NEEDING_SIDE_UPDATE.set(101);
		BLOCKS_NEEDING_SIDE_UPDATE.set(102);
		BLOCKS_NEEDING_SIDE_UPDATE.set(160);
		BLOCKS_NEEDING_SIDE_UPDATE.set(106);
		BLOCKS_NEEDING_SIDE_UPDATE.set(107);
		BLOCKS_NEEDING_SIDE_UPDATE.set(183);
		BLOCKS_NEEDING_SIDE_UPDATE.set(184);
		BLOCKS_NEEDING_SIDE_UPDATE.set(185);
		BLOCKS_NEEDING_SIDE_UPDATE.set(186);
		BLOCKS_NEEDING_SIDE_UPDATE.set(187);
		BLOCKS_NEEDING_SIDE_UPDATE.set(132);
		BLOCKS_NEEDING_SIDE_UPDATE.set(139);
		BLOCKS_NEEDING_SIDE_UPDATE.set(199);
	}

	static class ChunkNibbleArray {
		private static final int field_29879 = 2048;
		private static final int field_29880 = 4;
		private final byte[] contents;

		public ChunkNibbleArray() {
			this.contents = new byte[2048];
		}

		public ChunkNibbleArray(byte[] bs) {
			this.contents = bs;
			if (bs.length != 2048) {
				throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length);
			}
		}

		public int get(int x, int y, int i) {
			int j = this.getRawIndex(y << 8 | i << 4 | x);
			return this.usesLowNibble(y << 8 | i << 4 | x) ? this.contents[j] & 15 : this.contents[j] >> 4 & 15;
		}

		private boolean usesLowNibble(int index) {
			return (index & 1) == 0;
		}

		private int getRawIndex(int index) {
			return index >> 1;
		}
	}

	public static enum Facing {
		DOWN(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.Y),
		UP(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.Y),
		NORTH(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.Z),
		SOUTH(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.Z),
		WEST(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.X),
		EAST(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.X);

		private final ChunkPalettedStorageFix.Facing.Axis axis;
		private final ChunkPalettedStorageFix.Facing.Direction direction;

		private Facing(ChunkPalettedStorageFix.Facing.Direction direction, ChunkPalettedStorageFix.Facing.Axis axis) {
			this.axis = axis;
			this.direction = direction;
		}

		public ChunkPalettedStorageFix.Facing.Direction getDirection() {
			return this.direction;
		}

		public ChunkPalettedStorageFix.Facing.Axis getAxis() {
			return this.axis;
		}

		public static enum Axis {
			X,
			Y,
			Z;
		}

		public static enum Direction {
			POSITIVE(1),
			NEGATIVE(-1);

			private final int offset;

			private Direction(int j) {
				this.offset = j;
			}

			public int getOffset() {
				return this.offset;
			}
		}
	}

	static final class Level {
		private int sidesToUpgrade;
		private final ChunkPalettedStorageFix.Section[] sections = new ChunkPalettedStorageFix.Section[16];
		private final Dynamic<?> level;
		private final int x;
		private final int z;
		private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

		public Level(Dynamic<?> dynamic) {
			this.level = dynamic;
			this.x = dynamic.get("xPos").asInt(0) << 4;
			this.z = dynamic.get("zPos").asInt(0) << 4;
			dynamic.get("TileEntities").asStreamOpt().result().ifPresent(stream -> stream.forEach(dynamicx -> {
					int ix = dynamicx.get("x").asInt(0) - this.x & 15;
					int j = dynamicx.get("y").asInt(0);
					int k = dynamicx.get("z").asInt(0) - this.z & 15;
					int l = j << 8 | k << 4 | ix;
					if (this.blockEntities.put(l, dynamicx) != null) {
						ChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.x, this.z, ix, j, k);
					}
				}));
			boolean bl = dynamic.get("convertedFromAlphaFormat").asBoolean(false);
			dynamic.get("Sections").asStreamOpt().result().ifPresent(stream -> stream.forEach(dynamicx -> {
					ChunkPalettedStorageFix.Section sectionx = new ChunkPalettedStorageFix.Section(dynamicx);
					this.sidesToUpgrade = sectionx.visit(this.sidesToUpgrade);
					this.sections[sectionx.y] = sectionx;
				}));

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					ObjectIterator var7 = section.inPlaceUpdates.entrySet().iterator();

					while (var7.hasNext()) {
						java.util.Map.Entry<Integer, IntList> entry = (java.util.Map.Entry<Integer, IntList>)var7.next();
						int i = section.y << 12;
						switch (entry.getKey()) {
							case 2:
								IntListIterator var30 = ((IntList)entry.getValue()).iterator();

								while (var30.hasNext()) {
									int j = (Integer)var30.next();
									j |= i;
									Dynamic<?> dynamic2 = this.getBlock(j);
									if ("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic2))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(j, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(j, ChunkPalettedStorageFix.SNOWY_GRASS);
										}
									}
								}
								break;
							case 3:
								IntListIterator var29 = ((IntList)entry.getValue()).iterator();

								while (var29.hasNext()) {
									int k = (Integer)var29.next();
									k |= i;
									Dynamic<?> dynamic3 = this.getBlock(k);
									if ("minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic3))) {
										String string2 = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(k, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string2) || "minecraft:snow_layer".equals(string2)) {
											this.setBlock(k, ChunkPalettedStorageFix.PODZOL);
										}
									}
								}
								break;
							case 25:
								IntListIterator var28 = ((IntList)entry.getValue()).iterator();

								while (var28.hasNext()) {
									int m = (Integer)var28.next();
									m |= i;
									Dynamic<?> dynamic5 = this.removeBlockEntity(m);
									if (dynamic5 != null) {
										String string4 = Boolean.toString(dynamic5.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic5.get("note").asInt(0), 0), 24);
										this.setBlock(m, (Dynamic<?>)ChunkPalettedStorageFix.NOTE_BLOCK.getOrDefault(string4, (Dynamic)ChunkPalettedStorageFix.NOTE_BLOCK.get("false0")));
									}
								}
								break;
							case 26:
								IntListIterator var27 = ((IntList)entry.getValue()).iterator();

								while (var27.hasNext()) {
									int n = (Integer)var27.next();
									n |= i;
									Dynamic<?> dynamic6 = this.getBlockEntity(n);
									Dynamic<?> dynamic7 = this.getBlock(n);
									if (dynamic6 != null) {
										int o = dynamic6.get("color").asInt(0);
										if (o != 14 && o >= 0 && o < 16) {
											String string5 = ChunkPalettedStorageFix.getProperty(dynamic7, "facing")
												+ ChunkPalettedStorageFix.getProperty(dynamic7, "occupied")
												+ ChunkPalettedStorageFix.getProperty(dynamic7, "part")
												+ o;
											if (ChunkPalettedStorageFix.BED.containsKey(string5)) {
												this.setBlock(n, (Dynamic<?>)ChunkPalettedStorageFix.BED.get(string5));
											}
										}
									}
								}
								break;
							case 64:
							case 71:
							case 193:
							case 194:
							case 195:
							case 196:
							case 197:
								IntListIterator var26 = ((IntList)entry.getValue()).iterator();

								while (var26.hasNext()) {
									int u = (Integer)var26.next();
									u |= i;
									Dynamic<?> dynamic13 = this.getBlock(u);
									if (ChunkPalettedStorageFix.getName(dynamic13).endsWith("_door")) {
										Dynamic<?> dynamic14 = this.getBlock(u);
										if ("lower".equals(ChunkPalettedStorageFix.getProperty(dynamic14, "half"))) {
											int v = adjacentTo(u, ChunkPalettedStorageFix.Facing.UP);
											Dynamic<?> dynamic15 = this.getBlock(v);
											String string13 = ChunkPalettedStorageFix.getName(dynamic14);
											if (string13.equals(ChunkPalettedStorageFix.getName(dynamic15))) {
												String string14 = ChunkPalettedStorageFix.getProperty(dynamic14, "facing");
												String string15 = ChunkPalettedStorageFix.getProperty(dynamic14, "open");
												String string16 = bl ? "left" : ChunkPalettedStorageFix.getProperty(dynamic15, "hinge");
												String string17 = bl ? "false" : ChunkPalettedStorageFix.getProperty(dynamic15, "powered");
												this.setBlock(u, (Dynamic<?>)ChunkPalettedStorageFix.DOOR.get(string13 + string14 + "lower" + string16 + string15 + string17));
												this.setBlock(v, (Dynamic<?>)ChunkPalettedStorageFix.DOOR.get(string13 + string14 + "upper" + string16 + string15 + string17));
											}
										}
									}
								}
								break;
							case 86:
								IntListIterator var25 = ((IntList)entry.getValue()).iterator();

								while (var25.hasNext()) {
									int r = (Integer)var25.next();
									r |= i;
									Dynamic<?> dynamic10 = this.getBlock(r);
									if ("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic10))) {
										String string7 = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(r, ChunkPalettedStorageFix.Facing.DOWN)));
										if ("minecraft:grass_block".equals(string7) || "minecraft:dirt".equals(string7)) {
											this.setBlock(r, ChunkPalettedStorageFix.PUMPKIN);
										}
									}
								}
								break;
							case 110:
								IntListIterator var24 = ((IntList)entry.getValue()).iterator();

								while (var24.hasNext()) {
									int l = (Integer)var24.next();
									l |= i;
									Dynamic<?> dynamic4 = this.getBlock(l);
									if ("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic4))) {
										String string3 = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(l, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string3) || "minecraft:snow_layer".equals(string3)) {
											this.setBlock(l, ChunkPalettedStorageFix.SNOWY_MYCELIUM);
										}
									}
								}
								break;
							case 140:
								IntListIterator var23 = ((IntList)entry.getValue()).iterator();

								while (var23.hasNext()) {
									int s = (Integer)var23.next();
									s |= i;
									Dynamic<?> dynamic11 = this.removeBlockEntity(s);
									if (dynamic11 != null) {
										String string8 = dynamic11.get("Item").asString("") + dynamic11.get("Data").asInt(0);
										this.setBlock(
											s, (Dynamic<?>)ChunkPalettedStorageFix.FLOWER_POT.getOrDefault(string8, (Dynamic)ChunkPalettedStorageFix.FLOWER_POT.get("minecraft:air0"))
										);
									}
								}
								break;
							case 144:
								IntListIterator var22 = ((IntList)entry.getValue()).iterator();

								while (var22.hasNext()) {
									int t = (Integer)var22.next();
									t |= i;
									Dynamic<?> dynamic12 = this.getBlockEntity(t);
									if (dynamic12 != null) {
										String string9 = String.valueOf(dynamic12.get("SkullType").asInt(0));
										String string10 = ChunkPalettedStorageFix.getProperty(this.getBlock(t), "facing");
										String string12;
										if (!"up".equals(string10) && !"down".equals(string10)) {
											string12 = string9 + string10;
										} else {
											string12 = string9 + dynamic12.get("Rot").asInt(0);
										}

										dynamic12.remove("SkullType");
										dynamic12.remove("facing");
										dynamic12.remove("Rot");
										this.setBlock(t, (Dynamic<?>)ChunkPalettedStorageFix.SKULL.getOrDefault(string12, (Dynamic)ChunkPalettedStorageFix.SKULL.get("0north")));
									}
								}
								break;
							case 175:
								IntListIterator var21 = ((IntList)entry.getValue()).iterator();

								while (var21.hasNext()) {
									int w = (Integer)var21.next();
									w |= i;
									Dynamic<?> dynamic16 = this.getBlock(w);
									if ("upper".equals(ChunkPalettedStorageFix.getProperty(dynamic16, "half"))) {
										Dynamic<?> dynamic17 = this.getBlock(adjacentTo(w, ChunkPalettedStorageFix.Facing.DOWN));
										String string18 = ChunkPalettedStorageFix.getName(dynamic17);
										if ("minecraft:sunflower".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.SUNFLOWER_UPPER);
										} else if ("minecraft:lilac".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.LILAC_UPPER);
										} else if ("minecraft:tall_grass".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.GRASS_UPPER);
										} else if ("minecraft:large_fern".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.FERN_UPPER);
										} else if ("minecraft:rose_bush".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.ROSE_UPPER);
										} else if ("minecraft:peony".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.PEONY_UPPER);
										}
									}
								}
								break;
							case 176:
							case 177:
								IntListIterator var10 = ((IntList)entry.getValue()).iterator();

								while (var10.hasNext()) {
									int p = (Integer)var10.next();
									p |= i;
									Dynamic<?> dynamic8 = this.getBlockEntity(p);
									Dynamic<?> dynamic9 = this.getBlock(p);
									if (dynamic8 != null) {
										int q = dynamic8.get("Base").asInt(0);
										if (q != 15 && q >= 0 && q < 16) {
											String string6 = ChunkPalettedStorageFix.getProperty(dynamic9, entry.getKey() == 176 ? "rotation" : "facing") + "_" + q;
											if (ChunkPalettedStorageFix.BANNER.containsKey(string6)) {
												this.setBlock(p, (Dynamic<?>)ChunkPalettedStorageFix.BANNER.get(string6));
											}
										}
									}
								}
						}
					}
				}
			}
		}

		@Nullable
		private Dynamic<?> getBlockEntity(int i) {
			return (Dynamic<?>)this.blockEntities.get(i);
		}

		@Nullable
		private Dynamic<?> removeBlockEntity(int i) {
			return (Dynamic<?>)this.blockEntities.remove(i);
		}

		public static int adjacentTo(int i, ChunkPalettedStorageFix.Facing direction) {
			switch (direction.getAxis()) {
				case X:
					int j = (i & 15) + direction.getDirection().getOffset();
					return j >= 0 && j <= 15 ? i & -16 | j : -1;
				case Y:
					int k = (i >> 8) + direction.getDirection().getOffset();
					return k >= 0 && k <= 255 ? i & 0xFF | k << 8 : -1;
				case Z:
					int l = (i >> 4 & 15) + direction.getDirection().getOffset();
					return l >= 0 && l <= 15 ? i & -241 | l << 4 : -1;
				default:
					return -1;
			}
		}

		private void setBlock(int i, Dynamic<?> dynamic) {
			if (i >= 0 && i <= 65535) {
				ChunkPalettedStorageFix.Section section = this.getSection(i);
				if (section != null) {
					section.setBlock(i & 4095, dynamic);
				}
			}
		}

		@Nullable
		private ChunkPalettedStorageFix.Section getSection(int i) {
			int j = i >> 12;
			return j < this.sections.length ? this.sections[j] : null;
		}

		public Dynamic<?> getBlock(int i) {
			if (i >= 0 && i <= 65535) {
				ChunkPalettedStorageFix.Section section = this.getSection(i);
				return section == null ? ChunkPalettedStorageFix.AIR : section.getBlock(i & 4095);
			} else {
				return ChunkPalettedStorageFix.AIR;
			}
		}

		public Dynamic<?> transform() {
			Dynamic<?> dynamic = this.level;
			if (this.blockEntities.isEmpty()) {
				dynamic = dynamic.remove("TileEntities");
			} else {
				dynamic = dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
			}

			Dynamic<?> dynamic2 = dynamic.emptyMap();
			List<Dynamic<?>> list = Lists.newArrayList();

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					list.add(section.transform());
					dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.innerPositions.toIntArray())));
				}
			}

			Dynamic<?> dynamic3 = dynamic.emptyMap();
			dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.sidesToUpgrade));
			dynamic3 = dynamic3.set("Indices", dynamic2);
			return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic3.createList(list.stream()));
		}
	}

	static class Section {
		private final Int2ObjectBiMap<Dynamic<?>> paletteMap = new Int2ObjectBiMap<>(32);
		private final List<Dynamic<?>> paletteData;
		private final Dynamic<?> section;
		private final boolean hasBlocks;
		final Int2ObjectMap<IntList> inPlaceUpdates = new Int2ObjectLinkedOpenHashMap();
		final IntList innerPositions = new IntArrayList();
		public final int y;
		private final Set<Dynamic<?>> seenStates = Sets.newIdentityHashSet();
		private final int[] states = new int[4096];

		public Section(Dynamic<?> dynamic) {
			this.paletteData = Lists.newArrayList();
			this.section = dynamic;
			this.y = dynamic.get("Y").asInt(0);
			this.hasBlocks = dynamic.get("Blocks").result().isPresent();
		}

		public Dynamic<?> getBlock(int index) {
			if (index >= 0 && index <= 4095) {
				Dynamic<?> dynamic = this.paletteMap.get(this.states[index]);
				return dynamic == null ? ChunkPalettedStorageFix.AIR : dynamic;
			} else {
				return ChunkPalettedStorageFix.AIR;
			}
		}

		public void setBlock(int pos, Dynamic<?> dynamic) {
			if (this.seenStates.add(dynamic)) {
				this.paletteData.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? ChunkPalettedStorageFix.AIR : dynamic);
			}

			this.states[pos] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
		}

		public int visit(int sidesToUpgrade) {
			if (!this.hasBlocks) {
				return sidesToUpgrade;
			} else {
				ByteBuffer byteBuffer = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Data")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.result()
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray2 = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Add")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.result()
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				this.seenStates.add(ChunkPalettedStorageFix.AIR);
				ChunkPalettedStorageFix.addTo(this.paletteMap, ChunkPalettedStorageFix.AIR);
				this.paletteData.add(ChunkPalettedStorageFix.AIR);

				for (int i = 0; i < 4096; i++) {
					int j = i & 15;
					int k = i >> 8 & 15;
					int l = i >> 4 & 15;
					int m = chunkNibbleArray2.get(j, k, l) << 12 | (byteBuffer.get(i) & 255) << 4 | chunkNibbleArray.get(j, k, l);
					if (ChunkPalettedStorageFix.BLOCKS_NEEDING_IN_PLACE_UPDATE.get(m >> 4)) {
						this.addInPlaceUpdate(m >> 4, i);
					}

					if (ChunkPalettedStorageFix.BLOCKS_NEEDING_SIDE_UPDATE.get(m >> 4)) {
						int n = ChunkPalettedStorageFix.getSideToUpgradeFlag(j == 0, j == 15, l == 0, l == 15);
						if (n == 0) {
							this.innerPositions.add(i);
						} else {
							sidesToUpgrade |= n;
						}
					}

					this.setBlock(i, BlockStateFlattening.lookupState(m));
				}

				return sidesToUpgrade;
			}
		}

		private void addInPlaceUpdate(int section, int index) {
			IntList intList = (IntList)this.inPlaceUpdates.get(section);
			if (intList == null) {
				intList = new IntArrayList();
				this.inPlaceUpdates.put(section, intList);
			}

			intList.add(index);
		}

		public Dynamic<?> transform() {
			Dynamic<?> dynamic = this.section;
			if (!this.hasBlocks) {
				return dynamic;
			} else {
				dynamic = dynamic.set("Palette", dynamic.createList(this.paletteData.stream()));
				int i = Math.max(4, DataFixUtils.ceillog2(this.seenStates.size()));
				WordPackedArray wordPackedArray = new WordPackedArray(i, 4096);

				for (int j = 0; j < this.states.length; j++) {
					wordPackedArray.set(j, this.states[j]);
				}

				dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(wordPackedArray.getAlignedArray())));
				dynamic = dynamic.remove("Blocks");
				dynamic = dynamic.remove("Data");
				return dynamic.remove("Add");
			}
		}
	}
}
