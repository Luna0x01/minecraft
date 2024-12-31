package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.FlatWorldLayer;

public class FlatWorldHelper {
	private final List<FlatWorldLayer> layers = Lists.newArrayList();
	private final Map<String, Map<String, String>> structures = Maps.newHashMap();
	private int biomeId;

	public int getBiomeId() {
		return this.biomeId;
	}

	public void setBiomeId(int biomeId) {
		this.biomeId = biomeId;
	}

	public Map<String, Map<String, String>> getStructures() {
		return this.structures;
	}

	public List<FlatWorldLayer> getLayers() {
		return this.layers;
	}

	public void updateLayerLevel() {
		int i = 0;

		for (FlatWorldLayer flatWorldLayer : this.layers) {
			flatWorldLayer.setLayerLevel(i);
			i += flatWorldLayer.getThickness();
		}
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(3);
		stringBuilder.append(";");

		for (int i = 0; i < this.layers.size(); i++) {
			if (i > 0) {
				stringBuilder.append(",");
			}

			stringBuilder.append(((FlatWorldLayer)this.layers.get(i)).toString());
		}

		stringBuilder.append(";");
		stringBuilder.append(this.biomeId);
		if (!this.structures.isEmpty()) {
			stringBuilder.append(";");
			int j = 0;

			for (Entry<String, Map<String, String>> entry : this.structures.entrySet()) {
				if (j++ > 0) {
					stringBuilder.append(",");
				}

				stringBuilder.append(((String)entry.getKey()).toLowerCase());
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
		} else {
			stringBuilder.append(";");
		}

		return stringBuilder.toString();
	}

	private static FlatWorldLayer method_9228(int i, String string, int j) {
		String[] strings = i >= 3 ? string.split("\\*", 2) : string.split("x", 2);
		int k = 1;
		int l = 0;
		if (strings.length == 2) {
			try {
				k = Integer.parseInt(strings[0]);
				if (j + k >= 256) {
					k = 256 - j;
				}

				if (k < 0) {
					k = 0;
				}
			} catch (Throwable var8) {
				return null;
			}
		}

		Block block = null;

		try {
			String string2 = strings[strings.length - 1];
			if (i < 3) {
				strings = string2.split(":", 2);
				if (strings.length > 1) {
					l = Integer.parseInt(strings[1]);
				}

				block = Block.getById(Integer.parseInt(strings[0]));
			} else {
				strings = string2.split(":", 3);
				block = strings.length > 1 ? Block.get(strings[0] + ":" + strings[1]) : null;
				if (block != null) {
					l = strings.length > 2 ? Integer.parseInt(strings[2]) : 0;
				} else {
					block = Block.get(strings[0]);
					if (block != null) {
						l = strings.length > 1 ? Integer.parseInt(strings[1]) : 0;
					}
				}

				if (block == null) {
					return null;
				}
			}

			if (block == Blocks.AIR) {
				l = 0;
			}

			if (l < 0 || l > 15) {
				l = 0;
			}
		} catch (Throwable var9) {
			return null;
		}

		FlatWorldLayer flatWorldLayer = new FlatWorldLayer(i, k, block, l);
		flatWorldLayer.setLayerLevel(j);
		return flatWorldLayer;
	}

	private static List<FlatWorldLayer> method_9227(int i, String string) {
		if (string != null && string.length() >= 1) {
			List<FlatWorldLayer> list = Lists.newArrayList();
			String[] strings = string.split(",");
			int j = 0;

			for (String string2 : strings) {
				FlatWorldLayer flatWorldLayer = method_9228(i, string2, j);
				if (flatWorldLayer == null) {
					return null;
				}

				list.add(flatWorldLayer);
				j += flatWorldLayer.getThickness();
			}

			return list;
		} else {
			return null;
		}
	}

	public static FlatWorldHelper getHelper(String config) {
		if (config == null) {
			return createDefault();
		} else {
			String[] strings = config.split(";", -1);
			int i = strings.length == 1 ? 0 : MathHelper.parseInt(strings[0], 0);
			if (i >= 0 && i <= 3) {
				FlatWorldHelper flatWorldHelper = new FlatWorldHelper();
				int j = strings.length == 1 ? 0 : 1;
				List<FlatWorldLayer> list = method_9227(i, strings[j++]);
				if (list != null && !list.isEmpty()) {
					flatWorldHelper.getLayers().addAll(list);
					flatWorldHelper.updateLayerLevel();
					int k = Biome.PLAINS.id;
					if (i > 0 && strings.length > j) {
						k = MathHelper.parseInt(strings[j++], k);
					}

					flatWorldHelper.setBiomeId(k);
					if (i > 0 && strings.length > j) {
						String[] strings2 = strings[j++].toLowerCase().split(",");

						for (String string : strings2) {
							String[] strings4 = string.split("\\(", 2);
							Map<String, String> map = Maps.newHashMap();
							if (strings4[0].length() > 0) {
								flatWorldHelper.getStructures().put(strings4[0], map);
								if (strings4.length > 1 && strings4[1].endsWith(")") && strings4[1].length() > 1) {
									String[] strings5 = strings4[1].substring(0, strings4[1].length() - 1).split(" ");

									for (int n = 0; n < strings5.length; n++) {
										String[] strings6 = strings5[n].split("=", 2);
										if (strings6.length == 2) {
											map.put(strings6[0], strings6[1]);
										}
									}
								}
							}
						}
					} else {
						flatWorldHelper.getStructures().put("village", Maps.newHashMap());
					}

					return flatWorldHelper;
				} else {
					return createDefault();
				}
			} else {
				return createDefault();
			}
		}
	}

	public static FlatWorldHelper createDefault() {
		FlatWorldHelper flatWorldHelper = new FlatWorldHelper();
		flatWorldHelper.setBiomeId(Biome.PLAINS.id);
		flatWorldHelper.getLayers().add(new FlatWorldLayer(1, Blocks.BEDROCK));
		flatWorldHelper.getLayers().add(new FlatWorldLayer(2, Blocks.DIRT));
		flatWorldHelper.getLayers().add(new FlatWorldLayer(1, Blocks.GRASS));
		flatWorldHelper.updateLayerLevel();
		flatWorldHelper.getStructures().put("village", Maps.newHashMap());
		return flatWorldHelper;
	}
}
