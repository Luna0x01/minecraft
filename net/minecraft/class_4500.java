package net.minecraft;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.StringNbtReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4500 {
	private static final Logger field_22268 = LogManager.getLogger();
	private static final Dynamic<?>[] field_22269 = new Dynamic[4095];
	private static final Object2IntMap<Dynamic<?>> field_22270 = (Object2IntMap<Dynamic<?>>)DataFixUtils.make(
		new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1)
	);
	private static final Object2IntMap<String> field_22271 = (Object2IntMap<String>)DataFixUtils.make(
		new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1)
	);

	static void method_21601(int i, String string, String... strings) {
		field_22269[i] = method_21607(string);

		for (String string2 : strings) {
			Dynamic<?> dynamic = method_21607(string2);
			String string3 = dynamic.getString("Name");
			field_22271.putIfAbsent(string3, i);
			field_22270.put(dynamic, i);
		}
	}

	public static Dynamic<?> method_21602(Dynamic<?> dynamic) {
		int i = field_22270.getInt(dynamic);
		if (i >= 0 && i < field_22269.length) {
			Dynamic<?> dynamic2 = field_22269[i];
			return dynamic2 == null ? dynamic : dynamic2;
		} else {
			return dynamic;
		}
	}

	public static String method_21604(String string) {
		int i = field_22271.getInt(string);
		if (i >= 0 && i < field_22269.length) {
			Dynamic<?> dynamic = field_22269[i];
			return dynamic == null ? string : dynamic.getString("Name");
		} else {
			return string;
		}
	}

	public static String method_21600(int i) {
		if (i >= 0 && i < field_22269.length) {
			Dynamic<?> dynamic = field_22269[i];
			return dynamic == null ? "minecraft:air" : dynamic.getString("Name");
		} else {
			return "minecraft:air";
		}
	}

	public static Dynamic<?> method_21607(String string) {
		try {
			return new Dynamic(class_4372.field_21487, StringNbtReader.parse(string.replace('\'', '"')));
		} catch (Exception var2) {
			field_22268.error("Parsing {}", string, var2);
			throw new RuntimeException(var2);
		}
	}

	public static Dynamic<?> method_21605(int i) {
		Dynamic<?> dynamic = null;
		if (i > 0 && i < field_22269.length) {
			dynamic = field_22269[i];
		}

		return dynamic == null ? field_22269[0] : dynamic;
	}

	static {
		field_22270.defaultReturnValue(-1);
		method_21601(0, "{Name:'minecraft:air'}", "{Name:'minecraft:air'}");
		method_21601(16, "{Name:'minecraft:stone'}", "{Name:'minecraft:stone',Properties:{variant:'stone'}}");
		method_21601(17, "{Name:'minecraft:granite'}", "{Name:'minecraft:stone',Properties:{variant:'granite'}}");
		method_21601(18, "{Name:'minecraft:polished_granite'}", "{Name:'minecraft:stone',Properties:{variant:'smooth_granite'}}");
		method_21601(19, "{Name:'minecraft:diorite'}", "{Name:'minecraft:stone',Properties:{variant:'diorite'}}");
		method_21601(20, "{Name:'minecraft:polished_diorite'}", "{Name:'minecraft:stone',Properties:{variant:'smooth_diorite'}}");
		method_21601(21, "{Name:'minecraft:andesite'}", "{Name:'minecraft:stone',Properties:{variant:'andesite'}}");
		method_21601(22, "{Name:'minecraft:polished_andesite'}", "{Name:'minecraft:stone',Properties:{variant:'smooth_andesite'}}");
		method_21601(
			32,
			"{Name:'minecraft:grass_block',Properties:{snowy:'false'}}",
			"{Name:'minecraft:grass',Properties:{snowy:'false'}}",
			"{Name:'minecraft:grass',Properties:{snowy:'true'}}"
		);
		method_21601(
			48,
			"{Name:'minecraft:dirt'}",
			"{Name:'minecraft:dirt',Properties:{snowy:'false',variant:'dirt'}}",
			"{Name:'minecraft:dirt',Properties:{snowy:'true',variant:'dirt'}}"
		);
		method_21601(
			49,
			"{Name:'minecraft:coarse_dirt'}",
			"{Name:'minecraft:dirt',Properties:{snowy:'false',variant:'coarse_dirt'}}",
			"{Name:'minecraft:dirt',Properties:{snowy:'true',variant:'coarse_dirt'}}"
		);
		method_21601(
			50,
			"{Name:'minecraft:podzol',Properties:{snowy:'false'}}",
			"{Name:'minecraft:dirt',Properties:{snowy:'false',variant:'podzol'}}",
			"{Name:'minecraft:dirt',Properties:{snowy:'true',variant:'podzol'}}"
		);
		method_21601(64, "{Name:'minecraft:cobblestone'}", "{Name:'minecraft:cobblestone'}");
		method_21601(80, "{Name:'minecraft:oak_planks'}", "{Name:'minecraft:planks',Properties:{variant:'oak'}}");
		method_21601(81, "{Name:'minecraft:spruce_planks'}", "{Name:'minecraft:planks',Properties:{variant:'spruce'}}");
		method_21601(82, "{Name:'minecraft:birch_planks'}", "{Name:'minecraft:planks',Properties:{variant:'birch'}}");
		method_21601(83, "{Name:'minecraft:jungle_planks'}", "{Name:'minecraft:planks',Properties:{variant:'jungle'}}");
		method_21601(84, "{Name:'minecraft:acacia_planks'}", "{Name:'minecraft:planks',Properties:{variant:'acacia'}}");
		method_21601(85, "{Name:'minecraft:dark_oak_planks'}", "{Name:'minecraft:planks',Properties:{variant:'dark_oak'}}");
		method_21601(96, "{Name:'minecraft:oak_sapling',Properties:{stage:'0'}}", "{Name:'minecraft:sapling',Properties:{stage:'0',type:'oak'}}");
		method_21601(97, "{Name:'minecraft:spruce_sapling',Properties:{stage:'0'}}", "{Name:'minecraft:sapling',Properties:{stage:'0',type:'spruce'}}");
		method_21601(98, "{Name:'minecraft:birch_sapling',Properties:{stage:'0'}}", "{Name:'minecraft:sapling',Properties:{stage:'0',type:'birch'}}");
		method_21601(99, "{Name:'minecraft:jungle_sapling',Properties:{stage:'0'}}", "{Name:'minecraft:sapling',Properties:{stage:'0',type:'jungle'}}");
		method_21601(100, "{Name:'minecraft:acacia_sapling',Properties:{stage:'0'}}", "{Name:'minecraft:sapling',Properties:{stage:'0',type:'acacia'}}");
		method_21601(101, "{Name:'minecraft:dark_oak_sapling',Properties:{stage:'0'}}", "{Name:'minecraft:sapling',Properties:{stage:'0',type:'dark_oak'}}");
		method_21601(104, "{Name:'minecraft:oak_sapling',Properties:{stage:'1'}}", "{Name:'minecraft:sapling',Properties:{stage:'1',type:'oak'}}");
		method_21601(105, "{Name:'minecraft:spruce_sapling',Properties:{stage:'1'}}", "{Name:'minecraft:sapling',Properties:{stage:'1',type:'spruce'}}");
		method_21601(106, "{Name:'minecraft:birch_sapling',Properties:{stage:'1'}}", "{Name:'minecraft:sapling',Properties:{stage:'1',type:'birch'}}");
		method_21601(107, "{Name:'minecraft:jungle_sapling',Properties:{stage:'1'}}", "{Name:'minecraft:sapling',Properties:{stage:'1',type:'jungle'}}");
		method_21601(108, "{Name:'minecraft:acacia_sapling',Properties:{stage:'1'}}", "{Name:'minecraft:sapling',Properties:{stage:'1',type:'acacia'}}");
		method_21601(109, "{Name:'minecraft:dark_oak_sapling',Properties:{stage:'1'}}", "{Name:'minecraft:sapling',Properties:{stage:'1',type:'dark_oak'}}");
		method_21601(112, "{Name:'minecraft:bedrock'}", "{Name:'minecraft:bedrock'}");
		method_21601(128, "{Name:'minecraft:water',Properties:{level:'0'}}", "{Name:'minecraft:flowing_water',Properties:{level:'0'}}");
		method_21601(129, "{Name:'minecraft:water',Properties:{level:'1'}}", "{Name:'minecraft:flowing_water',Properties:{level:'1'}}");
		method_21601(130, "{Name:'minecraft:water',Properties:{level:'2'}}", "{Name:'minecraft:flowing_water',Properties:{level:'2'}}");
		method_21601(131, "{Name:'minecraft:water',Properties:{level:'3'}}", "{Name:'minecraft:flowing_water',Properties:{level:'3'}}");
		method_21601(132, "{Name:'minecraft:water',Properties:{level:'4'}}", "{Name:'minecraft:flowing_water',Properties:{level:'4'}}");
		method_21601(133, "{Name:'minecraft:water',Properties:{level:'5'}}", "{Name:'minecraft:flowing_water',Properties:{level:'5'}}");
		method_21601(134, "{Name:'minecraft:water',Properties:{level:'6'}}", "{Name:'minecraft:flowing_water',Properties:{level:'6'}}");
		method_21601(135, "{Name:'minecraft:water',Properties:{level:'7'}}", "{Name:'minecraft:flowing_water',Properties:{level:'7'}}");
		method_21601(136, "{Name:'minecraft:water',Properties:{level:'8'}}", "{Name:'minecraft:flowing_water',Properties:{level:'8'}}");
		method_21601(137, "{Name:'minecraft:water',Properties:{level:'9'}}", "{Name:'minecraft:flowing_water',Properties:{level:'9'}}");
		method_21601(138, "{Name:'minecraft:water',Properties:{level:'10'}}", "{Name:'minecraft:flowing_water',Properties:{level:'10'}}");
		method_21601(139, "{Name:'minecraft:water',Properties:{level:'11'}}", "{Name:'minecraft:flowing_water',Properties:{level:'11'}}");
		method_21601(140, "{Name:'minecraft:water',Properties:{level:'12'}}", "{Name:'minecraft:flowing_water',Properties:{level:'12'}}");
		method_21601(141, "{Name:'minecraft:water',Properties:{level:'13'}}", "{Name:'minecraft:flowing_water',Properties:{level:'13'}}");
		method_21601(142, "{Name:'minecraft:water',Properties:{level:'14'}}", "{Name:'minecraft:flowing_water',Properties:{level:'14'}}");
		method_21601(143, "{Name:'minecraft:water',Properties:{level:'15'}}", "{Name:'minecraft:flowing_water',Properties:{level:'15'}}");
		method_21601(144, "{Name:'minecraft:water',Properties:{level:'0'}}", "{Name:'minecraft:water',Properties:{level:'0'}}");
		method_21601(145, "{Name:'minecraft:water',Properties:{level:'1'}}", "{Name:'minecraft:water',Properties:{level:'1'}}");
		method_21601(146, "{Name:'minecraft:water',Properties:{level:'2'}}", "{Name:'minecraft:water',Properties:{level:'2'}}");
		method_21601(147, "{Name:'minecraft:water',Properties:{level:'3'}}", "{Name:'minecraft:water',Properties:{level:'3'}}");
		method_21601(148, "{Name:'minecraft:water',Properties:{level:'4'}}", "{Name:'minecraft:water',Properties:{level:'4'}}");
		method_21601(149, "{Name:'minecraft:water',Properties:{level:'5'}}", "{Name:'minecraft:water',Properties:{level:'5'}}");
		method_21601(150, "{Name:'minecraft:water',Properties:{level:'6'}}", "{Name:'minecraft:water',Properties:{level:'6'}}");
		method_21601(151, "{Name:'minecraft:water',Properties:{level:'7'}}", "{Name:'minecraft:water',Properties:{level:'7'}}");
		method_21601(152, "{Name:'minecraft:water',Properties:{level:'8'}}", "{Name:'minecraft:water',Properties:{level:'8'}}");
		method_21601(153, "{Name:'minecraft:water',Properties:{level:'9'}}", "{Name:'minecraft:water',Properties:{level:'9'}}");
		method_21601(154, "{Name:'minecraft:water',Properties:{level:'10'}}", "{Name:'minecraft:water',Properties:{level:'10'}}");
		method_21601(155, "{Name:'minecraft:water',Properties:{level:'11'}}", "{Name:'minecraft:water',Properties:{level:'11'}}");
		method_21601(156, "{Name:'minecraft:water',Properties:{level:'12'}}", "{Name:'minecraft:water',Properties:{level:'12'}}");
		method_21601(157, "{Name:'minecraft:water',Properties:{level:'13'}}", "{Name:'minecraft:water',Properties:{level:'13'}}");
		method_21601(158, "{Name:'minecraft:water',Properties:{level:'14'}}", "{Name:'minecraft:water',Properties:{level:'14'}}");
		method_21601(159, "{Name:'minecraft:water',Properties:{level:'15'}}", "{Name:'minecraft:water',Properties:{level:'15'}}");
		method_21601(160, "{Name:'minecraft:lava',Properties:{level:'0'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'0'}}");
		method_21601(161, "{Name:'minecraft:lava',Properties:{level:'1'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'1'}}");
		method_21601(162, "{Name:'minecraft:lava',Properties:{level:'2'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'2'}}");
		method_21601(163, "{Name:'minecraft:lava',Properties:{level:'3'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'3'}}");
		method_21601(164, "{Name:'minecraft:lava',Properties:{level:'4'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'4'}}");
		method_21601(165, "{Name:'minecraft:lava',Properties:{level:'5'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'5'}}");
		method_21601(166, "{Name:'minecraft:lava',Properties:{level:'6'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'6'}}");
		method_21601(167, "{Name:'minecraft:lava',Properties:{level:'7'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'7'}}");
		method_21601(168, "{Name:'minecraft:lava',Properties:{level:'8'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'8'}}");
		method_21601(169, "{Name:'minecraft:lava',Properties:{level:'9'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'9'}}");
		method_21601(170, "{Name:'minecraft:lava',Properties:{level:'10'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'10'}}");
		method_21601(171, "{Name:'minecraft:lava',Properties:{level:'11'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'11'}}");
		method_21601(172, "{Name:'minecraft:lava',Properties:{level:'12'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'12'}}");
		method_21601(173, "{Name:'minecraft:lava',Properties:{level:'13'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'13'}}");
		method_21601(174, "{Name:'minecraft:lava',Properties:{level:'14'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'14'}}");
		method_21601(175, "{Name:'minecraft:lava',Properties:{level:'15'}}", "{Name:'minecraft:flowing_lava',Properties:{level:'15'}}");
		method_21601(176, "{Name:'minecraft:lava',Properties:{level:'0'}}", "{Name:'minecraft:lava',Properties:{level:'0'}}");
		method_21601(177, "{Name:'minecraft:lava',Properties:{level:'1'}}", "{Name:'minecraft:lava',Properties:{level:'1'}}");
		method_21601(178, "{Name:'minecraft:lava',Properties:{level:'2'}}", "{Name:'minecraft:lava',Properties:{level:'2'}}");
		method_21601(179, "{Name:'minecraft:lava',Properties:{level:'3'}}", "{Name:'minecraft:lava',Properties:{level:'3'}}");
		method_21601(180, "{Name:'minecraft:lava',Properties:{level:'4'}}", "{Name:'minecraft:lava',Properties:{level:'4'}}");
		method_21601(181, "{Name:'minecraft:lava',Properties:{level:'5'}}", "{Name:'minecraft:lava',Properties:{level:'5'}}");
		method_21601(182, "{Name:'minecraft:lava',Properties:{level:'6'}}", "{Name:'minecraft:lava',Properties:{level:'6'}}");
		method_21601(183, "{Name:'minecraft:lava',Properties:{level:'7'}}", "{Name:'minecraft:lava',Properties:{level:'7'}}");
		method_21601(184, "{Name:'minecraft:lava',Properties:{level:'8'}}", "{Name:'minecraft:lava',Properties:{level:'8'}}");
		method_21601(185, "{Name:'minecraft:lava',Properties:{level:'9'}}", "{Name:'minecraft:lava',Properties:{level:'9'}}");
		method_21601(186, "{Name:'minecraft:lava',Properties:{level:'10'}}", "{Name:'minecraft:lava',Properties:{level:'10'}}");
		method_21601(187, "{Name:'minecraft:lava',Properties:{level:'11'}}", "{Name:'minecraft:lava',Properties:{level:'11'}}");
		method_21601(188, "{Name:'minecraft:lava',Properties:{level:'12'}}", "{Name:'minecraft:lava',Properties:{level:'12'}}");
		method_21601(189, "{Name:'minecraft:lava',Properties:{level:'13'}}", "{Name:'minecraft:lava',Properties:{level:'13'}}");
		method_21601(190, "{Name:'minecraft:lava',Properties:{level:'14'}}", "{Name:'minecraft:lava',Properties:{level:'14'}}");
		method_21601(191, "{Name:'minecraft:lava',Properties:{level:'15'}}", "{Name:'minecraft:lava',Properties:{level:'15'}}");
		method_21601(192, "{Name:'minecraft:sand'}", "{Name:'minecraft:sand',Properties:{variant:'sand'}}");
		method_21601(193, "{Name:'minecraft:red_sand'}", "{Name:'minecraft:sand',Properties:{variant:'red_sand'}}");
		method_21601(208, "{Name:'minecraft:gravel'}", "{Name:'minecraft:gravel'}");
		method_21601(224, "{Name:'minecraft:gold_ore'}", "{Name:'minecraft:gold_ore'}");
		method_21601(240, "{Name:'minecraft:iron_ore'}", "{Name:'minecraft:iron_ore'}");
		method_21601(256, "{Name:'minecraft:coal_ore'}", "{Name:'minecraft:coal_ore'}");
		method_21601(272, "{Name:'minecraft:oak_log',Properties:{axis:'y'}}", "{Name:'minecraft:log',Properties:{axis:'y',variant:'oak'}}");
		method_21601(273, "{Name:'minecraft:spruce_log',Properties:{axis:'y'}}", "{Name:'minecraft:log',Properties:{axis:'y',variant:'spruce'}}");
		method_21601(274, "{Name:'minecraft:birch_log',Properties:{axis:'y'}}", "{Name:'minecraft:log',Properties:{axis:'y',variant:'birch'}}");
		method_21601(275, "{Name:'minecraft:jungle_log',Properties:{axis:'y'}}", "{Name:'minecraft:log',Properties:{axis:'y',variant:'jungle'}}");
		method_21601(276, "{Name:'minecraft:oak_log',Properties:{axis:'x'}}", "{Name:'minecraft:log',Properties:{axis:'x',variant:'oak'}}");
		method_21601(277, "{Name:'minecraft:spruce_log',Properties:{axis:'x'}}", "{Name:'minecraft:log',Properties:{axis:'x',variant:'spruce'}}");
		method_21601(278, "{Name:'minecraft:birch_log',Properties:{axis:'x'}}", "{Name:'minecraft:log',Properties:{axis:'x',variant:'birch'}}");
		method_21601(279, "{Name:'minecraft:jungle_log',Properties:{axis:'x'}}", "{Name:'minecraft:log',Properties:{axis:'x',variant:'jungle'}}");
		method_21601(280, "{Name:'minecraft:oak_log',Properties:{axis:'z'}}", "{Name:'minecraft:log',Properties:{axis:'z',variant:'oak'}}");
		method_21601(281, "{Name:'minecraft:spruce_log',Properties:{axis:'z'}}", "{Name:'minecraft:log',Properties:{axis:'z',variant:'spruce'}}");
		method_21601(282, "{Name:'minecraft:birch_log',Properties:{axis:'z'}}", "{Name:'minecraft:log',Properties:{axis:'z',variant:'birch'}}");
		method_21601(283, "{Name:'minecraft:jungle_log',Properties:{axis:'z'}}", "{Name:'minecraft:log',Properties:{axis:'z',variant:'jungle'}}");
		method_21601(284, "{Name:'minecraft:oak_bark'}", "{Name:'minecraft:log',Properties:{axis:'none',variant:'oak'}}");
		method_21601(285, "{Name:'minecraft:spruce_bark'}", "{Name:'minecraft:log',Properties:{axis:'none',variant:'spruce'}}");
		method_21601(286, "{Name:'minecraft:birch_bark'}", "{Name:'minecraft:log',Properties:{axis:'none',variant:'birch'}}");
		method_21601(287, "{Name:'minecraft:jungle_bark'}", "{Name:'minecraft:log',Properties:{axis:'none',variant:'jungle'}}");
		method_21601(
			288,
			"{Name:'minecraft:oak_leaves',Properties:{check_decay:'false',decayable:'true'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'false',decayable:'true',variant:'oak'}}"
		);
		method_21601(
			289,
			"{Name:'minecraft:spruce_leaves',Properties:{check_decay:'false',decayable:'true'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'false',decayable:'true',variant:'spruce'}}"
		);
		method_21601(
			290,
			"{Name:'minecraft:birch_leaves',Properties:{check_decay:'false',decayable:'true'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'false',decayable:'true',variant:'birch'}}"
		);
		method_21601(
			291,
			"{Name:'minecraft:jungle_leaves',Properties:{check_decay:'false',decayable:'true'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'false',decayable:'true',variant:'jungle'}}"
		);
		method_21601(
			292,
			"{Name:'minecraft:oak_leaves',Properties:{check_decay:'false',decayable:'false'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'false',decayable:'false',variant:'oak'}}"
		);
		method_21601(
			293,
			"{Name:'minecraft:spruce_leaves',Properties:{check_decay:'false',decayable:'false'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'false',decayable:'false',variant:'spruce'}}"
		);
		method_21601(
			294,
			"{Name:'minecraft:birch_leaves',Properties:{check_decay:'false',decayable:'false'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'false',decayable:'false',variant:'birch'}}"
		);
		method_21601(
			295,
			"{Name:'minecraft:jungle_leaves',Properties:{check_decay:'false',decayable:'false'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'false',decayable:'false',variant:'jungle'}}"
		);
		method_21601(
			296,
			"{Name:'minecraft:oak_leaves',Properties:{check_decay:'true',decayable:'true'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'true',decayable:'true',variant:'oak'}}"
		);
		method_21601(
			297,
			"{Name:'minecraft:spruce_leaves',Properties:{check_decay:'true',decayable:'true'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'true',decayable:'true',variant:'spruce'}}"
		);
		method_21601(
			298,
			"{Name:'minecraft:birch_leaves',Properties:{check_decay:'true',decayable:'true'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'true',decayable:'true',variant:'birch'}}"
		);
		method_21601(
			299,
			"{Name:'minecraft:jungle_leaves',Properties:{check_decay:'true',decayable:'true'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'true',decayable:'true',variant:'jungle'}}"
		);
		method_21601(
			300,
			"{Name:'minecraft:oak_leaves',Properties:{check_decay:'true',decayable:'false'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'true',decayable:'false',variant:'oak'}}"
		);
		method_21601(
			301,
			"{Name:'minecraft:spruce_leaves',Properties:{check_decay:'true',decayable:'false'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'true',decayable:'false',variant:'spruce'}}"
		);
		method_21601(
			302,
			"{Name:'minecraft:birch_leaves',Properties:{check_decay:'true',decayable:'false'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'true',decayable:'false',variant:'birch'}}"
		);
		method_21601(
			303,
			"{Name:'minecraft:jungle_leaves',Properties:{check_decay:'true',decayable:'false'}}",
			"{Name:'minecraft:leaves',Properties:{check_decay:'true',decayable:'false',variant:'jungle'}}"
		);
		method_21601(304, "{Name:'minecraft:sponge'}", "{Name:'minecraft:sponge',Properties:{wet:'false'}}");
		method_21601(305, "{Name:'minecraft:wet_sponge'}", "{Name:'minecraft:sponge',Properties:{wet:'true'}}");
		method_21601(320, "{Name:'minecraft:glass'}", "{Name:'minecraft:glass'}");
		method_21601(336, "{Name:'minecraft:lapis_ore'}", "{Name:'minecraft:lapis_ore'}");
		method_21601(352, "{Name:'minecraft:lapis_block'}", "{Name:'minecraft:lapis_block'}");
		method_21601(
			368,
			"{Name:'minecraft:dispenser',Properties:{facing:'down',triggered:'false'}}",
			"{Name:'minecraft:dispenser',Properties:{facing:'down',triggered:'false'}}"
		);
		method_21601(
			369, "{Name:'minecraft:dispenser',Properties:{facing:'up',triggered:'false'}}", "{Name:'minecraft:dispenser',Properties:{facing:'up',triggered:'false'}}"
		);
		method_21601(
			370,
			"{Name:'minecraft:dispenser',Properties:{facing:'north',triggered:'false'}}",
			"{Name:'minecraft:dispenser',Properties:{facing:'north',triggered:'false'}}"
		);
		method_21601(
			371,
			"{Name:'minecraft:dispenser',Properties:{facing:'south',triggered:'false'}}",
			"{Name:'minecraft:dispenser',Properties:{facing:'south',triggered:'false'}}"
		);
		method_21601(
			372,
			"{Name:'minecraft:dispenser',Properties:{facing:'west',triggered:'false'}}",
			"{Name:'minecraft:dispenser',Properties:{facing:'west',triggered:'false'}}"
		);
		method_21601(
			373,
			"{Name:'minecraft:dispenser',Properties:{facing:'east',triggered:'false'}}",
			"{Name:'minecraft:dispenser',Properties:{facing:'east',triggered:'false'}}"
		);
		method_21601(
			376, "{Name:'minecraft:dispenser',Properties:{facing:'down',triggered:'true'}}", "{Name:'minecraft:dispenser',Properties:{facing:'down',triggered:'true'}}"
		);
		method_21601(
			377, "{Name:'minecraft:dispenser',Properties:{facing:'up',triggered:'true'}}", "{Name:'minecraft:dispenser',Properties:{facing:'up',triggered:'true'}}"
		);
		method_21601(
			378,
			"{Name:'minecraft:dispenser',Properties:{facing:'north',triggered:'true'}}",
			"{Name:'minecraft:dispenser',Properties:{facing:'north',triggered:'true'}}"
		);
		method_21601(
			379,
			"{Name:'minecraft:dispenser',Properties:{facing:'south',triggered:'true'}}",
			"{Name:'minecraft:dispenser',Properties:{facing:'south',triggered:'true'}}"
		);
		method_21601(
			380, "{Name:'minecraft:dispenser',Properties:{facing:'west',triggered:'true'}}", "{Name:'minecraft:dispenser',Properties:{facing:'west',triggered:'true'}}"
		);
		method_21601(
			381, "{Name:'minecraft:dispenser',Properties:{facing:'east',triggered:'true'}}", "{Name:'minecraft:dispenser',Properties:{facing:'east',triggered:'true'}}"
		);
		method_21601(384, "{Name:'minecraft:sandstone'}", "{Name:'minecraft:sandstone',Properties:{type:'sandstone'}}");
		method_21601(385, "{Name:'minecraft:chiseled_sandstone'}", "{Name:'minecraft:sandstone',Properties:{type:'chiseled_sandstone'}}");
		method_21601(386, "{Name:'minecraft:cut_sandstone'}", "{Name:'minecraft:sandstone',Properties:{type:'smooth_sandstone'}}");
		method_21601(400, "{Name:'minecraft:note_block'}", "{Name:'minecraft:noteblock'}");
		method_21601(
			416,
			"{Name:'minecraft:red_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}",
			"{Name:'minecraft:bed',Properties:{facing:'south',occupied:'false',part:'foot'}}",
			"{Name:'minecraft:bed',Properties:{facing:'south',occupied:'true',part:'foot'}}"
		);
		method_21601(
			417,
			"{Name:'minecraft:red_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}",
			"{Name:'minecraft:bed',Properties:{facing:'west',occupied:'false',part:'foot'}}",
			"{Name:'minecraft:bed',Properties:{facing:'west',occupied:'true',part:'foot'}}"
		);
		method_21601(
			418,
			"{Name:'minecraft:red_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}",
			"{Name:'minecraft:bed',Properties:{facing:'north',occupied:'false',part:'foot'}}",
			"{Name:'minecraft:bed',Properties:{facing:'north',occupied:'true',part:'foot'}}"
		);
		method_21601(
			419,
			"{Name:'minecraft:red_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}",
			"{Name:'minecraft:bed',Properties:{facing:'east',occupied:'false',part:'foot'}}",
			"{Name:'minecraft:bed',Properties:{facing:'east',occupied:'true',part:'foot'}}"
		);
		method_21601(
			424,
			"{Name:'minecraft:red_bed',Properties:{facing:'south',occupied:'false',part:'head'}}",
			"{Name:'minecraft:bed',Properties:{facing:'south',occupied:'false',part:'head'}}"
		);
		method_21601(
			425,
			"{Name:'minecraft:red_bed',Properties:{facing:'west',occupied:'false',part:'head'}}",
			"{Name:'minecraft:bed',Properties:{facing:'west',occupied:'false',part:'head'}}"
		);
		method_21601(
			426,
			"{Name:'minecraft:red_bed',Properties:{facing:'north',occupied:'false',part:'head'}}",
			"{Name:'minecraft:bed',Properties:{facing:'north',occupied:'false',part:'head'}}"
		);
		method_21601(
			427,
			"{Name:'minecraft:red_bed',Properties:{facing:'east',occupied:'false',part:'head'}}",
			"{Name:'minecraft:bed',Properties:{facing:'east',occupied:'false',part:'head'}}"
		);
		method_21601(
			428,
			"{Name:'minecraft:red_bed',Properties:{facing:'south',occupied:'true',part:'head'}}",
			"{Name:'minecraft:bed',Properties:{facing:'south',occupied:'true',part:'head'}}"
		);
		method_21601(
			429,
			"{Name:'minecraft:red_bed',Properties:{facing:'west',occupied:'true',part:'head'}}",
			"{Name:'minecraft:bed',Properties:{facing:'west',occupied:'true',part:'head'}}"
		);
		method_21601(
			430,
			"{Name:'minecraft:red_bed',Properties:{facing:'north',occupied:'true',part:'head'}}",
			"{Name:'minecraft:bed',Properties:{facing:'north',occupied:'true',part:'head'}}"
		);
		method_21601(
			431,
			"{Name:'minecraft:red_bed',Properties:{facing:'east',occupied:'true',part:'head'}}",
			"{Name:'minecraft:bed',Properties:{facing:'east',occupied:'true',part:'head'}}"
		);
		method_21601(
			432,
			"{Name:'minecraft:powered_rail',Properties:{powered:'false',shape:'north_south'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'false',shape:'north_south'}}"
		);
		method_21601(
			433,
			"{Name:'minecraft:powered_rail',Properties:{powered:'false',shape:'east_west'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'false',shape:'east_west'}}"
		);
		method_21601(
			434,
			"{Name:'minecraft:powered_rail',Properties:{powered:'false',shape:'ascending_east'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'false',shape:'ascending_east'}}"
		);
		method_21601(
			435,
			"{Name:'minecraft:powered_rail',Properties:{powered:'false',shape:'ascending_west'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'false',shape:'ascending_west'}}"
		);
		method_21601(
			436,
			"{Name:'minecraft:powered_rail',Properties:{powered:'false',shape:'ascending_north'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'false',shape:'ascending_north'}}"
		);
		method_21601(
			437,
			"{Name:'minecraft:powered_rail',Properties:{powered:'false',shape:'ascending_south'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'false',shape:'ascending_south'}}"
		);
		method_21601(
			440,
			"{Name:'minecraft:powered_rail',Properties:{powered:'true',shape:'north_south'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'true',shape:'north_south'}}"
		);
		method_21601(
			441,
			"{Name:'minecraft:powered_rail',Properties:{powered:'true',shape:'east_west'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'true',shape:'east_west'}}"
		);
		method_21601(
			442,
			"{Name:'minecraft:powered_rail',Properties:{powered:'true',shape:'ascending_east'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'true',shape:'ascending_east'}}"
		);
		method_21601(
			443,
			"{Name:'minecraft:powered_rail',Properties:{powered:'true',shape:'ascending_west'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'true',shape:'ascending_west'}}"
		);
		method_21601(
			444,
			"{Name:'minecraft:powered_rail',Properties:{powered:'true',shape:'ascending_north'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'true',shape:'ascending_north'}}"
		);
		method_21601(
			445,
			"{Name:'minecraft:powered_rail',Properties:{powered:'true',shape:'ascending_south'}}",
			"{Name:'minecraft:golden_rail',Properties:{powered:'true',shape:'ascending_south'}}"
		);
		method_21601(
			448,
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'north_south'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'north_south'}}"
		);
		method_21601(
			449,
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'east_west'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'east_west'}}"
		);
		method_21601(
			450,
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'ascending_east'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'ascending_east'}}"
		);
		method_21601(
			451,
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'ascending_west'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'ascending_west'}}"
		);
		method_21601(
			452,
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'ascending_north'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'ascending_north'}}"
		);
		method_21601(
			453,
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'ascending_south'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'false',shape:'ascending_south'}}"
		);
		method_21601(
			456,
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'north_south'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'north_south'}}"
		);
		method_21601(
			457,
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'east_west'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'east_west'}}"
		);
		method_21601(
			458,
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'ascending_east'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'ascending_east'}}"
		);
		method_21601(
			459,
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'ascending_west'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'ascending_west'}}"
		);
		method_21601(
			460,
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'ascending_north'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'ascending_north'}}"
		);
		method_21601(
			461,
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'ascending_south'}}",
			"{Name:'minecraft:detector_rail',Properties:{powered:'true',shape:'ascending_south'}}"
		);
		method_21601(
			464,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'down'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'down'}}"
		);
		method_21601(
			465,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'up'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'up'}}"
		);
		method_21601(
			466,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'north'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'north'}}"
		);
		method_21601(
			467,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'south'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'south'}}"
		);
		method_21601(
			468,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'west'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'west'}}"
		);
		method_21601(
			469,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'east'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'false',facing:'east'}}"
		);
		method_21601(
			472,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'down'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'down'}}"
		);
		method_21601(
			473,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'up'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'up'}}"
		);
		method_21601(
			474,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'north'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'north'}}"
		);
		method_21601(
			475,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'south'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'south'}}"
		);
		method_21601(
			476,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'west'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'west'}}"
		);
		method_21601(
			477,
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'east'}}",
			"{Name:'minecraft:sticky_piston',Properties:{extended:'true',facing:'east'}}"
		);
		method_21601(480, "{Name:'minecraft:cobweb'}", "{Name:'minecraft:web'}");
		method_21601(496, "{Name:'minecraft:dead_bush'}", "{Name:'minecraft:tallgrass',Properties:{type:'dead_bush'}}");
		method_21601(497, "{Name:'minecraft:grass'}", "{Name:'minecraft:tallgrass',Properties:{type:'tall_grass'}}");
		method_21601(498, "{Name:'minecraft:fern'}", "{Name:'minecraft:tallgrass',Properties:{type:'fern'}}");
		method_21601(512, "{Name:'minecraft:dead_bush'}", "{Name:'minecraft:deadbush'}");
		method_21601(
			528, "{Name:'minecraft:piston',Properties:{extended:'false',facing:'down'}}", "{Name:'minecraft:piston',Properties:{extended:'false',facing:'down'}}"
		);
		method_21601(
			529, "{Name:'minecraft:piston',Properties:{extended:'false',facing:'up'}}", "{Name:'minecraft:piston',Properties:{extended:'false',facing:'up'}}"
		);
		method_21601(
			530, "{Name:'minecraft:piston',Properties:{extended:'false',facing:'north'}}", "{Name:'minecraft:piston',Properties:{extended:'false',facing:'north'}}"
		);
		method_21601(
			531, "{Name:'minecraft:piston',Properties:{extended:'false',facing:'south'}}", "{Name:'minecraft:piston',Properties:{extended:'false',facing:'south'}}"
		);
		method_21601(
			532, "{Name:'minecraft:piston',Properties:{extended:'false',facing:'west'}}", "{Name:'minecraft:piston',Properties:{extended:'false',facing:'west'}}"
		);
		method_21601(
			533, "{Name:'minecraft:piston',Properties:{extended:'false',facing:'east'}}", "{Name:'minecraft:piston',Properties:{extended:'false',facing:'east'}}"
		);
		method_21601(
			536, "{Name:'minecraft:piston',Properties:{extended:'true',facing:'down'}}", "{Name:'minecraft:piston',Properties:{extended:'true',facing:'down'}}"
		);
		method_21601(537, "{Name:'minecraft:piston',Properties:{extended:'true',facing:'up'}}", "{Name:'minecraft:piston',Properties:{extended:'true',facing:'up'}}");
		method_21601(
			538, "{Name:'minecraft:piston',Properties:{extended:'true',facing:'north'}}", "{Name:'minecraft:piston',Properties:{extended:'true',facing:'north'}}"
		);
		method_21601(
			539, "{Name:'minecraft:piston',Properties:{extended:'true',facing:'south'}}", "{Name:'minecraft:piston',Properties:{extended:'true',facing:'south'}}"
		);
		method_21601(
			540, "{Name:'minecraft:piston',Properties:{extended:'true',facing:'west'}}", "{Name:'minecraft:piston',Properties:{extended:'true',facing:'west'}}"
		);
		method_21601(
			541, "{Name:'minecraft:piston',Properties:{extended:'true',facing:'east'}}", "{Name:'minecraft:piston',Properties:{extended:'true',facing:'east'}}"
		);
		method_21601(
			544,
			"{Name:'minecraft:piston_head',Properties:{facing:'down',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'down',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'down',short:'true',type:'normal'}}"
		);
		method_21601(
			545,
			"{Name:'minecraft:piston_head',Properties:{facing:'up',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'up',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'up',short:'true',type:'normal'}}"
		);
		method_21601(
			546,
			"{Name:'minecraft:piston_head',Properties:{facing:'north',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'north',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'north',short:'true',type:'normal'}}"
		);
		method_21601(
			547,
			"{Name:'minecraft:piston_head',Properties:{facing:'south',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'south',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'south',short:'true',type:'normal'}}"
		);
		method_21601(
			548,
			"{Name:'minecraft:piston_head',Properties:{facing:'west',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'west',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'west',short:'true',type:'normal'}}"
		);
		method_21601(
			549,
			"{Name:'minecraft:piston_head',Properties:{facing:'east',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'east',short:'false',type:'normal'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'east',short:'true',type:'normal'}}"
		);
		method_21601(
			552,
			"{Name:'minecraft:piston_head',Properties:{facing:'down',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'down',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'down',short:'true',type:'sticky'}}"
		);
		method_21601(
			553,
			"{Name:'minecraft:piston_head',Properties:{facing:'up',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'up',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'up',short:'true',type:'sticky'}}"
		);
		method_21601(
			554,
			"{Name:'minecraft:piston_head',Properties:{facing:'north',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'north',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'north',short:'true',type:'sticky'}}"
		);
		method_21601(
			555,
			"{Name:'minecraft:piston_head',Properties:{facing:'south',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'south',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'south',short:'true',type:'sticky'}}"
		);
		method_21601(
			556,
			"{Name:'minecraft:piston_head',Properties:{facing:'west',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'west',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'west',short:'true',type:'sticky'}}"
		);
		method_21601(
			557,
			"{Name:'minecraft:piston_head',Properties:{facing:'east',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'east',short:'false',type:'sticky'}}",
			"{Name:'minecraft:piston_head',Properties:{facing:'east',short:'true',type:'sticky'}}"
		);
		method_21601(560, "{Name:'minecraft:white_wool'}", "{Name:'minecraft:wool',Properties:{color:'white'}}");
		method_21601(561, "{Name:'minecraft:orange_wool'}", "{Name:'minecraft:wool',Properties:{color:'orange'}}");
		method_21601(562, "{Name:'minecraft:magenta_wool'}", "{Name:'minecraft:wool',Properties:{color:'magenta'}}");
		method_21601(563, "{Name:'minecraft:light_blue_wool'}", "{Name:'minecraft:wool',Properties:{color:'light_blue'}}");
		method_21601(564, "{Name:'minecraft:yellow_wool'}", "{Name:'minecraft:wool',Properties:{color:'yellow'}}");
		method_21601(565, "{Name:'minecraft:lime_wool'}", "{Name:'minecraft:wool',Properties:{color:'lime'}}");
		method_21601(566, "{Name:'minecraft:pink_wool'}", "{Name:'minecraft:wool',Properties:{color:'pink'}}");
		method_21601(567, "{Name:'minecraft:gray_wool'}", "{Name:'minecraft:wool',Properties:{color:'gray'}}");
		method_21601(568, "{Name:'minecraft:light_gray_wool'}", "{Name:'minecraft:wool',Properties:{color:'silver'}}");
		method_21601(569, "{Name:'minecraft:cyan_wool'}", "{Name:'minecraft:wool',Properties:{color:'cyan'}}");
		method_21601(570, "{Name:'minecraft:purple_wool'}", "{Name:'minecraft:wool',Properties:{color:'purple'}}");
		method_21601(571, "{Name:'minecraft:blue_wool'}", "{Name:'minecraft:wool',Properties:{color:'blue'}}");
		method_21601(572, "{Name:'minecraft:brown_wool'}", "{Name:'minecraft:wool',Properties:{color:'brown'}}");
		method_21601(573, "{Name:'minecraft:green_wool'}", "{Name:'minecraft:wool',Properties:{color:'green'}}");
		method_21601(574, "{Name:'minecraft:red_wool'}", "{Name:'minecraft:wool',Properties:{color:'red'}}");
		method_21601(575, "{Name:'minecraft:black_wool'}", "{Name:'minecraft:wool',Properties:{color:'black'}}");
		method_21601(
			576,
			"{Name:'minecraft:moving_piston',Properties:{facing:'down',type:'normal'}}",
			"{Name:'minecraft:piston_extension',Properties:{facing:'down',type:'normal'}}"
		);
		method_21601(
			577, "{Name:'minecraft:moving_piston',Properties:{facing:'up',type:'normal'}}", "{Name:'minecraft:piston_extension',Properties:{facing:'up',type:'normal'}}"
		);
		method_21601(
			578,
			"{Name:'minecraft:moving_piston',Properties:{facing:'north',type:'normal'}}",
			"{Name:'minecraft:piston_extension',Properties:{facing:'north',type:'normal'}}"
		);
		method_21601(
			579,
			"{Name:'minecraft:moving_piston',Properties:{facing:'south',type:'normal'}}",
			"{Name:'minecraft:piston_extension',Properties:{facing:'south',type:'normal'}}"
		);
		method_21601(
			580,
			"{Name:'minecraft:moving_piston',Properties:{facing:'west',type:'normal'}}",
			"{Name:'minecraft:piston_extension',Properties:{facing:'west',type:'normal'}}"
		);
		method_21601(
			581,
			"{Name:'minecraft:moving_piston',Properties:{facing:'east',type:'normal'}}",
			"{Name:'minecraft:piston_extension',Properties:{facing:'east',type:'normal'}}"
		);
		method_21601(
			584,
			"{Name:'minecraft:moving_piston',Properties:{facing:'down',type:'sticky'}}",
			"{Name:'minecraft:piston_extension',Properties:{facing:'down',type:'sticky'}}"
		);
		method_21601(
			585, "{Name:'minecraft:moving_piston',Properties:{facing:'up',type:'sticky'}}", "{Name:'minecraft:piston_extension',Properties:{facing:'up',type:'sticky'}}"
		);
		method_21601(
			586,
			"{Name:'minecraft:moving_piston',Properties:{facing:'north',type:'sticky'}}",
			"{Name:'minecraft:piston_extension',Properties:{facing:'north',type:'sticky'}}"
		);
		method_21601(
			587,
			"{Name:'minecraft:moving_piston',Properties:{facing:'south',type:'sticky'}}",
			"{Name:'minecraft:piston_extension',Properties:{facing:'south',type:'sticky'}}"
		);
		method_21601(
			588,
			"{Name:'minecraft:moving_piston',Properties:{facing:'west',type:'sticky'}}",
			"{Name:'minecraft:piston_extension',Properties:{facing:'west',type:'sticky'}}"
		);
		method_21601(
			589,
			"{Name:'minecraft:moving_piston',Properties:{facing:'east',type:'sticky'}}",
			"{Name:'minecraft:piston_extension',Properties:{facing:'east',type:'sticky'}}"
		);
		method_21601(592, "{Name:'minecraft:dandelion'}", "{Name:'minecraft:yellow_flower',Properties:{type:'dandelion'}}");
		method_21601(608, "{Name:'minecraft:poppy'}", "{Name:'minecraft:red_flower',Properties:{type:'poppy'}}");
		method_21601(609, "{Name:'minecraft:blue_orchid'}", "{Name:'minecraft:red_flower',Properties:{type:'blue_orchid'}}");
		method_21601(610, "{Name:'minecraft:allium'}", "{Name:'minecraft:red_flower',Properties:{type:'allium'}}");
		method_21601(611, "{Name:'minecraft:azure_bluet'}", "{Name:'minecraft:red_flower',Properties:{type:'houstonia'}}");
		method_21601(612, "{Name:'minecraft:red_tulip'}", "{Name:'minecraft:red_flower',Properties:{type:'red_tulip'}}");
		method_21601(613, "{Name:'minecraft:orange_tulip'}", "{Name:'minecraft:red_flower',Properties:{type:'orange_tulip'}}");
		method_21601(614, "{Name:'minecraft:white_tulip'}", "{Name:'minecraft:red_flower',Properties:{type:'white_tulip'}}");
		method_21601(615, "{Name:'minecraft:pink_tulip'}", "{Name:'minecraft:red_flower',Properties:{type:'pink_tulip'}}");
		method_21601(616, "{Name:'minecraft:oxeye_daisy'}", "{Name:'minecraft:red_flower',Properties:{type:'oxeye_daisy'}}");
		method_21601(624, "{Name:'minecraft:brown_mushroom'}", "{Name:'minecraft:brown_mushroom'}");
		method_21601(640, "{Name:'minecraft:red_mushroom'}", "{Name:'minecraft:red_mushroom'}");
		method_21601(656, "{Name:'minecraft:gold_block'}", "{Name:'minecraft:gold_block'}");
		method_21601(672, "{Name:'minecraft:iron_block'}", "{Name:'minecraft:iron_block'}");
		method_21601(
			688, "{Name:'minecraft:stone_slab',Properties:{type:'double'}}", "{Name:'minecraft:double_stone_slab',Properties:{seamless:'false',variant:'stone'}}"
		);
		method_21601(
			689,
			"{Name:'minecraft:sandstone_slab',Properties:{type:'double'}}",
			"{Name:'minecraft:double_stone_slab',Properties:{seamless:'false',variant:'sandstone'}}"
		);
		method_21601(
			690,
			"{Name:'minecraft:petrified_oak_slab',Properties:{type:'double'}}",
			"{Name:'minecraft:double_stone_slab',Properties:{seamless:'false',variant:'wood_old'}}"
		);
		method_21601(
			691,
			"{Name:'minecraft:cobblestone_slab',Properties:{type:'double'}}",
			"{Name:'minecraft:double_stone_slab',Properties:{seamless:'false',variant:'cobblestone'}}"
		);
		method_21601(
			692, "{Name:'minecraft:brick_slab',Properties:{type:'double'}}", "{Name:'minecraft:double_stone_slab',Properties:{seamless:'false',variant:'brick'}}"
		);
		method_21601(
			693,
			"{Name:'minecraft:stone_brick_slab',Properties:{type:'double'}}",
			"{Name:'minecraft:double_stone_slab',Properties:{seamless:'false',variant:'stone_brick'}}"
		);
		method_21601(
			694,
			"{Name:'minecraft:nether_brick_slab',Properties:{type:'double'}}",
			"{Name:'minecraft:double_stone_slab',Properties:{seamless:'false',variant:'nether_brick'}}"
		);
		method_21601(
			695, "{Name:'minecraft:quartz_slab',Properties:{type:'double'}}", "{Name:'minecraft:double_stone_slab',Properties:{seamless:'false',variant:'quartz'}}"
		);
		method_21601(696, "{Name:'minecraft:smooth_stone'}", "{Name:'minecraft:double_stone_slab',Properties:{seamless:'true',variant:'stone'}}");
		method_21601(697, "{Name:'minecraft:smooth_sandstone'}", "{Name:'minecraft:double_stone_slab',Properties:{seamless:'true',variant:'sandstone'}}");
		method_21601(
			698,
			"{Name:'minecraft:petrified_oak_slab',Properties:{type:'double'}}",
			"{Name:'minecraft:double_stone_slab',Properties:{seamless:'true',variant:'wood_old'}}"
		);
		method_21601(
			699,
			"{Name:'minecraft:cobblestone_slab',Properties:{type:'double'}}",
			"{Name:'minecraft:double_stone_slab',Properties:{seamless:'true',variant:'cobblestone'}}"
		);
		method_21601(
			700, "{Name:'minecraft:brick_slab',Properties:{type:'double'}}", "{Name:'minecraft:double_stone_slab',Properties:{seamless:'true',variant:'brick'}}"
		);
		method_21601(
			701,
			"{Name:'minecraft:stone_brick_slab',Properties:{type:'double'}}",
			"{Name:'minecraft:double_stone_slab',Properties:{seamless:'true',variant:'stone_brick'}}"
		);
		method_21601(
			702,
			"{Name:'minecraft:nether_brick_slab',Properties:{type:'double'}}",
			"{Name:'minecraft:double_stone_slab',Properties:{seamless:'true',variant:'nether_brick'}}"
		);
		method_21601(703, "{Name:'minecraft:smooth_quartz'}", "{Name:'minecraft:double_stone_slab',Properties:{seamless:'true',variant:'quartz'}}");
		method_21601(704, "{Name:'minecraft:stone_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:stone_slab',Properties:{half:'bottom',variant:'stone'}}");
		method_21601(
			705, "{Name:'minecraft:sandstone_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:stone_slab',Properties:{half:'bottom',variant:'sandstone'}}"
		);
		method_21601(
			706, "{Name:'minecraft:petrified_oak_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:stone_slab',Properties:{half:'bottom',variant:'wood_old'}}"
		);
		method_21601(
			707, "{Name:'minecraft:cobblestone_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:stone_slab',Properties:{half:'bottom',variant:'cobblestone'}}"
		);
		method_21601(708, "{Name:'minecraft:brick_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:stone_slab',Properties:{half:'bottom',variant:'brick'}}");
		method_21601(
			709, "{Name:'minecraft:stone_brick_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:stone_slab',Properties:{half:'bottom',variant:'stone_brick'}}"
		);
		method_21601(
			710, "{Name:'minecraft:nether_brick_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:stone_slab',Properties:{half:'bottom',variant:'nether_brick'}}"
		);
		method_21601(711, "{Name:'minecraft:quartz_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:stone_slab',Properties:{half:'bottom',variant:'quartz'}}");
		method_21601(712, "{Name:'minecraft:stone_slab',Properties:{type:'top'}}", "{Name:'minecraft:stone_slab',Properties:{half:'top',variant:'stone'}}");
		method_21601(713, "{Name:'minecraft:sandstone_slab',Properties:{type:'top'}}", "{Name:'minecraft:stone_slab',Properties:{half:'top',variant:'sandstone'}}");
		method_21601(714, "{Name:'minecraft:petrified_oak_slab',Properties:{type:'top'}}", "{Name:'minecraft:stone_slab',Properties:{half:'top',variant:'wood_old'}}");
		method_21601(
			715, "{Name:'minecraft:cobblestone_slab',Properties:{type:'top'}}", "{Name:'minecraft:stone_slab',Properties:{half:'top',variant:'cobblestone'}}"
		);
		method_21601(716, "{Name:'minecraft:brick_slab',Properties:{type:'top'}}", "{Name:'minecraft:stone_slab',Properties:{half:'top',variant:'brick'}}");
		method_21601(
			717, "{Name:'minecraft:stone_brick_slab',Properties:{type:'top'}}", "{Name:'minecraft:stone_slab',Properties:{half:'top',variant:'stone_brick'}}"
		);
		method_21601(
			718, "{Name:'minecraft:nether_brick_slab',Properties:{type:'top'}}", "{Name:'minecraft:stone_slab',Properties:{half:'top',variant:'nether_brick'}}"
		);
		method_21601(719, "{Name:'minecraft:quartz_slab',Properties:{type:'top'}}", "{Name:'minecraft:stone_slab',Properties:{half:'top',variant:'quartz'}}");
		method_21601(720, "{Name:'minecraft:bricks'}", "{Name:'minecraft:brick_block'}");
		method_21601(736, "{Name:'minecraft:tnt',Properties:{unstable:'false'}}", "{Name:'minecraft:tnt',Properties:{explode:'false'}}");
		method_21601(737, "{Name:'minecraft:tnt',Properties:{unstable:'true'}}", "{Name:'minecraft:tnt',Properties:{explode:'true'}}");
		method_21601(752, "{Name:'minecraft:bookshelf'}", "{Name:'minecraft:bookshelf'}");
		method_21601(768, "{Name:'minecraft:mossy_cobblestone'}", "{Name:'minecraft:mossy_cobblestone'}");
		method_21601(784, "{Name:'minecraft:obsidian'}", "{Name:'minecraft:obsidian'}");
		method_21601(801, "{Name:'minecraft:wall_torch',Properties:{facing:'east'}}", "{Name:'minecraft:torch',Properties:{facing:'east'}}");
		method_21601(802, "{Name:'minecraft:wall_torch',Properties:{facing:'west'}}", "{Name:'minecraft:torch',Properties:{facing:'west'}}");
		method_21601(803, "{Name:'minecraft:wall_torch',Properties:{facing:'south'}}", "{Name:'minecraft:torch',Properties:{facing:'south'}}");
		method_21601(804, "{Name:'minecraft:wall_torch',Properties:{facing:'north'}}", "{Name:'minecraft:torch',Properties:{facing:'north'}}");
		method_21601(805, "{Name:'minecraft:torch'}", "{Name:'minecraft:torch',Properties:{facing:'up'}}");
		method_21601(
			816,
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'0',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			817,
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'1',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			818,
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'2',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			819,
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'3',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			820,
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'4',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			821,
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'5',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			822,
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'6',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			823,
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'7',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			824,
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'8',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			825,
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'9',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			826,
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'10',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			827,
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'11',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			828,
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'12',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			829,
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'13',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			830,
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'14',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			831,
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:fire',Properties:{age:'15',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(832, "{Name:'minecraft:mob_spawner'}", "{Name:'minecraft:mob_spawner'}");
		method_21601(
			848,
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			849,
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			850,
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			851,
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			852,
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			853,
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			854,
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			855,
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:oak_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(866, "{Name:'minecraft:chest',Properties:{facing:'north',type:'single'}}", "{Name:'minecraft:chest',Properties:{facing:'north'}}");
		method_21601(867, "{Name:'minecraft:chest',Properties:{facing:'south',type:'single'}}", "{Name:'minecraft:chest',Properties:{facing:'south'}}");
		method_21601(868, "{Name:'minecraft:chest',Properties:{facing:'west',type:'single'}}", "{Name:'minecraft:chest',Properties:{facing:'west'}}");
		method_21601(869, "{Name:'minecraft:chest',Properties:{facing:'east',type:'single'}}", "{Name:'minecraft:chest',Properties:{facing:'east'}}");
		method_21601(
			880,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'0',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'0',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'0',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'0',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'0',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'0',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'0',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'0',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'0',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'0',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'0',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'0',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'0',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'0',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'0',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'0',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'0',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'0',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'0',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'0',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'0',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'0',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'0',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'0',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'0',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'0',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'0',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'0',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'0',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'0',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'0',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'0',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'0',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'0',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'0',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'0',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'0',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'0',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'0',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'0',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'0',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'0',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'0',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'0',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'0',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'0',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'0',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'0',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'0',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'0',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'0',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'0',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'0',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'0',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'0',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'0',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'0',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'0',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'0',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'0',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'0',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'0',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'0',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'0',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'0',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'0',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'0',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'0',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'0',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'0',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'0',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'0',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'0',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'0',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'0',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'0',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'0',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'0',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'0',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'0',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'0',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'0',south:'up',west:'up'}}"
		);
		method_21601(
			881,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'1',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'1',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'1',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'1',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'1',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'1',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'1',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'1',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'1',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'1',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'1',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'1',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'1',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'1',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'1',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'1',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'1',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'1',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'1',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'1',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'1',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'1',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'1',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'1',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'1',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'1',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'1',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'1',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'1',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'1',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'1',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'1',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'1',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'1',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'1',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'1',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'1',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'1',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'1',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'1',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'1',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'1',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'1',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'1',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'1',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'1',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'1',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'1',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'1',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'1',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'1',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'1',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'1',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'1',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'1',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'1',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'1',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'1',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'1',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'1',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'1',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'1',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'1',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'1',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'1',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'1',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'1',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'1',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'1',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'1',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'1',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'1',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'1',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'1',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'1',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'1',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'1',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'1',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'1',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'1',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'1',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'1',south:'up',west:'up'}}"
		);
		method_21601(
			882,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'2',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'2',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'2',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'2',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'2',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'2',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'2',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'2',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'2',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'2',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'2',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'2',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'2',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'2',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'2',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'2',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'2',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'2',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'2',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'2',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'2',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'2',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'2',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'2',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'2',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'2',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'2',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'2',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'2',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'2',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'2',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'2',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'2',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'2',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'2',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'2',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'2',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'2',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'2',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'2',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'2',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'2',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'2',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'2',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'2',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'2',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'2',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'2',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'2',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'2',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'2',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'2',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'2',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'2',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'2',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'2',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'2',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'2',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'2',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'2',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'2',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'2',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'2',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'2',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'2',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'2',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'2',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'2',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'2',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'2',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'2',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'2',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'2',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'2',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'2',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'2',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'2',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'2',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'2',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'2',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'2',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'2',south:'up',west:'up'}}"
		);
		method_21601(
			883,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'3',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'3',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'3',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'3',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'3',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'3',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'3',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'3',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'3',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'3',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'3',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'3',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'3',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'3',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'3',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'3',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'3',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'3',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'3',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'3',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'3',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'3',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'3',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'3',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'3',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'3',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'3',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'3',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'3',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'3',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'3',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'3',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'3',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'3',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'3',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'3',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'3',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'3',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'3',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'3',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'3',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'3',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'3',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'3',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'3',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'3',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'3',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'3',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'3',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'3',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'3',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'3',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'3',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'3',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'3',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'3',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'3',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'3',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'3',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'3',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'3',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'3',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'3',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'3',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'3',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'3',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'3',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'3',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'3',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'3',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'3',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'3',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'3',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'3',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'3',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'3',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'3',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'3',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'3',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'3',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'3',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'3',south:'up',west:'up'}}"
		);
		method_21601(
			884,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'4',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'4',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'4',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'4',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'4',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'4',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'4',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'4',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'4',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'4',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'4',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'4',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'4',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'4',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'4',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'4',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'4',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'4',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'4',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'4',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'4',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'4',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'4',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'4',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'4',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'4',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'4',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'4',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'4',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'4',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'4',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'4',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'4',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'4',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'4',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'4',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'4',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'4',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'4',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'4',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'4',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'4',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'4',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'4',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'4',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'4',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'4',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'4',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'4',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'4',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'4',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'4',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'4',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'4',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'4',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'4',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'4',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'4',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'4',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'4',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'4',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'4',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'4',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'4',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'4',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'4',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'4',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'4',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'4',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'4',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'4',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'4',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'4',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'4',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'4',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'4',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'4',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'4',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'4',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'4',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'4',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'4',south:'up',west:'up'}}"
		);
		method_21601(
			885,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'5',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'5',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'5',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'5',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'5',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'5',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'5',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'5',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'5',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'5',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'5',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'5',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'5',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'5',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'5',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'5',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'5',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'5',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'5',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'5',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'5',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'5',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'5',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'5',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'5',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'5',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'5',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'5',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'5',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'5',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'5',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'5',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'5',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'5',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'5',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'5',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'5',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'5',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'5',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'5',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'5',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'5',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'5',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'5',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'5',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'5',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'5',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'5',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'5',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'5',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'5',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'5',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'5',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'5',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'5',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'5',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'5',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'5',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'5',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'5',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'5',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'5',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'5',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'5',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'5',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'5',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'5',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'5',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'5',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'5',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'5',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'5',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'5',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'5',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'5',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'5',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'5',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'5',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'5',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'5',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'5',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'5',south:'up',west:'up'}}"
		);
		method_21601(
			886,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'6',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'6',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'6',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'6',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'6',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'6',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'6',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'6',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'6',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'6',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'6',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'6',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'6',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'6',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'6',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'6',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'6',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'6',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'6',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'6',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'6',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'6',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'6',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'6',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'6',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'6',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'6',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'6',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'6',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'6',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'6',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'6',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'6',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'6',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'6',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'6',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'6',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'6',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'6',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'6',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'6',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'6',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'6',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'6',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'6',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'6',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'6',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'6',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'6',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'6',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'6',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'6',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'6',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'6',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'6',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'6',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'6',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'6',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'6',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'6',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'6',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'6',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'6',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'6',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'6',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'6',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'6',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'6',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'6',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'6',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'6',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'6',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'6',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'6',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'6',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'6',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'6',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'6',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'6',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'6',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'6',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'6',south:'up',west:'up'}}"
		);
		method_21601(
			887,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'7',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'7',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'7',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'7',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'7',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'7',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'7',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'7',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'7',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'7',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'7',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'7',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'7',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'7',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'7',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'7',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'7',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'7',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'7',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'7',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'7',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'7',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'7',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'7',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'7',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'7',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'7',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'7',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'7',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'7',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'7',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'7',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'7',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'7',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'7',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'7',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'7',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'7',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'7',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'7',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'7',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'7',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'7',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'7',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'7',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'7',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'7',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'7',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'7',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'7',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'7',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'7',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'7',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'7',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'7',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'7',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'7',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'7',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'7',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'7',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'7',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'7',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'7',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'7',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'7',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'7',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'7',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'7',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'7',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'7',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'7',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'7',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'7',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'7',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'7',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'7',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'7',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'7',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'7',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'7',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'7',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'7',south:'up',west:'up'}}"
		);
		method_21601(
			888,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'8',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'8',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'8',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'8',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'8',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'8',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'8',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'8',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'8',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'8',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'8',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'8',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'8',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'8',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'8',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'8',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'8',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'8',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'8',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'8',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'8',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'8',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'8',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'8',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'8',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'8',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'8',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'8',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'8',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'8',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'8',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'8',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'8',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'8',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'8',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'8',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'8',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'8',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'8',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'8',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'8',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'8',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'8',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'8',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'8',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'8',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'8',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'8',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'8',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'8',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'8',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'8',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'8',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'8',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'8',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'8',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'8',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'8',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'8',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'8',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'8',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'8',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'8',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'8',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'8',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'8',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'8',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'8',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'8',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'8',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'8',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'8',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'8',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'8',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'8',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'8',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'8',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'8',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'8',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'8',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'8',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'8',south:'up',west:'up'}}"
		);
		method_21601(
			889,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'9',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'9',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'9',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'9',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'9',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'9',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'9',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'9',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'9',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'9',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'9',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'9',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'9',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'9',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'9',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'9',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'9',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'9',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'9',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'9',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'9',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'9',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'9',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'9',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'9',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'9',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'9',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'9',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'9',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'9',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'9',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'9',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'9',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'9',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'9',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'9',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'9',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'9',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'9',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'9',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'9',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'9',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'9',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'9',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'9',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'9',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'9',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'9',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'9',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'9',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'9',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'9',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'9',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'9',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'9',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'9',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'9',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'9',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'9',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'9',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'9',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'9',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'9',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'9',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'9',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'9',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'9',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'9',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'9',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'9',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'9',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'9',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'9',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'9',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'9',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'9',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'9',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'9',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'9',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'9',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'9',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'9',south:'up',west:'up'}}"
		);
		method_21601(
			890,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'10',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'10',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'10',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'10',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'10',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'10',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'10',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'10',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'10',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'10',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'10',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'10',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'10',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'10',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'10',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'10',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'10',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'10',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'10',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'10',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'10',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'10',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'10',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'10',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'10',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'10',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'10',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'10',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'10',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'10',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'10',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'10',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'10',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'10',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'10',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'10',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'10',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'10',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'10',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'10',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'10',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'10',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'10',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'10',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'10',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'10',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'10',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'10',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'10',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'10',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'10',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'10',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'10',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'10',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'10',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'10',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'10',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'10',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'10',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'10',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'10',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'10',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'10',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'10',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'10',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'10',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'10',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'10',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'10',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'10',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'10',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'10',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'10',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'10',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'10',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'10',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'10',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'10',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'10',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'10',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'10',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'10',south:'up',west:'up'}}"
		);
		method_21601(
			891,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'11',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'11',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'11',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'11',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'11',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'11',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'11',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'11',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'11',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'11',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'11',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'11',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'11',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'11',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'11',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'11',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'11',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'11',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'11',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'11',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'11',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'11',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'11',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'11',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'11',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'11',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'11',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'11',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'11',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'11',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'11',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'11',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'11',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'11',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'11',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'11',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'11',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'11',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'11',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'11',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'11',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'11',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'11',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'11',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'11',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'11',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'11',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'11',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'11',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'11',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'11',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'11',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'11',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'11',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'11',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'11',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'11',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'11',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'11',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'11',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'11',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'11',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'11',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'11',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'11',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'11',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'11',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'11',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'11',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'11',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'11',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'11',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'11',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'11',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'11',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'11',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'11',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'11',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'11',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'11',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'11',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'11',south:'up',west:'up'}}"
		);
		method_21601(
			892,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'12',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'12',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'12',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'12',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'12',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'12',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'12',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'12',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'12',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'12',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'12',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'12',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'12',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'12',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'12',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'12',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'12',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'12',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'12',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'12',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'12',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'12',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'12',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'12',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'12',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'12',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'12',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'12',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'12',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'12',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'12',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'12',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'12',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'12',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'12',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'12',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'12',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'12',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'12',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'12',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'12',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'12',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'12',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'12',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'12',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'12',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'12',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'12',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'12',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'12',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'12',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'12',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'12',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'12',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'12',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'12',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'12',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'12',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'12',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'12',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'12',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'12',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'12',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'12',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'12',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'12',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'12',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'12',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'12',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'12',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'12',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'12',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'12',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'12',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'12',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'12',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'12',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'12',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'12',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'12',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'12',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'12',south:'up',west:'up'}}"
		);
		method_21601(
			893,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'13',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'13',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'13',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'13',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'13',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'13',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'13',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'13',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'13',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'13',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'13',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'13',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'13',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'13',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'13',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'13',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'13',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'13',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'13',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'13',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'13',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'13',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'13',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'13',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'13',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'13',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'13',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'13',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'13',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'13',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'13',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'13',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'13',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'13',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'13',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'13',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'13',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'13',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'13',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'13',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'13',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'13',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'13',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'13',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'13',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'13',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'13',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'13',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'13',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'13',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'13',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'13',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'13',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'13',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'13',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'13',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'13',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'13',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'13',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'13',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'13',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'13',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'13',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'13',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'13',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'13',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'13',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'13',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'13',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'13',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'13',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'13',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'13',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'13',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'13',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'13',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'13',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'13',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'13',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'13',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'13',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'13',south:'up',west:'up'}}"
		);
		method_21601(
			894,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'14',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'14',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'14',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'14',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'14',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'14',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'14',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'14',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'14',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'14',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'14',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'14',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'14',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'14',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'14',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'14',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'14',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'14',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'14',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'14',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'14',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'14',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'14',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'14',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'14',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'14',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'14',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'14',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'14',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'14',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'14',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'14',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'14',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'14',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'14',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'14',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'14',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'14',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'14',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'14',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'14',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'14',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'14',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'14',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'14',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'14',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'14',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'14',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'14',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'14',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'14',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'14',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'14',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'14',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'14',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'14',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'14',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'14',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'14',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'14',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'14',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'14',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'14',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'14',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'14',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'14',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'14',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'14',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'14',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'14',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'14',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'14',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'14',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'14',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'14',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'14',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'14',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'14',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'14',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'14',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'14',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'14',south:'up',west:'up'}}"
		);
		method_21601(
			895,
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'15',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'15',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'15',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'15',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'15',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'15',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'15',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'15',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'15',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'none',power:'15',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'15',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'15',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'15',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'15',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'15',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'15',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'15',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'15',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'side',power:'15',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'15',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'15',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'15',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'15',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'15',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'15',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'15',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'15',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'none',north:'up',power:'15',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'15',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'15',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'15',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'15',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'15',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'15',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'15',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'15',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'none',power:'15',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'15',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'15',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'15',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'15',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'15',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'15',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'15',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'15',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'side',power:'15',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'15',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'15',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'15',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'15',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'15',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'15',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'15',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'15',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'side',north:'up',power:'15',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'15',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'15',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'15',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'15',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'15',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'15',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'15',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'15',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'none',power:'15',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'15',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'15',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'15',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'15',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'15',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'15',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'15',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'15',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'side',power:'15',south:'up',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'15',south:'none',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'15',south:'none',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'15',south:'none',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'15',south:'side',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'15',south:'side',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'15',south:'side',west:'up'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'15',south:'up',west:'none'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'15',south:'up',west:'side'}}",
			"{Name:'minecraft:redstone_wire',Properties:{east:'up',north:'up',power:'15',south:'up',west:'up'}}"
		);
		method_21601(896, "{Name:'minecraft:diamond_ore'}", "{Name:'minecraft:diamond_ore'}");
		method_21601(912, "{Name:'minecraft:diamond_block'}", "{Name:'minecraft:diamond_block'}");
		method_21601(928, "{Name:'minecraft:crafting_table'}", "{Name:'minecraft:crafting_table'}");
		method_21601(944, "{Name:'minecraft:wheat',Properties:{age:'0'}}", "{Name:'minecraft:wheat',Properties:{age:'0'}}");
		method_21601(945, "{Name:'minecraft:wheat',Properties:{age:'1'}}", "{Name:'minecraft:wheat',Properties:{age:'1'}}");
		method_21601(946, "{Name:'minecraft:wheat',Properties:{age:'2'}}", "{Name:'minecraft:wheat',Properties:{age:'2'}}");
		method_21601(947, "{Name:'minecraft:wheat',Properties:{age:'3'}}", "{Name:'minecraft:wheat',Properties:{age:'3'}}");
		method_21601(948, "{Name:'minecraft:wheat',Properties:{age:'4'}}", "{Name:'minecraft:wheat',Properties:{age:'4'}}");
		method_21601(949, "{Name:'minecraft:wheat',Properties:{age:'5'}}", "{Name:'minecraft:wheat',Properties:{age:'5'}}");
		method_21601(950, "{Name:'minecraft:wheat',Properties:{age:'6'}}", "{Name:'minecraft:wheat',Properties:{age:'6'}}");
		method_21601(951, "{Name:'minecraft:wheat',Properties:{age:'7'}}", "{Name:'minecraft:wheat',Properties:{age:'7'}}");
		method_21601(960, "{Name:'minecraft:farmland',Properties:{moisture:'0'}}", "{Name:'minecraft:farmland',Properties:{moisture:'0'}}");
		method_21601(961, "{Name:'minecraft:farmland',Properties:{moisture:'1'}}", "{Name:'minecraft:farmland',Properties:{moisture:'1'}}");
		method_21601(962, "{Name:'minecraft:farmland',Properties:{moisture:'2'}}", "{Name:'minecraft:farmland',Properties:{moisture:'2'}}");
		method_21601(963, "{Name:'minecraft:farmland',Properties:{moisture:'3'}}", "{Name:'minecraft:farmland',Properties:{moisture:'3'}}");
		method_21601(964, "{Name:'minecraft:farmland',Properties:{moisture:'4'}}", "{Name:'minecraft:farmland',Properties:{moisture:'4'}}");
		method_21601(965, "{Name:'minecraft:farmland',Properties:{moisture:'5'}}", "{Name:'minecraft:farmland',Properties:{moisture:'5'}}");
		method_21601(966, "{Name:'minecraft:farmland',Properties:{moisture:'6'}}", "{Name:'minecraft:farmland',Properties:{moisture:'6'}}");
		method_21601(967, "{Name:'minecraft:farmland',Properties:{moisture:'7'}}", "{Name:'minecraft:farmland',Properties:{moisture:'7'}}");
		method_21601(978, "{Name:'minecraft:furnace',Properties:{facing:'north',lit:'false'}}", "{Name:'minecraft:furnace',Properties:{facing:'north'}}");
		method_21601(979, "{Name:'minecraft:furnace',Properties:{facing:'south',lit:'false'}}", "{Name:'minecraft:furnace',Properties:{facing:'south'}}");
		method_21601(980, "{Name:'minecraft:furnace',Properties:{facing:'west',lit:'false'}}", "{Name:'minecraft:furnace',Properties:{facing:'west'}}");
		method_21601(981, "{Name:'minecraft:furnace',Properties:{facing:'east',lit:'false'}}", "{Name:'minecraft:furnace',Properties:{facing:'east'}}");
		method_21601(994, "{Name:'minecraft:furnace',Properties:{facing:'north',lit:'true'}}", "{Name:'minecraft:lit_furnace',Properties:{facing:'north'}}");
		method_21601(995, "{Name:'minecraft:furnace',Properties:{facing:'south',lit:'true'}}", "{Name:'minecraft:lit_furnace',Properties:{facing:'south'}}");
		method_21601(996, "{Name:'minecraft:furnace',Properties:{facing:'west',lit:'true'}}", "{Name:'minecraft:lit_furnace',Properties:{facing:'west'}}");
		method_21601(997, "{Name:'minecraft:furnace',Properties:{facing:'east',lit:'true'}}", "{Name:'minecraft:lit_furnace',Properties:{facing:'east'}}");
		method_21601(1008, "{Name:'minecraft:sign',Properties:{rotation:'0'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'0'}}");
		method_21601(1009, "{Name:'minecraft:sign',Properties:{rotation:'1'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'1'}}");
		method_21601(1010, "{Name:'minecraft:sign',Properties:{rotation:'2'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'2'}}");
		method_21601(1011, "{Name:'minecraft:sign',Properties:{rotation:'3'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'3'}}");
		method_21601(1012, "{Name:'minecraft:sign',Properties:{rotation:'4'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'4'}}");
		method_21601(1013, "{Name:'minecraft:sign',Properties:{rotation:'5'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'5'}}");
		method_21601(1014, "{Name:'minecraft:sign',Properties:{rotation:'6'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'6'}}");
		method_21601(1015, "{Name:'minecraft:sign',Properties:{rotation:'7'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'7'}}");
		method_21601(1016, "{Name:'minecraft:sign',Properties:{rotation:'8'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'8'}}");
		method_21601(1017, "{Name:'minecraft:sign',Properties:{rotation:'9'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'9'}}");
		method_21601(1018, "{Name:'minecraft:sign',Properties:{rotation:'10'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'10'}}");
		method_21601(1019, "{Name:'minecraft:sign',Properties:{rotation:'11'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'11'}}");
		method_21601(1020, "{Name:'minecraft:sign',Properties:{rotation:'12'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'12'}}");
		method_21601(1021, "{Name:'minecraft:sign',Properties:{rotation:'13'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'13'}}");
		method_21601(1022, "{Name:'minecraft:sign',Properties:{rotation:'14'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'14'}}");
		method_21601(1023, "{Name:'minecraft:sign',Properties:{rotation:'15'}}", "{Name:'minecraft:standing_sign',Properties:{rotation:'15'}}");
		method_21601(
			1024,
			"{Name:'minecraft:oak_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			1025,
			"{Name:'minecraft:oak_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			1026,
			"{Name:'minecraft:oak_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			1027,
			"{Name:'minecraft:oak_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			1028,
			"{Name:'minecraft:oak_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			1029,
			"{Name:'minecraft:oak_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			1030,
			"{Name:'minecraft:oak_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			1031,
			"{Name:'minecraft:oak_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			1032,
			"{Name:'minecraft:oak_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"
		);
		method_21601(
			1033,
			"{Name:'minecraft:oak_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"
		);
		method_21601(
			1034,
			"{Name:'minecraft:oak_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"
		);
		method_21601(
			1035,
			"{Name:'minecraft:oak_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:wooden_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(1036, "{Name:'minecraft:oak_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}");
		method_21601(1037, "{Name:'minecraft:oak_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}");
		method_21601(1038, "{Name:'minecraft:oak_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}");
		method_21601(1039, "{Name:'minecraft:oak_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}");
		method_21601(1042, "{Name:'minecraft:ladder',Properties:{facing:'north'}}", "{Name:'minecraft:ladder',Properties:{facing:'north'}}");
		method_21601(1043, "{Name:'minecraft:ladder',Properties:{facing:'south'}}", "{Name:'minecraft:ladder',Properties:{facing:'south'}}");
		method_21601(1044, "{Name:'minecraft:ladder',Properties:{facing:'west'}}", "{Name:'minecraft:ladder',Properties:{facing:'west'}}");
		method_21601(1045, "{Name:'minecraft:ladder',Properties:{facing:'east'}}", "{Name:'minecraft:ladder',Properties:{facing:'east'}}");
		method_21601(1056, "{Name:'minecraft:rail',Properties:{shape:'north_south'}}", "{Name:'minecraft:rail',Properties:{shape:'north_south'}}");
		method_21601(1057, "{Name:'minecraft:rail',Properties:{shape:'east_west'}}", "{Name:'minecraft:rail',Properties:{shape:'east_west'}}");
		method_21601(1058, "{Name:'minecraft:rail',Properties:{shape:'ascending_east'}}", "{Name:'minecraft:rail',Properties:{shape:'ascending_east'}}");
		method_21601(1059, "{Name:'minecraft:rail',Properties:{shape:'ascending_west'}}", "{Name:'minecraft:rail',Properties:{shape:'ascending_west'}}");
		method_21601(1060, "{Name:'minecraft:rail',Properties:{shape:'ascending_north'}}", "{Name:'minecraft:rail',Properties:{shape:'ascending_north'}}");
		method_21601(1061, "{Name:'minecraft:rail',Properties:{shape:'ascending_south'}}", "{Name:'minecraft:rail',Properties:{shape:'ascending_south'}}");
		method_21601(1062, "{Name:'minecraft:rail',Properties:{shape:'south_east'}}", "{Name:'minecraft:rail',Properties:{shape:'south_east'}}");
		method_21601(1063, "{Name:'minecraft:rail',Properties:{shape:'south_west'}}", "{Name:'minecraft:rail',Properties:{shape:'south_west'}}");
		method_21601(1064, "{Name:'minecraft:rail',Properties:{shape:'north_west'}}", "{Name:'minecraft:rail',Properties:{shape:'north_west'}}");
		method_21601(1065, "{Name:'minecraft:rail',Properties:{shape:'north_east'}}", "{Name:'minecraft:rail',Properties:{shape:'north_east'}}");
		method_21601(
			1072,
			"{Name:'minecraft:cobblestone_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1073,
			"{Name:'minecraft:cobblestone_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1074,
			"{Name:'minecraft:cobblestone_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1075,
			"{Name:'minecraft:cobblestone_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1076,
			"{Name:'minecraft:cobblestone_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			1077,
			"{Name:'minecraft:cobblestone_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			1078,
			"{Name:'minecraft:cobblestone_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			1079,
			"{Name:'minecraft:cobblestone_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:stone_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(1090, "{Name:'minecraft:wall_sign',Properties:{facing:'north'}}", "{Name:'minecraft:wall_sign',Properties:{facing:'north'}}");
		method_21601(1091, "{Name:'minecraft:wall_sign',Properties:{facing:'south'}}", "{Name:'minecraft:wall_sign',Properties:{facing:'south'}}");
		method_21601(1092, "{Name:'minecraft:wall_sign',Properties:{facing:'west'}}", "{Name:'minecraft:wall_sign',Properties:{facing:'west'}}");
		method_21601(1093, "{Name:'minecraft:wall_sign',Properties:{facing:'east'}}", "{Name:'minecraft:wall_sign',Properties:{facing:'east'}}");
		method_21601(
			1104,
			"{Name:'minecraft:lever',Properties:{face:'ceiling',facing:'west',powered:'false'}}",
			"{Name:'minecraft:lever',Properties:{facing:'down_x',powered:'false'}}"
		);
		method_21601(
			1105,
			"{Name:'minecraft:lever',Properties:{face:'wall',facing:'east',powered:'false'}}",
			"{Name:'minecraft:lever',Properties:{facing:'east',powered:'false'}}"
		);
		method_21601(
			1106,
			"{Name:'minecraft:lever',Properties:{face:'wall',facing:'west',powered:'false'}}",
			"{Name:'minecraft:lever',Properties:{facing:'west',powered:'false'}}"
		);
		method_21601(
			1107,
			"{Name:'minecraft:lever',Properties:{face:'wall',facing:'south',powered:'false'}}",
			"{Name:'minecraft:lever',Properties:{facing:'south',powered:'false'}}"
		);
		method_21601(
			1108,
			"{Name:'minecraft:lever',Properties:{face:'wall',facing:'north',powered:'false'}}",
			"{Name:'minecraft:lever',Properties:{facing:'north',powered:'false'}}"
		);
		method_21601(
			1109,
			"{Name:'minecraft:lever',Properties:{face:'floor',facing:'north',powered:'false'}}",
			"{Name:'minecraft:lever',Properties:{facing:'up_z',powered:'false'}}"
		);
		method_21601(
			1110,
			"{Name:'minecraft:lever',Properties:{face:'floor',facing:'west',powered:'false'}}",
			"{Name:'minecraft:lever',Properties:{facing:'up_x',powered:'false'}}"
		);
		method_21601(
			1111,
			"{Name:'minecraft:lever',Properties:{face:'ceiling',facing:'north',powered:'false'}}",
			"{Name:'minecraft:lever',Properties:{facing:'down_z',powered:'false'}}"
		);
		method_21601(
			1112,
			"{Name:'minecraft:lever',Properties:{face:'ceiling',facing:'west',powered:'true'}}",
			"{Name:'minecraft:lever',Properties:{facing:'down_x',powered:'true'}}"
		);
		method_21601(
			1113, "{Name:'minecraft:lever',Properties:{face:'wall',facing:'east',powered:'true'}}", "{Name:'minecraft:lever',Properties:{facing:'east',powered:'true'}}"
		);
		method_21601(
			1114, "{Name:'minecraft:lever',Properties:{face:'wall',facing:'west',powered:'true'}}", "{Name:'minecraft:lever',Properties:{facing:'west',powered:'true'}}"
		);
		method_21601(
			1115,
			"{Name:'minecraft:lever',Properties:{face:'wall',facing:'south',powered:'true'}}",
			"{Name:'minecraft:lever',Properties:{facing:'south',powered:'true'}}"
		);
		method_21601(
			1116,
			"{Name:'minecraft:lever',Properties:{face:'wall',facing:'north',powered:'true'}}",
			"{Name:'minecraft:lever',Properties:{facing:'north',powered:'true'}}"
		);
		method_21601(
			1117,
			"{Name:'minecraft:lever',Properties:{face:'floor',facing:'north',powered:'true'}}",
			"{Name:'minecraft:lever',Properties:{facing:'up_z',powered:'true'}}"
		);
		method_21601(
			1118,
			"{Name:'minecraft:lever',Properties:{face:'floor',facing:'west',powered:'true'}}",
			"{Name:'minecraft:lever',Properties:{facing:'up_x',powered:'true'}}"
		);
		method_21601(
			1119,
			"{Name:'minecraft:lever',Properties:{face:'ceiling',facing:'north',powered:'true'}}",
			"{Name:'minecraft:lever',Properties:{facing:'down_z',powered:'true'}}"
		);
		method_21601(
			1120, "{Name:'minecraft:stone_pressure_plate',Properties:{powered:'false'}}", "{Name:'minecraft:stone_pressure_plate',Properties:{powered:'false'}}"
		);
		method_21601(
			1121, "{Name:'minecraft:stone_pressure_plate',Properties:{powered:'true'}}", "{Name:'minecraft:stone_pressure_plate',Properties:{powered:'true'}}"
		);
		method_21601(
			1136,
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			1137,
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			1138,
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			1139,
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			1140,
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			1141,
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			1142,
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			1143,
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			1144,
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"
		);
		method_21601(
			1145,
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"
		);
		method_21601(
			1146,
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"
		);
		method_21601(
			1147,
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:iron_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(1148, "{Name:'minecraft:iron_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}");
		method_21601(1149, "{Name:'minecraft:iron_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}");
		method_21601(1150, "{Name:'minecraft:iron_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}");
		method_21601(1151, "{Name:'minecraft:iron_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}");
		method_21601(
			1152, "{Name:'minecraft:oak_pressure_plate',Properties:{powered:'false'}}", "{Name:'minecraft:wooden_pressure_plate',Properties:{powered:'false'}}"
		);
		method_21601(
			1153, "{Name:'minecraft:oak_pressure_plate',Properties:{powered:'true'}}", "{Name:'minecraft:wooden_pressure_plate',Properties:{powered:'true'}}"
		);
		method_21601(1168, "{Name:'minecraft:redstone_ore',Properties:{lit:'false'}}", "{Name:'minecraft:redstone_ore'}");
		method_21601(1184, "{Name:'minecraft:redstone_ore',Properties:{lit:'true'}}", "{Name:'minecraft:lit_redstone_ore'}");
		method_21601(
			1201, "{Name:'minecraft:redstone_wall_torch',Properties:{facing:'east',lit:'false'}}", "{Name:'minecraft:unlit_redstone_torch',Properties:{facing:'east'}}"
		);
		method_21601(
			1202, "{Name:'minecraft:redstone_wall_torch',Properties:{facing:'west',lit:'false'}}", "{Name:'minecraft:unlit_redstone_torch',Properties:{facing:'west'}}"
		);
		method_21601(
			1203,
			"{Name:'minecraft:redstone_wall_torch',Properties:{facing:'south',lit:'false'}}",
			"{Name:'minecraft:unlit_redstone_torch',Properties:{facing:'south'}}"
		);
		method_21601(
			1204,
			"{Name:'minecraft:redstone_wall_torch',Properties:{facing:'north',lit:'false'}}",
			"{Name:'minecraft:unlit_redstone_torch',Properties:{facing:'north'}}"
		);
		method_21601(1205, "{Name:'minecraft:redstone_torch',Properties:{lit:'false'}}", "{Name:'minecraft:unlit_redstone_torch',Properties:{facing:'up'}}");
		method_21601(
			1217, "{Name:'minecraft:redstone_wall_torch',Properties:{facing:'east',lit:'true'}}", "{Name:'minecraft:redstone_torch',Properties:{facing:'east'}}"
		);
		method_21601(
			1218, "{Name:'minecraft:redstone_wall_torch',Properties:{facing:'west',lit:'true'}}", "{Name:'minecraft:redstone_torch',Properties:{facing:'west'}}"
		);
		method_21601(
			1219, "{Name:'minecraft:redstone_wall_torch',Properties:{facing:'south',lit:'true'}}", "{Name:'minecraft:redstone_torch',Properties:{facing:'south'}}"
		);
		method_21601(
			1220, "{Name:'minecraft:redstone_wall_torch',Properties:{facing:'north',lit:'true'}}", "{Name:'minecraft:redstone_torch',Properties:{facing:'north'}}"
		);
		method_21601(1221, "{Name:'minecraft:redstone_torch',Properties:{lit:'true'}}", "{Name:'minecraft:redstone_torch',Properties:{facing:'up'}}");
		method_21601(
			1232,
			"{Name:'minecraft:stone_button',Properties:{face:'ceiling',facing:'north',powered:'false'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'down',powered:'false'}}"
		);
		method_21601(
			1233,
			"{Name:'minecraft:stone_button',Properties:{face:'wall',facing:'east',powered:'false'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'east',powered:'false'}}"
		);
		method_21601(
			1234,
			"{Name:'minecraft:stone_button',Properties:{face:'wall',facing:'west',powered:'false'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'west',powered:'false'}}"
		);
		method_21601(
			1235,
			"{Name:'minecraft:stone_button',Properties:{face:'wall',facing:'south',powered:'false'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'south',powered:'false'}}"
		);
		method_21601(
			1236,
			"{Name:'minecraft:stone_button',Properties:{face:'wall',facing:'north',powered:'false'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'north',powered:'false'}}"
		);
		method_21601(
			1237,
			"{Name:'minecraft:stone_button',Properties:{face:'floor',facing:'north',powered:'false'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'up',powered:'false'}}"
		);
		method_21601(
			1240,
			"{Name:'minecraft:stone_button',Properties:{face:'ceiling',facing:'north',powered:'true'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'down',powered:'true'}}"
		);
		method_21601(
			1241,
			"{Name:'minecraft:stone_button',Properties:{face:'wall',facing:'east',powered:'true'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'east',powered:'true'}}"
		);
		method_21601(
			1242,
			"{Name:'minecraft:stone_button',Properties:{face:'wall',facing:'west',powered:'true'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'west',powered:'true'}}"
		);
		method_21601(
			1243,
			"{Name:'minecraft:stone_button',Properties:{face:'wall',facing:'south',powered:'true'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'south',powered:'true'}}"
		);
		method_21601(
			1244,
			"{Name:'minecraft:stone_button',Properties:{face:'wall',facing:'north',powered:'true'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'north',powered:'true'}}"
		);
		method_21601(
			1245,
			"{Name:'minecraft:stone_button',Properties:{face:'floor',facing:'north',powered:'true'}}",
			"{Name:'minecraft:stone_button',Properties:{facing:'up',powered:'true'}}"
		);
		method_21601(1248, "{Name:'minecraft:snow',Properties:{layers:'1'}}", "{Name:'minecraft:snow_layer',Properties:{layers:'1'}}");
		method_21601(1249, "{Name:'minecraft:snow',Properties:{layers:'2'}}", "{Name:'minecraft:snow_layer',Properties:{layers:'2'}}");
		method_21601(1250, "{Name:'minecraft:snow',Properties:{layers:'3'}}", "{Name:'minecraft:snow_layer',Properties:{layers:'3'}}");
		method_21601(1251, "{Name:'minecraft:snow',Properties:{layers:'4'}}", "{Name:'minecraft:snow_layer',Properties:{layers:'4'}}");
		method_21601(1252, "{Name:'minecraft:snow',Properties:{layers:'5'}}", "{Name:'minecraft:snow_layer',Properties:{layers:'5'}}");
		method_21601(1253, "{Name:'minecraft:snow',Properties:{layers:'6'}}", "{Name:'minecraft:snow_layer',Properties:{layers:'6'}}");
		method_21601(1254, "{Name:'minecraft:snow',Properties:{layers:'7'}}", "{Name:'minecraft:snow_layer',Properties:{layers:'7'}}");
		method_21601(1255, "{Name:'minecraft:snow',Properties:{layers:'8'}}", "{Name:'minecraft:snow_layer',Properties:{layers:'8'}}");
		method_21601(1264, "{Name:'minecraft:ice'}", "{Name:'minecraft:ice'}");
		method_21601(1280, "{Name:'minecraft:snow_block'}", "{Name:'minecraft:snow'}");
		method_21601(1296, "{Name:'minecraft:cactus',Properties:{age:'0'}}", "{Name:'minecraft:cactus',Properties:{age:'0'}}");
		method_21601(1297, "{Name:'minecraft:cactus',Properties:{age:'1'}}", "{Name:'minecraft:cactus',Properties:{age:'1'}}");
		method_21601(1298, "{Name:'minecraft:cactus',Properties:{age:'2'}}", "{Name:'minecraft:cactus',Properties:{age:'2'}}");
		method_21601(1299, "{Name:'minecraft:cactus',Properties:{age:'3'}}", "{Name:'minecraft:cactus',Properties:{age:'3'}}");
		method_21601(1300, "{Name:'minecraft:cactus',Properties:{age:'4'}}", "{Name:'minecraft:cactus',Properties:{age:'4'}}");
		method_21601(1301, "{Name:'minecraft:cactus',Properties:{age:'5'}}", "{Name:'minecraft:cactus',Properties:{age:'5'}}");
		method_21601(1302, "{Name:'minecraft:cactus',Properties:{age:'6'}}", "{Name:'minecraft:cactus',Properties:{age:'6'}}");
		method_21601(1303, "{Name:'minecraft:cactus',Properties:{age:'7'}}", "{Name:'minecraft:cactus',Properties:{age:'7'}}");
		method_21601(1304, "{Name:'minecraft:cactus',Properties:{age:'8'}}", "{Name:'minecraft:cactus',Properties:{age:'8'}}");
		method_21601(1305, "{Name:'minecraft:cactus',Properties:{age:'9'}}", "{Name:'minecraft:cactus',Properties:{age:'9'}}");
		method_21601(1306, "{Name:'minecraft:cactus',Properties:{age:'10'}}", "{Name:'minecraft:cactus',Properties:{age:'10'}}");
		method_21601(1307, "{Name:'minecraft:cactus',Properties:{age:'11'}}", "{Name:'minecraft:cactus',Properties:{age:'11'}}");
		method_21601(1308, "{Name:'minecraft:cactus',Properties:{age:'12'}}", "{Name:'minecraft:cactus',Properties:{age:'12'}}");
		method_21601(1309, "{Name:'minecraft:cactus',Properties:{age:'13'}}", "{Name:'minecraft:cactus',Properties:{age:'13'}}");
		method_21601(1310, "{Name:'minecraft:cactus',Properties:{age:'14'}}", "{Name:'minecraft:cactus',Properties:{age:'14'}}");
		method_21601(1311, "{Name:'minecraft:cactus',Properties:{age:'15'}}", "{Name:'minecraft:cactus',Properties:{age:'15'}}");
		method_21601(1312, "{Name:'minecraft:clay'}", "{Name:'minecraft:clay'}");
		method_21601(1328, "{Name:'minecraft:sugar_cane',Properties:{age:'0'}}", "{Name:'minecraft:reeds',Properties:{age:'0'}}");
		method_21601(1329, "{Name:'minecraft:sugar_cane',Properties:{age:'1'}}", "{Name:'minecraft:reeds',Properties:{age:'1'}}");
		method_21601(1330, "{Name:'minecraft:sugar_cane',Properties:{age:'2'}}", "{Name:'minecraft:reeds',Properties:{age:'2'}}");
		method_21601(1331, "{Name:'minecraft:sugar_cane',Properties:{age:'3'}}", "{Name:'minecraft:reeds',Properties:{age:'3'}}");
		method_21601(1332, "{Name:'minecraft:sugar_cane',Properties:{age:'4'}}", "{Name:'minecraft:reeds',Properties:{age:'4'}}");
		method_21601(1333, "{Name:'minecraft:sugar_cane',Properties:{age:'5'}}", "{Name:'minecraft:reeds',Properties:{age:'5'}}");
		method_21601(1334, "{Name:'minecraft:sugar_cane',Properties:{age:'6'}}", "{Name:'minecraft:reeds',Properties:{age:'6'}}");
		method_21601(1335, "{Name:'minecraft:sugar_cane',Properties:{age:'7'}}", "{Name:'minecraft:reeds',Properties:{age:'7'}}");
		method_21601(1336, "{Name:'minecraft:sugar_cane',Properties:{age:'8'}}", "{Name:'minecraft:reeds',Properties:{age:'8'}}");
		method_21601(1337, "{Name:'minecraft:sugar_cane',Properties:{age:'9'}}", "{Name:'minecraft:reeds',Properties:{age:'9'}}");
		method_21601(1338, "{Name:'minecraft:sugar_cane',Properties:{age:'10'}}", "{Name:'minecraft:reeds',Properties:{age:'10'}}");
		method_21601(1339, "{Name:'minecraft:sugar_cane',Properties:{age:'11'}}", "{Name:'minecraft:reeds',Properties:{age:'11'}}");
		method_21601(1340, "{Name:'minecraft:sugar_cane',Properties:{age:'12'}}", "{Name:'minecraft:reeds',Properties:{age:'12'}}");
		method_21601(1341, "{Name:'minecraft:sugar_cane',Properties:{age:'13'}}", "{Name:'minecraft:reeds',Properties:{age:'13'}}");
		method_21601(1342, "{Name:'minecraft:sugar_cane',Properties:{age:'14'}}", "{Name:'minecraft:reeds',Properties:{age:'14'}}");
		method_21601(1343, "{Name:'minecraft:sugar_cane',Properties:{age:'15'}}", "{Name:'minecraft:reeds',Properties:{age:'15'}}");
		method_21601(1344, "{Name:'minecraft:jukebox',Properties:{has_record:'false'}}", "{Name:'minecraft:jukebox',Properties:{has_record:'false'}}");
		method_21601(1345, "{Name:'minecraft:jukebox',Properties:{has_record:'true'}}", "{Name:'minecraft:jukebox',Properties:{has_record:'true'}}");
		method_21601(
			1360,
			"{Name:'minecraft:oak_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:fence',Properties:{east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:fence',Properties:{east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:fence',Properties:{east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:fence',Properties:{east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:fence',Properties:{east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:fence',Properties:{east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:fence',Properties:{east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:fence',Properties:{east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:fence',Properties:{east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:fence',Properties:{east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:fence',Properties:{east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:fence',Properties:{east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:fence',Properties:{east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:fence',Properties:{east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:fence',Properties:{east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(1376, "{Name:'minecraft:carved_pumpkin',Properties:{facing:'south'}}", "{Name:'minecraft:pumpkin',Properties:{facing:'south'}}");
		method_21601(1377, "{Name:'minecraft:carved_pumpkin',Properties:{facing:'west'}}", "{Name:'minecraft:pumpkin',Properties:{facing:'west'}}");
		method_21601(1378, "{Name:'minecraft:carved_pumpkin',Properties:{facing:'north'}}", "{Name:'minecraft:pumpkin',Properties:{facing:'north'}}");
		method_21601(1379, "{Name:'minecraft:carved_pumpkin',Properties:{facing:'east'}}", "{Name:'minecraft:pumpkin',Properties:{facing:'east'}}");
		method_21601(1392, "{Name:'minecraft:netherrack'}", "{Name:'minecraft:netherrack'}");
		method_21601(1408, "{Name:'minecraft:soul_sand'}", "{Name:'minecraft:soul_sand'}");
		method_21601(1424, "{Name:'minecraft:glowstone'}", "{Name:'minecraft:glowstone'}");
		method_21601(1441, "{Name:'minecraft:portal',Properties:{axis:'x'}}", "{Name:'minecraft:portal',Properties:{axis:'x'}}");
		method_21601(1442, "{Name:'minecraft:portal',Properties:{axis:'z'}}", "{Name:'minecraft:portal',Properties:{axis:'z'}}");
		method_21601(1456, "{Name:'minecraft:jack_o_lantern',Properties:{facing:'south'}}", "{Name:'minecraft:lit_pumpkin',Properties:{facing:'south'}}");
		method_21601(1457, "{Name:'minecraft:jack_o_lantern',Properties:{facing:'west'}}", "{Name:'minecraft:lit_pumpkin',Properties:{facing:'west'}}");
		method_21601(1458, "{Name:'minecraft:jack_o_lantern',Properties:{facing:'north'}}", "{Name:'minecraft:lit_pumpkin',Properties:{facing:'north'}}");
		method_21601(1459, "{Name:'minecraft:jack_o_lantern',Properties:{facing:'east'}}", "{Name:'minecraft:lit_pumpkin',Properties:{facing:'east'}}");
		method_21601(1472, "{Name:'minecraft:cake',Properties:{bites:'0'}}", "{Name:'minecraft:cake',Properties:{bites:'0'}}");
		method_21601(1473, "{Name:'minecraft:cake',Properties:{bites:'1'}}", "{Name:'minecraft:cake',Properties:{bites:'1'}}");
		method_21601(1474, "{Name:'minecraft:cake',Properties:{bites:'2'}}", "{Name:'minecraft:cake',Properties:{bites:'2'}}");
		method_21601(1475, "{Name:'minecraft:cake',Properties:{bites:'3'}}", "{Name:'minecraft:cake',Properties:{bites:'3'}}");
		method_21601(1476, "{Name:'minecraft:cake',Properties:{bites:'4'}}", "{Name:'minecraft:cake',Properties:{bites:'4'}}");
		method_21601(1477, "{Name:'minecraft:cake',Properties:{bites:'5'}}", "{Name:'minecraft:cake',Properties:{bites:'5'}}");
		method_21601(1478, "{Name:'minecraft:cake',Properties:{bites:'6'}}", "{Name:'minecraft:cake',Properties:{bites:'6'}}");
		method_21601(
			1488,
			"{Name:'minecraft:repeater',Properties:{delay:'1',facing:'south',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'1',facing:'south',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'1',facing:'south',locked:'true'}}"
		);
		method_21601(
			1489,
			"{Name:'minecraft:repeater',Properties:{delay:'1',facing:'west',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'1',facing:'west',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'1',facing:'west',locked:'true'}}"
		);
		method_21601(
			1490,
			"{Name:'minecraft:repeater',Properties:{delay:'1',facing:'north',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'1',facing:'north',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'1',facing:'north',locked:'true'}}"
		);
		method_21601(
			1491,
			"{Name:'minecraft:repeater',Properties:{delay:'1',facing:'east',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'1',facing:'east',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'1',facing:'east',locked:'true'}}"
		);
		method_21601(
			1492,
			"{Name:'minecraft:repeater',Properties:{delay:'2',facing:'south',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'2',facing:'south',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'2',facing:'south',locked:'true'}}"
		);
		method_21601(
			1493,
			"{Name:'minecraft:repeater',Properties:{delay:'2',facing:'west',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'2',facing:'west',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'2',facing:'west',locked:'true'}}"
		);
		method_21601(
			1494,
			"{Name:'minecraft:repeater',Properties:{delay:'2',facing:'north',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'2',facing:'north',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'2',facing:'north',locked:'true'}}"
		);
		method_21601(
			1495,
			"{Name:'minecraft:repeater',Properties:{delay:'2',facing:'east',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'2',facing:'east',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'2',facing:'east',locked:'true'}}"
		);
		method_21601(
			1496,
			"{Name:'minecraft:repeater',Properties:{delay:'3',facing:'south',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'3',facing:'south',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'3',facing:'south',locked:'true'}}"
		);
		method_21601(
			1497,
			"{Name:'minecraft:repeater',Properties:{delay:'3',facing:'west',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'3',facing:'west',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'3',facing:'west',locked:'true'}}"
		);
		method_21601(
			1498,
			"{Name:'minecraft:repeater',Properties:{delay:'3',facing:'north',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'3',facing:'north',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'3',facing:'north',locked:'true'}}"
		);
		method_21601(
			1499,
			"{Name:'minecraft:repeater',Properties:{delay:'3',facing:'east',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'3',facing:'east',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'3',facing:'east',locked:'true'}}"
		);
		method_21601(
			1500,
			"{Name:'minecraft:repeater',Properties:{delay:'4',facing:'south',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'4',facing:'south',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'4',facing:'south',locked:'true'}}"
		);
		method_21601(
			1501,
			"{Name:'minecraft:repeater',Properties:{delay:'4',facing:'west',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'4',facing:'west',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'4',facing:'west',locked:'true'}}"
		);
		method_21601(
			1502,
			"{Name:'minecraft:repeater',Properties:{delay:'4',facing:'north',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'4',facing:'north',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'4',facing:'north',locked:'true'}}"
		);
		method_21601(
			1503,
			"{Name:'minecraft:repeater',Properties:{delay:'4',facing:'east',locked:'false',powered:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'4',facing:'east',locked:'false'}}",
			"{Name:'minecraft:unpowered_repeater',Properties:{delay:'4',facing:'east',locked:'true'}}"
		);
		method_21601(
			1504,
			"{Name:'minecraft:repeater',Properties:{delay:'1',facing:'south',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'1',facing:'south',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'1',facing:'south',locked:'true'}}"
		);
		method_21601(
			1505,
			"{Name:'minecraft:repeater',Properties:{delay:'1',facing:'west',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'1',facing:'west',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'1',facing:'west',locked:'true'}}"
		);
		method_21601(
			1506,
			"{Name:'minecraft:repeater',Properties:{delay:'1',facing:'north',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'1',facing:'north',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'1',facing:'north',locked:'true'}}"
		);
		method_21601(
			1507,
			"{Name:'minecraft:repeater',Properties:{delay:'1',facing:'east',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'1',facing:'east',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'1',facing:'east',locked:'true'}}"
		);
		method_21601(
			1508,
			"{Name:'minecraft:repeater',Properties:{delay:'2',facing:'south',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'2',facing:'south',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'2',facing:'south',locked:'true'}}"
		);
		method_21601(
			1509,
			"{Name:'minecraft:repeater',Properties:{delay:'2',facing:'west',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'2',facing:'west',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'2',facing:'west',locked:'true'}}"
		);
		method_21601(
			1510,
			"{Name:'minecraft:repeater',Properties:{delay:'2',facing:'north',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'2',facing:'north',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'2',facing:'north',locked:'true'}}"
		);
		method_21601(
			1511,
			"{Name:'minecraft:repeater',Properties:{delay:'2',facing:'east',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'2',facing:'east',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'2',facing:'east',locked:'true'}}"
		);
		method_21601(
			1512,
			"{Name:'minecraft:repeater',Properties:{delay:'3',facing:'south',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'3',facing:'south',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'3',facing:'south',locked:'true'}}"
		);
		method_21601(
			1513,
			"{Name:'minecraft:repeater',Properties:{delay:'3',facing:'west',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'3',facing:'west',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'3',facing:'west',locked:'true'}}"
		);
		method_21601(
			1514,
			"{Name:'minecraft:repeater',Properties:{delay:'3',facing:'north',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'3',facing:'north',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'3',facing:'north',locked:'true'}}"
		);
		method_21601(
			1515,
			"{Name:'minecraft:repeater',Properties:{delay:'3',facing:'east',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'3',facing:'east',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'3',facing:'east',locked:'true'}}"
		);
		method_21601(
			1516,
			"{Name:'minecraft:repeater',Properties:{delay:'4',facing:'south',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'4',facing:'south',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'4',facing:'south',locked:'true'}}"
		);
		method_21601(
			1517,
			"{Name:'minecraft:repeater',Properties:{delay:'4',facing:'west',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'4',facing:'west',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'4',facing:'west',locked:'true'}}"
		);
		method_21601(
			1518,
			"{Name:'minecraft:repeater',Properties:{delay:'4',facing:'north',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'4',facing:'north',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'4',facing:'north',locked:'true'}}"
		);
		method_21601(
			1519,
			"{Name:'minecraft:repeater',Properties:{delay:'4',facing:'east',locked:'false',powered:'true'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'4',facing:'east',locked:'false'}}",
			"{Name:'minecraft:powered_repeater',Properties:{delay:'4',facing:'east',locked:'true'}}"
		);
		method_21601(1520, "{Name:'minecraft:white_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'white'}}");
		method_21601(1521, "{Name:'minecraft:orange_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'orange'}}");
		method_21601(1522, "{Name:'minecraft:magenta_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'magenta'}}");
		method_21601(1523, "{Name:'minecraft:light_blue_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'light_blue'}}");
		method_21601(1524, "{Name:'minecraft:yellow_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'yellow'}}");
		method_21601(1525, "{Name:'minecraft:lime_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'lime'}}");
		method_21601(1526, "{Name:'minecraft:pink_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'pink'}}");
		method_21601(1527, "{Name:'minecraft:gray_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'gray'}}");
		method_21601(1528, "{Name:'minecraft:light_gray_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'silver'}}");
		method_21601(1529, "{Name:'minecraft:cyan_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'cyan'}}");
		method_21601(1530, "{Name:'minecraft:purple_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'purple'}}");
		method_21601(1531, "{Name:'minecraft:blue_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'blue'}}");
		method_21601(1532, "{Name:'minecraft:brown_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'brown'}}");
		method_21601(1533, "{Name:'minecraft:green_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'green'}}");
		method_21601(1534, "{Name:'minecraft:red_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'red'}}");
		method_21601(1535, "{Name:'minecraft:black_stained_glass'}", "{Name:'minecraft:stained_glass',Properties:{color:'black'}}");
		method_21601(
			1536,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'north',half:'bottom',open:'false'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'north',half:'bottom',open:'false'}}"
		);
		method_21601(
			1537,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'south',half:'bottom',open:'false'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'south',half:'bottom',open:'false'}}"
		);
		method_21601(
			1538,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'west',half:'bottom',open:'false'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'west',half:'bottom',open:'false'}}"
		);
		method_21601(
			1539,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'east',half:'bottom',open:'false'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'east',half:'bottom',open:'false'}}"
		);
		method_21601(
			1540,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'north',half:'bottom',open:'true'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'north',half:'bottom',open:'true'}}"
		);
		method_21601(
			1541,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'south',half:'bottom',open:'true'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'south',half:'bottom',open:'true'}}"
		);
		method_21601(
			1542,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'west',half:'bottom',open:'true'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'west',half:'bottom',open:'true'}}"
		);
		method_21601(
			1543,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'east',half:'bottom',open:'true'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'east',half:'bottom',open:'true'}}"
		);
		method_21601(
			1544,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'north',half:'top',open:'false'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'north',half:'top',open:'false'}}"
		);
		method_21601(
			1545,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'south',half:'top',open:'false'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'south',half:'top',open:'false'}}"
		);
		method_21601(
			1546,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'west',half:'top',open:'false'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'west',half:'top',open:'false'}}"
		);
		method_21601(
			1547,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'east',half:'top',open:'false'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'east',half:'top',open:'false'}}"
		);
		method_21601(
			1548,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'north',half:'top',open:'true'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'north',half:'top',open:'true'}}"
		);
		method_21601(
			1549,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'south',half:'top',open:'true'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'south',half:'top',open:'true'}}"
		);
		method_21601(
			1550,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'west',half:'top',open:'true'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'west',half:'top',open:'true'}}"
		);
		method_21601(
			1551,
			"{Name:'minecraft:oak_trapdoor',Properties:{facing:'east',half:'top',open:'true'}}",
			"{Name:'minecraft:trapdoor',Properties:{facing:'east',half:'top',open:'true'}}"
		);
		method_21601(1552, "{Name:'minecraft:infested_stone'}", "{Name:'minecraft:monster_egg',Properties:{variant:'stone'}}");
		method_21601(1553, "{Name:'minecraft:infested_cobblestone'}", "{Name:'minecraft:monster_egg',Properties:{variant:'cobblestone'}}");
		method_21601(1554, "{Name:'minecraft:infested_stone_bricks'}", "{Name:'minecraft:monster_egg',Properties:{variant:'stone_brick'}}");
		method_21601(1555, "{Name:'minecraft:infested_mossy_stone_bricks'}", "{Name:'minecraft:monster_egg',Properties:{variant:'mossy_brick'}}");
		method_21601(1556, "{Name:'minecraft:infested_cracked_stone_bricks'}", "{Name:'minecraft:monster_egg',Properties:{variant:'cracked_brick'}}");
		method_21601(1557, "{Name:'minecraft:infested_chiseled_stone_bricks'}", "{Name:'minecraft:monster_egg',Properties:{variant:'chiseled_brick'}}");
		method_21601(1568, "{Name:'minecraft:stone_bricks'}", "{Name:'minecraft:stonebrick',Properties:{variant:'stonebrick'}}");
		method_21601(1569, "{Name:'minecraft:mossy_stone_bricks'}", "{Name:'minecraft:stonebrick',Properties:{variant:'mossy_stonebrick'}}");
		method_21601(1570, "{Name:'minecraft:cracked_stone_bricks'}", "{Name:'minecraft:stonebrick',Properties:{variant:'cracked_stonebrick'}}");
		method_21601(1571, "{Name:'minecraft:chiseled_stone_bricks'}", "{Name:'minecraft:stonebrick',Properties:{variant:'chiseled_stonebrick'}}");
		method_21601(
			1584,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'false',up:'false',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'all_inside'}}"
		);
		method_21601(
			1585,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'true',east:'false',south:'false',west:'true',up:'true',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'north_west'}}"
		);
		method_21601(
			1586,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'true',east:'false',south:'false',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'north'}}"
		);
		method_21601(
			1587,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'true',east:'true',south:'false',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'north_east'}}"
		);
		method_21601(
			1588,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'true',up:'true',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'west'}}"
		);
		method_21601(
			1589,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'center'}}"
		);
		method_21601(
			1590,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'false',east:'true',south:'false',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'east'}}"
		);
		method_21601(
			1591,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'false',east:'false',south:'true',west:'true',up:'true',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'south_west'}}"
		);
		method_21601(
			1592,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'false',east:'false',south:'true',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'south'}}"
		);
		method_21601(
			1593,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'false',east:'true',south:'true',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'south_east'}}"
		);
		method_21601(
			1594,
			"{Name:'minecraft:mushroom_stem',Properties:{north:'true',east:'true',south:'true',west:'true',up:'false',down:'false'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'stem'}}"
		);
		method_21601(1595, "{Name:'minecraft:brown_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'false',up:'false',down:'false'}}");
		method_21601(1596, "{Name:'minecraft:brown_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'false',up:'false',down:'false'}}");
		method_21601(1597, "{Name:'minecraft:brown_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'false',up:'false',down:'false'}}");
		method_21601(
			1598,
			"{Name:'minecraft:brown_mushroom_block',Properties:{north:'true',east:'true',south:'true',west:'true',up:'true',down:'true'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'all_outside'}}"
		);
		method_21601(
			1599,
			"{Name:'minecraft:mushroom_stem',Properties:{north:'true',east:'true',south:'true',west:'true',up:'true',down:'true'}}",
			"{Name:'minecraft:brown_mushroom_block',Properties:{variant:'all_stem'}}"
		);
		method_21601(
			1600,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'false',up:'false',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'all_inside'}}"
		);
		method_21601(
			1601,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'true',east:'false',south:'false',west:'true',up:'true',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'north_west'}}"
		);
		method_21601(
			1602,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'true',east:'false',south:'false',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'north'}}"
		);
		method_21601(
			1603,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'true',east:'true',south:'false',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'north_east'}}"
		);
		method_21601(
			1604,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'true',up:'true',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'west'}}"
		);
		method_21601(
			1605,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'center'}}"
		);
		method_21601(
			1606,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'false',east:'true',south:'false',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'east'}}"
		);
		method_21601(
			1607,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'false',east:'false',south:'true',west:'true',up:'true',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'south_west'}}"
		);
		method_21601(
			1608,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'false',east:'false',south:'true',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'south'}}"
		);
		method_21601(
			1609,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'false',east:'true',south:'true',west:'false',up:'true',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'south_east'}}"
		);
		method_21601(
			1610,
			"{Name:'minecraft:mushroom_stem',Properties:{north:'true',east:'true',south:'true',west:'true',up:'false',down:'false'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'stem'}}"
		);
		method_21601(1611, "{Name:'minecraft:red_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'false',up:'false',down:'false'}}");
		method_21601(1612, "{Name:'minecraft:red_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'false',up:'false',down:'false'}}");
		method_21601(1613, "{Name:'minecraft:red_mushroom_block',Properties:{north:'false',east:'false',south:'false',west:'false',up:'false',down:'false'}}");
		method_21601(
			1614,
			"{Name:'minecraft:red_mushroom_block',Properties:{north:'true',east:'true',south:'true',west:'true',up:'true',down:'true'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'all_outside'}}"
		);
		method_21601(
			1615,
			"{Name:'minecraft:mushroom_stem',Properties:{north:'true',east:'true',south:'true',west:'true',up:'true',down:'true'}}",
			"{Name:'minecraft:red_mushroom_block',Properties:{variant:'all_stem'}}"
		);
		method_21601(
			1616,
			"{Name:'minecraft:iron_bars',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:iron_bars',Properties:{east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			1632,
			"{Name:'minecraft:glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:glass_pane',Properties:{east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(1648, "{Name:'minecraft:melon_block'}", "{Name:'minecraft:melon_block'}");
		method_21601(
			1664,
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'0'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'0',facing:'east'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'0',facing:'north'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'0',facing:'south'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'0',facing:'up'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'0',facing:'west'}}"
		);
		method_21601(
			1665,
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'1'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'1',facing:'east'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'1',facing:'north'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'1',facing:'south'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'1',facing:'up'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'1',facing:'west'}}"
		);
		method_21601(
			1666,
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'2'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'2',facing:'east'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'2',facing:'north'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'2',facing:'south'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'2',facing:'up'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'2',facing:'west'}}"
		);
		method_21601(
			1667,
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'3'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'3',facing:'east'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'3',facing:'north'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'3',facing:'south'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'3',facing:'up'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'3',facing:'west'}}"
		);
		method_21601(
			1668,
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'4'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'4',facing:'east'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'4',facing:'north'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'4',facing:'south'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'4',facing:'up'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'4',facing:'west'}}"
		);
		method_21601(
			1669,
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'5'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'5',facing:'east'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'5',facing:'north'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'5',facing:'south'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'5',facing:'up'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'5',facing:'west'}}"
		);
		method_21601(
			1670,
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'6'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'6',facing:'east'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'6',facing:'north'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'6',facing:'south'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'6',facing:'up'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'6',facing:'west'}}"
		);
		method_21601(
			1671,
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'7'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'7',facing:'east'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'7',facing:'north'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'7',facing:'south'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'7',facing:'up'}}",
			"{Name:'minecraft:pumpkin_stem',Properties:{age:'7',facing:'west'}}"
		);
		method_21601(
			1680,
			"{Name:'minecraft:melon_stem',Properties:{age:'0'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'0',facing:'east'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'0',facing:'north'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'0',facing:'south'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'0',facing:'up'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'0',facing:'west'}}"
		);
		method_21601(
			1681,
			"{Name:'minecraft:melon_stem',Properties:{age:'1'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'1',facing:'east'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'1',facing:'north'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'1',facing:'south'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'1',facing:'up'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'1',facing:'west'}}"
		);
		method_21601(
			1682,
			"{Name:'minecraft:melon_stem',Properties:{age:'2'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'2',facing:'east'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'2',facing:'north'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'2',facing:'south'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'2',facing:'up'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'2',facing:'west'}}"
		);
		method_21601(
			1683,
			"{Name:'minecraft:melon_stem',Properties:{age:'3'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'3',facing:'east'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'3',facing:'north'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'3',facing:'south'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'3',facing:'up'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'3',facing:'west'}}"
		);
		method_21601(
			1684,
			"{Name:'minecraft:melon_stem',Properties:{age:'4'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'4',facing:'east'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'4',facing:'north'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'4',facing:'south'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'4',facing:'up'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'4',facing:'west'}}"
		);
		method_21601(
			1685,
			"{Name:'minecraft:melon_stem',Properties:{age:'5'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'5',facing:'east'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'5',facing:'north'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'5',facing:'south'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'5',facing:'up'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'5',facing:'west'}}"
		);
		method_21601(
			1686,
			"{Name:'minecraft:melon_stem',Properties:{age:'6'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'6',facing:'east'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'6',facing:'north'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'6',facing:'south'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'6',facing:'up'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'6',facing:'west'}}"
		);
		method_21601(
			1687,
			"{Name:'minecraft:melon_stem',Properties:{age:'7'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'7',facing:'east'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'7',facing:'north'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'7',facing:'south'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'7',facing:'up'}}",
			"{Name:'minecraft:melon_stem',Properties:{age:'7',facing:'west'}}"
		);
		method_21601(
			1696,
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'false',up:'true',west:'false'}}"
		);
		method_21601(
			1697,
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'true',up:'true',west:'false'}}"
		);
		method_21601(
			1698,
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'false',up:'true',west:'true'}}"
		);
		method_21601(
			1699,
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'false',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			1700,
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'false',up:'true',west:'false'}}"
		);
		method_21601(
			1701,
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'true',up:'true',west:'false'}}"
		);
		method_21601(
			1702,
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'false',up:'true',west:'true'}}"
		);
		method_21601(
			1703,
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'false',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			1704,
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'false',up:'true',west:'false'}}"
		);
		method_21601(
			1705,
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'true',up:'true',west:'false'}}"
		);
		method_21601(
			1706,
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'false',up:'true',west:'true'}}"
		);
		method_21601(
			1707,
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'false',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			1708,
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'false',up:'true',west:'false'}}"
		);
		method_21601(
			1709,
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'true',up:'true',west:'false'}}"
		);
		method_21601(
			1710,
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'false',up:'true',west:'true'}}"
		);
		method_21601(
			1711,
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:vine',Properties:{east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(
			1712,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			1713,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			1714,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			1715,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			1716,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			1717,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			1718,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			1719,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			1720,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			1721,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			1722,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			1723,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			1724,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			1725,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			1726,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			1727,
			"{Name:'minecraft:oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			1728,
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1729,
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1730,
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1731,
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1732,
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			1733,
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			1734,
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			1735,
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:brick_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(
			1744,
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1745,
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1746,
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1747,
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1748,
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			1749,
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			1750,
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			1751,
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:stone_brick_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(
			1760,
			"{Name:'minecraft:mycelium',Properties:{snowy:'false'}}",
			"{Name:'minecraft:mycelium',Properties:{snowy:'false'}}",
			"{Name:'minecraft:mycelium',Properties:{snowy:'true'}}"
		);
		method_21601(1776, "{Name:'minecraft:lily_pad'}", "{Name:'minecraft:waterlily'}");
		method_21601(1792, "{Name:'minecraft:nether_bricks'}", "{Name:'minecraft:nether_brick'}");
		method_21601(
			1808,
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:nether_brick_fence',Properties:{east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			1824,
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1825,
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1826,
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1827,
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			1828,
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			1829,
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			1830,
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			1831,
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:nether_brick_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(1840, "{Name:'minecraft:nether_wart',Properties:{age:'0'}}", "{Name:'minecraft:nether_wart',Properties:{age:'0'}}");
		method_21601(1841, "{Name:'minecraft:nether_wart',Properties:{age:'1'}}", "{Name:'minecraft:nether_wart',Properties:{age:'1'}}");
		method_21601(1842, "{Name:'minecraft:nether_wart',Properties:{age:'2'}}", "{Name:'minecraft:nether_wart',Properties:{age:'2'}}");
		method_21601(1843, "{Name:'minecraft:nether_wart',Properties:{age:'3'}}", "{Name:'minecraft:nether_wart',Properties:{age:'3'}}");
		method_21601(1856, "{Name:'minecraft:enchanting_table'}", "{Name:'minecraft:enchanting_table'}");
		method_21601(
			1872,
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'false',has_bottle_1:'false',has_bottle_2:'false'}}",
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'false',has_bottle_1:'false',has_bottle_2:'false'}}"
		);
		method_21601(
			1873,
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'true',has_bottle_1:'false',has_bottle_2:'false'}}",
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'true',has_bottle_1:'false',has_bottle_2:'false'}}"
		);
		method_21601(
			1874,
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'false',has_bottle_1:'true',has_bottle_2:'false'}}",
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'false',has_bottle_1:'true',has_bottle_2:'false'}}"
		);
		method_21601(
			1875,
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'true',has_bottle_1:'true',has_bottle_2:'false'}}",
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'true',has_bottle_1:'true',has_bottle_2:'false'}}"
		);
		method_21601(
			1876,
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'false',has_bottle_1:'false',has_bottle_2:'true'}}",
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'false',has_bottle_1:'false',has_bottle_2:'true'}}"
		);
		method_21601(
			1877,
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'true',has_bottle_1:'false',has_bottle_2:'true'}}",
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'true',has_bottle_1:'false',has_bottle_2:'true'}}"
		);
		method_21601(
			1878,
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'false',has_bottle_1:'true',has_bottle_2:'true'}}",
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'false',has_bottle_1:'true',has_bottle_2:'true'}}"
		);
		method_21601(
			1879,
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'true',has_bottle_1:'true',has_bottle_2:'true'}}",
			"{Name:'minecraft:brewing_stand',Properties:{has_bottle_0:'true',has_bottle_1:'true',has_bottle_2:'true'}}"
		);
		method_21601(1888, "{Name:'minecraft:cauldron',Properties:{level:'0'}}", "{Name:'minecraft:cauldron',Properties:{level:'0'}}");
		method_21601(1889, "{Name:'minecraft:cauldron',Properties:{level:'1'}}", "{Name:'minecraft:cauldron',Properties:{level:'1'}}");
		method_21601(1890, "{Name:'minecraft:cauldron',Properties:{level:'2'}}", "{Name:'minecraft:cauldron',Properties:{level:'2'}}");
		method_21601(1891, "{Name:'minecraft:cauldron',Properties:{level:'3'}}", "{Name:'minecraft:cauldron',Properties:{level:'3'}}");
		method_21601(1904, "{Name:'minecraft:end_portal'}", "{Name:'minecraft:end_portal'}");
		method_21601(
			1920,
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'false',facing:'south'}}",
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'false',facing:'south'}}"
		);
		method_21601(
			1921,
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'false',facing:'west'}}",
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'false',facing:'west'}}"
		);
		method_21601(
			1922,
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'false',facing:'north'}}",
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'false',facing:'north'}}"
		);
		method_21601(
			1923,
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'false',facing:'east'}}",
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'false',facing:'east'}}"
		);
		method_21601(
			1924,
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'true',facing:'south'}}",
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'true',facing:'south'}}"
		);
		method_21601(
			1925,
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'true',facing:'west'}}",
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'true',facing:'west'}}"
		);
		method_21601(
			1926,
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'true',facing:'north'}}",
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'true',facing:'north'}}"
		);
		method_21601(
			1927,
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'true',facing:'east'}}",
			"{Name:'minecraft:end_portal_frame',Properties:{eye:'true',facing:'east'}}"
		);
		method_21601(1936, "{Name:'minecraft:end_stone'}", "{Name:'minecraft:end_stone'}");
		method_21601(1952, "{Name:'minecraft:dragon_egg'}", "{Name:'minecraft:dragon_egg'}");
		method_21601(1968, "{Name:'minecraft:redstone_lamp',Properties:{lit:'false'}}", "{Name:'minecraft:redstone_lamp'}");
		method_21601(1984, "{Name:'minecraft:redstone_lamp',Properties:{lit:'true'}}", "{Name:'minecraft:lit_redstone_lamp'}");
		method_21601(2000, "{Name:'minecraft:oak_slab',Properties:{type:'double'}}", "{Name:'minecraft:double_wooden_slab',Properties:{variant:'oak'}}");
		method_21601(2001, "{Name:'minecraft:spruce_slab',Properties:{type:'double'}}", "{Name:'minecraft:double_wooden_slab',Properties:{variant:'spruce'}}");
		method_21601(2002, "{Name:'minecraft:birch_slab',Properties:{type:'double'}}", "{Name:'minecraft:double_wooden_slab',Properties:{variant:'birch'}}");
		method_21601(2003, "{Name:'minecraft:jungle_slab',Properties:{type:'double'}}", "{Name:'minecraft:double_wooden_slab',Properties:{variant:'jungle'}}");
		method_21601(2004, "{Name:'minecraft:acacia_slab',Properties:{type:'double'}}", "{Name:'minecraft:double_wooden_slab',Properties:{variant:'acacia'}}");
		method_21601(2005, "{Name:'minecraft:dark_oak_slab',Properties:{type:'double'}}", "{Name:'minecraft:double_wooden_slab',Properties:{variant:'dark_oak'}}");
		method_21601(2016, "{Name:'minecraft:oak_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'bottom',variant:'oak'}}");
		method_21601(2017, "{Name:'minecraft:spruce_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'bottom',variant:'spruce'}}");
		method_21601(2018, "{Name:'minecraft:birch_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'bottom',variant:'birch'}}");
		method_21601(2019, "{Name:'minecraft:jungle_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'bottom',variant:'jungle'}}");
		method_21601(2020, "{Name:'minecraft:acacia_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'bottom',variant:'acacia'}}");
		method_21601(
			2021, "{Name:'minecraft:dark_oak_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'bottom',variant:'dark_oak'}}"
		);
		method_21601(2024, "{Name:'minecraft:oak_slab',Properties:{type:'top'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'top',variant:'oak'}}");
		method_21601(2025, "{Name:'minecraft:spruce_slab',Properties:{type:'top'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'top',variant:'spruce'}}");
		method_21601(2026, "{Name:'minecraft:birch_slab',Properties:{type:'top'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'top',variant:'birch'}}");
		method_21601(2027, "{Name:'minecraft:jungle_slab',Properties:{type:'top'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'top',variant:'jungle'}}");
		method_21601(2028, "{Name:'minecraft:acacia_slab',Properties:{type:'top'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'top',variant:'acacia'}}");
		method_21601(2029, "{Name:'minecraft:dark_oak_slab',Properties:{type:'top'}}", "{Name:'minecraft:wooden_slab',Properties:{half:'top',variant:'dark_oak'}}");
		method_21601(2032, "{Name:'minecraft:cocoa',Properties:{age:'0',facing:'south'}}", "{Name:'minecraft:cocoa',Properties:{age:'0',facing:'south'}}");
		method_21601(2033, "{Name:'minecraft:cocoa',Properties:{age:'0',facing:'west'}}", "{Name:'minecraft:cocoa',Properties:{age:'0',facing:'west'}}");
		method_21601(2034, "{Name:'minecraft:cocoa',Properties:{age:'0',facing:'north'}}", "{Name:'minecraft:cocoa',Properties:{age:'0',facing:'north'}}");
		method_21601(2035, "{Name:'minecraft:cocoa',Properties:{age:'0',facing:'east'}}", "{Name:'minecraft:cocoa',Properties:{age:'0',facing:'east'}}");
		method_21601(2036, "{Name:'minecraft:cocoa',Properties:{age:'1',facing:'south'}}", "{Name:'minecraft:cocoa',Properties:{age:'1',facing:'south'}}");
		method_21601(2037, "{Name:'minecraft:cocoa',Properties:{age:'1',facing:'west'}}", "{Name:'minecraft:cocoa',Properties:{age:'1',facing:'west'}}");
		method_21601(2038, "{Name:'minecraft:cocoa',Properties:{age:'1',facing:'north'}}", "{Name:'minecraft:cocoa',Properties:{age:'1',facing:'north'}}");
		method_21601(2039, "{Name:'minecraft:cocoa',Properties:{age:'1',facing:'east'}}", "{Name:'minecraft:cocoa',Properties:{age:'1',facing:'east'}}");
		method_21601(2040, "{Name:'minecraft:cocoa',Properties:{age:'2',facing:'south'}}", "{Name:'minecraft:cocoa',Properties:{age:'2',facing:'south'}}");
		method_21601(2041, "{Name:'minecraft:cocoa',Properties:{age:'2',facing:'west'}}", "{Name:'minecraft:cocoa',Properties:{age:'2',facing:'west'}}");
		method_21601(2042, "{Name:'minecraft:cocoa',Properties:{age:'2',facing:'north'}}", "{Name:'minecraft:cocoa',Properties:{age:'2',facing:'north'}}");
		method_21601(2043, "{Name:'minecraft:cocoa',Properties:{age:'2',facing:'east'}}", "{Name:'minecraft:cocoa',Properties:{age:'2',facing:'east'}}");
		method_21601(
			2048,
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2049,
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2050,
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2051,
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2052,
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			2053,
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			2054,
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			2055,
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:sandstone_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(2064, "{Name:'minecraft:emerald_ore'}", "{Name:'minecraft:emerald_ore'}");
		method_21601(2082, "{Name:'minecraft:ender_chest',Properties:{facing:'north'}}", "{Name:'minecraft:ender_chest',Properties:{facing:'north'}}");
		method_21601(2083, "{Name:'minecraft:ender_chest',Properties:{facing:'south'}}", "{Name:'minecraft:ender_chest',Properties:{facing:'south'}}");
		method_21601(2084, "{Name:'minecraft:ender_chest',Properties:{facing:'west'}}", "{Name:'minecraft:ender_chest',Properties:{facing:'west'}}");
		method_21601(2085, "{Name:'minecraft:ender_chest',Properties:{facing:'east'}}", "{Name:'minecraft:ender_chest',Properties:{facing:'east'}}");
		method_21601(
			2096,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'south',powered:'false'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'south',powered:'false'}}"
		);
		method_21601(
			2097,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'west',powered:'false'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'west',powered:'false'}}"
		);
		method_21601(
			2098,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'north',powered:'false'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'north',powered:'false'}}"
		);
		method_21601(
			2099,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'east',powered:'false'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'east',powered:'false'}}"
		);
		method_21601(
			2100,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'south',powered:'false'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'south',powered:'false'}}"
		);
		method_21601(
			2101,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'west',powered:'false'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'west',powered:'false'}}"
		);
		method_21601(
			2102,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'north',powered:'false'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'north',powered:'false'}}"
		);
		method_21601(
			2103,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'east',powered:'false'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'east',powered:'false'}}"
		);
		method_21601(
			2104,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'south',powered:'true'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'south',powered:'true'}}"
		);
		method_21601(
			2105,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'west',powered:'true'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'west',powered:'true'}}"
		);
		method_21601(
			2106,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'north',powered:'true'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'north',powered:'true'}}"
		);
		method_21601(
			2107,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'east',powered:'true'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'false',facing:'east',powered:'true'}}"
		);
		method_21601(
			2108,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'south',powered:'true'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'south',powered:'true'}}"
		);
		method_21601(
			2109,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'west',powered:'true'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'west',powered:'true'}}"
		);
		method_21601(
			2110,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'north',powered:'true'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'north',powered:'true'}}"
		);
		method_21601(
			2111,
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'east',powered:'true'}}",
			"{Name:'minecraft:tripwire_hook',Properties:{attached:'true',facing:'east',powered:'true'}}"
		);
		method_21601(
			2112,
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'true',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'true',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'true',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'true',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'false',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'false',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'false',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'true',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'true',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'true',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'true',powered:'false',south:'true',west:'true'}}"
		);
		method_21601(
			2113,
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'true',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'true',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'true',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'true',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'false',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'false',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'false',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'true',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'true',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'true',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'true',north:'true',powered:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2114, "{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'false',south:'false',west:'false'}}"
		);
		method_21601(
			2115, "{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'false',east:'false',north:'false',powered:'true',south:'false',west:'false'}}"
		);
		method_21601(
			2116,
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'true',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'true',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'true',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'true',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'false',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'false',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'false',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'true',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'true',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'true',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'true',powered:'false',south:'true',west:'true'}}"
		);
		method_21601(
			2117,
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'true',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'true',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'true',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'true',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'false',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'false',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'false',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'true',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'true',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'true',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'true',north:'true',powered:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2118, "{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'false',south:'false',west:'false'}}"
		);
		method_21601(
			2119, "{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'false',east:'false',north:'false',powered:'true',south:'false',west:'false'}}"
		);
		method_21601(
			2120,
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'true',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'true',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'true',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'true',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'false',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'false',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'false',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'true',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'true',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'true',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'true',powered:'false',south:'true',west:'true'}}"
		);
		method_21601(
			2121,
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'true',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'true',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'true',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'true',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'false',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'false',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'false',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'true',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'true',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'true',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'true',north:'true',powered:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2122, "{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'false',south:'false',west:'false'}}"
		);
		method_21601(
			2123, "{Name:'minecraft:tripwire',Properties:{attached:'false',disarmed:'true',east:'false',north:'false',powered:'true',south:'false',west:'false'}}"
		);
		method_21601(
			2124,
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'true',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'true',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'true',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'true',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'false',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'false',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'false',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'false',powered:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'true',powered:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'true',powered:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'true',powered:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'true',powered:'false',south:'true',west:'true'}}"
		);
		method_21601(
			2125,
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'true',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'true',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'true',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'true',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'false',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'false',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'false',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'false',powered:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'true',powered:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'true',powered:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'true',powered:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'true',north:'true',powered:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2126, "{Name:'minecraft:tripwire',Properties:{attached:'true',disarmed:'true',east:'false',north:'false',powered:'false',south:'false',west:'false'}}"
		);
		method_21601(2128, "{Name:'minecraft:emerald_block'}", "{Name:'minecraft:emerald_block'}");
		method_21601(
			2144,
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2145,
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2146,
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2147,
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2148,
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			2149,
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			2150,
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			2151,
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:spruce_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(
			2160,
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2161,
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2162,
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2163,
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2164,
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			2165,
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			2166,
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			2167,
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:birch_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(
			2176,
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2177,
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2178,
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2179,
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2180,
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			2181,
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			2182,
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			2183,
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:jungle_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(
			2192,
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'down'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'down'}}"
		);
		method_21601(
			2193,
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'up'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'up'}}"
		);
		method_21601(
			2194,
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'north'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'north'}}"
		);
		method_21601(
			2195,
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'south'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'south'}}"
		);
		method_21601(
			2196,
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'west'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'west'}}"
		);
		method_21601(
			2197,
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'east'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'false',facing:'east'}}"
		);
		method_21601(
			2200,
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'down'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'down'}}"
		);
		method_21601(
			2201,
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'up'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'up'}}"
		);
		method_21601(
			2202,
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'north'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'north'}}"
		);
		method_21601(
			2203,
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'south'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'south'}}"
		);
		method_21601(
			2204,
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'west'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'west'}}"
		);
		method_21601(
			2205,
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'east'}}",
			"{Name:'minecraft:command_block',Properties:{conditional:'true',facing:'east'}}"
		);
		method_21601(2208, "{Name:'minecraft:beacon'}", "{Name:'minecraft:beacon'}");
		method_21601(
			2224,
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'false',up:'false',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'false',up:'false',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'false',up:'true',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'false',up:'true',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'true',up:'false',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'true',up:'false',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'true',up:'true',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'true',up:'true',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'false',up:'false',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'false',up:'false',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'false',up:'true',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'false',up:'true',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'true',up:'false',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'true',up:'false',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'true',up:'true',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'true',up:'true',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'false',up:'false',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'false',up:'false',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'false',up:'true',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'false',up:'true',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'true',up:'false',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'true',up:'false',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'true',up:'true',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'true',up:'true',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'false',up:'false',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'false',up:'false',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'false',up:'true',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'false',up:'true',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'true',up:'false',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'true',up:'false',variant:'cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'true',up:'true',variant:'cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'true',up:'true',variant:'cobblestone',west:'true'}}"
		);
		method_21601(
			2225,
			"{Name:'minecraft:mossy_cobblestone_wall',Properties:{east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'false',up:'false',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'false',up:'false',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'false',up:'true',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'false',up:'true',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'true',up:'false',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'true',up:'false',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'true',up:'true',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'false',south:'true',up:'true',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'false',up:'false',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'false',up:'false',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'false',up:'true',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'false',up:'true',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'true',up:'false',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'true',up:'false',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'true',up:'true',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'false',north:'true',south:'true',up:'true',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'false',up:'false',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'false',up:'false',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'false',up:'true',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'false',up:'true',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'true',up:'false',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'true',up:'false',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'true',up:'true',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'false',south:'true',up:'true',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'false',up:'false',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'false',up:'false',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'false',up:'true',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'false',up:'true',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'true',up:'false',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'true',up:'false',variant:'mossy_cobblestone',west:'true'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'true',up:'true',variant:'mossy_cobblestone',west:'false'}}",
			"{Name:'minecraft:cobblestone_wall',Properties:{east:'true',north:'true',south:'true',up:'true',variant:'mossy_cobblestone',west:'true'}}"
		);
		method_21601(
			2240,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'0'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'0'}}"
		);
		method_21601(
			2241,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'1'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'1'}}"
		);
		method_21601(
			2242,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'2'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'2'}}"
		);
		method_21601(
			2243,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'3'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'3'}}"
		);
		method_21601(
			2244,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'4'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'4'}}"
		);
		method_21601(
			2245,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'5'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'5'}}"
		);
		method_21601(
			2246,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'6'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'6'}}"
		);
		method_21601(
			2247,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'7'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'7'}}"
		);
		method_21601(
			2248,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'8'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'8'}}"
		);
		method_21601(
			2249,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'9'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'9'}}"
		);
		method_21601(
			2250,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'10'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'10'}}"
		);
		method_21601(
			2251,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'11'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'11'}}"
		);
		method_21601(
			2252,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'12'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'12'}}"
		);
		method_21601(
			2253,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'13'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'13'}}"
		);
		method_21601(
			2254,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'14'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'14'}}"
		);
		method_21601(
			2255,
			"{Name:'minecraft:potted_cactus'}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'acacia_sapling',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'allium',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'birch_sapling',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'blue_orchid',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'cactus',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dandelion',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dark_oak_sapling',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'dead_bush',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'empty',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'fern',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'houstonia',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'jungle_sapling',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_brown',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'mushroom_red',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oak_sapling',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'orange_tulip',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'oxeye_daisy',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'pink_tulip',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'red_tulip',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'rose',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'spruce_sapling',legacy_data:'15'}}",
			"{Name:'minecraft:flower_pot',Properties:{contents:'white_tulip',legacy_data:'15'}}"
		);
		method_21601(2256, "{Name:'minecraft:carrots',Properties:{age:'0'}}", "{Name:'minecraft:carrots',Properties:{age:'0'}}");
		method_21601(2257, "{Name:'minecraft:carrots',Properties:{age:'1'}}", "{Name:'minecraft:carrots',Properties:{age:'1'}}");
		method_21601(2258, "{Name:'minecraft:carrots',Properties:{age:'2'}}", "{Name:'minecraft:carrots',Properties:{age:'2'}}");
		method_21601(2259, "{Name:'minecraft:carrots',Properties:{age:'3'}}", "{Name:'minecraft:carrots',Properties:{age:'3'}}");
		method_21601(2260, "{Name:'minecraft:carrots',Properties:{age:'4'}}", "{Name:'minecraft:carrots',Properties:{age:'4'}}");
		method_21601(2261, "{Name:'minecraft:carrots',Properties:{age:'5'}}", "{Name:'minecraft:carrots',Properties:{age:'5'}}");
		method_21601(2262, "{Name:'minecraft:carrots',Properties:{age:'6'}}", "{Name:'minecraft:carrots',Properties:{age:'6'}}");
		method_21601(2263, "{Name:'minecraft:carrots',Properties:{age:'7'}}", "{Name:'minecraft:carrots',Properties:{age:'7'}}");
		method_21601(2272, "{Name:'minecraft:potatoes',Properties:{age:'0'}}", "{Name:'minecraft:potatoes',Properties:{age:'0'}}");
		method_21601(2273, "{Name:'minecraft:potatoes',Properties:{age:'1'}}", "{Name:'minecraft:potatoes',Properties:{age:'1'}}");
		method_21601(2274, "{Name:'minecraft:potatoes',Properties:{age:'2'}}", "{Name:'minecraft:potatoes',Properties:{age:'2'}}");
		method_21601(2275, "{Name:'minecraft:potatoes',Properties:{age:'3'}}", "{Name:'minecraft:potatoes',Properties:{age:'3'}}");
		method_21601(2276, "{Name:'minecraft:potatoes',Properties:{age:'4'}}", "{Name:'minecraft:potatoes',Properties:{age:'4'}}");
		method_21601(2277, "{Name:'minecraft:potatoes',Properties:{age:'5'}}", "{Name:'minecraft:potatoes',Properties:{age:'5'}}");
		method_21601(2278, "{Name:'minecraft:potatoes',Properties:{age:'6'}}", "{Name:'minecraft:potatoes',Properties:{age:'6'}}");
		method_21601(2279, "{Name:'minecraft:potatoes',Properties:{age:'7'}}", "{Name:'minecraft:potatoes',Properties:{age:'7'}}");
		method_21601(
			2288,
			"{Name:'minecraft:oak_button',Properties:{face:'ceiling',facing:'north',powered:'false'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'down',powered:'false'}}"
		);
		method_21601(
			2289,
			"{Name:'minecraft:oak_button',Properties:{face:'wall',facing:'east',powered:'false'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'east',powered:'false'}}"
		);
		method_21601(
			2290,
			"{Name:'minecraft:oak_button',Properties:{face:'wall',facing:'west',powered:'false'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'west',powered:'false'}}"
		);
		method_21601(
			2291,
			"{Name:'minecraft:oak_button',Properties:{face:'wall',facing:'south',powered:'false'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'south',powered:'false'}}"
		);
		method_21601(
			2292,
			"{Name:'minecraft:oak_button',Properties:{face:'wall',facing:'north',powered:'false'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'north',powered:'false'}}"
		);
		method_21601(
			2293,
			"{Name:'minecraft:oak_button',Properties:{face:'floor',facing:'north',powered:'false'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'up',powered:'false'}}"
		);
		method_21601(
			2296,
			"{Name:'minecraft:oak_button',Properties:{face:'ceiling',facing:'north',powered:'true'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'down',powered:'true'}}"
		);
		method_21601(
			2297,
			"{Name:'minecraft:oak_button',Properties:{face:'wall',facing:'east',powered:'true'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'east',powered:'true'}}"
		);
		method_21601(
			2298,
			"{Name:'minecraft:oak_button',Properties:{face:'wall',facing:'west',powered:'true'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'west',powered:'true'}}"
		);
		method_21601(
			2299,
			"{Name:'minecraft:oak_button',Properties:{face:'wall',facing:'south',powered:'true'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'south',powered:'true'}}"
		);
		method_21601(
			2300,
			"{Name:'minecraft:oak_button',Properties:{face:'wall',facing:'north',powered:'true'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'north',powered:'true'}}"
		);
		method_21601(
			2301,
			"{Name:'minecraft:oak_button',Properties:{face:'floor',facing:'north',powered:'true'}}",
			"{Name:'minecraft:wooden_button',Properties:{facing:'up',powered:'true'}}"
		);
		method_21601(2304, "{Name:'%%FILTER_ME%%',Properties:{facing:'down',nodrop:'false'}}", "{Name:'minecraft:skull',Properties:{facing:'down',nodrop:'false'}}");
		method_21601(2305, "{Name:'%%FILTER_ME%%',Properties:{facing:'up',nodrop:'false'}}", "{Name:'minecraft:skull',Properties:{facing:'up',nodrop:'false'}}");
		method_21601(2306, "{Name:'%%FILTER_ME%%',Properties:{facing:'north',nodrop:'false'}}", "{Name:'minecraft:skull',Properties:{facing:'north',nodrop:'false'}}");
		method_21601(2307, "{Name:'%%FILTER_ME%%',Properties:{facing:'south',nodrop:'false'}}", "{Name:'minecraft:skull',Properties:{facing:'south',nodrop:'false'}}");
		method_21601(2308, "{Name:'%%FILTER_ME%%',Properties:{facing:'west',nodrop:'false'}}", "{Name:'minecraft:skull',Properties:{facing:'west',nodrop:'false'}}");
		method_21601(2309, "{Name:'%%FILTER_ME%%',Properties:{facing:'east',nodrop:'false'}}", "{Name:'minecraft:skull',Properties:{facing:'east',nodrop:'false'}}");
		method_21601(2312, "{Name:'%%FILTER_ME%%',Properties:{facing:'down',nodrop:'true'}}", "{Name:'minecraft:skull',Properties:{facing:'down',nodrop:'true'}}");
		method_21601(2313, "{Name:'%%FILTER_ME%%',Properties:{facing:'up',nodrop:'true'}}", "{Name:'minecraft:skull',Properties:{facing:'up',nodrop:'true'}}");
		method_21601(2314, "{Name:'%%FILTER_ME%%',Properties:{facing:'north',nodrop:'true'}}", "{Name:'minecraft:skull',Properties:{facing:'north',nodrop:'true'}}");
		method_21601(2315, "{Name:'%%FILTER_ME%%',Properties:{facing:'south',nodrop:'true'}}", "{Name:'minecraft:skull',Properties:{facing:'south',nodrop:'true'}}");
		method_21601(2316, "{Name:'%%FILTER_ME%%',Properties:{facing:'west',nodrop:'true'}}", "{Name:'minecraft:skull',Properties:{facing:'west',nodrop:'true'}}");
		method_21601(2317, "{Name:'%%FILTER_ME%%',Properties:{facing:'east',nodrop:'true'}}", "{Name:'minecraft:skull',Properties:{facing:'east',nodrop:'true'}}");
		method_21601(2320, "{Name:'minecraft:anvil',Properties:{facing:'south'}}", "{Name:'minecraft:anvil',Properties:{damage:'0',facing:'south'}}");
		method_21601(2321, "{Name:'minecraft:anvil',Properties:{facing:'west'}}", "{Name:'minecraft:anvil',Properties:{damage:'0',facing:'west'}}");
		method_21601(2322, "{Name:'minecraft:anvil',Properties:{facing:'north'}}", "{Name:'minecraft:anvil',Properties:{damage:'0',facing:'north'}}");
		method_21601(2323, "{Name:'minecraft:anvil',Properties:{facing:'east'}}", "{Name:'minecraft:anvil',Properties:{damage:'0',facing:'east'}}");
		method_21601(2324, "{Name:'minecraft:chipped_anvil',Properties:{facing:'south'}}", "{Name:'minecraft:anvil',Properties:{damage:'1',facing:'south'}}");
		method_21601(2325, "{Name:'minecraft:chipped_anvil',Properties:{facing:'west'}}", "{Name:'minecraft:anvil',Properties:{damage:'1',facing:'west'}}");
		method_21601(2326, "{Name:'minecraft:chipped_anvil',Properties:{facing:'north'}}", "{Name:'minecraft:anvil',Properties:{damage:'1',facing:'north'}}");
		method_21601(2327, "{Name:'minecraft:chipped_anvil',Properties:{facing:'east'}}", "{Name:'minecraft:anvil',Properties:{damage:'1',facing:'east'}}");
		method_21601(2328, "{Name:'minecraft:damaged_anvil',Properties:{facing:'south'}}", "{Name:'minecraft:anvil',Properties:{damage:'2',facing:'south'}}");
		method_21601(2329, "{Name:'minecraft:damaged_anvil',Properties:{facing:'west'}}", "{Name:'minecraft:anvil',Properties:{damage:'2',facing:'west'}}");
		method_21601(2330, "{Name:'minecraft:damaged_anvil',Properties:{facing:'north'}}", "{Name:'minecraft:anvil',Properties:{damage:'2',facing:'north'}}");
		method_21601(2331, "{Name:'minecraft:damaged_anvil',Properties:{facing:'east'}}", "{Name:'minecraft:anvil',Properties:{damage:'2',facing:'east'}}");
		method_21601(
			2338, "{Name:'minecraft:trapped_chest',Properties:{facing:'north',type:'single'}}", "{Name:'minecraft:trapped_chest',Properties:{facing:'north'}}"
		);
		method_21601(
			2339, "{Name:'minecraft:trapped_chest',Properties:{facing:'south',type:'single'}}", "{Name:'minecraft:trapped_chest',Properties:{facing:'south'}}"
		);
		method_21601(2340, "{Name:'minecraft:trapped_chest',Properties:{facing:'west',type:'single'}}", "{Name:'minecraft:trapped_chest',Properties:{facing:'west'}}");
		method_21601(2341, "{Name:'minecraft:trapped_chest',Properties:{facing:'east',type:'single'}}", "{Name:'minecraft:trapped_chest',Properties:{facing:'east'}}");
		method_21601(
			2352, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'0'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'0'}}"
		);
		method_21601(
			2353, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'1'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'1'}}"
		);
		method_21601(
			2354, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'2'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'2'}}"
		);
		method_21601(
			2355, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'3'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'3'}}"
		);
		method_21601(
			2356, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'4'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'4'}}"
		);
		method_21601(
			2357, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'5'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'5'}}"
		);
		method_21601(
			2358, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'6'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'6'}}"
		);
		method_21601(
			2359, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'7'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'7'}}"
		);
		method_21601(
			2360, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'8'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'8'}}"
		);
		method_21601(
			2361, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'9'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'9'}}"
		);
		method_21601(
			2362, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'10'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'10'}}"
		);
		method_21601(
			2363, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'11'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'11'}}"
		);
		method_21601(
			2364, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'12'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'12'}}"
		);
		method_21601(
			2365, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'13'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'13'}}"
		);
		method_21601(
			2366, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'14'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'14'}}"
		);
		method_21601(
			2367, "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'15'}}", "{Name:'minecraft:light_weighted_pressure_plate',Properties:{power:'15'}}"
		);
		method_21601(
			2368, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'0'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'0'}}"
		);
		method_21601(
			2369, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'1'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'1'}}"
		);
		method_21601(
			2370, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'2'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'2'}}"
		);
		method_21601(
			2371, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'3'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'3'}}"
		);
		method_21601(
			2372, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'4'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'4'}}"
		);
		method_21601(
			2373, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'5'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'5'}}"
		);
		method_21601(
			2374, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'6'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'6'}}"
		);
		method_21601(
			2375, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'7'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'7'}}"
		);
		method_21601(
			2376, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'8'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'8'}}"
		);
		method_21601(
			2377, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'9'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'9'}}"
		);
		method_21601(
			2378, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'10'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'10'}}"
		);
		method_21601(
			2379, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'11'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'11'}}"
		);
		method_21601(
			2380, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'12'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'12'}}"
		);
		method_21601(
			2381, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'13'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'13'}}"
		);
		method_21601(
			2382, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'14'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'14'}}"
		);
		method_21601(
			2383, "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'15'}}", "{Name:'minecraft:heavy_weighted_pressure_plate',Properties:{power:'15'}}"
		);
		method_21601(
			2384,
			"{Name:'minecraft:comparator',Properties:{facing:'south',mode:'compare',powered:'false'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'south',mode:'compare',powered:'false'}}"
		);
		method_21601(
			2385,
			"{Name:'minecraft:comparator',Properties:{facing:'west',mode:'compare',powered:'false'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'west',mode:'compare',powered:'false'}}"
		);
		method_21601(
			2386,
			"{Name:'minecraft:comparator',Properties:{facing:'north',mode:'compare',powered:'false'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'north',mode:'compare',powered:'false'}}"
		);
		method_21601(
			2387,
			"{Name:'minecraft:comparator',Properties:{facing:'east',mode:'compare',powered:'false'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'east',mode:'compare',powered:'false'}}"
		);
		method_21601(
			2388,
			"{Name:'minecraft:comparator',Properties:{facing:'south',mode:'subtract',powered:'false'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'south',mode:'subtract',powered:'false'}}"
		);
		method_21601(
			2389,
			"{Name:'minecraft:comparator',Properties:{facing:'west',mode:'subtract',powered:'false'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'west',mode:'subtract',powered:'false'}}"
		);
		method_21601(
			2390,
			"{Name:'minecraft:comparator',Properties:{facing:'north',mode:'subtract',powered:'false'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'north',mode:'subtract',powered:'false'}}"
		);
		method_21601(
			2391,
			"{Name:'minecraft:comparator',Properties:{facing:'east',mode:'subtract',powered:'false'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'east',mode:'subtract',powered:'false'}}"
		);
		method_21601(
			2392,
			"{Name:'minecraft:comparator',Properties:{facing:'south',mode:'compare',powered:'true'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'south',mode:'compare',powered:'true'}}"
		);
		method_21601(
			2393,
			"{Name:'minecraft:comparator',Properties:{facing:'west',mode:'compare',powered:'true'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'west',mode:'compare',powered:'true'}}"
		);
		method_21601(
			2394,
			"{Name:'minecraft:comparator',Properties:{facing:'north',mode:'compare',powered:'true'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'north',mode:'compare',powered:'true'}}"
		);
		method_21601(
			2395,
			"{Name:'minecraft:comparator',Properties:{facing:'east',mode:'compare',powered:'true'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'east',mode:'compare',powered:'true'}}"
		);
		method_21601(
			2396,
			"{Name:'minecraft:comparator',Properties:{facing:'south',mode:'subtract',powered:'true'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'south',mode:'subtract',powered:'true'}}"
		);
		method_21601(
			2397,
			"{Name:'minecraft:comparator',Properties:{facing:'west',mode:'subtract',powered:'true'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'west',mode:'subtract',powered:'true'}}"
		);
		method_21601(
			2398,
			"{Name:'minecraft:comparator',Properties:{facing:'north',mode:'subtract',powered:'true'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'north',mode:'subtract',powered:'true'}}"
		);
		method_21601(
			2399,
			"{Name:'minecraft:comparator',Properties:{facing:'east',mode:'subtract',powered:'true'}}",
			"{Name:'minecraft:unpowered_comparator',Properties:{facing:'east',mode:'subtract',powered:'true'}}"
		);
		method_21601(
			2400,
			"{Name:'minecraft:comparator',Properties:{facing:'south',mode:'compare',powered:'false'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'south',mode:'compare',powered:'false'}}"
		);
		method_21601(
			2401,
			"{Name:'minecraft:comparator',Properties:{facing:'west',mode:'compare',powered:'false'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'west',mode:'compare',powered:'false'}}"
		);
		method_21601(
			2402,
			"{Name:'minecraft:comparator',Properties:{facing:'north',mode:'compare',powered:'false'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'north',mode:'compare',powered:'false'}}"
		);
		method_21601(
			2403,
			"{Name:'minecraft:comparator',Properties:{facing:'east',mode:'compare',powered:'false'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'east',mode:'compare',powered:'false'}}"
		);
		method_21601(
			2404,
			"{Name:'minecraft:comparator',Properties:{facing:'south',mode:'subtract',powered:'false'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'south',mode:'subtract',powered:'false'}}"
		);
		method_21601(
			2405,
			"{Name:'minecraft:comparator',Properties:{facing:'west',mode:'subtract',powered:'false'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'west',mode:'subtract',powered:'false'}}"
		);
		method_21601(
			2406,
			"{Name:'minecraft:comparator',Properties:{facing:'north',mode:'subtract',powered:'false'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'north',mode:'subtract',powered:'false'}}"
		);
		method_21601(
			2407,
			"{Name:'minecraft:comparator',Properties:{facing:'east',mode:'subtract',powered:'false'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'east',mode:'subtract',powered:'false'}}"
		);
		method_21601(
			2408,
			"{Name:'minecraft:comparator',Properties:{facing:'south',mode:'compare',powered:'true'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'south',mode:'compare',powered:'true'}}"
		);
		method_21601(
			2409,
			"{Name:'minecraft:comparator',Properties:{facing:'west',mode:'compare',powered:'true'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'west',mode:'compare',powered:'true'}}"
		);
		method_21601(
			2410,
			"{Name:'minecraft:comparator',Properties:{facing:'north',mode:'compare',powered:'true'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'north',mode:'compare',powered:'true'}}"
		);
		method_21601(
			2411,
			"{Name:'minecraft:comparator',Properties:{facing:'east',mode:'compare',powered:'true'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'east',mode:'compare',powered:'true'}}"
		);
		method_21601(
			2412,
			"{Name:'minecraft:comparator',Properties:{facing:'south',mode:'subtract',powered:'true'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'south',mode:'subtract',powered:'true'}}"
		);
		method_21601(
			2413,
			"{Name:'minecraft:comparator',Properties:{facing:'west',mode:'subtract',powered:'true'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'west',mode:'subtract',powered:'true'}}"
		);
		method_21601(
			2414,
			"{Name:'minecraft:comparator',Properties:{facing:'north',mode:'subtract',powered:'true'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'north',mode:'subtract',powered:'true'}}"
		);
		method_21601(
			2415,
			"{Name:'minecraft:comparator',Properties:{facing:'east',mode:'subtract',powered:'true'}}",
			"{Name:'minecraft:powered_comparator',Properties:{facing:'east',mode:'subtract',powered:'true'}}"
		);
		method_21601(
			2416, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'0'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'0'}}"
		);
		method_21601(
			2417, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'1'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'1'}}"
		);
		method_21601(
			2418, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'2'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'2'}}"
		);
		method_21601(
			2419, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'3'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'3'}}"
		);
		method_21601(
			2420, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'4'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'4'}}"
		);
		method_21601(
			2421, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'5'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'5'}}"
		);
		method_21601(
			2422, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'6'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'6'}}"
		);
		method_21601(
			2423, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'7'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'7'}}"
		);
		method_21601(
			2424, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'8'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'8'}}"
		);
		method_21601(
			2425, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'9'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'9'}}"
		);
		method_21601(
			2426, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'10'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'10'}}"
		);
		method_21601(
			2427, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'11'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'11'}}"
		);
		method_21601(
			2428, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'12'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'12'}}"
		);
		method_21601(
			2429, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'13'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'13'}}"
		);
		method_21601(
			2430, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'14'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'14'}}"
		);
		method_21601(
			2431, "{Name:'minecraft:daylight_detector',Properties:{inverted:'false',power:'15'}}", "{Name:'minecraft:daylight_detector',Properties:{power:'15'}}"
		);
		method_21601(2432, "{Name:'minecraft:redstone_block'}", "{Name:'minecraft:redstone_block'}");
		method_21601(2448, "{Name:'minecraft:nether_quartz_ore'}", "{Name:'minecraft:quartz_ore'}");
		method_21601(
			2464, "{Name:'minecraft:hopper',Properties:{enabled:'true',facing:'down'}}", "{Name:'minecraft:hopper',Properties:{enabled:'true',facing:'down'}}"
		);
		method_21601(
			2466, "{Name:'minecraft:hopper',Properties:{enabled:'true',facing:'north'}}", "{Name:'minecraft:hopper',Properties:{enabled:'true',facing:'north'}}"
		);
		method_21601(
			2467, "{Name:'minecraft:hopper',Properties:{enabled:'true',facing:'south'}}", "{Name:'minecraft:hopper',Properties:{enabled:'true',facing:'south'}}"
		);
		method_21601(
			2468, "{Name:'minecraft:hopper',Properties:{enabled:'true',facing:'west'}}", "{Name:'minecraft:hopper',Properties:{enabled:'true',facing:'west'}}"
		);
		method_21601(
			2469, "{Name:'minecraft:hopper',Properties:{enabled:'true',facing:'east'}}", "{Name:'minecraft:hopper',Properties:{enabled:'true',facing:'east'}}"
		);
		method_21601(
			2472, "{Name:'minecraft:hopper',Properties:{enabled:'false',facing:'down'}}", "{Name:'minecraft:hopper',Properties:{enabled:'false',facing:'down'}}"
		);
		method_21601(
			2474, "{Name:'minecraft:hopper',Properties:{enabled:'false',facing:'north'}}", "{Name:'minecraft:hopper',Properties:{enabled:'false',facing:'north'}}"
		);
		method_21601(
			2475, "{Name:'minecraft:hopper',Properties:{enabled:'false',facing:'south'}}", "{Name:'minecraft:hopper',Properties:{enabled:'false',facing:'south'}}"
		);
		method_21601(
			2476, "{Name:'minecraft:hopper',Properties:{enabled:'false',facing:'west'}}", "{Name:'minecraft:hopper',Properties:{enabled:'false',facing:'west'}}"
		);
		method_21601(
			2477, "{Name:'minecraft:hopper',Properties:{enabled:'false',facing:'east'}}", "{Name:'minecraft:hopper',Properties:{enabled:'false',facing:'east'}}"
		);
		method_21601(2480, "{Name:'minecraft:quartz_block'}", "{Name:'minecraft:quartz_block',Properties:{variant:'default'}}");
		method_21601(2481, "{Name:'minecraft:chiseled_quartz_block'}", "{Name:'minecraft:quartz_block',Properties:{variant:'chiseled'}}");
		method_21601(2482, "{Name:'minecraft:quartz_pillar',Properties:{axis:'y'}}", "{Name:'minecraft:quartz_block',Properties:{variant:'lines_y'}}");
		method_21601(2483, "{Name:'minecraft:quartz_pillar',Properties:{axis:'x'}}", "{Name:'minecraft:quartz_block',Properties:{variant:'lines_x'}}");
		method_21601(2484, "{Name:'minecraft:quartz_pillar',Properties:{axis:'z'}}", "{Name:'minecraft:quartz_block',Properties:{variant:'lines_z'}}");
		method_21601(
			2496,
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2497,
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2498,
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2499,
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2500,
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			2501,
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			2502,
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			2503,
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:quartz_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(
			2512,
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'north_south'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'north_south'}}"
		);
		method_21601(
			2513,
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'east_west'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'east_west'}}"
		);
		method_21601(
			2514,
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'ascending_east'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'ascending_east'}}"
		);
		method_21601(
			2515,
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'ascending_west'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'ascending_west'}}"
		);
		method_21601(
			2516,
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'ascending_north'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'ascending_north'}}"
		);
		method_21601(
			2517,
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'ascending_south'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'false',shape:'ascending_south'}}"
		);
		method_21601(
			2520,
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'north_south'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'north_south'}}"
		);
		method_21601(
			2521,
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'east_west'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'east_west'}}"
		);
		method_21601(
			2522,
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'ascending_east'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'ascending_east'}}"
		);
		method_21601(
			2523,
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'ascending_west'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'ascending_west'}}"
		);
		method_21601(
			2524,
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'ascending_north'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'ascending_north'}}"
		);
		method_21601(
			2525,
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'ascending_south'}}",
			"{Name:'minecraft:activator_rail',Properties:{powered:'true',shape:'ascending_south'}}"
		);
		method_21601(
			2528, "{Name:'minecraft:dropper',Properties:{facing:'down',triggered:'false'}}", "{Name:'minecraft:dropper',Properties:{facing:'down',triggered:'false'}}"
		);
		method_21601(
			2529, "{Name:'minecraft:dropper',Properties:{facing:'up',triggered:'false'}}", "{Name:'minecraft:dropper',Properties:{facing:'up',triggered:'false'}}"
		);
		method_21601(
			2530, "{Name:'minecraft:dropper',Properties:{facing:'north',triggered:'false'}}", "{Name:'minecraft:dropper',Properties:{facing:'north',triggered:'false'}}"
		);
		method_21601(
			2531, "{Name:'minecraft:dropper',Properties:{facing:'south',triggered:'false'}}", "{Name:'minecraft:dropper',Properties:{facing:'south',triggered:'false'}}"
		);
		method_21601(
			2532, "{Name:'minecraft:dropper',Properties:{facing:'west',triggered:'false'}}", "{Name:'minecraft:dropper',Properties:{facing:'west',triggered:'false'}}"
		);
		method_21601(
			2533, "{Name:'minecraft:dropper',Properties:{facing:'east',triggered:'false'}}", "{Name:'minecraft:dropper',Properties:{facing:'east',triggered:'false'}}"
		);
		method_21601(
			2536, "{Name:'minecraft:dropper',Properties:{facing:'down',triggered:'true'}}", "{Name:'minecraft:dropper',Properties:{facing:'down',triggered:'true'}}"
		);
		method_21601(
			2537, "{Name:'minecraft:dropper',Properties:{facing:'up',triggered:'true'}}", "{Name:'minecraft:dropper',Properties:{facing:'up',triggered:'true'}}"
		);
		method_21601(
			2538, "{Name:'minecraft:dropper',Properties:{facing:'north',triggered:'true'}}", "{Name:'minecraft:dropper',Properties:{facing:'north',triggered:'true'}}"
		);
		method_21601(
			2539, "{Name:'minecraft:dropper',Properties:{facing:'south',triggered:'true'}}", "{Name:'minecraft:dropper',Properties:{facing:'south',triggered:'true'}}"
		);
		method_21601(
			2540, "{Name:'minecraft:dropper',Properties:{facing:'west',triggered:'true'}}", "{Name:'minecraft:dropper',Properties:{facing:'west',triggered:'true'}}"
		);
		method_21601(
			2541, "{Name:'minecraft:dropper',Properties:{facing:'east',triggered:'true'}}", "{Name:'minecraft:dropper',Properties:{facing:'east',triggered:'true'}}"
		);
		method_21601(2544, "{Name:'minecraft:white_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'white'}}");
		method_21601(2545, "{Name:'minecraft:orange_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'orange'}}");
		method_21601(2546, "{Name:'minecraft:magenta_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'magenta'}}");
		method_21601(2547, "{Name:'minecraft:light_blue_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'light_blue'}}");
		method_21601(2548, "{Name:'minecraft:yellow_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'yellow'}}");
		method_21601(2549, "{Name:'minecraft:lime_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'lime'}}");
		method_21601(2550, "{Name:'minecraft:pink_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'pink'}}");
		method_21601(2551, "{Name:'minecraft:gray_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'gray'}}");
		method_21601(2552, "{Name:'minecraft:light_gray_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'silver'}}");
		method_21601(2553, "{Name:'minecraft:cyan_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'cyan'}}");
		method_21601(2554, "{Name:'minecraft:purple_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'purple'}}");
		method_21601(2555, "{Name:'minecraft:blue_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'blue'}}");
		method_21601(2556, "{Name:'minecraft:brown_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'brown'}}");
		method_21601(2557, "{Name:'minecraft:green_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'green'}}");
		method_21601(2558, "{Name:'minecraft:red_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'red'}}");
		method_21601(2559, "{Name:'minecraft:black_terracotta'}", "{Name:'minecraft:stained_hardened_clay',Properties:{color:'black'}}");
		method_21601(
			2560,
			"{Name:'minecraft:white_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'white',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2561,
			"{Name:'minecraft:orange_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'orange',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2562,
			"{Name:'minecraft:magenta_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'magenta',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2563,
			"{Name:'minecraft:light_blue_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'light_blue',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2564,
			"{Name:'minecraft:yellow_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'yellow',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2565,
			"{Name:'minecraft:lime_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'lime',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2566,
			"{Name:'minecraft:pink_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'pink',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2567,
			"{Name:'minecraft:gray_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'gray',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2568,
			"{Name:'minecraft:light_gray_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'silver',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2569,
			"{Name:'minecraft:cyan_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'cyan',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2570,
			"{Name:'minecraft:purple_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'purple',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2571,
			"{Name:'minecraft:blue_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'blue',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2572,
			"{Name:'minecraft:brown_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'brown',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2573,
			"{Name:'minecraft:green_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'green',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2574,
			"{Name:'minecraft:red_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'red',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2575,
			"{Name:'minecraft:black_stained_glass_pane',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:stained_glass_pane',Properties:{color:'black',east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			2576,
			"{Name:'minecraft:acacia_leaves',Properties:{check_decay:'false',decayable:'true'}}",
			"{Name:'minecraft:leaves2',Properties:{check_decay:'false',decayable:'true',variant:'acacia'}}"
		);
		method_21601(
			2577,
			"{Name:'minecraft:dark_oak_leaves',Properties:{check_decay:'false',decayable:'true'}}",
			"{Name:'minecraft:leaves2',Properties:{check_decay:'false',decayable:'true',variant:'dark_oak'}}"
		);
		method_21601(
			2580,
			"{Name:'minecraft:acacia_leaves',Properties:{check_decay:'false',decayable:'false'}}",
			"{Name:'minecraft:leaves2',Properties:{check_decay:'false',decayable:'false',variant:'acacia'}}"
		);
		method_21601(
			2581,
			"{Name:'minecraft:dark_oak_leaves',Properties:{check_decay:'false',decayable:'false'}}",
			"{Name:'minecraft:leaves2',Properties:{check_decay:'false',decayable:'false',variant:'dark_oak'}}"
		);
		method_21601(
			2584,
			"{Name:'minecraft:acacia_leaves',Properties:{check_decay:'true',decayable:'true'}}",
			"{Name:'minecraft:leaves2',Properties:{check_decay:'true',decayable:'true',variant:'acacia'}}"
		);
		method_21601(
			2585,
			"{Name:'minecraft:dark_oak_leaves',Properties:{check_decay:'true',decayable:'true'}}",
			"{Name:'minecraft:leaves2',Properties:{check_decay:'true',decayable:'true',variant:'dark_oak'}}"
		);
		method_21601(
			2588,
			"{Name:'minecraft:acacia_leaves',Properties:{check_decay:'true',decayable:'false'}}",
			"{Name:'minecraft:leaves2',Properties:{check_decay:'true',decayable:'false',variant:'acacia'}}"
		);
		method_21601(
			2589,
			"{Name:'minecraft:dark_oak_leaves',Properties:{check_decay:'true',decayable:'false'}}",
			"{Name:'minecraft:leaves2',Properties:{check_decay:'true',decayable:'false',variant:'dark_oak'}}"
		);
		method_21601(2592, "{Name:'minecraft:acacia_log',Properties:{axis:'y'}}", "{Name:'minecraft:log2',Properties:{axis:'y',variant:'acacia'}}");
		method_21601(2593, "{Name:'minecraft:dark_oak_log',Properties:{axis:'y'}}", "{Name:'minecraft:log2',Properties:{axis:'y',variant:'dark_oak'}}");
		method_21601(2596, "{Name:'minecraft:acacia_log',Properties:{axis:'x'}}", "{Name:'minecraft:log2',Properties:{axis:'x',variant:'acacia'}}");
		method_21601(2597, "{Name:'minecraft:dark_oak_log',Properties:{axis:'x'}}", "{Name:'minecraft:log2',Properties:{axis:'x',variant:'dark_oak'}}");
		method_21601(2600, "{Name:'minecraft:acacia_log',Properties:{axis:'z'}}", "{Name:'minecraft:log2',Properties:{axis:'z',variant:'acacia'}}");
		method_21601(2601, "{Name:'minecraft:dark_oak_log',Properties:{axis:'z'}}", "{Name:'minecraft:log2',Properties:{axis:'z',variant:'dark_oak'}}");
		method_21601(2604, "{Name:'minecraft:acacia_bark'}", "{Name:'minecraft:log2',Properties:{axis:'none',variant:'acacia'}}");
		method_21601(2605, "{Name:'minecraft:dark_oak_bark'}", "{Name:'minecraft:log2',Properties:{axis:'none',variant:'dark_oak'}}");
		method_21601(
			2608,
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2609,
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2610,
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2611,
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2612,
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			2613,
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			2614,
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			2615,
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:acacia_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(
			2624,
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2625,
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2626,
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2627,
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2628,
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			2629,
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			2630,
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			2631,
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:dark_oak_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(2640, "{Name:'minecraft:slime_block'}", "{Name:'minecraft:slime'}");
		method_21601(2656, "{Name:'minecraft:barrier'}", "{Name:'minecraft:barrier'}");
		method_21601(
			2672,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'north',half:'bottom',open:'false'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'north',half:'bottom',open:'false'}}"
		);
		method_21601(
			2673,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'south',half:'bottom',open:'false'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'south',half:'bottom',open:'false'}}"
		);
		method_21601(
			2674,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'west',half:'bottom',open:'false'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'west',half:'bottom',open:'false'}}"
		);
		method_21601(
			2675,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'east',half:'bottom',open:'false'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'east',half:'bottom',open:'false'}}"
		);
		method_21601(
			2676,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'north',half:'bottom',open:'true'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'north',half:'bottom',open:'true'}}"
		);
		method_21601(
			2677,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'south',half:'bottom',open:'true'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'south',half:'bottom',open:'true'}}"
		);
		method_21601(
			2678,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'west',half:'bottom',open:'true'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'west',half:'bottom',open:'true'}}"
		);
		method_21601(
			2679,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'east',half:'bottom',open:'true'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'east',half:'bottom',open:'true'}}"
		);
		method_21601(
			2680,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'north',half:'top',open:'false'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'north',half:'top',open:'false'}}"
		);
		method_21601(
			2681,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'south',half:'top',open:'false'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'south',half:'top',open:'false'}}"
		);
		method_21601(
			2682,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'west',half:'top',open:'false'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'west',half:'top',open:'false'}}"
		);
		method_21601(
			2683,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'east',half:'top',open:'false'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'east',half:'top',open:'false'}}"
		);
		method_21601(
			2684,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'north',half:'top',open:'true'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'north',half:'top',open:'true'}}"
		);
		method_21601(
			2685,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'south',half:'top',open:'true'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'south',half:'top',open:'true'}}"
		);
		method_21601(
			2686,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'west',half:'top',open:'true'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'west',half:'top',open:'true'}}"
		);
		method_21601(
			2687,
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'east',half:'top',open:'true'}}",
			"{Name:'minecraft:iron_trapdoor',Properties:{facing:'east',half:'top',open:'true'}}"
		);
		method_21601(2688, "{Name:'minecraft:prismarine'}", "{Name:'minecraft:prismarine',Properties:{variant:'prismarine'}}");
		method_21601(2689, "{Name:'minecraft:prismarine_bricks'}", "{Name:'minecraft:prismarine',Properties:{variant:'prismarine_bricks'}}");
		method_21601(2690, "{Name:'minecraft:dark_prismarine'}", "{Name:'minecraft:prismarine',Properties:{variant:'dark_prismarine'}}");
		method_21601(2704, "{Name:'minecraft:sea_lantern'}", "{Name:'minecraft:sea_lantern'}");
		method_21601(2720, "{Name:'minecraft:hay_block',Properties:{axis:'y'}}", "{Name:'minecraft:hay_block',Properties:{axis:'y'}}");
		method_21601(2724, "{Name:'minecraft:hay_block',Properties:{axis:'x'}}", "{Name:'minecraft:hay_block',Properties:{axis:'x'}}");
		method_21601(2728, "{Name:'minecraft:hay_block',Properties:{axis:'z'}}", "{Name:'minecraft:hay_block',Properties:{axis:'z'}}");
		method_21601(2736, "{Name:'minecraft:white_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'white'}}");
		method_21601(2737, "{Name:'minecraft:orange_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'orange'}}");
		method_21601(2738, "{Name:'minecraft:magenta_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'magenta'}}");
		method_21601(2739, "{Name:'minecraft:light_blue_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'light_blue'}}");
		method_21601(2740, "{Name:'minecraft:yellow_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'yellow'}}");
		method_21601(2741, "{Name:'minecraft:lime_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'lime'}}");
		method_21601(2742, "{Name:'minecraft:pink_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'pink'}}");
		method_21601(2743, "{Name:'minecraft:gray_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'gray'}}");
		method_21601(2744, "{Name:'minecraft:light_gray_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'silver'}}");
		method_21601(2745, "{Name:'minecraft:cyan_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'cyan'}}");
		method_21601(2746, "{Name:'minecraft:purple_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'purple'}}");
		method_21601(2747, "{Name:'minecraft:blue_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'blue'}}");
		method_21601(2748, "{Name:'minecraft:brown_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'brown'}}");
		method_21601(2749, "{Name:'minecraft:green_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'green'}}");
		method_21601(2750, "{Name:'minecraft:red_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'red'}}");
		method_21601(2751, "{Name:'minecraft:black_carpet'}", "{Name:'minecraft:carpet',Properties:{color:'black'}}");
		method_21601(2752, "{Name:'minecraft:terracotta'}", "{Name:'minecraft:hardened_clay'}");
		method_21601(2768, "{Name:'minecraft:coal_block'}", "{Name:'minecraft:coal_block'}");
		method_21601(2784, "{Name:'minecraft:packed_ice'}", "{Name:'minecraft:packed_ice'}");
		method_21601(
			2800,
			"{Name:'minecraft:sunflower',Properties:{half:'lower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'lower',variant:'sunflower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'lower',variant:'sunflower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'lower',variant:'sunflower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'lower',variant:'sunflower'}}"
		);
		method_21601(
			2801,
			"{Name:'minecraft:lilac',Properties:{half:'lower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'lower',variant:'syringa'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'lower',variant:'syringa'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'lower',variant:'syringa'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'lower',variant:'syringa'}}"
		);
		method_21601(
			2802,
			"{Name:'minecraft:tall_grass',Properties:{half:'lower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'lower',variant:'double_grass'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'lower',variant:'double_grass'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'lower',variant:'double_grass'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'lower',variant:'double_grass'}}"
		);
		method_21601(
			2803,
			"{Name:'minecraft:large_fern',Properties:{half:'lower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'lower',variant:'double_fern'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'lower',variant:'double_fern'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'lower',variant:'double_fern'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'lower',variant:'double_fern'}}"
		);
		method_21601(
			2804,
			"{Name:'minecraft:rose_bush',Properties:{half:'lower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'lower',variant:'double_rose'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'lower',variant:'double_rose'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'lower',variant:'double_rose'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'lower',variant:'double_rose'}}"
		);
		method_21601(
			2805,
			"{Name:'minecraft:peony',Properties:{half:'lower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'lower',variant:'paeonia'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'lower',variant:'paeonia'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'lower',variant:'paeonia'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'lower',variant:'paeonia'}}"
		);
		method_21601(
			2808,
			"{Name:'minecraft:peony',Properties:{half:'upper'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'upper',variant:'double_fern'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'upper',variant:'double_grass'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'upper',variant:'double_rose'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'upper',variant:'paeonia'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'upper',variant:'sunflower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'south',half:'upper',variant:'syringa'}}"
		);
		method_21601(
			2809,
			"{Name:'minecraft:peony',Properties:{half:'upper'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'upper',variant:'double_fern'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'upper',variant:'double_grass'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'upper',variant:'double_rose'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'upper',variant:'paeonia'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'upper',variant:'sunflower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'west',half:'upper',variant:'syringa'}}"
		);
		method_21601(
			2810,
			"{Name:'minecraft:peony',Properties:{half:'upper'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'upper',variant:'double_fern'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'upper',variant:'double_grass'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'upper',variant:'double_rose'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'upper',variant:'paeonia'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'upper',variant:'sunflower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'north',half:'upper',variant:'syringa'}}"
		);
		method_21601(
			2811,
			"{Name:'minecraft:peony',Properties:{half:'upper'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'upper',variant:'double_fern'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'upper',variant:'double_grass'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'upper',variant:'double_rose'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'upper',variant:'paeonia'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'upper',variant:'sunflower'}}",
			"{Name:'minecraft:double_plant',Properties:{facing:'east',half:'upper',variant:'syringa'}}"
		);
		method_21601(2816, "{Name:'minecraft:white_banner',Properties:{rotation:'0'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'0'}}");
		method_21601(2817, "{Name:'minecraft:white_banner',Properties:{rotation:'1'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'1'}}");
		method_21601(2818, "{Name:'minecraft:white_banner',Properties:{rotation:'2'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'2'}}");
		method_21601(2819, "{Name:'minecraft:white_banner',Properties:{rotation:'3'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'3'}}");
		method_21601(2820, "{Name:'minecraft:white_banner',Properties:{rotation:'4'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'4'}}");
		method_21601(2821, "{Name:'minecraft:white_banner',Properties:{rotation:'5'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'5'}}");
		method_21601(2822, "{Name:'minecraft:white_banner',Properties:{rotation:'6'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'6'}}");
		method_21601(2823, "{Name:'minecraft:white_banner',Properties:{rotation:'7'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'7'}}");
		method_21601(2824, "{Name:'minecraft:white_banner',Properties:{rotation:'8'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'8'}}");
		method_21601(2825, "{Name:'minecraft:white_banner',Properties:{rotation:'9'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'9'}}");
		method_21601(2826, "{Name:'minecraft:white_banner',Properties:{rotation:'10'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'10'}}");
		method_21601(2827, "{Name:'minecraft:white_banner',Properties:{rotation:'11'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'11'}}");
		method_21601(2828, "{Name:'minecraft:white_banner',Properties:{rotation:'12'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'12'}}");
		method_21601(2829, "{Name:'minecraft:white_banner',Properties:{rotation:'13'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'13'}}");
		method_21601(2830, "{Name:'minecraft:white_banner',Properties:{rotation:'14'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'14'}}");
		method_21601(2831, "{Name:'minecraft:white_banner',Properties:{rotation:'15'}}", "{Name:'minecraft:standing_banner',Properties:{rotation:'15'}}");
		method_21601(2834, "{Name:'minecraft:white_wall_banner',Properties:{facing:'north'}}", "{Name:'minecraft:wall_banner',Properties:{facing:'north'}}");
		method_21601(2835, "{Name:'minecraft:white_wall_banner',Properties:{facing:'south'}}", "{Name:'minecraft:wall_banner',Properties:{facing:'south'}}");
		method_21601(2836, "{Name:'minecraft:white_wall_banner',Properties:{facing:'west'}}", "{Name:'minecraft:wall_banner',Properties:{facing:'west'}}");
		method_21601(2837, "{Name:'minecraft:white_wall_banner',Properties:{facing:'east'}}", "{Name:'minecraft:wall_banner',Properties:{facing:'east'}}");
		method_21601(
			2848, "{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'0'}}", "{Name:'minecraft:daylight_detector_inverted',Properties:{power:'0'}}"
		);
		method_21601(
			2849, "{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'1'}}", "{Name:'minecraft:daylight_detector_inverted',Properties:{power:'1'}}"
		);
		method_21601(
			2850, "{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'2'}}", "{Name:'minecraft:daylight_detector_inverted',Properties:{power:'2'}}"
		);
		method_21601(
			2851, "{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'3'}}", "{Name:'minecraft:daylight_detector_inverted',Properties:{power:'3'}}"
		);
		method_21601(
			2852, "{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'4'}}", "{Name:'minecraft:daylight_detector_inverted',Properties:{power:'4'}}"
		);
		method_21601(
			2853, "{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'5'}}", "{Name:'minecraft:daylight_detector_inverted',Properties:{power:'5'}}"
		);
		method_21601(
			2854, "{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'6'}}", "{Name:'minecraft:daylight_detector_inverted',Properties:{power:'6'}}"
		);
		method_21601(
			2855, "{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'7'}}", "{Name:'minecraft:daylight_detector_inverted',Properties:{power:'7'}}"
		);
		method_21601(
			2856, "{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'8'}}", "{Name:'minecraft:daylight_detector_inverted',Properties:{power:'8'}}"
		);
		method_21601(
			2857, "{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'9'}}", "{Name:'minecraft:daylight_detector_inverted',Properties:{power:'9'}}"
		);
		method_21601(
			2858,
			"{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'10'}}",
			"{Name:'minecraft:daylight_detector_inverted',Properties:{power:'10'}}"
		);
		method_21601(
			2859,
			"{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'11'}}",
			"{Name:'minecraft:daylight_detector_inverted',Properties:{power:'11'}}"
		);
		method_21601(
			2860,
			"{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'12'}}",
			"{Name:'minecraft:daylight_detector_inverted',Properties:{power:'12'}}"
		);
		method_21601(
			2861,
			"{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'13'}}",
			"{Name:'minecraft:daylight_detector_inverted',Properties:{power:'13'}}"
		);
		method_21601(
			2862,
			"{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'14'}}",
			"{Name:'minecraft:daylight_detector_inverted',Properties:{power:'14'}}"
		);
		method_21601(
			2863,
			"{Name:'minecraft:daylight_detector',Properties:{inverted:'true',power:'15'}}",
			"{Name:'minecraft:daylight_detector_inverted',Properties:{power:'15'}}"
		);
		method_21601(2864, "{Name:'minecraft:red_sandstone'}", "{Name:'minecraft:red_sandstone',Properties:{type:'red_sandstone'}}");
		method_21601(2865, "{Name:'minecraft:chiseled_red_sandstone'}", "{Name:'minecraft:red_sandstone',Properties:{type:'chiseled_red_sandstone'}}");
		method_21601(2866, "{Name:'minecraft:cut_red_sandstone'}", "{Name:'minecraft:red_sandstone',Properties:{type:'smooth_red_sandstone'}}");
		method_21601(
			2880,
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2881,
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2882,
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2883,
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			2884,
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			2885,
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			2886,
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			2887,
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:red_sandstone_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(
			2896,
			"{Name:'minecraft:red_sandstone_slab',Properties:{type:'double'}}",
			"{Name:'minecraft:double_stone_slab2',Properties:{seamless:'false',variant:'red_sandstone'}}"
		);
		method_21601(2904, "{Name:'minecraft:smooth_red_sandstone'}", "{Name:'minecraft:double_stone_slab2',Properties:{seamless:'true',variant:'red_sandstone'}}");
		method_21601(
			2912,
			"{Name:'minecraft:red_sandstone_slab',Properties:{type:'bottom'}}",
			"{Name:'minecraft:stone_slab2',Properties:{half:'bottom',variant:'red_sandstone'}}"
		);
		method_21601(
			2920, "{Name:'minecraft:red_sandstone_slab',Properties:{type:'top'}}", "{Name:'minecraft:stone_slab2',Properties:{half:'top',variant:'red_sandstone'}}"
		);
		method_21601(
			2928,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2929,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2930,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2931,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2932,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2933,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2934,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2935,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2936,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2937,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2938,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2939,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2940,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2941,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2942,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2943,
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2944,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2945,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2946,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2947,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2948,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2949,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2950,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2951,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2952,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2953,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2954,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2955,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2956,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2957,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2958,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2959,
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2960,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2961,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2962,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2963,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2964,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2965,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2966,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2967,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2968,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2969,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2970,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2971,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2972,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2973,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2974,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2975,
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2976,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2977,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2978,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2979,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2980,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2981,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2982,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2983,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2984,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2985,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2986,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2987,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			2988,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2989,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2990,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2991,
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			2992,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2993,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2994,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2995,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'false'}}"
		);
		method_21601(
			2996,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2997,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2998,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			2999,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'false'}}"
		);
		method_21601(
			3000,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			3001,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			3002,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			3003,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'false',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'true',open:'false',powered:'true'}}"
		);
		method_21601(
			3004,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'south',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			3005,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'west',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			3006,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'north',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			3007,
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'false',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_fence_gate',Properties:{facing:'east',in_wall:'true',open:'true',powered:'true'}}"
		);
		method_21601(
			3008,
			"{Name:'minecraft:spruce_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:spruce_fence',Properties:{east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			3024,
			"{Name:'minecraft:birch_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:birch_fence',Properties:{east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			3040,
			"{Name:'minecraft:jungle_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:jungle_fence',Properties:{east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			3056,
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:dark_oak_fence',Properties:{east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			3072,
			"{Name:'minecraft:acacia_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'false',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'false',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'false',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'false',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'false',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'false',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'false',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'false',north:'true',south:'true',west:'true'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'true',north:'false',south:'false',west:'false'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'true',north:'false',south:'false',west:'true'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'true',north:'false',south:'true',west:'false'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'true',north:'false',south:'true',west:'true'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'true',north:'true',south:'false',west:'false'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'true',north:'true',south:'false',west:'true'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'true',north:'true',south:'true',west:'false'}}",
			"{Name:'minecraft:acacia_fence',Properties:{east:'true',north:'true',south:'true',west:'true'}}"
		);
		method_21601(
			3088,
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3089,
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3090,
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3091,
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3092,
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3093,
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3094,
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3095,
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3096,
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"
		);
		method_21601(
			3097,
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"
		);
		method_21601(
			3098,
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"
		);
		method_21601(
			3099,
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:spruce_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3104,
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3105,
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3106,
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3107,
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3108,
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3109,
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3110,
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3111,
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3112,
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"
		);
		method_21601(
			3113,
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"
		);
		method_21601(
			3114,
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"
		);
		method_21601(
			3115,
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:birch_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3120,
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3121,
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3122,
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3123,
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3124,
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3125,
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3126,
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3127,
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3128,
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"
		);
		method_21601(
			3129,
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"
		);
		method_21601(
			3130,
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"
		);
		method_21601(
			3131,
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:jungle_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3136,
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3137,
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3138,
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3139,
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3140,
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3141,
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3142,
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3143,
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3144,
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"
		);
		method_21601(
			3145,
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"
		);
		method_21601(
			3146,
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"
		);
		method_21601(
			3147,
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:acacia_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3152,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3153,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3154,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3155,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"
		);
		method_21601(
			3156,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3157,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3158,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3159,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(
			3160,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"
		);
		method_21601(
			3161,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"
		);
		method_21601(
			3162,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"
		);
		method_21601(
			3163,
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}",
			"{Name:'minecraft:dark_oak_door',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"
		);
		method_21601(3168, "{Name:'minecraft:end_rod',Properties:{facing:'down'}}", "{Name:'minecraft:end_rod',Properties:{facing:'down'}}");
		method_21601(3169, "{Name:'minecraft:end_rod',Properties:{facing:'up'}}", "{Name:'minecraft:end_rod',Properties:{facing:'up'}}");
		method_21601(3170, "{Name:'minecraft:end_rod',Properties:{facing:'north'}}", "{Name:'minecraft:end_rod',Properties:{facing:'north'}}");
		method_21601(3171, "{Name:'minecraft:end_rod',Properties:{facing:'south'}}", "{Name:'minecraft:end_rod',Properties:{facing:'south'}}");
		method_21601(3172, "{Name:'minecraft:end_rod',Properties:{facing:'west'}}", "{Name:'minecraft:end_rod',Properties:{facing:'west'}}");
		method_21601(3173, "{Name:'minecraft:end_rod',Properties:{facing:'east'}}", "{Name:'minecraft:end_rod',Properties:{facing:'east'}}");
		method_21601(
			3184,
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'false',east:'true',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'false',north:'true',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'false',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'false',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'false',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'false',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'false',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'false',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'false',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'false',south:'true',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'true',south:'false',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'true',south:'false',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'true',south:'false',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'true',south:'false',up:'true',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'true',south:'true',up:'false',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'true',south:'true',up:'false',west:'true'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'true',south:'true',up:'true',west:'false'}}",
			"{Name:'minecraft:chorus_plant',Properties:{down:'true',east:'true',north:'true',south:'true',up:'true',west:'true'}}"
		);
		method_21601(3200, "{Name:'minecraft:chorus_flower',Properties:{age:'0'}}", "{Name:'minecraft:chorus_flower',Properties:{age:'0'}}");
		method_21601(3201, "{Name:'minecraft:chorus_flower',Properties:{age:'1'}}", "{Name:'minecraft:chorus_flower',Properties:{age:'1'}}");
		method_21601(3202, "{Name:'minecraft:chorus_flower',Properties:{age:'2'}}", "{Name:'minecraft:chorus_flower',Properties:{age:'2'}}");
		method_21601(3203, "{Name:'minecraft:chorus_flower',Properties:{age:'3'}}", "{Name:'minecraft:chorus_flower',Properties:{age:'3'}}");
		method_21601(3204, "{Name:'minecraft:chorus_flower',Properties:{age:'4'}}", "{Name:'minecraft:chorus_flower',Properties:{age:'4'}}");
		method_21601(3205, "{Name:'minecraft:chorus_flower',Properties:{age:'5'}}", "{Name:'minecraft:chorus_flower',Properties:{age:'5'}}");
		method_21601(3216, "{Name:'minecraft:purpur_block'}", "{Name:'minecraft:purpur_block'}");
		method_21601(3232, "{Name:'minecraft:purpur_pillar',Properties:{axis:'y'}}", "{Name:'minecraft:purpur_pillar',Properties:{axis:'y'}}");
		method_21601(3236, "{Name:'minecraft:purpur_pillar',Properties:{axis:'x'}}", "{Name:'minecraft:purpur_pillar',Properties:{axis:'x'}}");
		method_21601(3240, "{Name:'minecraft:purpur_pillar',Properties:{axis:'z'}}", "{Name:'minecraft:purpur_pillar',Properties:{axis:'z'}}");
		method_21601(
			3248,
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			3249,
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			3250,
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			3251,
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'bottom',shape:'inner_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'bottom',shape:'outer_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'bottom',shape:'straight'}}"
		);
		method_21601(
			3252,
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'east',half:'top',shape:'straight'}}"
		);
		method_21601(
			3253,
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'west',half:'top',shape:'straight'}}"
		);
		method_21601(
			3254,
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'south',half:'top',shape:'straight'}}"
		);
		method_21601(
			3255,
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'top',shape:'inner_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'top',shape:'inner_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'top',shape:'outer_left'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'top',shape:'outer_right'}}",
			"{Name:'minecraft:purpur_stairs',Properties:{facing:'north',half:'top',shape:'straight'}}"
		);
		method_21601(3264, "{Name:'minecraft:purpur_slab',Properties:{type:'double'}}", "{Name:'minecraft:purpur_double_slab',Properties:{variant:'default'}}");
		method_21601(3280, "{Name:'minecraft:purpur_slab',Properties:{type:'bottom'}}", "{Name:'minecraft:purpur_slab',Properties:{half:'bottom',variant:'default'}}");
		method_21601(3288, "{Name:'minecraft:purpur_slab',Properties:{type:'top'}}", "{Name:'minecraft:purpur_slab',Properties:{half:'top',variant:'default'}}");
		method_21601(3296, "{Name:'minecraft:end_stone_bricks'}", "{Name:'minecraft:end_bricks'}");
		method_21601(3312, "{Name:'minecraft:beetroots',Properties:{age:'0'}}", "{Name:'minecraft:beetroots',Properties:{age:'0'}}");
		method_21601(3313, "{Name:'minecraft:beetroots',Properties:{age:'1'}}", "{Name:'minecraft:beetroots',Properties:{age:'1'}}");
		method_21601(3314, "{Name:'minecraft:beetroots',Properties:{age:'2'}}", "{Name:'minecraft:beetroots',Properties:{age:'2'}}");
		method_21601(3315, "{Name:'minecraft:beetroots',Properties:{age:'3'}}", "{Name:'minecraft:beetroots',Properties:{age:'3'}}");
		method_21601(3328, "{Name:'minecraft:grass_path'}", "{Name:'minecraft:grass_path'}");
		method_21601(3344, "{Name:'minecraft:end_gateway'}", "{Name:'minecraft:end_gateway'}");
		method_21601(
			3360,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'down'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'down'}}"
		);
		method_21601(
			3361,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'up'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'up'}}"
		);
		method_21601(
			3362,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'north'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'north'}}"
		);
		method_21601(
			3363,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'south'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'south'}}"
		);
		method_21601(
			3364,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'west'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'west'}}"
		);
		method_21601(
			3365,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'east'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'false',facing:'east'}}"
		);
		method_21601(
			3368,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'down'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'down'}}"
		);
		method_21601(
			3369,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'up'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'up'}}"
		);
		method_21601(
			3370,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'north'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'north'}}"
		);
		method_21601(
			3371,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'south'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'south'}}"
		);
		method_21601(
			3372,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'west'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'west'}}"
		);
		method_21601(
			3373,
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'east'}}",
			"{Name:'minecraft:repeating_command_block',Properties:{conditional:'true',facing:'east'}}"
		);
		method_21601(
			3376,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'down'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'down'}}"
		);
		method_21601(
			3377,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'up'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'up'}}"
		);
		method_21601(
			3378,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'north'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'north'}}"
		);
		method_21601(
			3379,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'south'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'south'}}"
		);
		method_21601(
			3380,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'west'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'west'}}"
		);
		method_21601(
			3381,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'east'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'false',facing:'east'}}"
		);
		method_21601(
			3384,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'down'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'down'}}"
		);
		method_21601(
			3385,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'up'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'up'}}"
		);
		method_21601(
			3386,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'north'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'north'}}"
		);
		method_21601(
			3387,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'south'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'south'}}"
		);
		method_21601(
			3388,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'west'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'west'}}"
		);
		method_21601(
			3389,
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'east'}}",
			"{Name:'minecraft:chain_command_block',Properties:{conditional:'true',facing:'east'}}"
		);
		method_21601(3392, "{Name:'minecraft:frosted_ice',Properties:{age:'0'}}", "{Name:'minecraft:frosted_ice',Properties:{age:'0'}}");
		method_21601(3393, "{Name:'minecraft:frosted_ice',Properties:{age:'1'}}", "{Name:'minecraft:frosted_ice',Properties:{age:'1'}}");
		method_21601(3394, "{Name:'minecraft:frosted_ice',Properties:{age:'2'}}", "{Name:'minecraft:frosted_ice',Properties:{age:'2'}}");
		method_21601(3395, "{Name:'minecraft:frosted_ice',Properties:{age:'3'}}", "{Name:'minecraft:frosted_ice',Properties:{age:'3'}}");
		method_21601(3408, "{Name:'minecraft:magma_block'}", "{Name:'minecraft:magma'}");
		method_21601(3424, "{Name:'minecraft:nether_wart_block'}", "{Name:'minecraft:nether_wart_block'}");
		method_21601(3440, "{Name:'minecraft:red_nether_bricks'}", "{Name:'minecraft:red_nether_brick'}");
		method_21601(3456, "{Name:'minecraft:bone_block',Properties:{axis:'y'}}", "{Name:'minecraft:bone_block',Properties:{axis:'y'}}");
		method_21601(3460, "{Name:'minecraft:bone_block',Properties:{axis:'x'}}", "{Name:'minecraft:bone_block',Properties:{axis:'x'}}");
		method_21601(3464, "{Name:'minecraft:bone_block',Properties:{axis:'z'}}", "{Name:'minecraft:bone_block',Properties:{axis:'z'}}");
		method_21601(3472, "{Name:'minecraft:structure_void'}", "{Name:'minecraft:structure_void'}");
		method_21601(
			3488, "{Name:'minecraft:observer',Properties:{facing:'down',powered:'false'}}", "{Name:'minecraft:observer',Properties:{facing:'down',powered:'false'}}"
		);
		method_21601(
			3489, "{Name:'minecraft:observer',Properties:{facing:'up',powered:'false'}}", "{Name:'minecraft:observer',Properties:{facing:'up',powered:'false'}}"
		);
		method_21601(
			3490, "{Name:'minecraft:observer',Properties:{facing:'north',powered:'false'}}", "{Name:'minecraft:observer',Properties:{facing:'north',powered:'false'}}"
		);
		method_21601(
			3491, "{Name:'minecraft:observer',Properties:{facing:'south',powered:'false'}}", "{Name:'minecraft:observer',Properties:{facing:'south',powered:'false'}}"
		);
		method_21601(
			3492, "{Name:'minecraft:observer',Properties:{facing:'west',powered:'false'}}", "{Name:'minecraft:observer',Properties:{facing:'west',powered:'false'}}"
		);
		method_21601(
			3493, "{Name:'minecraft:observer',Properties:{facing:'east',powered:'false'}}", "{Name:'minecraft:observer',Properties:{facing:'east',powered:'false'}}"
		);
		method_21601(
			3496, "{Name:'minecraft:observer',Properties:{facing:'down',powered:'true'}}", "{Name:'minecraft:observer',Properties:{facing:'down',powered:'true'}}"
		);
		method_21601(
			3497, "{Name:'minecraft:observer',Properties:{facing:'up',powered:'true'}}", "{Name:'minecraft:observer',Properties:{facing:'up',powered:'true'}}"
		);
		method_21601(
			3498, "{Name:'minecraft:observer',Properties:{facing:'north',powered:'true'}}", "{Name:'minecraft:observer',Properties:{facing:'north',powered:'true'}}"
		);
		method_21601(
			3499, "{Name:'minecraft:observer',Properties:{facing:'south',powered:'true'}}", "{Name:'minecraft:observer',Properties:{facing:'south',powered:'true'}}"
		);
		method_21601(
			3500, "{Name:'minecraft:observer',Properties:{facing:'west',powered:'true'}}", "{Name:'minecraft:observer',Properties:{facing:'west',powered:'true'}}"
		);
		method_21601(
			3501, "{Name:'minecraft:observer',Properties:{facing:'east',powered:'true'}}", "{Name:'minecraft:observer',Properties:{facing:'east',powered:'true'}}"
		);
		method_21601(3504, "{Name:'minecraft:white_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:white_shulker_box',Properties:{facing:'down'}}");
		method_21601(3505, "{Name:'minecraft:white_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:white_shulker_box',Properties:{facing:'up'}}");
		method_21601(3506, "{Name:'minecraft:white_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:white_shulker_box',Properties:{facing:'north'}}");
		method_21601(3507, "{Name:'minecraft:white_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:white_shulker_box',Properties:{facing:'south'}}");
		method_21601(3508, "{Name:'minecraft:white_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:white_shulker_box',Properties:{facing:'west'}}");
		method_21601(3509, "{Name:'minecraft:white_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:white_shulker_box',Properties:{facing:'east'}}");
		method_21601(3520, "{Name:'minecraft:orange_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:orange_shulker_box',Properties:{facing:'down'}}");
		method_21601(3521, "{Name:'minecraft:orange_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:orange_shulker_box',Properties:{facing:'up'}}");
		method_21601(3522, "{Name:'minecraft:orange_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:orange_shulker_box',Properties:{facing:'north'}}");
		method_21601(3523, "{Name:'minecraft:orange_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:orange_shulker_box',Properties:{facing:'south'}}");
		method_21601(3524, "{Name:'minecraft:orange_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:orange_shulker_box',Properties:{facing:'west'}}");
		method_21601(3525, "{Name:'minecraft:orange_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:orange_shulker_box',Properties:{facing:'east'}}");
		method_21601(3536, "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'down'}}");
		method_21601(3537, "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'up'}}");
		method_21601(3538, "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'north'}}");
		method_21601(3539, "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'south'}}");
		method_21601(3540, "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'west'}}");
		method_21601(3541, "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:magenta_shulker_box',Properties:{facing:'east'}}");
		method_21601(
			3552, "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'down'}}"
		);
		method_21601(3553, "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'up'}}");
		method_21601(
			3554, "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'north'}}"
		);
		method_21601(
			3555, "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'south'}}"
		);
		method_21601(
			3556, "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'west'}}"
		);
		method_21601(
			3557, "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:light_blue_shulker_box',Properties:{facing:'east'}}"
		);
		method_21601(3568, "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'down'}}");
		method_21601(3569, "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'up'}}");
		method_21601(3570, "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'north'}}");
		method_21601(3571, "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'south'}}");
		method_21601(3572, "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'west'}}");
		method_21601(3573, "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:yellow_shulker_box',Properties:{facing:'east'}}");
		method_21601(3584, "{Name:'minecraft:lime_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:lime_shulker_box',Properties:{facing:'down'}}");
		method_21601(3585, "{Name:'minecraft:lime_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:lime_shulker_box',Properties:{facing:'up'}}");
		method_21601(3586, "{Name:'minecraft:lime_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:lime_shulker_box',Properties:{facing:'north'}}");
		method_21601(3587, "{Name:'minecraft:lime_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:lime_shulker_box',Properties:{facing:'south'}}");
		method_21601(3588, "{Name:'minecraft:lime_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:lime_shulker_box',Properties:{facing:'west'}}");
		method_21601(3589, "{Name:'minecraft:lime_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:lime_shulker_box',Properties:{facing:'east'}}");
		method_21601(3600, "{Name:'minecraft:pink_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:pink_shulker_box',Properties:{facing:'down'}}");
		method_21601(3601, "{Name:'minecraft:pink_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:pink_shulker_box',Properties:{facing:'up'}}");
		method_21601(3602, "{Name:'minecraft:pink_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:pink_shulker_box',Properties:{facing:'north'}}");
		method_21601(3603, "{Name:'minecraft:pink_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:pink_shulker_box',Properties:{facing:'south'}}");
		method_21601(3604, "{Name:'minecraft:pink_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:pink_shulker_box',Properties:{facing:'west'}}");
		method_21601(3605, "{Name:'minecraft:pink_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:pink_shulker_box',Properties:{facing:'east'}}");
		method_21601(3616, "{Name:'minecraft:gray_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:gray_shulker_box',Properties:{facing:'down'}}");
		method_21601(3617, "{Name:'minecraft:gray_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:gray_shulker_box',Properties:{facing:'up'}}");
		method_21601(3618, "{Name:'minecraft:gray_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:gray_shulker_box',Properties:{facing:'north'}}");
		method_21601(3619, "{Name:'minecraft:gray_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:gray_shulker_box',Properties:{facing:'south'}}");
		method_21601(3620, "{Name:'minecraft:gray_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:gray_shulker_box',Properties:{facing:'west'}}");
		method_21601(3621, "{Name:'minecraft:gray_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:gray_shulker_box',Properties:{facing:'east'}}");
		method_21601(3632, "{Name:'minecraft:light_gray_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:silver_shulker_box',Properties:{facing:'down'}}");
		method_21601(3633, "{Name:'minecraft:light_gray_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:silver_shulker_box',Properties:{facing:'up'}}");
		method_21601(
			3634, "{Name:'minecraft:light_gray_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:silver_shulker_box',Properties:{facing:'north'}}"
		);
		method_21601(
			3635, "{Name:'minecraft:light_gray_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:silver_shulker_box',Properties:{facing:'south'}}"
		);
		method_21601(3636, "{Name:'minecraft:light_gray_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:silver_shulker_box',Properties:{facing:'west'}}");
		method_21601(3637, "{Name:'minecraft:light_gray_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:silver_shulker_box',Properties:{facing:'east'}}");
		method_21601(3648, "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'down'}}");
		method_21601(3649, "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'up'}}");
		method_21601(3650, "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'north'}}");
		method_21601(3651, "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'south'}}");
		method_21601(3652, "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'west'}}");
		method_21601(3653, "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:cyan_shulker_box',Properties:{facing:'east'}}");
		method_21601(3664, "{Name:'minecraft:purple_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:purple_shulker_box',Properties:{facing:'down'}}");
		method_21601(3665, "{Name:'minecraft:purple_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:purple_shulker_box',Properties:{facing:'up'}}");
		method_21601(3666, "{Name:'minecraft:purple_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:purple_shulker_box',Properties:{facing:'north'}}");
		method_21601(3667, "{Name:'minecraft:purple_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:purple_shulker_box',Properties:{facing:'south'}}");
		method_21601(3668, "{Name:'minecraft:purple_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:purple_shulker_box',Properties:{facing:'west'}}");
		method_21601(3669, "{Name:'minecraft:purple_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:purple_shulker_box',Properties:{facing:'east'}}");
		method_21601(3680, "{Name:'minecraft:blue_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:blue_shulker_box',Properties:{facing:'down'}}");
		method_21601(3681, "{Name:'minecraft:blue_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:blue_shulker_box',Properties:{facing:'up'}}");
		method_21601(3682, "{Name:'minecraft:blue_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:blue_shulker_box',Properties:{facing:'north'}}");
		method_21601(3683, "{Name:'minecraft:blue_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:blue_shulker_box',Properties:{facing:'south'}}");
		method_21601(3684, "{Name:'minecraft:blue_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:blue_shulker_box',Properties:{facing:'west'}}");
		method_21601(3685, "{Name:'minecraft:blue_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:blue_shulker_box',Properties:{facing:'east'}}");
		method_21601(3696, "{Name:'minecraft:brown_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:brown_shulker_box',Properties:{facing:'down'}}");
		method_21601(3697, "{Name:'minecraft:brown_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:brown_shulker_box',Properties:{facing:'up'}}");
		method_21601(3698, "{Name:'minecraft:brown_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:brown_shulker_box',Properties:{facing:'north'}}");
		method_21601(3699, "{Name:'minecraft:brown_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:brown_shulker_box',Properties:{facing:'south'}}");
		method_21601(3700, "{Name:'minecraft:brown_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:brown_shulker_box',Properties:{facing:'west'}}");
		method_21601(3701, "{Name:'minecraft:brown_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:brown_shulker_box',Properties:{facing:'east'}}");
		method_21601(3712, "{Name:'minecraft:green_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:green_shulker_box',Properties:{facing:'down'}}");
		method_21601(3713, "{Name:'minecraft:green_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:green_shulker_box',Properties:{facing:'up'}}");
		method_21601(3714, "{Name:'minecraft:green_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:green_shulker_box',Properties:{facing:'north'}}");
		method_21601(3715, "{Name:'minecraft:green_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:green_shulker_box',Properties:{facing:'south'}}");
		method_21601(3716, "{Name:'minecraft:green_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:green_shulker_box',Properties:{facing:'west'}}");
		method_21601(3717, "{Name:'minecraft:green_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:green_shulker_box',Properties:{facing:'east'}}");
		method_21601(3728, "{Name:'minecraft:red_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:red_shulker_box',Properties:{facing:'down'}}");
		method_21601(3729, "{Name:'minecraft:red_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:red_shulker_box',Properties:{facing:'up'}}");
		method_21601(3730, "{Name:'minecraft:red_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:red_shulker_box',Properties:{facing:'north'}}");
		method_21601(3731, "{Name:'minecraft:red_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:red_shulker_box',Properties:{facing:'south'}}");
		method_21601(3732, "{Name:'minecraft:red_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:red_shulker_box',Properties:{facing:'west'}}");
		method_21601(3733, "{Name:'minecraft:red_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:red_shulker_box',Properties:{facing:'east'}}");
		method_21601(3744, "{Name:'minecraft:black_shulker_box',Properties:{facing:'down'}}", "{Name:'minecraft:black_shulker_box',Properties:{facing:'down'}}");
		method_21601(3745, "{Name:'minecraft:black_shulker_box',Properties:{facing:'up'}}", "{Name:'minecraft:black_shulker_box',Properties:{facing:'up'}}");
		method_21601(3746, "{Name:'minecraft:black_shulker_box',Properties:{facing:'north'}}", "{Name:'minecraft:black_shulker_box',Properties:{facing:'north'}}");
		method_21601(3747, "{Name:'minecraft:black_shulker_box',Properties:{facing:'south'}}", "{Name:'minecraft:black_shulker_box',Properties:{facing:'south'}}");
		method_21601(3748, "{Name:'minecraft:black_shulker_box',Properties:{facing:'west'}}", "{Name:'minecraft:black_shulker_box',Properties:{facing:'west'}}");
		method_21601(3749, "{Name:'minecraft:black_shulker_box',Properties:{facing:'east'}}", "{Name:'minecraft:black_shulker_box',Properties:{facing:'east'}}");
		method_21601(
			3760, "{Name:'minecraft:white_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:white_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3761, "{Name:'minecraft:white_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:white_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3762, "{Name:'minecraft:white_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:white_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3763, "{Name:'minecraft:white_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:white_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3776, "{Name:'minecraft:orange_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:orange_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3777, "{Name:'minecraft:orange_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:orange_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3778, "{Name:'minecraft:orange_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:orange_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3779, "{Name:'minecraft:orange_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:orange_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3792, "{Name:'minecraft:magenta_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:magenta_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3793, "{Name:'minecraft:magenta_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:magenta_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3794, "{Name:'minecraft:magenta_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:magenta_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3795, "{Name:'minecraft:magenta_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:magenta_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3808,
			"{Name:'minecraft:light_blue_glazed_terracotta',Properties:{facing:'south'}}",
			"{Name:'minecraft:light_blue_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3809,
			"{Name:'minecraft:light_blue_glazed_terracotta',Properties:{facing:'west'}}",
			"{Name:'minecraft:light_blue_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3810,
			"{Name:'minecraft:light_blue_glazed_terracotta',Properties:{facing:'north'}}",
			"{Name:'minecraft:light_blue_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3811,
			"{Name:'minecraft:light_blue_glazed_terracotta',Properties:{facing:'east'}}",
			"{Name:'minecraft:light_blue_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3824, "{Name:'minecraft:yellow_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:yellow_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3825, "{Name:'minecraft:yellow_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:yellow_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3826, "{Name:'minecraft:yellow_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:yellow_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3827, "{Name:'minecraft:yellow_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:yellow_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3840, "{Name:'minecraft:lime_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:lime_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3841, "{Name:'minecraft:lime_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:lime_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3842, "{Name:'minecraft:lime_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:lime_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3843, "{Name:'minecraft:lime_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:lime_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3856, "{Name:'minecraft:pink_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:pink_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3857, "{Name:'minecraft:pink_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:pink_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3858, "{Name:'minecraft:pink_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:pink_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3859, "{Name:'minecraft:pink_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:pink_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3872, "{Name:'minecraft:gray_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:gray_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3873, "{Name:'minecraft:gray_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:gray_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3874, "{Name:'minecraft:gray_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:gray_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3875, "{Name:'minecraft:gray_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:gray_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3888,
			"{Name:'minecraft:light_gray_glazed_terracotta',Properties:{facing:'south'}}",
			"{Name:'minecraft:silver_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3889, "{Name:'minecraft:light_gray_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:silver_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3890,
			"{Name:'minecraft:light_gray_glazed_terracotta',Properties:{facing:'north'}}",
			"{Name:'minecraft:silver_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3891, "{Name:'minecraft:light_gray_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:silver_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3904, "{Name:'minecraft:cyan_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:cyan_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3905, "{Name:'minecraft:cyan_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:cyan_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3906, "{Name:'minecraft:cyan_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:cyan_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3907, "{Name:'minecraft:cyan_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:cyan_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3920, "{Name:'minecraft:purple_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:purple_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3921, "{Name:'minecraft:purple_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:purple_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3922, "{Name:'minecraft:purple_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:purple_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3923, "{Name:'minecraft:purple_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:purple_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3936, "{Name:'minecraft:blue_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:blue_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3937, "{Name:'minecraft:blue_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:blue_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3938, "{Name:'minecraft:blue_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:blue_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3939, "{Name:'minecraft:blue_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:blue_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3952, "{Name:'minecraft:brown_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:brown_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3953, "{Name:'minecraft:brown_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:brown_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3954, "{Name:'minecraft:brown_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:brown_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3955, "{Name:'minecraft:brown_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:brown_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3968, "{Name:'minecraft:green_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:green_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3969, "{Name:'minecraft:green_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:green_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3970, "{Name:'minecraft:green_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:green_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3971, "{Name:'minecraft:green_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:green_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			3984, "{Name:'minecraft:red_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:red_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			3985, "{Name:'minecraft:red_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:red_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			3986, "{Name:'minecraft:red_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:red_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			3987, "{Name:'minecraft:red_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:red_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(
			4000, "{Name:'minecraft:black_glazed_terracotta',Properties:{facing:'south'}}", "{Name:'minecraft:black_glazed_terracotta',Properties:{facing:'south'}}"
		);
		method_21601(
			4001, "{Name:'minecraft:black_glazed_terracotta',Properties:{facing:'west'}}", "{Name:'minecraft:black_glazed_terracotta',Properties:{facing:'west'}}"
		);
		method_21601(
			4002, "{Name:'minecraft:black_glazed_terracotta',Properties:{facing:'north'}}", "{Name:'minecraft:black_glazed_terracotta',Properties:{facing:'north'}}"
		);
		method_21601(
			4003, "{Name:'minecraft:black_glazed_terracotta',Properties:{facing:'east'}}", "{Name:'minecraft:black_glazed_terracotta',Properties:{facing:'east'}}"
		);
		method_21601(4016, "{Name:'minecraft:white_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'white'}}");
		method_21601(4017, "{Name:'minecraft:orange_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'orange'}}");
		method_21601(4018, "{Name:'minecraft:magenta_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'magenta'}}");
		method_21601(4019, "{Name:'minecraft:light_blue_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'light_blue'}}");
		method_21601(4020, "{Name:'minecraft:yellow_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'yellow'}}");
		method_21601(4021, "{Name:'minecraft:lime_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'lime'}}");
		method_21601(4022, "{Name:'minecraft:pink_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'pink'}}");
		method_21601(4023, "{Name:'minecraft:gray_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'gray'}}");
		method_21601(4024, "{Name:'minecraft:light_gray_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'silver'}}");
		method_21601(4025, "{Name:'minecraft:cyan_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'cyan'}}");
		method_21601(4026, "{Name:'minecraft:purple_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'purple'}}");
		method_21601(4027, "{Name:'minecraft:blue_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'blue'}}");
		method_21601(4028, "{Name:'minecraft:brown_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'brown'}}");
		method_21601(4029, "{Name:'minecraft:green_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'green'}}");
		method_21601(4030, "{Name:'minecraft:red_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'red'}}");
		method_21601(4031, "{Name:'minecraft:black_concrete'}", "{Name:'minecraft:concrete',Properties:{color:'black'}}");
		method_21601(4032, "{Name:'minecraft:white_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'white'}}");
		method_21601(4033, "{Name:'minecraft:orange_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'orange'}}");
		method_21601(4034, "{Name:'minecraft:magenta_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'magenta'}}");
		method_21601(4035, "{Name:'minecraft:light_blue_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'light_blue'}}");
		method_21601(4036, "{Name:'minecraft:yellow_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'yellow'}}");
		method_21601(4037, "{Name:'minecraft:lime_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'lime'}}");
		method_21601(4038, "{Name:'minecraft:pink_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'pink'}}");
		method_21601(4039, "{Name:'minecraft:gray_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'gray'}}");
		method_21601(4040, "{Name:'minecraft:light_gray_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'silver'}}");
		method_21601(4041, "{Name:'minecraft:cyan_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'cyan'}}");
		method_21601(4042, "{Name:'minecraft:purple_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'purple'}}");
		method_21601(4043, "{Name:'minecraft:blue_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'blue'}}");
		method_21601(4044, "{Name:'minecraft:brown_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'brown'}}");
		method_21601(4045, "{Name:'minecraft:green_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'green'}}");
		method_21601(4046, "{Name:'minecraft:red_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'red'}}");
		method_21601(4047, "{Name:'minecraft:black_concrete_powder'}", "{Name:'minecraft:concrete_powder',Properties:{color:'black'}}");
		method_21601(4080, "{Name:'minecraft:structure_block',Properties:{mode:'save'}}", "{Name:'minecraft:structure_block',Properties:{mode:'save'}}");
		method_21601(4081, "{Name:'minecraft:structure_block',Properties:{mode:'load'}}", "{Name:'minecraft:structure_block',Properties:{mode:'load'}}");
		method_21601(4082, "{Name:'minecraft:structure_block',Properties:{mode:'corner'}}", "{Name:'minecraft:structure_block',Properties:{mode:'corner'}}");
		method_21601(4083, "{Name:'minecraft:structure_block',Properties:{mode:'data'}}", "{Name:'minecraft:structure_block',Properties:{mode:'data'}}");
	}
}
