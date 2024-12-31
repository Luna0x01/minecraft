package net.minecraft;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.VillagePieces;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.layer.FlatWorldLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3917 extends class_3797 {
	private static final Logger field_19309 = LogManager.getLogger();
	private static final class_3821<class_3866, class_3870> field_19310 = Biome.method_16433(
		class_3844.field_19182, new class_3866(0.004, class_3867.class_3014.NORMAL), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3911, class_3870> field_19311 = Biome.method_16433(
		class_3844.field_19181, new class_3911(0, VillagePieces.class_3996.OAK), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3900, class_3870> field_19312 = Biome.method_16433(
		class_3844.field_19189, new class_3900(), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3905, class_3870> field_19288 = Biome.method_16433(
		class_3844.field_19188, new class_3905(), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3835, class_3870> field_19289 = Biome.method_16433(
		class_3844.field_19185, new class_3835(), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3859, class_3870> field_19290 = Biome.method_16433(
		class_3844.field_19184, new class_3859(), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3855, class_3870> field_19291 = Biome.method_16433(
		class_3844.field_19186, new class_3855(), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3890, class_3870> field_19292 = Biome.method_16433(
		class_3844.field_19187, new class_3890(false), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3872, class_3870> field_19293 = Biome.method_16433(
		class_3844.field_19190, new class_3872(), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3864, class_3948> field_19294 = Biome.method_16433(
		class_3844.field_19167, new class_3864(Blocks.WATER), Biome.field_17541, new class_3948(4)
	);
	private static final class_3821<class_3864, class_3948> field_19295 = Biome.method_16433(
		class_3844.field_19167, new class_3864(Blocks.LAVA), Biome.field_17540, new class_3948(80)
	);
	private static final class_3821<class_3841, class_3870> field_19296 = Biome.method_16433(
		class_3844.field_19193, new class_3841(), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3914, class_3870> field_19297 = Biome.method_16433(
		class_3844.field_19183, new class_3914(), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3868, class_3870> field_19298 = Biome.method_16433(
		class_3844.field_19192, new class_3868(), Biome.field_17612, class_3830.field_19084
	);
	private static final class_3821<class_3874, class_3870> field_19299 = Biome.method_16433(
		class_3844.field_19191, new class_3874(class_3983.class_3985.COLD, 0.3F, 0.1F), Biome.field_17612, class_3830.field_19084
	);
	public static final Map<class_3821<?, ?>, class_3801.class_3803> field_19306 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put(field_19310, class_3801.class_3803.UNDERGROUND_STRUCTURES);
		hashMap.put(field_19311, class_3801.class_3803.SURFACE_STRUCTURES);
		hashMap.put(field_19312, class_3801.class_3803.UNDERGROUND_STRUCTURES);
		hashMap.put(field_19288, class_3801.class_3803.SURFACE_STRUCTURES);
		hashMap.put(field_19289, class_3801.class_3803.SURFACE_STRUCTURES);
		hashMap.put(field_19290, class_3801.class_3803.SURFACE_STRUCTURES);
		hashMap.put(field_19291, class_3801.class_3803.SURFACE_STRUCTURES);
		hashMap.put(field_19292, class_3801.class_3803.SURFACE_STRUCTURES);
		hashMap.put(field_19299, class_3801.class_3803.SURFACE_STRUCTURES);
		hashMap.put(field_19294, class_3801.class_3803.LOCAL_MODIFICATIONS);
		hashMap.put(field_19295, class_3801.class_3803.LOCAL_MODIFICATIONS);
		hashMap.put(field_19296, class_3801.class_3803.SURFACE_STRUCTURES);
		hashMap.put(field_19297, class_3801.class_3803.SURFACE_STRUCTURES);
		hashMap.put(field_19298, class_3801.class_3803.UNDERGROUND_STRUCTURES);
		hashMap.put(field_19293, class_3801.class_3803.SURFACE_STRUCTURES);
	});
	public static final Map<String, class_3821<?, ?>[]> field_19307 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put("mineshaft", new class_3821[]{field_19310});
		hashMap.put("village", new class_3821[]{field_19311});
		hashMap.put("stronghold", new class_3821[]{field_19312});
		hashMap.put("biome_1", new class_3821[]{field_19288, field_19289, field_19290, field_19291, field_19299, field_19292});
		hashMap.put("oceanmonument", new class_3821[]{field_19293});
		hashMap.put("lake", new class_3821[]{field_19294});
		hashMap.put("lava_lake", new class_3821[]{field_19295});
		hashMap.put("endcity", new class_3821[]{field_19296});
		hashMap.put("mansion", new class_3821[]{field_19297});
		hashMap.put("fortress", new class_3821[]{field_19298});
	});
	public static final Map<class_3821<?, ?>, class_3845> field_19308 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put(field_19310, new class_3866(0.004, class_3867.class_3014.NORMAL));
		hashMap.put(field_19311, new class_3911(0, VillagePieces.class_3996.OAK));
		hashMap.put(field_19312, new class_3900());
		hashMap.put(field_19288, new class_3905());
		hashMap.put(field_19289, new class_3835());
		hashMap.put(field_19290, new class_3859());
		hashMap.put(field_19291, new class_3855());
		hashMap.put(field_19299, new class_3874(class_3983.class_3985.COLD, 0.3F, 0.9F));
		hashMap.put(field_19292, new class_3890(false));
		hashMap.put(field_19293, new class_3872());
		hashMap.put(field_19296, new class_3841());
		hashMap.put(field_19297, new class_3914());
		hashMap.put(field_19298, new class_3868());
	});
	private final List<FlatWorldLayer> field_19300 = Lists.newArrayList();
	private final Map<String, Map<String, String>> field_19301 = Maps.newHashMap();
	private Biome field_19302;
	private final BlockState[] field_19303 = new BlockState[256];
	private boolean field_19304;
	private int field_19305;

	@Nullable
	public static Block method_17485(String string) {
		try {
			Identifier identifier = new Identifier(string);
			if (Registry.BLOCK.containsId(identifier)) {
				return Registry.BLOCK.get(identifier);
			}
		} catch (IllegalArgumentException var2) {
			field_19309.warn("Invalid blockstate: {}", string, var2);
		}

		return null;
	}

	public Biome method_17497() {
		return this.field_19302;
	}

	public void method_17476(Biome biome) {
		this.field_19302 = biome;
	}

	public Map<String, Map<String, String>> method_17498() {
		return this.field_19301;
	}

	public List<FlatWorldLayer> method_17499() {
		return this.field_19300;
	}

	public void method_17500() {
		int i = 0;

		for (FlatWorldLayer flatWorldLayer : this.field_19300) {
			flatWorldLayer.setLayerLevel(i);
			i += flatWorldLayer.getThickness();
		}

		this.field_19305 = 0;
		this.field_19304 = true;
		i = 0;

		for (FlatWorldLayer flatWorldLayer2 : this.field_19300) {
			for (int k = flatWorldLayer2.method_4111(); k < flatWorldLayer2.method_4111() + flatWorldLayer2.getThickness(); k++) {
				BlockState blockState = flatWorldLayer2.getBlockState();
				if (blockState.getBlock() != Blocks.AIR) {
					this.field_19304 = false;
					this.field_19303[k] = blockState;
				}
			}

			if (flatWorldLayer2.getBlockState().getBlock() == Blocks.AIR) {
				i += flatWorldLayer2.getThickness();
			} else {
				this.field_19305 = this.field_19305 + flatWorldLayer2.getThickness() + i;
				i = 0;
			}
		}
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < this.field_19300.size(); i++) {
			if (i > 0) {
				stringBuilder.append(",");
			}

			stringBuilder.append(this.field_19300.get(i));
		}

		stringBuilder.append(";");
		stringBuilder.append(Registry.BIOME.getId(this.field_19302));
		stringBuilder.append(";");
		if (!this.field_19301.isEmpty()) {
			int j = 0;

			for (Entry<String, Map<String, String>> entry : this.field_19301.entrySet()) {
				if (j++ > 0) {
					stringBuilder.append(",");
				}

				stringBuilder.append(((String)entry.getKey()).toLowerCase(Locale.ROOT));
				Map<String, String> map = (Map<String, String>)entry.getValue();
				if (!map.isEmpty()) {
					stringBuilder.append("(");
					int k = 0;

					for (Entry<String, String> entry2 : map.entrySet()) {
						if (k++ > 0) {
							stringBuilder.append(" ");
						}

						stringBuilder.append((String)entry2.getKey());
						stringBuilder.append("=");
						stringBuilder.append((String)entry2.getValue());
					}

					stringBuilder.append(")");
				}
			}
		}

		return stringBuilder.toString();
	}

	@Nullable
	private static FlatWorldLayer method_17486(String string, int i) {
		String[] strings = string.split("\\*", 2);
		int j;
		if (strings.length == 2) {
			try {
				j = MathHelper.clamp(Integer.parseInt(strings[0]), 0, 256 - i);
			} catch (NumberFormatException var7) {
				field_19309.error("Error while parsing flat world string => {}", var7.getMessage());
				return null;
			}
		} else {
			j = 1;
		}

		Block block;
		try {
			block = method_17485(strings[strings.length - 1]);
		} catch (Exception var6) {
			field_19309.error("Error while parsing flat world string => {}", var6.getMessage());
			return null;
		}

		if (block == null) {
			field_19309.error("Error while parsing flat world string => Unknown block, {}", strings[strings.length - 1]);
			return null;
		} else {
			FlatWorldLayer flatWorldLayer = new FlatWorldLayer(j, block);
			flatWorldLayer.setLayerLevel(i);
			return flatWorldLayer;
		}
	}

	private static List<FlatWorldLayer> method_17494(String string) {
		List<FlatWorldLayer> list = Lists.newArrayList();
		String[] strings = string.split(",");
		int i = 0;

		for (String string2 : strings) {
			FlatWorldLayer flatWorldLayer = method_17486(string2, i);
			if (flatWorldLayer == null) {
				return Collections.emptyList();
			}

			list.add(flatWorldLayer);
			i += flatWorldLayer.getThickness();
		}

		return list;
	}

	public <T> Dynamic<T> method_17481(DynamicOps<T> dynamicOps) {
		T object = (T)dynamicOps.createList(
			this.field_19300
				.stream()
				.map(
					flatWorldLayer -> dynamicOps.createMap(
							ImmutableMap.of(
								dynamicOps.createString("height"),
								dynamicOps.createInt(flatWorldLayer.getThickness()),
								dynamicOps.createString("block"),
								dynamicOps.createString(Registry.BLOCK.getId(flatWorldLayer.getBlockState().getBlock()).toString())
							)
						)
				)
		);
		T object2 = (T)dynamicOps.createMap(
			(Map)this.field_19301
				.entrySet()
				.stream()
				.map(
					entry -> Pair.of(
							dynamicOps.createString(((String)entry.getKey()).toLowerCase(Locale.ROOT)),
							dynamicOps.createMap(
								(Map)((Map)entry.getValue())
									.entrySet()
									.stream()
									.map(entryx -> Pair.of(dynamicOps.createString((String)entryx.getKey()), dynamicOps.createString((String)entryx.getValue())))
									.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
							)
						)
				)
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
		);
		return new Dynamic(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("layers"),
					object,
					dynamicOps.createString("biome"),
					dynamicOps.createString(Registry.BIOME.getId(this.field_19302).toString()),
					dynamicOps.createString("structures"),
					object2
				)
			)
		);
	}

	public static class_3917 method_17480(Dynamic<?> dynamic) {
		class_3917 lv = ChunkGeneratorType.FLAT.method_17040();
		List<Pair<Integer, Block>> list = (List<Pair<Integer, Block>>)((Stream)dynamic.get("layers").flatMap(Dynamic::getStream).orElse(Stream.empty()))
			.map(dynamicx -> Pair.of(dynamicx.getInt("height", 1), method_17485(dynamicx.getString("block"))))
			.collect(Collectors.toList());
		if (list.stream().anyMatch(pair -> pair.getSecond() == null)) {
			return method_17501();
		} else {
			List<FlatWorldLayer> list2 = (List<FlatWorldLayer>)list.stream()
				.map(pair -> new FlatWorldLayer((Integer)pair.getFirst(), (Block)pair.getSecond()))
				.collect(Collectors.toList());
			if (list2.isEmpty()) {
				return method_17501();
			} else {
				lv.method_17499().addAll(list2);
				lv.method_17500();
				lv.method_17476(Registry.BIOME.getByIdentifier(new Identifier(dynamic.getString("biome"))));
				dynamic.get("structures")
					.flatMap(Dynamic::getMapValues)
					.ifPresent(map -> map.keySet().forEach(dynamicx -> dynamicx.getStringValue().map(string -> (Map)lv.method_17498().put(string, Maps.newHashMap()))));
				return lv;
			}
		}
	}

	public static class_3917 method_17492(String string) {
		Iterator<String> iterator = Splitter.on(';').split(string).iterator();
		if (!iterator.hasNext()) {
			return method_17501();
		} else {
			class_3917 lv = ChunkGeneratorType.FLAT.method_17040();
			List<FlatWorldLayer> list = method_17494((String)iterator.next());
			if (list.isEmpty()) {
				return method_17501();
			} else {
				lv.method_17499().addAll(list);
				lv.method_17500();
				Biome biome = iterator.hasNext() ? Registry.BIOME.getByIdentifier(new Identifier((String)iterator.next())) : null;
				lv.method_17476(biome == null ? Biomes.PLAINS : biome);
				if (iterator.hasNext()) {
					String[] strings = ((String)iterator.next()).toLowerCase(Locale.ROOT).split(",");

					for (String string2 : strings) {
						String[] strings2 = string2.split("\\(", 2);
						if (!strings2[0].isEmpty()) {
							lv.method_17496(strings2[0]);
							if (strings2.length > 1 && strings2[1].endsWith(")") && strings2[1].length() > 1) {
								String[] strings3 = strings2[1].substring(0, strings2[1].length() - 1).split(" ");

								for (String string3 : strings3) {
									String[] strings4 = string3.split("=", 2);
									if (strings4.length == 2) {
										lv.method_17487(strings2[0], strings4[0], strings4[1]);
									}
								}
							}
						}
					}
				} else {
					lv.method_17498().put("village", Maps.newHashMap());
				}

				return lv;
			}
		}
	}

	private void method_17496(String string) {
		Map<String, String> map = Maps.newHashMap();
		this.field_19301.put(string, map);
	}

	private void method_17487(String string, String string2, String string3) {
		((Map)this.field_19301.get(string)).put(string2, string3);
		if ("village".equals(string) && "distance".equals(string2)) {
			this.field_18969 = MathHelper.parseInt(string3, this.field_18969, 9);
		}

		if ("biome_1".equals(string) && "distance".equals(string2)) {
			this.field_18976 = MathHelper.parseInt(string3, this.field_18976, 9);
		}

		if ("stronghold".equals(string)) {
			if ("distance".equals(string2)) {
				this.field_18973 = MathHelper.parseInt(string3, this.field_18973, 1);
			} else if ("count".equals(string2)) {
				this.field_18974 = MathHelper.parseInt(string3, this.field_18974, 1);
			} else if ("spread".equals(string2)) {
				this.field_18975 = MathHelper.parseInt(string3, this.field_18975, 1);
			}
		}

		if ("oceanmonument".equals(string)) {
			if ("separation".equals(string2)) {
				this.field_18972 = MathHelper.parseInt(string3, this.field_18972, 1);
			} else if ("spacing".equals(string2)) {
				this.field_18971 = MathHelper.parseInt(string3, this.field_18971, 1);
			}
		}

		if ("endcity".equals(string) && "distance".equals(string2)) {
			this.field_18980 = MathHelper.parseInt(string3, this.field_18980, 1);
		}

		if ("mansion".equals(string) && "distance".equals(string2)) {
			this.field_18984 = MathHelper.parseInt(string3, this.field_18984, 1);
		}
	}

	public static class_3917 method_17501() {
		class_3917 lv = ChunkGeneratorType.FLAT.method_17040();
		lv.method_17476(Biomes.PLAINS);
		lv.method_17499().add(new FlatWorldLayer(1, Blocks.BEDROCK));
		lv.method_17499().add(new FlatWorldLayer(2, Blocks.DIRT));
		lv.method_17499().add(new FlatWorldLayer(1, Blocks.GRASS_BLOCK));
		lv.method_17500();
		lv.method_17498().put("village", Maps.newHashMap());
		return lv;
	}

	public boolean method_17502() {
		return this.field_19304;
	}

	public BlockState[] method_17475() {
		return this.field_19303;
	}
}
