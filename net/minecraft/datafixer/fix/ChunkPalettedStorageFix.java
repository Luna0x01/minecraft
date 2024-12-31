package net.minecraft.datafixer.fix;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.PackedIntegerArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPalettedStorageFix extends DataFix {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final BitSet blocksNeedingSideUpdate = new BitSet(256);
	private static final BitSet blocksNeedingInPlaceUpdate = new BitSet(256);
	private static final Dynamic<?> pumpkin = BlockStateFlattening.parseState("{Name:'minecraft:pumpkin'}");
	private static final Dynamic<?> podzol = BlockStateFlattening.parseState("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
	private static final Dynamic<?> snowyGrass = BlockStateFlattening.parseState("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
	private static final Dynamic<?> snowyMycelium = BlockStateFlattening.parseState("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
	private static final Dynamic<?> sunflowerUpper = BlockStateFlattening.parseState("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
	private static final Dynamic<?> lilacUpper = BlockStateFlattening.parseState("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
	private static final Dynamic<?> grassUpper = BlockStateFlattening.parseState("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
	private static final Dynamic<?> fernUpper = BlockStateFlattening.parseState("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
	private static final Dynamic<?> roseUpper = BlockStateFlattening.parseState("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
	private static final Dynamic<?> peonyUpper = BlockStateFlattening.parseState("{Name:'minecraft:peony',Properties:{half:'upper'}}");
	private static final Map<String, Dynamic<?>> flowerPot = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
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
	private static final Map<String, Dynamic<?>> skull = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		buildSkull(hashMap, 0, "skeleton", "skull");
		buildSkull(hashMap, 1, "wither_skeleton", "skull");
		buildSkull(hashMap, 2, "zombie", "head");
		buildSkull(hashMap, 3, "player", "head");
		buildSkull(hashMap, 4, "creeper", "head");
		buildSkull(hashMap, 5, "dragon", "head");
	});
	private static final Map<String, Dynamic<?>> door = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		buildDoor(hashMap, "oak_door", 1024);
		buildDoor(hashMap, "iron_door", 1136);
		buildDoor(hashMap, "spruce_door", 3088);
		buildDoor(hashMap, "birch_door", 3104);
		buildDoor(hashMap, "jungle_door", 3120);
		buildDoor(hashMap, "acacia_door", 3136);
		buildDoor(hashMap, "dark_oak_door", 3152);
	});
	private static final Map<String, Dynamic<?>> noteblock = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		for (int i = 0; i < 26; i++) {
			hashMap.put("true" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
			hashMap.put("false" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
		}
	});
	private static final Int2ObjectMap<String> colors = (Int2ObjectMap<String>)DataFixUtils.make(new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
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
	private static final Map<String, Dynamic<?>> bed = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		ObjectIterator var1 = colors.int2ObjectEntrySet().iterator();

		while (var1.hasNext()) {
			Entry<String> entry = (Entry<String>)var1.next();
			if (!Objects.equals(entry.getValue(), "red")) {
				buildBed(hashMap, entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	private static final Map<String, Dynamic<?>> banner = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		ObjectIterator var1 = colors.int2ObjectEntrySet().iterator();

		while (var1.hasNext()) {
			Entry<String> entry = (Entry<String>)var1.next();
			if (!Objects.equals(entry.getValue(), "white")) {
				buildBanner(hashMap, 15 - entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	private static final Dynamic<?> air = BlockStateFlattening.lookupState(0);

	public ChunkPalettedStorageFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private static void buildSkull(Map<String, Dynamic<?>> map, int i, String string, String string2) {
		map.put(i + "north", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'north'}}"));
		map.put(i + "east", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'east'}}"));
		map.put(i + "south", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'south'}}"));
		map.put(i + "west", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'west'}}"));

		for (int j = 0; j < 16; j++) {
			map.put(i + "" + j, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_" + string2 + "',Properties:{rotation:'" + j + "'}}"));
		}
	}

	private static void buildDoor(Map<String, Dynamic<?>> map, String string, int i) {
		map.put(
			"minecraft:" + string + "eastlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "eastlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastlowerrightfalsefalse", BlockStateFlattening.lookupState(i));
		map.put(
			"minecraft:" + string + "eastlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastlowerrighttruefalse", BlockStateFlattening.lookupState(i + 4));
		map.put(
			"minecraft:" + string + "eastlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastupperleftfalsefalse", BlockStateFlattening.lookupState(i + 8));
		map.put("minecraft:" + string + "eastupperleftfalsetrue", BlockStateFlattening.lookupState(i + 10));
		map.put(
			"minecraft:" + string + "eastupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastupperrightfalsefalse", BlockStateFlattening.lookupState(i + 9));
		map.put("minecraft:" + string + "eastupperrightfalsetrue", BlockStateFlattening.lookupState(i + 11));
		map.put(
			"minecraft:" + string + "eastupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "northlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 3));
		map.put(
			"minecraft:" + string + "northlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "northlowerrighttruefalse", BlockStateFlattening.lookupState(i + 7));
		map.put(
			"minecraft:" + string + "northlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrightfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "southlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 1));
		map.put(
			"minecraft:" + string + "southlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "southlowerrighttruefalse", BlockStateFlattening.lookupState(i + 5));
		map.put(
			"minecraft:" + string + "southlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrightfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "westlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 2));
		map.put(
			"minecraft:" + string + "westlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "westlowerrighttruefalse", BlockStateFlattening.lookupState(i + 6));
		map.put(
			"minecraft:" + string + "westlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrightfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
	}

	private static void buildBed(Map<String, Dynamic<?>> map, int i, String string) {
		map.put(
			"southfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}")
		);
		map.put("westfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
		map.put(
			"northfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}")
		);
		map.put("eastfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
		map.put(
			"southfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}")
		);
		map.put("westfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
		map.put(
			"northfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}")
		);
		map.put("eastfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
		map.put("southtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
		map.put("westtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
		map.put("northtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
		map.put("easttruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
	}

	private static void buildBanner(Map<String, Dynamic<?>> map, int i, String string) {
		for (int j = 0; j < 16; j++) {
			map.put("" + j + "_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_banner',Properties:{rotation:'" + j + "'}}"));
		}

		map.put("north_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'north'}}"));
		map.put("south_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'south'}}"));
		map.put("west_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'west'}}"));
		map.put("east_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'east'}}"));
	}

	public static String getName(Dynamic<?> dynamic) {
		return dynamic.get("Name").asString("");
	}

	public static String getProperty(Dynamic<?> dynamic, String string) {
		return dynamic.get("Properties").get(string).asString("");
	}

	public static int addTo(Int2ObjectBiMap<Dynamic<?>> int2ObjectBiMap, Dynamic<?> dynamic) {
		int i = int2ObjectBiMap.getId(dynamic);
		if (i == -1) {
			i = int2ObjectBiMap.add(dynamic);
		}

		return i;
	}

	private Dynamic<?> fixChunk(Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = dynamic.get("Level").get();
		return optional.isPresent() && ((Dynamic)optional.get()).get("Sections").asStreamOpt().isPresent()
			? dynamic.set("Level", new ChunkPalettedStorageFix.Level((Dynamic<?>)optional.get()).transform())
			: dynamic;
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
		return this.writeFixAndRead("ChunkPalettedStorageFix", type, type2, this::fixChunk);
	}

	public static int getSideToUpgradeFlag(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		int i = 0;
		if (bl3) {
			if (bl2) {
				i |= 2;
			} else if (bl) {
				i |= 128;
			} else {
				i |= 1;
			}
		} else if (bl4) {
			if (bl) {
				i |= 32;
			} else if (bl2) {
				i |= 8;
			} else {
				i |= 16;
			}
		} else if (bl2) {
			i |= 4;
		} else if (bl) {
			i |= 64;
		}

		return i;
	}

	static {
		blocksNeedingInPlaceUpdate.set(2);
		blocksNeedingInPlaceUpdate.set(3);
		blocksNeedingInPlaceUpdate.set(110);
		blocksNeedingInPlaceUpdate.set(140);
		blocksNeedingInPlaceUpdate.set(144);
		blocksNeedingInPlaceUpdate.set(25);
		blocksNeedingInPlaceUpdate.set(86);
		blocksNeedingInPlaceUpdate.set(26);
		blocksNeedingInPlaceUpdate.set(176);
		blocksNeedingInPlaceUpdate.set(177);
		blocksNeedingInPlaceUpdate.set(175);
		blocksNeedingInPlaceUpdate.set(64);
		blocksNeedingInPlaceUpdate.set(71);
		blocksNeedingInPlaceUpdate.set(193);
		blocksNeedingInPlaceUpdate.set(194);
		blocksNeedingInPlaceUpdate.set(195);
		blocksNeedingInPlaceUpdate.set(196);
		blocksNeedingInPlaceUpdate.set(197);
		blocksNeedingSideUpdate.set(54);
		blocksNeedingSideUpdate.set(146);
		blocksNeedingSideUpdate.set(25);
		blocksNeedingSideUpdate.set(26);
		blocksNeedingSideUpdate.set(51);
		blocksNeedingSideUpdate.set(53);
		blocksNeedingSideUpdate.set(67);
		blocksNeedingSideUpdate.set(108);
		blocksNeedingSideUpdate.set(109);
		blocksNeedingSideUpdate.set(114);
		blocksNeedingSideUpdate.set(128);
		blocksNeedingSideUpdate.set(134);
		blocksNeedingSideUpdate.set(135);
		blocksNeedingSideUpdate.set(136);
		blocksNeedingSideUpdate.set(156);
		blocksNeedingSideUpdate.set(163);
		blocksNeedingSideUpdate.set(164);
		blocksNeedingSideUpdate.set(180);
		blocksNeedingSideUpdate.set(203);
		blocksNeedingSideUpdate.set(55);
		blocksNeedingSideUpdate.set(85);
		blocksNeedingSideUpdate.set(113);
		blocksNeedingSideUpdate.set(188);
		blocksNeedingSideUpdate.set(189);
		blocksNeedingSideUpdate.set(190);
		blocksNeedingSideUpdate.set(191);
		blocksNeedingSideUpdate.set(192);
		blocksNeedingSideUpdate.set(93);
		blocksNeedingSideUpdate.set(94);
		blocksNeedingSideUpdate.set(101);
		blocksNeedingSideUpdate.set(102);
		blocksNeedingSideUpdate.set(160);
		blocksNeedingSideUpdate.set(106);
		blocksNeedingSideUpdate.set(107);
		blocksNeedingSideUpdate.set(183);
		blocksNeedingSideUpdate.set(184);
		blocksNeedingSideUpdate.set(185);
		blocksNeedingSideUpdate.set(186);
		blocksNeedingSideUpdate.set(187);
		blocksNeedingSideUpdate.set(132);
		blocksNeedingSideUpdate.set(139);
		blocksNeedingSideUpdate.set(199);
	}

	static class ChunkNibbleArray {
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

		public int get(int i, int j, int k) {
			int l = this.getRawIndex(j << 8 | k << 4 | i);
			return this.usesLowNibble(j << 8 | k << 4 | i) ? this.contents[l] & 15 : this.contents[l] >> 4 & 15;
		}

		private boolean usesLowNibble(int i) {
			return (i & 1) == 0;
		}

		private int getRawIndex(int i) {
			return i >> 1;
		}
	}

	public static enum Facing {
		field_15858(ChunkPalettedStorageFix.Facing.Direction.field_15870, ChunkPalettedStorageFix.Facing.Axis.field_15866),
		field_15863(ChunkPalettedStorageFix.Facing.Direction.field_15873, ChunkPalettedStorageFix.Facing.Axis.field_15866),
		field_15859(ChunkPalettedStorageFix.Facing.Direction.field_15870, ChunkPalettedStorageFix.Facing.Axis.field_15867),
		field_15862(ChunkPalettedStorageFix.Facing.Direction.field_15873, ChunkPalettedStorageFix.Facing.Axis.field_15867),
		field_15857(ChunkPalettedStorageFix.Facing.Direction.field_15870, ChunkPalettedStorageFix.Facing.Axis.field_15869),
		field_15860(ChunkPalettedStorageFix.Facing.Direction.field_15873, ChunkPalettedStorageFix.Facing.Axis.field_15869);

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
			field_15869,
			field_15866,
			field_15867;
		}

		public static enum Direction {
			field_15873(1),
			field_15870(-1);

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
		private final int xPos;
		private final int yPos;
		private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

		public Level(Dynamic<?> dynamic) {
			this.level = dynamic;
			this.xPos = dynamic.get("xPos").asInt(0) << 4;
			this.yPos = dynamic.get("zPos").asInt(0) << 4;
			dynamic.get("TileEntities").asStreamOpt().ifPresent(stream -> stream.forEach(dynamicx -> {
					int ix = dynamicx.get("x").asInt(0) - this.xPos & 15;
					int j = dynamicx.get("y").asInt(0);
					int k = dynamicx.get("z").asInt(0) - this.yPos & 15;
					int l = j << 8 | k << 4 | ix;
					if (this.blockEntities.put(l, dynamicx) != null) {
						ChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.xPos, this.yPos, ix, j, k);
					}
				}));
			boolean bl = dynamic.get("convertedFromAlphaFormat").asBoolean(false);
			dynamic.get("Sections").asStreamOpt().ifPresent(stream -> stream.forEach(dynamicx -> {
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
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(j, ChunkPalettedStorageFix.Facing.field_15863)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(j, ChunkPalettedStorageFix.snowyGrass);
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
										String string2 = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(k, ChunkPalettedStorageFix.Facing.field_15863)));
										if ("minecraft:snow".equals(string2) || "minecraft:snow_layer".equals(string2)) {
											this.setBlock(k, ChunkPalettedStorageFix.podzol);
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
										this.setBlock(m, (Dynamic<?>)ChunkPalettedStorageFix.noteblock.getOrDefault(string4, ChunkPalettedStorageFix.noteblock.get("false0")));
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
											if (ChunkPalettedStorageFix.bed.containsKey(string5)) {
												this.setBlock(n, (Dynamic<?>)ChunkPalettedStorageFix.bed.get(string5));
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
											int v = adjacentTo(u, ChunkPalettedStorageFix.Facing.field_15863);
											Dynamic<?> dynamic15 = this.getBlock(v);
											String string13 = ChunkPalettedStorageFix.getName(dynamic14);
											if (string13.equals(ChunkPalettedStorageFix.getName(dynamic15))) {
												String string14 = ChunkPalettedStorageFix.getProperty(dynamic14, "facing");
												String string15 = ChunkPalettedStorageFix.getProperty(dynamic14, "open");
												String string16 = bl ? "left" : ChunkPalettedStorageFix.getProperty(dynamic15, "hinge");
												String string17 = bl ? "false" : ChunkPalettedStorageFix.getProperty(dynamic15, "powered");
												this.setBlock(u, (Dynamic<?>)ChunkPalettedStorageFix.door.get(string13 + string14 + "lower" + string16 + string15 + string17));
												this.setBlock(v, (Dynamic<?>)ChunkPalettedStorageFix.door.get(string13 + string14 + "upper" + string16 + string15 + string17));
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
										String string7 = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(r, ChunkPalettedStorageFix.Facing.field_15858)));
										if ("minecraft:grass_block".equals(string7) || "minecraft:dirt".equals(string7)) {
											this.setBlock(r, ChunkPalettedStorageFix.pumpkin);
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
										String string3 = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(l, ChunkPalettedStorageFix.Facing.field_15863)));
										if ("minecraft:snow".equals(string3) || "minecraft:snow_layer".equals(string3)) {
											this.setBlock(l, ChunkPalettedStorageFix.snowyMycelium);
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
										this.setBlock(s, (Dynamic<?>)ChunkPalettedStorageFix.flowerPot.getOrDefault(string8, ChunkPalettedStorageFix.flowerPot.get("minecraft:air0")));
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
											string12 = string9 + String.valueOf(dynamic12.get("Rot").asInt(0));
										}

										dynamic12.remove("SkullType");
										dynamic12.remove("facing");
										dynamic12.remove("Rot");
										this.setBlock(t, (Dynamic<?>)ChunkPalettedStorageFix.skull.getOrDefault(string12, ChunkPalettedStorageFix.skull.get("0north")));
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
										Dynamic<?> dynamic17 = this.getBlock(adjacentTo(w, ChunkPalettedStorageFix.Facing.field_15858));
										String string18 = ChunkPalettedStorageFix.getName(dynamic17);
										if ("minecraft:sunflower".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.sunflowerUpper);
										} else if ("minecraft:lilac".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.lilacUpper);
										} else if ("minecraft:tall_grass".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.grassUpper);
										} else if ("minecraft:large_fern".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.fernUpper);
										} else if ("minecraft:rose_bush".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.roseUpper);
										} else if ("minecraft:peony".equals(string18)) {
											this.setBlock(w, ChunkPalettedStorageFix.peonyUpper);
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
											if (ChunkPalettedStorageFix.banner.containsKey(string6)) {
												this.setBlock(p, (Dynamic<?>)ChunkPalettedStorageFix.banner.get(string6));
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

		public static int adjacentTo(int i, ChunkPalettedStorageFix.Facing facing) {
			switch (facing.getAxis()) {
				case field_15869:
					int j = (i & 15) + facing.getDirection().getOffset();
					return j >= 0 && j <= 15 ? i & -16 | j : -1;
				case field_15866:
					int k = (i >> 8) + facing.getDirection().getOffset();
					return k >= 0 && k <= 255 ? i & 0xFF | k << 8 : -1;
				case field_15867:
					int l = (i >> 4 & 15) + facing.getDirection().getOffset();
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
				return section == null ? ChunkPalettedStorageFix.air : section.getBlock(i & 4095);
			} else {
				return ChunkPalettedStorageFix.air;
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
			Dynamic<?> dynamic3 = dynamic.emptyList();

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					dynamic3 = dynamic3.merge(section.transform());
					dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.innerPositions.toIntArray())));
				}
			}

			Dynamic<?> dynamic4 = dynamic.emptyMap();
			dynamic4 = dynamic4.set("Sides", dynamic4.createByte((byte)this.sidesToUpgrade));
			dynamic4 = dynamic4.set("Indices", dynamic2);
			return dynamic.set("UpgradeData", dynamic4).set("Sections", dynamic3);
		}
	}

	static class Section {
		private final Int2ObjectBiMap<Dynamic<?>> paletteMap = new Int2ObjectBiMap<>(32);
		private Dynamic<?> paletteData;
		private final Dynamic<?> section;
		private final boolean hasBlocks;
		private final Int2ObjectMap<IntList> inPlaceUpdates = new Int2ObjectLinkedOpenHashMap();
		private final IntList innerPositions = new IntArrayList();
		public final int y;
		private final Set<Dynamic<?>> seenStates = Sets.newIdentityHashSet();
		private final int[] states = new int[4096];

		public Section(Dynamic<?> dynamic) {
			this.paletteData = dynamic.emptyList();
			this.section = dynamic;
			this.y = dynamic.get("Y").asInt(0);
			this.hasBlocks = dynamic.get("Blocks").get().isPresent();
		}

		public Dynamic<?> getBlock(int i) {
			if (i >= 0 && i <= 4095) {
				Dynamic<?> dynamic = this.paletteMap.get(this.states[i]);
				return dynamic == null ? ChunkPalettedStorageFix.air : dynamic;
			} else {
				return ChunkPalettedStorageFix.air;
			}
		}

		public void setBlock(int i, Dynamic<?> dynamic) {
			if (this.seenStates.add(dynamic)) {
				this.paletteData = this.paletteData.merge("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? ChunkPalettedStorageFix.air : dynamic);
			}

			this.states[i] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
		}

		public int visit(int i) {
			if (!this.hasBlocks) {
				return i;
			} else {
				ByteBuffer byteBuffer = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().get();
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Data")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray2 = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Add")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				this.seenStates.add(ChunkPalettedStorageFix.air);
				ChunkPalettedStorageFix.addTo(this.paletteMap, ChunkPalettedStorageFix.air);
				this.paletteData = this.paletteData.merge(ChunkPalettedStorageFix.air);

				for (int j = 0; j < 4096; j++) {
					int k = j & 15;
					int l = j >> 8 & 15;
					int m = j >> 4 & 15;
					int n = chunkNibbleArray2.get(k, l, m) << 12 | (byteBuffer.get(j) & 255) << 4 | chunkNibbleArray.get(k, l, m);
					if (ChunkPalettedStorageFix.blocksNeedingInPlaceUpdate.get(n >> 4)) {
						this.addInPlaceUpdate(n >> 4, j);
					}

					if (ChunkPalettedStorageFix.blocksNeedingSideUpdate.get(n >> 4)) {
						int o = ChunkPalettedStorageFix.getSideToUpgradeFlag(k == 0, k == 15, m == 0, m == 15);
						if (o == 0) {
							this.innerPositions.add(j);
						} else {
							i |= o;
						}
					}

					this.setBlock(j, BlockStateFlattening.lookupState(n));
				}

				return i;
			}
		}

		private void addInPlaceUpdate(int i, int j) {
			IntList intList = (IntList)this.inPlaceUpdates.get(i);
			if (intList == null) {
				intList = new IntArrayList();
				this.inPlaceUpdates.put(i, intList);
			}

			intList.add(j);
		}

		public Dynamic<?> transform() {
			Dynamic<?> dynamic = this.section;
			if (!this.hasBlocks) {
				return dynamic;
			} else {
				dynamic = dynamic.set("Palette", this.paletteData);
				int i = Math.max(4, DataFixUtils.ceillog2(this.seenStates.size()));
				PackedIntegerArray packedIntegerArray = new PackedIntegerArray(i, 4096);

				for (int j = 0; j < this.states.length; j++) {
					packedIntegerArray.set(j, this.states[j]);
				}

				dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(packedIntegerArray.getStorage())));
				dynamic = dynamic.remove("Blocks");
				dynamic = dynamic.remove("Data");
				return dynamic.remove("Add");
			}
		}
	}
}
