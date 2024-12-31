package net.minecraft;

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
import net.minecraft.world.chunk.palette.PaletteData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4502 extends DataFix {
	private static final Logger field_22272 = LogManager.getLogger();
	private static final BitSet field_22273 = new BitSet(256);
	private static final BitSet field_22274 = new BitSet(256);
	private static final Dynamic<?> field_22275 = class_4500.method_21607("{Name:'minecraft:pumpkin'}");
	private static final Dynamic<?> field_22276 = class_4500.method_21607("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
	private static final Dynamic<?> field_22277 = class_4500.method_21607("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
	private static final Dynamic<?> field_22278 = class_4500.method_21607("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
	private static final Dynamic<?> field_22279 = class_4500.method_21607("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
	private static final Dynamic<?> field_22280 = class_4500.method_21607("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
	private static final Dynamic<?> field_22281 = class_4500.method_21607("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
	private static final Dynamic<?> field_22282 = class_4500.method_21607("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
	private static final Dynamic<?> field_22283 = class_4500.method_21607("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
	private static final Dynamic<?> field_22284 = class_4500.method_21607("{Name:'minecraft:peony',Properties:{half:'upper'}}");
	private static final Map<String, Dynamic<?>> field_22285 = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		hashMap.put("minecraft:air0", class_4500.method_21607("{Name:'minecraft:flower_pot'}"));
		hashMap.put("minecraft:red_flower0", class_4500.method_21607("{Name:'minecraft:potted_poppy'}"));
		hashMap.put("minecraft:red_flower1", class_4500.method_21607("{Name:'minecraft:potted_blue_orchid'}"));
		hashMap.put("minecraft:red_flower2", class_4500.method_21607("{Name:'minecraft:potted_allium'}"));
		hashMap.put("minecraft:red_flower3", class_4500.method_21607("{Name:'minecraft:potted_azure_bluet'}"));
		hashMap.put("minecraft:red_flower4", class_4500.method_21607("{Name:'minecraft:potted_red_tulip'}"));
		hashMap.put("minecraft:red_flower5", class_4500.method_21607("{Name:'minecraft:potted_orange_tulip'}"));
		hashMap.put("minecraft:red_flower6", class_4500.method_21607("{Name:'minecraft:potted_white_tulip'}"));
		hashMap.put("minecraft:red_flower7", class_4500.method_21607("{Name:'minecraft:potted_pink_tulip'}"));
		hashMap.put("minecraft:red_flower8", class_4500.method_21607("{Name:'minecraft:potted_oxeye_daisy'}"));
		hashMap.put("minecraft:yellow_flower0", class_4500.method_21607("{Name:'minecraft:potted_dandelion'}"));
		hashMap.put("minecraft:sapling0", class_4500.method_21607("{Name:'minecraft:potted_oak_sapling'}"));
		hashMap.put("minecraft:sapling1", class_4500.method_21607("{Name:'minecraft:potted_spruce_sapling'}"));
		hashMap.put("minecraft:sapling2", class_4500.method_21607("{Name:'minecraft:potted_birch_sapling'}"));
		hashMap.put("minecraft:sapling3", class_4500.method_21607("{Name:'minecraft:potted_jungle_sapling'}"));
		hashMap.put("minecraft:sapling4", class_4500.method_21607("{Name:'minecraft:potted_acacia_sapling'}"));
		hashMap.put("minecraft:sapling5", class_4500.method_21607("{Name:'minecraft:potted_dark_oak_sapling'}"));
		hashMap.put("minecraft:red_mushroom0", class_4500.method_21607("{Name:'minecraft:potted_red_mushroom'}"));
		hashMap.put("minecraft:brown_mushroom0", class_4500.method_21607("{Name:'minecraft:potted_brown_mushroom'}"));
		hashMap.put("minecraft:deadbush0", class_4500.method_21607("{Name:'minecraft:potted_dead_bush'}"));
		hashMap.put("minecraft:tallgrass2", class_4500.method_21607("{Name:'minecraft:potted_fern'}"));
		hashMap.put("minecraft:cactus0", class_4500.method_21605(2240));
	});
	private static final Map<String, Dynamic<?>> field_22286 = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		method_21616(hashMap, 0, "skeleton", "skull");
		method_21616(hashMap, 1, "wither_skeleton", "skull");
		method_21616(hashMap, 2, "zombie", "head");
		method_21616(hashMap, 3, "player", "head");
		method_21616(hashMap, 4, "creeper", "head");
		method_21616(hashMap, 5, "dragon", "head");
	});
	private static final Map<String, Dynamic<?>> field_22287 = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		method_21617(hashMap, "oak_door", 1024);
		method_21617(hashMap, "iron_door", 1136);
		method_21617(hashMap, "spruce_door", 3088);
		method_21617(hashMap, "birch_door", 3104);
		method_21617(hashMap, "jungle_door", 3120);
		method_21617(hashMap, "acacia_door", 3136);
		method_21617(hashMap, "dark_oak_door", 3152);
	});
	private static final Map<String, Dynamic<?>> field_22288 = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		for (int i = 0; i < 26; i++) {
			hashMap.put("true" + i, class_4500.method_21607("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
			hashMap.put("false" + i, class_4500.method_21607("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
		}
	});
	private static final Int2ObjectMap<String> field_22289 = (Int2ObjectMap<String>)DataFixUtils.make(new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
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
	private static final Map<String, Dynamic<?>> field_22290 = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		ObjectIterator var1 = field_22289.int2ObjectEntrySet().iterator();

		while (var1.hasNext()) {
			Entry<String> entry = (Entry<String>)var1.next();
			if (!Objects.equals(entry.getValue(), "red")) {
				method_21615(hashMap, entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	private static final Map<String, Dynamic<?>> field_22291 = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
		ObjectIterator var1 = field_22289.int2ObjectEntrySet().iterator();

		while (var1.hasNext()) {
			Entry<String> entry = (Entry<String>)var1.next();
			if (!Objects.equals(entry.getValue(), "white")) {
				method_21623(hashMap, 15 - entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	private static final Dynamic<?> field_22292 = class_4500.method_21605(0);

	public class_4502(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private static void method_21616(Map<String, Dynamic<?>> map, int i, String string, String string2) {
		map.put(i + "north", class_4500.method_21607("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'north'}}"));
		map.put(i + "east", class_4500.method_21607("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'east'}}"));
		map.put(i + "south", class_4500.method_21607("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'south'}}"));
		map.put(i + "west", class_4500.method_21607("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'west'}}"));

		for (int j = 0; j < 16; j++) {
			map.put(i + "" + j, class_4500.method_21607("{Name:'minecraft:" + string + "_" + string2 + "',Properties:{rotation:'" + j + "'}}"));
		}
	}

	private static void method_21617(Map<String, Dynamic<?>> map, String string, int i) {
		map.put(
			"minecraft:" + string + "eastlowerleftfalsefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastlowerleftfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "eastlowerlefttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastlowerlefttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastlowerrightfalsefalse", class_4500.method_21605(i));
		map.put(
			"minecraft:" + string + "eastlowerrightfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastlowerrighttruefalse", class_4500.method_21605(i + 4));
		map.put(
			"minecraft:" + string + "eastlowerrighttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastupperleftfalsefalse", class_4500.method_21605(i + 8));
		map.put("minecraft:" + string + "eastupperleftfalsetrue", class_4500.method_21605(i + 10));
		map.put(
			"minecraft:" + string + "eastupperlefttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastupperlefttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastupperrightfalsefalse", class_4500.method_21605(i + 9));
		map.put("minecraft:" + string + "eastupperrightfalsetrue", class_4500.method_21605(i + 11));
		map.put(
			"minecraft:" + string + "eastupperrighttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastupperrighttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerleftfalsefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerleftfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerlefttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerlefttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "northlowerrightfalsefalse", class_4500.method_21605(i + 3));
		map.put(
			"minecraft:" + string + "northlowerrightfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "northlowerrighttruefalse", class_4500.method_21605(i + 7));
		map.put(
			"minecraft:" + string + "northlowerrighttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperleftfalsefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperleftfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperlefttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperlefttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrightfalsefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrightfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrighttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrighttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerleftfalsefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerleftfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerlefttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerlefttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "southlowerrightfalsefalse", class_4500.method_21605(i + 1));
		map.put(
			"minecraft:" + string + "southlowerrightfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "southlowerrighttruefalse", class_4500.method_21605(i + 5));
		map.put(
			"minecraft:" + string + "southlowerrighttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperleftfalsefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperleftfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperlefttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperlefttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrightfalsefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrightfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrighttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrighttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerleftfalsefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerleftfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerlefttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerlefttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "westlowerrightfalsefalse", class_4500.method_21605(i + 2));
		map.put(
			"minecraft:" + string + "westlowerrightfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "westlowerrighttruefalse", class_4500.method_21605(i + 6));
		map.put(
			"minecraft:" + string + "westlowerrighttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperleftfalsefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperleftfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperlefttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperlefttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrightfalsefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrightfalsetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrighttruefalse",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrighttruetrue",
			class_4500.method_21607("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
	}

	private static void method_21615(Map<String, Dynamic<?>> map, int i, String string) {
		map.put("southfalsefoot" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
		map.put("westfalsefoot" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
		map.put("northfalsefoot" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
		map.put("eastfalsefoot" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
		map.put("southfalsehead" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
		map.put("westfalsehead" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
		map.put("northfalsehead" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
		map.put("eastfalsehead" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
		map.put("southtruehead" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
		map.put("westtruehead" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
		map.put("northtruehead" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
		map.put("easttruehead" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
	}

	private static void method_21623(Map<String, Dynamic<?>> map, int i, String string) {
		for (int j = 0; j < 16; j++) {
			map.put("" + j + "_" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_banner',Properties:{rotation:'" + j + "'}}"));
		}

		map.put("north_" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'north'}}"));
		map.put("south_" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'south'}}"));
		map.put("west_" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'west'}}"));
		map.put("east_" + i, class_4500.method_21607("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'east'}}"));
	}

	public static String method_21610(Dynamic<?> dynamic) {
		return dynamic.getString("Name");
	}

	public static String method_21611(Dynamic<?> dynamic, String string) {
		return (String)dynamic.get("Properties").map(dynamicx -> dynamicx.getString(string)).orElse("");
	}

	public static int method_21618(class_2929<Dynamic<?>> arg, Dynamic<?> dynamic) {
		int i = arg.getId(dynamic);
		if (i == -1) {
			i = arg.method_12864(dynamic);
		}

		return i;
	}

	private Dynamic<?> method_21621(Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = dynamic.get("Level");
		return optional.isPresent() && ((Dynamic)optional.get()).get("Sections").flatMap(Dynamic::getStream).isPresent()
			? dynamic.set("Level", new class_4502.class_4508((Dynamic<?>)optional.get()).method_21661())
			: dynamic;
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_3402.field_16584);
		Type<?> type2 = this.getOutputSchema().getType(class_3402.field_16584);
		return this.writeFixAndRead("ChunkPalettedStorageFix", type, type2, this::method_21621);
	}

	public static int method_21619(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
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
		field_22274.set(2);
		field_22274.set(3);
		field_22274.set(110);
		field_22274.set(140);
		field_22274.set(144);
		field_22274.set(25);
		field_22274.set(86);
		field_22274.set(26);
		field_22274.set(176);
		field_22274.set(177);
		field_22274.set(175);
		field_22274.set(64);
		field_22274.set(71);
		field_22274.set(193);
		field_22274.set(194);
		field_22274.set(195);
		field_22274.set(196);
		field_22274.set(197);
		field_22273.set(54);
		field_22273.set(146);
		field_22273.set(25);
		field_22273.set(26);
		field_22273.set(51);
		field_22273.set(53);
		field_22273.set(67);
		field_22273.set(108);
		field_22273.set(109);
		field_22273.set(114);
		field_22273.set(128);
		field_22273.set(134);
		field_22273.set(135);
		field_22273.set(136);
		field_22273.set(156);
		field_22273.set(163);
		field_22273.set(164);
		field_22273.set(180);
		field_22273.set(203);
		field_22273.set(55);
		field_22273.set(85);
		field_22273.set(113);
		field_22273.set(188);
		field_22273.set(189);
		field_22273.set(190);
		field_22273.set(191);
		field_22273.set(192);
		field_22273.set(93);
		field_22273.set(94);
		field_22273.set(101);
		field_22273.set(102);
		field_22273.set(160);
		field_22273.set(106);
		field_22273.set(107);
		field_22273.set(183);
		field_22273.set(184);
		field_22273.set(185);
		field_22273.set(186);
		field_22273.set(187);
		field_22273.set(132);
		field_22273.set(139);
		field_22273.set(199);
	}

	static class class_4503 {
		private final byte[] field_22294;

		public class_4503() {
			this.field_22294 = new byte[2048];
		}

		public class_4503(byte[] bs) {
			this.field_22294 = bs;
			if (bs.length != 2048) {
				throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length);
			}
		}

		public int method_21647(int i, int j, int k) {
			int l = this.method_21648(j << 8 | k << 4 | i);
			return this.method_21646(j << 8 | k << 4 | i) ? this.field_22294[l] & 15 : this.field_22294[l] >> 4 & 15;
		}

		private boolean method_21646(int i) {
			return (i & 1) == 0;
		}

		private int method_21648(int i) {
			return i >> 1;
		}
	}

	public static enum class_4504 {
		DOWN(class_4502.class_4504.class_4506.NEGATIVE, class_4502.class_4504.class_4505.Y),
		UP(class_4502.class_4504.class_4506.POSITIVE, class_4502.class_4504.class_4505.Y),
		NORTH(class_4502.class_4504.class_4506.NEGATIVE, class_4502.class_4504.class_4505.Z),
		SOUTH(class_4502.class_4504.class_4506.POSITIVE, class_4502.class_4504.class_4505.Z),
		WEST(class_4502.class_4504.class_4506.NEGATIVE, class_4502.class_4504.class_4505.X),
		EAST(class_4502.class_4504.class_4506.POSITIVE, class_4502.class_4504.class_4505.X);

		private final class_4502.class_4504.class_4505 field_22301;
		private final class_4502.class_4504.class_4506 field_22302;

		private class_4504(class_4502.class_4504.class_4506 arg, class_4502.class_4504.class_4505 arg2) {
			this.field_22301 = arg2;
			this.field_22302 = arg;
		}

		public class_4502.class_4504.class_4506 method_21649() {
			return this.field_22302;
		}

		public class_4502.class_4504.class_4505 method_21650() {
			return this.field_22301;
		}

		public static enum class_4505 {
			X,
			Y,
			Z;
		}

		public static enum class_4506 {
			POSITIVE(1),
			NEGATIVE(-1);

			private final int field_22310;

			private class_4506(int j) {
				this.field_22310 = j;
			}

			public int method_21651() {
				return this.field_22310;
			}
		}
	}

	static class class_4507 {
		private final class_2929<Dynamic<?>> field_22313 = new class_2929<>(32);
		private Dynamic<?> field_22314;
		private final Dynamic<?> field_22315;
		private final boolean field_22316;
		private final Int2ObjectMap<IntList> field_22317 = new Int2ObjectLinkedOpenHashMap();
		private final IntList field_22318 = new IntArrayList();
		public int field_22312;
		private final Set<Dynamic<?>> field_22319 = Sets.newIdentityHashSet();
		private final int[] field_22320 = new int[4096];

		public class_4507(Dynamic<?> dynamic) {
			this.field_22314 = dynamic.emptyList();
			this.field_22315 = dynamic;
			this.field_22312 = dynamic.getInt("Y");
			this.field_22316 = dynamic.get("Blocks").isPresent();
		}

		public Dynamic<?> method_21653(int i) {
			if (i >= 0 && i <= 4095) {
				Dynamic<?> dynamic = this.field_22313.getById(this.field_22320[i]);
				return dynamic == null ? class_4502.field_22292 : dynamic;
			} else {
				return class_4502.field_22292;
			}
		}

		public void method_21655(int i, Dynamic<?> dynamic) {
			if (this.field_22319.add(dynamic)) {
				this.field_22314 = this.field_22314.merge("%%FILTER_ME%%".equals(class_4502.method_21610(dynamic)) ? class_4502.field_22292 : dynamic);
			}

			this.field_22320[i] = class_4502.method_21618(this.field_22313, dynamic);
		}

		public int method_21658(int i) {
			if (!this.field_22316) {
				return i;
			} else {
				ByteBuffer byteBuffer = (ByteBuffer)this.field_22315.get("Blocks").flatMap(Dynamic::getByteBuffer).get();
				class_4502.class_4503 lv = (class_4502.class_4503)this.field_22315
					.get("Data")
					.flatMap(Dynamic::getByteBuffer)
					.map(byteBufferx -> new class_4502.class_4503(DataFixUtils.toArray(byteBufferx)))
					.orElseGet(class_4502.class_4503::new);
				class_4502.class_4503 lv2 = (class_4502.class_4503)this.field_22315
					.get("Add")
					.flatMap(Dynamic::getByteBuffer)
					.map(byteBufferx -> new class_4502.class_4503(DataFixUtils.toArray(byteBufferx)))
					.orElseGet(class_4502.class_4503::new);
				this.field_22319.add(class_4502.field_22292);
				class_4502.method_21618(this.field_22313, class_4502.field_22292);
				this.field_22314 = this.field_22314.merge(class_4502.field_22292);

				for (int j = 0; j < 4096; j++) {
					int k = j & 15;
					int l = j >> 8 & 15;
					int m = j >> 4 & 15;
					int n = lv2.method_21647(k, l, m) << 12 | (byteBuffer.get(j) & 255) << 4 | lv.method_21647(k, l, m);
					if (class_4502.field_22274.get(n >> 4)) {
						this.method_21654(n >> 4, j);
					}

					if (class_4502.field_22273.get(n >> 4)) {
						int o = class_4502.method_21619(k == 0, k == 15, m == 0, m == 15);
						if (o == 0) {
							this.field_22318.add(j);
						} else {
							i |= o;
						}
					}

					this.method_21655(j, class_4500.method_21605(n));
				}

				return i;
			}
		}

		private void method_21654(int i, int j) {
			IntList intList = (IntList)this.field_22317.get(i);
			if (intList == null) {
				intList = new IntArrayList();
				this.field_22317.put(i, intList);
			}

			intList.add(j);
		}

		public Dynamic<?> method_21652() {
			Dynamic<?> dynamic = this.field_22315;
			if (!this.field_22316) {
				return dynamic;
			} else {
				dynamic = dynamic.set("Palette", this.field_22314);
				int i = Math.max(4, DataFixUtils.ceillog2(this.field_22319.size()));
				PaletteData paletteData = new PaletteData(i, 4096);

				for (int j = 0; j < this.field_22320.length; j++) {
					paletteData.set(j, this.field_22320[j]);
				}

				dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(paletteData.getBlockStateIds())));
				dynamic = dynamic.remove("Blocks");
				dynamic = dynamic.remove("Data");
				return dynamic.remove("Add");
			}
		}
	}

	static final class class_4508 {
		private int field_22321;
		private final class_4502.class_4507[] field_22322 = new class_4502.class_4507[16];
		private final Dynamic<?> field_22323;
		private final int field_22324;
		private final int field_22325;
		private final Int2ObjectMap<Dynamic<?>> field_22326 = new Int2ObjectLinkedOpenHashMap(16);

		public class_4508(Dynamic<?> dynamic) {
			this.field_22323 = dynamic;
			this.field_22324 = dynamic.getInt("xPos") << 4;
			this.field_22325 = dynamic.getInt("zPos") << 4;
			dynamic.get("TileEntities").flatMap(Dynamic::getStream).ifPresent(stream -> stream.forEach(dynamicx -> {
					int ix = dynamicx.getInt("x") - this.field_22324 & 15;
					int j = dynamicx.getInt("y");
					int k = dynamicx.getInt("z") - this.field_22325 & 15;
					int l = j << 8 | k << 4 | ix;
					if (this.field_22326.put(l, dynamicx) != null) {
						class_4502.field_22272.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.field_22324, this.field_22325, ix, j, k);
					}
				}));
			boolean bl = dynamic.getBoolean("convertedFromAlphaFormat");
			dynamic.get("Sections").flatMap(Dynamic::getStream).ifPresent(stream -> stream.forEach(dynamicx -> {
					class_4502.class_4507 lvx = new class_4502.class_4507(dynamicx);
					this.field_22321 = lvx.method_21658(this.field_22321);
					this.field_22322[lvx.field_22312] = lvx;
				}));

			for (class_4502.class_4507 lv : this.field_22322) {
				if (lv != null) {
					ObjectIterator var7 = lv.field_22317.entrySet().iterator();

					while (var7.hasNext()) {
						java.util.Map.Entry<Integer, IntList> entry = (java.util.Map.Entry<Integer, IntList>)var7.next();
						int i = lv.field_22312 << 12;
						switch (entry.getKey()) {
							case 2:
								IntListIterator var30 = ((IntList)entry.getValue()).iterator();

								while (var30.hasNext()) {
									int j = (Integer)var30.next();
									j |= i;
									Dynamic<?> dynamic2 = this.method_21662(j);
									if ("minecraft:grass_block".equals(class_4502.method_21610(dynamic2))) {
										String string = class_4502.method_21610(this.method_21662(method_21664(j, class_4502.class_4504.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.method_21663(j, class_4502.field_22277);
										}
									}
								}
								break;
							case 3:
								IntListIterator var29 = ((IntList)entry.getValue()).iterator();

								while (var29.hasNext()) {
									int k = (Integer)var29.next();
									k |= i;
									Dynamic<?> dynamic3 = this.method_21662(k);
									if ("minecraft:podzol".equals(class_4502.method_21610(dynamic3))) {
										String string2 = class_4502.method_21610(this.method_21662(method_21664(k, class_4502.class_4504.UP)));
										if ("minecraft:snow".equals(string2) || "minecraft:snow_layer".equals(string2)) {
											this.method_21663(k, class_4502.field_22276);
										}
									}
								}
								break;
							case 25:
								IntListIterator var28 = ((IntList)entry.getValue()).iterator();

								while (var28.hasNext()) {
									int m = (Integer)var28.next();
									m |= i;
									Dynamic<?> dynamic5 = this.method_21670(m);
									if (dynamic5 != null) {
										String string4 = Boolean.toString(dynamic5.getBoolean("powered")) + (byte)Math.min(Math.max(dynamic5.getByte("note"), 0), 24);
										this.method_21663(m, (Dynamic<?>)class_4502.field_22288.getOrDefault(string4, class_4502.field_22288.get("false0")));
									}
								}
								break;
							case 26:
								IntListIterator var27 = ((IntList)entry.getValue()).iterator();

								while (var27.hasNext()) {
									int n = (Integer)var27.next();
									n |= i;
									Dynamic<?> dynamic6 = this.method_21667(n);
									Dynamic<?> dynamic7 = this.method_21662(n);
									if (dynamic6 != null) {
										int o = dynamic6.getInt("color");
										if (o != 14 && o >= 0 && o < 16) {
											String string5 = class_4502.method_21611(dynamic7, "facing")
												+ class_4502.method_21611(dynamic7, "occupied")
												+ class_4502.method_21611(dynamic7, "part")
												+ o;
											if (class_4502.field_22290.containsKey(string5)) {
												this.method_21663(n, (Dynamic<?>)class_4502.field_22290.get(string5));
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
									Dynamic<?> dynamic13 = this.method_21662(u);
									if (class_4502.method_21610(dynamic13).endsWith("_door")) {
										Dynamic<?> dynamic14 = this.method_21662(u);
										if ("lower".equals(class_4502.method_21611(dynamic14, "half"))) {
											int v = method_21664(u, class_4502.class_4504.UP);
											Dynamic<?> dynamic15 = this.method_21662(v);
											String string13 = class_4502.method_21610(dynamic14);
											if (string13.equals(class_4502.method_21610(dynamic15))) {
												String string14 = class_4502.method_21611(dynamic14, "facing");
												String string15 = class_4502.method_21611(dynamic14, "open");
												String string16 = bl ? "left" : class_4502.method_21611(dynamic15, "hinge");
												String string17 = bl ? "false" : class_4502.method_21611(dynamic15, "powered");
												this.method_21663(u, (Dynamic<?>)class_4502.field_22287.get(string13 + string14 + "lower" + string16 + string15 + string17));
												this.method_21663(v, (Dynamic<?>)class_4502.field_22287.get(string13 + string14 + "upper" + string16 + string15 + string17));
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
									Dynamic<?> dynamic10 = this.method_21662(r);
									if ("minecraft:carved_pumpkin".equals(class_4502.method_21610(dynamic10))) {
										String string7 = class_4502.method_21610(this.method_21662(method_21664(r, class_4502.class_4504.DOWN)));
										if ("minecraft:grass_block".equals(string7) || "minecraft:dirt".equals(string7)) {
											this.method_21663(r, class_4502.field_22275);
										}
									}
								}
								break;
							case 110:
								IntListIterator var24 = ((IntList)entry.getValue()).iterator();

								while (var24.hasNext()) {
									int l = (Integer)var24.next();
									l |= i;
									Dynamic<?> dynamic4 = this.method_21662(l);
									if ("minecraft:mycelium".equals(class_4502.method_21610(dynamic4))) {
										String string3 = class_4502.method_21610(this.method_21662(method_21664(l, class_4502.class_4504.UP)));
										if ("minecraft:snow".equals(string3) || "minecraft:snow_layer".equals(string3)) {
											this.method_21663(l, class_4502.field_22278);
										}
									}
								}
								break;
							case 140:
								IntListIterator var23 = ((IntList)entry.getValue()).iterator();

								while (var23.hasNext()) {
									int s = (Integer)var23.next();
									s |= i;
									Dynamic<?> dynamic11 = this.method_21670(s);
									if (dynamic11 != null) {
										String string8 = dynamic11.getString("Item") + dynamic11.getInt("Data");
										this.method_21663(s, (Dynamic<?>)class_4502.field_22285.getOrDefault(string8, class_4502.field_22285.get("minecraft:air0")));
									}
								}
								break;
							case 144:
								IntListIterator var22 = ((IntList)entry.getValue()).iterator();

								while (var22.hasNext()) {
									int t = (Integer)var22.next();
									t |= i;
									Dynamic<?> dynamic12 = this.method_21667(t);
									if (dynamic12 != null) {
										String string9 = String.valueOf(dynamic12.getByte("SkullType"));
										String string10 = class_4502.method_21611(this.method_21662(t), "facing");
										String string12;
										if (!"up".equals(string10) && !"down".equals(string10)) {
											string12 = string9 + string10;
										} else {
											string12 = string9 + String.valueOf(dynamic12.getInt("Rot"));
										}

										dynamic12.remove("SkullType");
										dynamic12.remove("facing");
										dynamic12.remove("Rot");
										this.method_21663(t, (Dynamic<?>)class_4502.field_22286.getOrDefault(string12, class_4502.field_22286.get("0north")));
									}
								}
								break;
							case 175:
								IntListIterator var21 = ((IntList)entry.getValue()).iterator();

								while (var21.hasNext()) {
									int w = (Integer)var21.next();
									w |= i;
									Dynamic<?> dynamic16 = this.method_21662(w);
									if ("upper".equals(class_4502.method_21611(dynamic16, "half"))) {
										Dynamic<?> dynamic17 = this.method_21662(method_21664(w, class_4502.class_4504.DOWN));
										String string18 = class_4502.method_21610(dynamic17);
										if ("minecraft:sunflower".equals(string18)) {
											this.method_21663(w, class_4502.field_22279);
										} else if ("minecraft:lilac".equals(string18)) {
											this.method_21663(w, class_4502.field_22280);
										} else if ("minecraft:tall_grass".equals(string18)) {
											this.method_21663(w, class_4502.field_22281);
										} else if ("minecraft:large_fern".equals(string18)) {
											this.method_21663(w, class_4502.field_22282);
										} else if ("minecraft:rose_bush".equals(string18)) {
											this.method_21663(w, class_4502.field_22283);
										} else if ("minecraft:peony".equals(string18)) {
											this.method_21663(w, class_4502.field_22284);
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
									Dynamic<?> dynamic8 = this.method_21667(p);
									Dynamic<?> dynamic9 = this.method_21662(p);
									if (dynamic8 != null) {
										int q = dynamic8.getInt("Base");
										if (q != 15 && q >= 0 && q < 16) {
											String string6 = class_4502.method_21611(dynamic9, entry.getKey() == 176 ? "rotation" : "facing") + "_" + q;
											if (class_4502.field_22291.containsKey(string6)) {
												this.method_21663(p, (Dynamic<?>)class_4502.field_22291.get(string6));
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
		private Dynamic<?> method_21667(int i) {
			return (Dynamic<?>)this.field_22326.get(i);
		}

		@Nullable
		private Dynamic<?> method_21670(int i) {
			return (Dynamic<?>)this.field_22326.remove(i);
		}

		public static int method_21664(int i, class_4502.class_4504 arg) {
			switch (arg.method_21650()) {
				case X:
					int j = (i & 15) + arg.method_21649().method_21651();
					return j >= 0 && j <= 15 ? i & -16 | j : -1;
				case Y:
					int k = (i >> 8) + arg.method_21649().method_21651();
					return k >= 0 && k <= 255 ? i & 0xFF | k << 8 : -1;
				case Z:
					int l = (i >> 4 & 15) + arg.method_21649().method_21651();
					return l >= 0 && l <= 15 ? i & -241 | l << 4 : -1;
				default:
					return -1;
			}
		}

		private void method_21663(int i, Dynamic<?> dynamic) {
			if (i >= 0 && i <= 65535) {
				class_4502.class_4507 lv = this.method_21671(i);
				if (lv != null) {
					lv.method_21655(i & 4095, dynamic);
				}
			}
		}

		@Nullable
		private class_4502.class_4507 method_21671(int i) {
			int j = i >> 12;
			return j < this.field_22322.length ? this.field_22322[j] : null;
		}

		public Dynamic<?> method_21662(int i) {
			if (i >= 0 && i <= 65535) {
				class_4502.class_4507 lv = this.method_21671(i);
				return lv == null ? class_4502.field_22292 : lv.method_21653(i & 4095);
			} else {
				return class_4502.field_22292;
			}
		}

		public Dynamic<?> method_21661() {
			Dynamic<?> dynamic = this.field_22323;
			if (this.field_22326.isEmpty()) {
				dynamic = dynamic.remove("TileEntities");
			} else {
				dynamic = dynamic.set("TileEntities", dynamic.createList(this.field_22326.values().stream()));
			}

			Dynamic<?> dynamic2 = dynamic.emptyMap();
			Dynamic<?> dynamic3 = dynamic.emptyList();

			for (class_4502.class_4507 lv : this.field_22322) {
				if (lv != null) {
					dynamic3 = dynamic3.merge(lv.method_21652());
					dynamic2 = dynamic2.set(String.valueOf(lv.field_22312), dynamic2.createIntList(Arrays.stream(lv.field_22318.toIntArray())));
				}
			}

			Dynamic<?> dynamic4 = dynamic.emptyMap();
			dynamic4 = dynamic4.set("Sides", dynamic4.createByte((byte)this.field_22321));
			dynamic4 = dynamic4.set("Indices", dynamic2);
			return dynamic.set("UpgradeData", dynamic4).set("Sections", dynamic3);
		}
	}
}
