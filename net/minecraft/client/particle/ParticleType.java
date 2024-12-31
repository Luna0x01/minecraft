package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

public enum ParticleType {
	EXPLOSION("explode", 0, true),
	LARGE_EXPLOSION("largeexplode", 1, true),
	HUGE_EXPLOSION("hugeexplosion", 2, true),
	FIREWORK_SPARK("fireworksSpark", 3, false),
	BUBBLE("bubble", 4, false),
	WATER("splash", 5, false),
	WATER_WAKE("wake", 6, false),
	SUSPENDED("suspended", 7, false),
	SUSPENDED_DEPTH("depthsuspend", 8, false),
	CRIT("crit", 9, false),
	CRIT_MAGIC("magicCrit", 10, false),
	SMOKE("smoke", 11, false),
	SMOKE_LARGE("largesmoke", 12, false),
	SPELL("spell", 13, false),
	INSTANT_SPELL("instantSpell", 14, false),
	MOB_SPELL("mobSpell", 15, false),
	AMBIENT_MOB_SPELL("mobSpellAmbient", 16, false),
	WITCH_SPELL("witchMagic", 17, false),
	WATER_DRIP("dripWater", 18, false),
	LAVA_DRIP("dripLava", 19, false),
	ANGRY_VILLAGER("angryVillager", 20, false),
	HAPPY_VILLAGER("happyVillager", 21, false),
	TOWN_AURA("townaura", 22, false),
	NOTE("note", 23, false),
	NETHER_PORTAL("portal", 24, false),
	ENCHANTMENT_TABLE("enchantmenttable", 25, false),
	FIRE("flame", 26, false),
	LAVA("lava", 27, false),
	FOOTSTEP("footstep", 28, false),
	CLOUD("cloud", 29, false),
	REDSTONE("reddust", 30, false),
	SNOWBALL("snowballpoof", 31, false),
	SNOW_SHOVEL("snowshovel", 32, false),
	SLIME("slime", 33, false),
	HEART("heart", 34, false),
	BARRIER("barrier", 35, false),
	ITEM_CRACK("iconcrack_", 36, false, 2),
	BLOCK_CRACK("blockcrack_", 37, false, 1),
	BLOCK_DUST("blockdust_", 38, false, 1),
	WATER_DROP("droplet", 39, false),
	ITEM_TAKE("take", 40, false),
	MOB_APPEARANCE("mobappearance", 41, true);

	private final String name;
	private final int id;
	private final boolean alwaysShow;
	private final int arguments;
	private static final Map<Integer, ParticleType> TYPES = Maps.newHashMap();
	private static final String[] NAMES;

	private ParticleType(String string2, int j, boolean bl, int k) {
		this.name = string2;
		this.id = j;
		this.alwaysShow = bl;
		this.arguments = k;
	}

	private ParticleType(String string2, int j, boolean bl) {
		this(string2, j, bl, 0);
	}

	public static String[] getNames() {
		return NAMES;
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	public int getArgs() {
		return this.arguments;
	}

	public boolean getAlwaysShow() {
		return this.alwaysShow;
	}

	public boolean hasArguments() {
		return this.arguments > 0;
	}

	public static ParticleType getById(int id) {
		return (ParticleType)TYPES.get(id);
	}

	static {
		List<String> list = Lists.newArrayList();

		for (ParticleType particleType : values()) {
			TYPES.put(particleType.getId(), particleType);
			if (!particleType.getName().endsWith("_")) {
				list.add(particleType.getName());
			}
		}

		NAMES = (String[])list.toArray(new String[list.size()]);
	}
}
