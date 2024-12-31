package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3056;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.PrismarineBlock;
import net.minecraft.block.RedSandstoneBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.StoneBrickBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;

public class Item {
	public static final SimpleRegistry<Identifier, Item> REGISTRY = new SimpleRegistry<>();
	private static final Map<Block, Item> BLOCK_ITEMS = Maps.newHashMap();
	private static final ItemPropertyGetter GETTER_DAMAGED = new ItemPropertyGetter() {
		@Override
		public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
			return stack.isDamaged() ? 1.0F : 0.0F;
		}
	};
	private static final ItemPropertyGetter GETTER_DAMAGE = new ItemPropertyGetter() {
		@Override
		public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
			return MathHelper.clamp((float)stack.getDamage() / (float)stack.getMaxDamage(), 0.0F, 1.0F);
		}
	};
	private static final ItemPropertyGetter GETTER_HAND = new ItemPropertyGetter() {
		@Override
		public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
			return entity != null && entity.getDurability() != HandOption.RIGHT ? 1.0F : 0.0F;
		}
	};
	private static final ItemPropertyGetter GETTER_COOLDOWN = new ItemPropertyGetter() {
		@Override
		public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
			return entity instanceof PlayerEntity ? ((PlayerEntity)entity).getItemCooldownManager().getCooldownProgress(stack.getItem(), 0.0F) : 0.0F;
		}
	};
	private final Registry<Identifier, ItemPropertyGetter> field_12303 = new MutableRegistry<>();
	protected static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	private ItemGroup group;
	protected static Random RANDOM = new Random();
	protected int maxCount = 64;
	private int maxDamage;
	protected boolean handheld;
	protected boolean damageable;
	private Item recipeRemainder;
	private String translationKey;

	public static int getRawId(Item item) {
		return item == null ? 0 : REGISTRY.getRawId(item);
	}

	public static Item byRawId(int id) {
		return REGISTRY.getByRawId(id);
	}

	public static Item fromBlock(Block block) {
		Item item = (Item)BLOCK_ITEMS.get(block);
		return item == null ? Items.AIR : item;
	}

	@Nullable
	public static Item getFromId(String id) {
		Item item = REGISTRY.get(new Identifier(id));
		if (item == null) {
			try {
				return byRawId(Integer.parseInt(id));
			} catch (NumberFormatException var3) {
			}
		}

		return item;
	}

	public ItemStack getDefaultStack() {
		return new ItemStack(this);
	}

	public final void addProperty(Identifier identifier, ItemPropertyGetter itemPropertyGetter) {
		this.field_12303.put(identifier, itemPropertyGetter);
	}

	@Nullable
	public ItemPropertyGetter getProperty(Identifier identifier) {
		return this.field_12303.get(identifier);
	}

	public boolean hasProperties() {
		return !this.field_12303.getKeySet().isEmpty();
	}

	public boolean postProcessNbt(NbtCompound nbt) {
		return false;
	}

	public Item() {
		this.addProperty(new Identifier("lefthanded"), GETTER_HAND);
		this.addProperty(new Identifier("cooldown"), GETTER_COOLDOWN);
	}

	public Item setMaxCount(int count) {
		this.maxCount = count;
		return this;
	}

	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		return ActionResult.PASS;
	}

	public float getBlockBreakingSpeed(ItemStack stack, BlockState state) {
		return 1.0F;
	}

	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
	}

	public ItemStack method_3367(ItemStack stack, World world, LivingEntity entity) {
		return stack;
	}

	public int getMaxCount() {
		return this.maxCount;
	}

	public int getMeta(int i) {
		return 0;
	}

	public boolean isUnbreakable() {
		return this.damageable;
	}

	protected Item setUnbreakable(boolean unbreakable) {
		this.damageable = unbreakable;
		return this;
	}

	public int getMaxDamage() {
		return this.maxDamage;
	}

	protected Item setMaxDamage(int damage) {
		this.maxDamage = damage;
		if (damage > 0) {
			this.addProperty(new Identifier("damaged"), GETTER_DAMAGED);
			this.addProperty(new Identifier("damage"), GETTER_DAMAGE);
		}

		return this;
	}

	public boolean isDamageable() {
		return this.maxDamage > 0 && (!this.damageable || this.maxCount == 1);
	}

	public boolean onEntityHit(ItemStack stack, LivingEntity entity1, LivingEntity entity2) {
		return false;
	}

	public boolean method_3356(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		return false;
	}

	public boolean method_3346(BlockState blockState) {
		return false;
	}

	public boolean method_3353(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		return false;
	}

	public Item setHandheld() {
		this.handheld = true;
		return this;
	}

	public boolean isHandheld() {
		return this.handheld;
	}

	public boolean shouldRotate() {
		return false;
	}

	public Item setTranslationKey(String translationKey) {
		this.translationKey = translationKey;
		return this;
	}

	public String getItemStackTranslatedName(ItemStack stack) {
		return CommonI18n.translate(this.getTranslationKey(stack));
	}

	public String getTranslationKey() {
		return "item." + this.translationKey;
	}

	public String getTranslationKey(ItemStack stack) {
		return "item." + this.translationKey;
	}

	public Item setRecipeRemainder(Item recipeRemainder) {
		this.recipeRemainder = recipeRemainder;
		return this;
	}

	public boolean shouldSyncNbtToClient() {
		return true;
	}

	@Nullable
	public Item getRecipeRemainder() {
		return this.recipeRemainder;
	}

	public boolean isFood() {
		return this.recipeRemainder != null;
	}

	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
	}

	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
	}

	public boolean isNetworkSynced() {
		return false;
	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}

	public int getMaxUseTime(ItemStack stack) {
		return 0;
	}

	public void method_3359(ItemStack stack, World world, LivingEntity entity, int i) {
	}

	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
	}

	public String getDisplayName(ItemStack stack) {
		return CommonI18n.translate(this.getItemStackTranslatedName(stack) + ".name").trim();
	}

	public boolean hasEnchantmentGlint(ItemStack stack) {
		return stack.hasEnchantments();
	}

	public Rarity getRarity(ItemStack stack) {
		return stack.hasEnchantments() ? Rarity.RARE : Rarity.COMMON;
	}

	public boolean isEnchantable(ItemStack stack) {
		return this.getMaxCount() == 1 && this.isDamageable();
	}

	protected BlockHitResult onHit(World world, PlayerEntity player, boolean liquid) {
		float f = player.pitch;
		float g = player.yaw;
		double d = player.x;
		double e = player.y + (double)player.getEyeHeight();
		double h = player.z;
		Vec3d vec3d = new Vec3d(d, e, h);
		float i = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float k = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
		float l = MathHelper.sin(-f * (float) (Math.PI / 180.0));
		float m = j * k;
		float o = i * k;
		double p = 5.0;
		Vec3d vec3d2 = vec3d.add((double)m * 5.0, (double)l * 5.0, (double)o * 5.0);
		return world.rayTrace(vec3d, vec3d2, liquid, !liquid, false);
	}

	public int getEnchantability() {
		return 0;
	}

	public void method_13648(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		defaultedList.add(new ItemStack(item));
	}

	@Nullable
	public ItemGroup getItemGroup() {
		return this.group;
	}

	public Item setItemGroup(ItemGroup group) {
		this.group = group;
		return this;
	}

	public boolean hasSubTypes() {
		return false;
	}

	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return false;
	}

	public Multimap<String, AttributeModifier> method_6326(EquipmentSlot equipmentSlot) {
		return HashMultimap.create();
	}

	public static void setup() {
		registerBlockItem(Blocks.AIR, new class_3056(Blocks.AIR));
		registerBlockItem(Blocks.STONE, new VariantBlockItem(Blocks.STONE, Blocks.STONE, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return StoneBlock.StoneType.getById(itemStack.getData()).getTranslationKey();
			}
		}).setTranslationKey("stone"));
		registerBlockItem(Blocks.GRASS, new GrassBlockItem(Blocks.GRASS, false));
		registerBlockItem(Blocks.DIRT, new VariantBlockItem(Blocks.DIRT, Blocks.DIRT, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return DirtBlock.DirtType.getById(itemStack.getData()).getStateName();
			}
		}).setTranslationKey("dirt"));
		registerBlockItem(Blocks.COBBLESTONE);
		registerBlockItem(Blocks.PLANKS, new VariantBlockItem(Blocks.PLANKS, Blocks.PLANKS, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return PlanksBlock.WoodType.getById(itemStack.getData()).getOldName();
			}
		}).setTranslationKey("wood"));
		registerBlockItem(Blocks.SAPLING, new VariantBlockItem(Blocks.SAPLING, Blocks.SAPLING, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return PlanksBlock.WoodType.getById(itemStack.getData()).getOldName();
			}
		}).setTranslationKey("sapling"));
		registerBlockItem(Blocks.BEDROCK);
		registerBlockItem(Blocks.SAND, new VariantBlockItem(Blocks.SAND, Blocks.SAND, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return SandBlock.SandType.getById(itemStack.getData()).getTranslationKey();
			}
		}).setTranslationKey("sand"));
		registerBlockItem(Blocks.GRAVEL);
		registerBlockItem(Blocks.GOLD_ORE);
		registerBlockItem(Blocks.IRON_ORE);
		registerBlockItem(Blocks.COAL_ORE);
		registerBlockItem(Blocks.LOG, new VariantBlockItem(Blocks.LOG, Blocks.LOG, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return PlanksBlock.WoodType.getById(itemStack.getData()).getOldName();
			}
		}).setTranslationKey("log"));
		registerBlockItem(Blocks.LOG2, new VariantBlockItem(Blocks.LOG2, Blocks.LOG2, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return PlanksBlock.WoodType.getById(itemStack.getData() + 4).getOldName();
			}
		}).setTranslationKey("log"));
		registerBlockItem(Blocks.LEAVES, new LeavesItem(Blocks.LEAVES).setTranslationKey("leaves"));
		registerBlockItem(Blocks.LEAVES2, new LeavesItem(Blocks.LEAVES2).setTranslationKey("leaves"));
		registerBlockItem(Blocks.SPONGE, new VariantBlockItem(Blocks.SPONGE, Blocks.SPONGE, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return (itemStack.getData() & 1) == 1 ? "wet" : "dry";
			}
		}).setTranslationKey("sponge"));
		registerBlockItem(Blocks.GLASS);
		registerBlockItem(Blocks.LAPIS_LAZULI_ORE);
		registerBlockItem(Blocks.LAPIS_LAZULI_BLOCK);
		registerBlockItem(Blocks.DISPENSER);
		registerBlockItem(Blocks.SANDSTONE, new VariantBlockItem(Blocks.SANDSTONE, Blocks.SANDSTONE, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return SandstoneBlock.SandstoneType.getById(itemStack.getData()).getBlockStateName();
			}
		}).setTranslationKey("sandStone"));
		registerBlockItem(Blocks.NOTEBLOCK);
		registerBlockItem(Blocks.POWERED_RAIL);
		registerBlockItem(Blocks.DETECTOR_RAIL);
		registerBlockItem(Blocks.STICKY_PISTON, new StickyPistonBlockItem(Blocks.STICKY_PISTON));
		registerBlockItem(Blocks.COBWEB);
		registerBlockItem(Blocks.TALLGRASS, new GrassBlockItem(Blocks.TALLGRASS, true).setNamed(new String[]{"shrub", "grass", "fern"}));
		registerBlockItem(Blocks.DEADBUSH);
		registerBlockItem(Blocks.PISTON, new StickyPistonBlockItem(Blocks.PISTON));
		registerBlockItem(Blocks.WOOL, new WoolItem(Blocks.WOOL).setTranslationKey("cloth"));
		registerBlockItem(Blocks.YELLOW_FLOWER, new VariantBlockItem(Blocks.YELLOW_FLOWER, Blocks.YELLOW_FLOWER, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return FlowerBlock.FlowerType.getType(FlowerBlock.Color.YELLOW, itemStack.getData()).getName();
			}
		}).setTranslationKey("flower"));
		registerBlockItem(Blocks.RED_FLOWER, new VariantBlockItem(Blocks.RED_FLOWER, Blocks.RED_FLOWER, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return FlowerBlock.FlowerType.getType(FlowerBlock.Color.RED, itemStack.getData()).getName();
			}
		}).setTranslationKey("rose"));
		registerBlockItem(Blocks.BROWN_MUSHROOM);
		registerBlockItem(Blocks.RED_MUSHROOM);
		registerBlockItem(Blocks.GOLD_BLOCK);
		registerBlockItem(Blocks.IRON_BLOCK);
		registerBlockItem(Blocks.STONE_SLAB, new StoneSlabItem(Blocks.STONE_SLAB, Blocks.STONE_SLAB, Blocks.DOUBLE_STONE_SLAB).setTranslationKey("stoneSlab"));
		registerBlockItem(Blocks.BRICKS);
		registerBlockItem(Blocks.TNT);
		registerBlockItem(Blocks.BOOKSHELF);
		registerBlockItem(Blocks.MOSSY_COBBLESTONE);
		registerBlockItem(Blocks.OBSIDIAN);
		registerBlockItem(Blocks.TORCH);
		registerBlockItem(Blocks.END_ROD);
		registerBlockItem(Blocks.CHORUS_PLANT);
		registerBlockItem(Blocks.CHORUS_FLOWER);
		registerBlockItem(Blocks.PURPUR_BLOCK);
		registerBlockItem(Blocks.PURPUR_PILLAR);
		registerBlockItem(Blocks.PURPUR_STAIRS);
		registerBlockItem(Blocks.PURPUR_SLAB, new StoneSlabItem(Blocks.PURPUR_SLAB, Blocks.PURPUR_SLAB, Blocks.DOUBLE_PURPUR_SLAB).setTranslationKey("purpurSlab"));
		registerBlockItem(Blocks.SPAWNER);
		registerBlockItem(Blocks.WOODEN_STAIRS);
		registerBlockItem(Blocks.CHEST);
		registerBlockItem(Blocks.DIAMOND_ORE);
		registerBlockItem(Blocks.DIAMOND_BLOCK);
		registerBlockItem(Blocks.CRAFTING_TABLE);
		registerBlockItem(Blocks.FARMLAND);
		registerBlockItem(Blocks.FURNACE);
		registerBlockItem(Blocks.LADDER);
		registerBlockItem(Blocks.RAIL);
		registerBlockItem(Blocks.STONE_STAIRS);
		registerBlockItem(Blocks.LEVER);
		registerBlockItem(Blocks.STONE_PRESSURE_PLATE);
		registerBlockItem(Blocks.WOODEN_PRESSURE_PLATE);
		registerBlockItem(Blocks.REDSTONE_ORE);
		registerBlockItem(Blocks.REDSTONE_TORCH);
		registerBlockItem(Blocks.STONE_BUTTON);
		registerBlockItem(Blocks.SNOW_LAYER, new SnowLayerItem(Blocks.SNOW_LAYER));
		registerBlockItem(Blocks.ICE);
		registerBlockItem(Blocks.SNOW);
		registerBlockItem(Blocks.CACTUS);
		registerBlockItem(Blocks.CLAY);
		registerBlockItem(Blocks.JUKEBOX);
		registerBlockItem(Blocks.OAK_FENCE);
		registerBlockItem(Blocks.SPRUCE_FENCE);
		registerBlockItem(Blocks.BIRCH_FENCE);
		registerBlockItem(Blocks.JUNGLE_FENCE);
		registerBlockItem(Blocks.DARK_OAK_FENCE);
		registerBlockItem(Blocks.ACACIA_FENCE);
		registerBlockItem(Blocks.PUMPKIN);
		registerBlockItem(Blocks.NETHERRACK);
		registerBlockItem(Blocks.SOULSAND);
		registerBlockItem(Blocks.GLOWSTONE);
		registerBlockItem(Blocks.JACK_O_LANTERN);
		registerBlockItem(Blocks.TRAPDOOR);
		registerBlockItem(Blocks.MONSTER_EGG, new VariantBlockItem(Blocks.MONSTER_EGG, Blocks.MONSTER_EGG, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return InfestedBlock.Variants.getById(itemStack.getData()).getTranslationKey();
			}
		}).setTranslationKey("monsterStoneEgg"));
		registerBlockItem(Blocks.STONE_BRICKS, new VariantBlockItem(Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return StoneBrickBlock.Type.getById(itemStack.getData()).getValueName();
			}
		}).setTranslationKey("stonebricksmooth"));
		registerBlockItem(Blocks.BROWN_MUSHROOM_BLOCK);
		registerBlockItem(Blocks.RED_MUSHROOM_BLOCK);
		registerBlockItem(Blocks.IRON_BARS);
		registerBlockItem(Blocks.GLASS_PANE);
		registerBlockItem(Blocks.MELON_BLOCK);
		registerBlockItem(Blocks.VINE, new GrassBlockItem(Blocks.VINE, false));
		registerBlockItem(Blocks.OAK_FENCE_GATE);
		registerBlockItem(Blocks.SPRUCE_FENCE_GATE);
		registerBlockItem(Blocks.BIRCH_FENCE_GATE);
		registerBlockItem(Blocks.JUNGLE_FENCE_GATE);
		registerBlockItem(Blocks.DARK_OAK_FENCE_GATE);
		registerBlockItem(Blocks.ACACIA_FENCE_GATE);
		registerBlockItem(Blocks.BRICK_STAIRS);
		registerBlockItem(Blocks.STONE_BRICK_STAIRS);
		registerBlockItem(Blocks.MYCELIUM);
		registerBlockItem(Blocks.LILY_PAD, new LilyPadItem(Blocks.LILY_PAD));
		registerBlockItem(Blocks.NETHER_BRICKS);
		registerBlockItem(Blocks.NETHER_BRICK_FENCE);
		registerBlockItem(Blocks.NETHER_BRICK_STAIRS);
		registerBlockItem(Blocks.ENCHANTING_TABLE);
		registerBlockItem(Blocks.END_PORTAL_FRAME);
		registerBlockItem(Blocks.END_STONE);
		registerBlockItem(Blocks.END_BRICKS);
		registerBlockItem(Blocks.DRAGON_EGG);
		registerBlockItem(Blocks.REDSTONE_LAMP);
		registerBlockItem(Blocks.WOODEN_SLAB, new StoneSlabItem(Blocks.WOODEN_SLAB, Blocks.WOODEN_SLAB, Blocks.DOUBLE_WOODEN_SLAB).setTranslationKey("woodSlab"));
		registerBlockItem(Blocks.SANDSTONE_STAIRS);
		registerBlockItem(Blocks.EMERALD_ORE);
		registerBlockItem(Blocks.ENDERCHEST);
		registerBlockItem(Blocks.TRIPWIRE_HOOK);
		registerBlockItem(Blocks.EMERALD_BLOCK);
		registerBlockItem(Blocks.SPRUCE_STAIRS);
		registerBlockItem(Blocks.BIRCH_STAIRS);
		registerBlockItem(Blocks.JUNGLE_STAIRS);
		registerBlockItem(Blocks.COMMAND_BLOCK);
		registerBlockItem(Blocks.BEACON);
		registerBlockItem(Blocks.COBBLESTONE_WALL, new VariantBlockItem(Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE_WALL, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return WallBlock.WallType.getById(itemStack.getData()).getBlockStateName();
			}
		}).setTranslationKey("cobbleWall"));
		registerBlockItem(Blocks.WOODEN_BUTTON);
		registerBlockItem(Blocks.ANVIL, new AnvilItem(Blocks.ANVIL).setTranslationKey("anvil"));
		registerBlockItem(Blocks.TRAPPED_CHEST);
		registerBlockItem(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
		registerBlockItem(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
		registerBlockItem(Blocks.DAYLIGHT_DETECTOR);
		registerBlockItem(Blocks.REDSTONE_BLOCK);
		registerBlockItem(Blocks.NETHER_QUARTZ_ORE);
		registerBlockItem(Blocks.HOPPER);
		registerBlockItem(
			Blocks.QUARTZ_BLOCK,
			new VariantBlockItem(Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, new String[]{"default", "chiseled", "lines"}).setTranslationKey("quartzBlock")
		);
		registerBlockItem(Blocks.QUARTZ_STAIRS);
		registerBlockItem(Blocks.ACTIVATOR_RAIL);
		registerBlockItem(Blocks.DROPPER);
		registerBlockItem(Blocks.STAINED_TERRACOTTA, new WoolItem(Blocks.STAINED_TERRACOTTA).setTranslationKey("clayHardenedStained"));
		registerBlockItem(Blocks.BARRIER);
		registerBlockItem(Blocks.IRON_TRAPDOOR);
		registerBlockItem(Blocks.HAY_BALE);
		registerBlockItem(Blocks.CARPET, new WoolItem(Blocks.CARPET).setTranslationKey("woolCarpet"));
		registerBlockItem(Blocks.TERRACOTTA);
		registerBlockItem(Blocks.COAL_BLOCK);
		registerBlockItem(Blocks.PACKED_ICE);
		registerBlockItem(Blocks.ACACIA_STAIRS);
		registerBlockItem(Blocks.DARK_OAK_STAIRS);
		registerBlockItem(Blocks.SLIME_BLOCK);
		registerBlockItem(Blocks.GRASS_PATH);
		registerBlockItem(Blocks.DOUBLE_PLANT, new VariantBlockItem(Blocks.DOUBLE_PLANT, Blocks.DOUBLE_PLANT, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return DoublePlantBlock.DoublePlantType.getById(itemStack.getData()).getSingleName();
			}
		}).setTranslationKey("doublePlant"));
		registerBlockItem(Blocks.STAINED_GLASS, new WoolItem(Blocks.STAINED_GLASS).setTranslationKey("stainedGlass"));
		registerBlockItem(Blocks.STAINED_GLASS_PANE, new WoolItem(Blocks.STAINED_GLASS_PANE).setTranslationKey("stainedGlassPane"));
		registerBlockItem(Blocks.PRISMARINE, new VariantBlockItem(Blocks.PRISMARINE, Blocks.PRISMARINE, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return PrismarineBlock.PrismarineType.getById(itemStack.getData()).getStateName();
			}
		}).setTranslationKey("prismarine"));
		registerBlockItem(Blocks.SEA_LANTERN);
		registerBlockItem(Blocks.RED_SANDSTONE, new VariantBlockItem(Blocks.RED_SANDSTONE, Blocks.RED_SANDSTONE, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
				return RedSandstoneBlock.RedSandstoneType.getById(itemStack.getData()).getBlockStateName();
			}
		}).setTranslationKey("redSandStone"));
		registerBlockItem(Blocks.RED_SANDSTONE_STAIRS);
		registerBlockItem(Blocks.STONE_SLAB2, new StoneSlabItem(Blocks.STONE_SLAB2, Blocks.STONE_SLAB2, Blocks.DOUBLE_STONE_SLAB2).setTranslationKey("stoneSlab2"));
		registerBlockItem(Blocks.REPEATING_COMMAND_BLOCK);
		registerBlockItem(Blocks.CHAIN_COMMAND_BLOCK);
		registerBlockItem(Blocks.MAGMA);
		registerBlockItem(Blocks.NETHER_WART_BLOCK);
		registerBlockItem(Blocks.RED_NETHER_BRICK);
		registerBlockItem(Blocks.BONE_BLOCK);
		registerBlockItem(Blocks.STRUCTURE_VOID);
		registerBlockItem(Blocks.OBSERVER);
		registerBlockItem(Blocks.WHITE_SHULKER_BOX, new ShulkerBoxItem(Blocks.WHITE_SHULKER_BOX));
		registerBlockItem(Blocks.ORANGE_SHULKER_BOX, new ShulkerBoxItem(Blocks.ORANGE_SHULKER_BOX));
		registerBlockItem(Blocks.MAGENTA_SHULKER_BOX, new ShulkerBoxItem(Blocks.MAGENTA_SHULKER_BOX));
		registerBlockItem(Blocks.LIGHT_BLUE_SHULKER_BOX, new ShulkerBoxItem(Blocks.LIGHT_BLUE_SHULKER_BOX));
		registerBlockItem(Blocks.YELLOW_SHULKER_BOX, new ShulkerBoxItem(Blocks.YELLOW_SHULKER_BOX));
		registerBlockItem(Blocks.LIME_SHULKER_BOX, new ShulkerBoxItem(Blocks.LIME_SHULKER_BOX));
		registerBlockItem(Blocks.PINK_SHULKER_BOX, new ShulkerBoxItem(Blocks.PINK_SHULKER_BOX));
		registerBlockItem(Blocks.GRAY_SHULKER_BOX, new ShulkerBoxItem(Blocks.GRAY_SHULKER_BOX));
		registerBlockItem(Blocks.SILVER_SHULKER_BOX, new ShulkerBoxItem(Blocks.SILVER_SHULKER_BOX));
		registerBlockItem(Blocks.CYAN_SHULKER_BOX, new ShulkerBoxItem(Blocks.CYAN_SHULKER_BOX));
		registerBlockItem(Blocks.PURPLE_SHULKER_BOX, new ShulkerBoxItem(Blocks.PURPLE_SHULKER_BOX));
		registerBlockItem(Blocks.BLUE_SHULKER_BOX, new ShulkerBoxItem(Blocks.BLUE_SHULKER_BOX));
		registerBlockItem(Blocks.BROWN_SHULKER_BOX, new ShulkerBoxItem(Blocks.BROWN_SHULKER_BOX));
		registerBlockItem(Blocks.GREEN_SHULKER_BOX, new ShulkerBoxItem(Blocks.GREEN_SHULKER_BOX));
		registerBlockItem(Blocks.RED_SHULKER_BOX, new ShulkerBoxItem(Blocks.RED_SHULKER_BOX));
		registerBlockItem(Blocks.BLACK_SHULKER_BOX, new ShulkerBoxItem(Blocks.BLACK_SHULKER_BOX));
		registerBlockItem(Blocks.STRUCTURE_BLOCK);
		register(256, "iron_shovel", new ShovelItem(Item.ToolMaterialType.IRON).setTranslationKey("shovelIron"));
		register(257, "iron_pickaxe", new PickaxeItem(Item.ToolMaterialType.IRON).setTranslationKey("pickaxeIron"));
		register(258, "iron_axe", new AxeItem(Item.ToolMaterialType.IRON).setTranslationKey("hatchetIron"));
		register(259, "flint_and_steel", new FlintAndSteelItem().setTranslationKey("flintAndSteel"));
		register(260, "apple", new FoodItem(4, 0.3F, false).setTranslationKey("apple"));
		register(261, "bow", new BowItem().setTranslationKey("bow"));
		register(262, "arrow", new ArrowItem().setTranslationKey("arrow"));
		register(263, "coal", new CoalItem().setTranslationKey("coal"));
		register(264, "diamond", new Item().setTranslationKey("diamond").setItemGroup(ItemGroup.MATERIALS));
		register(265, "iron_ingot", new Item().setTranslationKey("ingotIron").setItemGroup(ItemGroup.MATERIALS));
		register(266, "gold_ingot", new Item().setTranslationKey("ingotGold").setItemGroup(ItemGroup.MATERIALS));
		register(267, "iron_sword", new SwordItem(Item.ToolMaterialType.IRON).setTranslationKey("swordIron"));
		register(268, "wooden_sword", new SwordItem(Item.ToolMaterialType.WOOD).setTranslationKey("swordWood"));
		register(269, "wooden_shovel", new ShovelItem(Item.ToolMaterialType.WOOD).setTranslationKey("shovelWood"));
		register(270, "wooden_pickaxe", new PickaxeItem(Item.ToolMaterialType.WOOD).setTranslationKey("pickaxeWood"));
		register(271, "wooden_axe", new AxeItem(Item.ToolMaterialType.WOOD).setTranslationKey("hatchetWood"));
		register(272, "stone_sword", new SwordItem(Item.ToolMaterialType.STONE).setTranslationKey("swordStone"));
		register(273, "stone_shovel", new ShovelItem(Item.ToolMaterialType.STONE).setTranslationKey("shovelStone"));
		register(274, "stone_pickaxe", new PickaxeItem(Item.ToolMaterialType.STONE).setTranslationKey("pickaxeStone"));
		register(275, "stone_axe", new AxeItem(Item.ToolMaterialType.STONE).setTranslationKey("hatchetStone"));
		register(276, "diamond_sword", new SwordItem(Item.ToolMaterialType.DIAMOND).setTranslationKey("swordDiamond"));
		register(277, "diamond_shovel", new ShovelItem(Item.ToolMaterialType.DIAMOND).setTranslationKey("shovelDiamond"));
		register(278, "diamond_pickaxe", new PickaxeItem(Item.ToolMaterialType.DIAMOND).setTranslationKey("pickaxeDiamond"));
		register(279, "diamond_axe", new AxeItem(Item.ToolMaterialType.DIAMOND).setTranslationKey("hatchetDiamond"));
		register(280, "stick", new Item().setHandheld().setTranslationKey("stick").setItemGroup(ItemGroup.MATERIALS));
		register(281, "bowl", new Item().setTranslationKey("bowl").setItemGroup(ItemGroup.MATERIALS));
		register(282, "mushroom_stew", new StewItem(6).setTranslationKey("mushroomStew"));
		register(283, "golden_sword", new SwordItem(Item.ToolMaterialType.GOLD).setTranslationKey("swordGold"));
		register(284, "golden_shovel", new ShovelItem(Item.ToolMaterialType.GOLD).setTranslationKey("shovelGold"));
		register(285, "golden_pickaxe", new PickaxeItem(Item.ToolMaterialType.GOLD).setTranslationKey("pickaxeGold"));
		register(286, "golden_axe", new AxeItem(Item.ToolMaterialType.GOLD).setTranslationKey("hatchetGold"));
		register(287, "string", new PlaceableItem(Blocks.TRIPWIRE).setTranslationKey("string").setItemGroup(ItemGroup.MATERIALS));
		register(288, "feather", new Item().setTranslationKey("feather").setItemGroup(ItemGroup.MATERIALS));
		register(289, "gunpowder", new Item().setTranslationKey("sulphur").setItemGroup(ItemGroup.MATERIALS));
		register(290, "wooden_hoe", new HoeItem(Item.ToolMaterialType.WOOD).setTranslationKey("hoeWood"));
		register(291, "stone_hoe", new HoeItem(Item.ToolMaterialType.STONE).setTranslationKey("hoeStone"));
		register(292, "iron_hoe", new HoeItem(Item.ToolMaterialType.IRON).setTranslationKey("hoeIron"));
		register(293, "diamond_hoe", new HoeItem(Item.ToolMaterialType.DIAMOND).setTranslationKey("hoeDiamond"));
		register(294, "golden_hoe", new HoeItem(Item.ToolMaterialType.GOLD).setTranslationKey("hoeGold"));
		register(295, "wheat_seeds", new SeedItem(Blocks.WHEAT, Blocks.FARMLAND).setTranslationKey("seeds"));
		register(296, "wheat", new Item().setTranslationKey("wheat").setItemGroup(ItemGroup.MATERIALS));
		register(297, "bread", new FoodItem(5, 0.6F, false).setTranslationKey("bread"));
		register(298, "leather_helmet", new ArmorItem(ArmorItem.Material.LEATHER, 0, EquipmentSlot.HEAD).setTranslationKey("helmetCloth"));
		register(299, "leather_chestplate", new ArmorItem(ArmorItem.Material.LEATHER, 0, EquipmentSlot.CHEST).setTranslationKey("chestplateCloth"));
		register(300, "leather_leggings", new ArmorItem(ArmorItem.Material.LEATHER, 0, EquipmentSlot.LEGS).setTranslationKey("leggingsCloth"));
		register(301, "leather_boots", new ArmorItem(ArmorItem.Material.LEATHER, 0, EquipmentSlot.FEET).setTranslationKey("bootsCloth"));
		register(302, "chainmail_helmet", new ArmorItem(ArmorItem.Material.CHAIN, 1, EquipmentSlot.HEAD).setTranslationKey("helmetChain"));
		register(303, "chainmail_chestplate", new ArmorItem(ArmorItem.Material.CHAIN, 1, EquipmentSlot.CHEST).setTranslationKey("chestplateChain"));
		register(304, "chainmail_leggings", new ArmorItem(ArmorItem.Material.CHAIN, 1, EquipmentSlot.LEGS).setTranslationKey("leggingsChain"));
		register(305, "chainmail_boots", new ArmorItem(ArmorItem.Material.CHAIN, 1, EquipmentSlot.FEET).setTranslationKey("bootsChain"));
		register(306, "iron_helmet", new ArmorItem(ArmorItem.Material.IRON, 2, EquipmentSlot.HEAD).setTranslationKey("helmetIron"));
		register(307, "iron_chestplate", new ArmorItem(ArmorItem.Material.IRON, 2, EquipmentSlot.CHEST).setTranslationKey("chestplateIron"));
		register(308, "iron_leggings", new ArmorItem(ArmorItem.Material.IRON, 2, EquipmentSlot.LEGS).setTranslationKey("leggingsIron"));
		register(309, "iron_boots", new ArmorItem(ArmorItem.Material.IRON, 2, EquipmentSlot.FEET).setTranslationKey("bootsIron"));
		register(310, "diamond_helmet", new ArmorItem(ArmorItem.Material.DIAMOND, 3, EquipmentSlot.HEAD).setTranslationKey("helmetDiamond"));
		register(311, "diamond_chestplate", new ArmorItem(ArmorItem.Material.DIAMOND, 3, EquipmentSlot.CHEST).setTranslationKey("chestplateDiamond"));
		register(312, "diamond_leggings", new ArmorItem(ArmorItem.Material.DIAMOND, 3, EquipmentSlot.LEGS).setTranslationKey("leggingsDiamond"));
		register(313, "diamond_boots", new ArmorItem(ArmorItem.Material.DIAMOND, 3, EquipmentSlot.FEET).setTranslationKey("bootsDiamond"));
		register(314, "golden_helmet", new ArmorItem(ArmorItem.Material.GOLD, 4, EquipmentSlot.HEAD).setTranslationKey("helmetGold"));
		register(315, "golden_chestplate", new ArmorItem(ArmorItem.Material.GOLD, 4, EquipmentSlot.CHEST).setTranslationKey("chestplateGold"));
		register(316, "golden_leggings", new ArmorItem(ArmorItem.Material.GOLD, 4, EquipmentSlot.LEGS).setTranslationKey("leggingsGold"));
		register(317, "golden_boots", new ArmorItem(ArmorItem.Material.GOLD, 4, EquipmentSlot.FEET).setTranslationKey("bootsGold"));
		register(318, "flint", new Item().setTranslationKey("flint").setItemGroup(ItemGroup.MATERIALS));
		register(319, "porkchop", new FoodItem(3, 0.3F, true).setTranslationKey("porkchopRaw"));
		register(320, "cooked_porkchop", new FoodItem(8, 0.8F, true).setTranslationKey("porkchopCooked"));
		register(321, "painting", new WallHangableItem(PaintingEntity.class).setTranslationKey("painting"));
		register(322, "golden_apple", new AppleItem(4, 1.2F, false).alwaysEdible().setTranslationKey("appleGold"));
		register(323, "sign", new SignItem().setTranslationKey("sign"));
		register(324, "wooden_door", new WoodenDoorItem(Blocks.WOODEN_DOOR).setTranslationKey("doorOak"));
		Item item = new BucketItem(Blocks.AIR).setTranslationKey("bucket").setMaxCount(16);
		register(325, "bucket", item);
		register(326, "water_bucket", new BucketItem(Blocks.FLOWING_WATER).setTranslationKey("bucketWater").setRecipeRemainder(item));
		register(327, "lava_bucket", new BucketItem(Blocks.FLOWING_LAVA).setTranslationKey("bucketLava").setRecipeRemainder(item));
		register(328, "minecart", new MinecartItem(AbstractMinecartEntity.Type.RIDEABLE).setTranslationKey("minecart"));
		register(329, "saddle", new SaddleItem().setTranslationKey("saddle"));
		register(330, "iron_door", new WoodenDoorItem(Blocks.IRON_DOOR).setTranslationKey("doorIron"));
		register(331, "redstone", new RedstoneItem().setTranslationKey("redstone"));
		register(332, "snowball", new SnowballItem().setTranslationKey("snowball"));
		register(333, "boat", new BoatItem(BoatEntity.Type.OAK));
		register(334, "leather", new Item().setTranslationKey("leather").setItemGroup(ItemGroup.MATERIALS));
		register(335, "milk_bucket", new MilkBucketItem().setTranslationKey("milk").setRecipeRemainder(item));
		register(336, "brick", new Item().setTranslationKey("brick").setItemGroup(ItemGroup.MATERIALS));
		register(337, "clay_ball", new Item().setTranslationKey("clay").setItemGroup(ItemGroup.MATERIALS));
		register(338, "reeds", new PlaceableItem(Blocks.SUGARCANE).setTranslationKey("reeds").setItemGroup(ItemGroup.MATERIALS));
		register(339, "paper", new Item().setTranslationKey("paper").setItemGroup(ItemGroup.MISC));
		register(340, "book", new BookItem().setTranslationKey("book").setItemGroup(ItemGroup.MISC));
		register(341, "slime_ball", new Item().setTranslationKey("slimeball").setItemGroup(ItemGroup.MISC));
		register(342, "chest_minecart", new MinecartItem(AbstractMinecartEntity.Type.CHEST).setTranslationKey("minecartChest"));
		register(343, "furnace_minecart", new MinecartItem(AbstractMinecartEntity.Type.FURNACE).setTranslationKey("minecartFurnace"));
		register(344, "egg", new EggItem().setTranslationKey("egg"));
		register(345, "compass", new CompassItem().setTranslationKey("compass").setItemGroup(ItemGroup.TOOLS));
		register(346, "fishing_rod", new FishingRodItem().setTranslationKey("fishingRod"));
		register(347, "clock", new ClockItem().setTranslationKey("clock").setItemGroup(ItemGroup.TOOLS));
		register(348, "glowstone_dust", new Item().setTranslationKey("yellowDust").setItemGroup(ItemGroup.MATERIALS));
		register(349, "fish", new FishItem(false).setTranslationKey("fish").setUnbreakable(true));
		register(350, "cooked_fish", new FishItem(true).setTranslationKey("fish").setUnbreakable(true));
		register(351, "dye", new DyeItem().setTranslationKey("dyePowder"));
		register(352, "bone", new Item().setTranslationKey("bone").setHandheld().setItemGroup(ItemGroup.MISC));
		register(353, "sugar", new Item().setTranslationKey("sugar").setItemGroup(ItemGroup.MATERIALS));
		register(354, "cake", new PlaceableItem(Blocks.CAKE).setMaxCount(1).setTranslationKey("cake").setItemGroup(ItemGroup.FOOD));
		register(355, "bed", new BedItem().setMaxCount(1).setTranslationKey("bed"));
		register(356, "repeater", new PlaceableItem(Blocks.UNPOWERED_REPEATER).setTranslationKey("diode").setItemGroup(ItemGroup.REDSTONE));
		register(357, "cookie", new FoodItem(2, 0.1F, false).setTranslationKey("cookie"));
		register(358, "filled_map", new FilledMapItem().setTranslationKey("map"));
		register(359, "shears", new ShearsItem().setTranslationKey("shears"));
		register(360, "melon", new FoodItem(2, 0.3F, false).setTranslationKey("melon"));
		register(361, "pumpkin_seeds", new SeedItem(Blocks.PUMPKIN_STEM, Blocks.FARMLAND).setTranslationKey("seeds_pumpkin"));
		register(362, "melon_seeds", new SeedItem(Blocks.MELON_STEM, Blocks.FARMLAND).setTranslationKey("seeds_melon"));
		register(363, "beef", new FoodItem(3, 0.3F, true).setTranslationKey("beefRaw"));
		register(364, "cooked_beef", new FoodItem(8, 0.8F, true).setTranslationKey("beefCooked"));
		register(
			365, "chicken", new FoodItem(2, 0.3F, true).method_11371(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.3F).setTranslationKey("chickenRaw")
		);
		register(366, "cooked_chicken", new FoodItem(6, 0.6F, true).setTranslationKey("chickenCooked"));
		register(
			367, "rotten_flesh", new FoodItem(4, 0.1F, true).method_11371(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F).setTranslationKey("rottenFlesh")
		);
		register(368, "ender_pearl", new EnderPearlItem().setTranslationKey("enderPearl"));
		register(369, "blaze_rod", new Item().setTranslationKey("blazeRod").setItemGroup(ItemGroup.MATERIALS).setHandheld());
		register(370, "ghast_tear", new Item().setTranslationKey("ghastTear").setItemGroup(ItemGroup.BREWING));
		register(371, "gold_nugget", new Item().setTranslationKey("goldNugget").setItemGroup(ItemGroup.MATERIALS));
		register(372, "nether_wart", new SeedItem(Blocks.NETHER_WART, Blocks.SOULSAND).setTranslationKey("netherStalkSeeds"));
		register(373, "potion", new PotionItem().setTranslationKey("potion"));
		Item item2 = new GlassBottleItem().setTranslationKey("glassBottle");
		register(374, "glass_bottle", item2);
		register(
			375, "spider_eye", new FoodItem(2, 0.8F, false).method_11371(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 1.0F).setTranslationKey("spiderEye")
		);
		register(376, "fermented_spider_eye", new Item().setTranslationKey("fermentedSpiderEye").setItemGroup(ItemGroup.BREWING));
		register(377, "blaze_powder", new Item().setTranslationKey("blazePowder").setItemGroup(ItemGroup.BREWING));
		register(378, "magma_cream", new Item().setTranslationKey("magmaCream").setItemGroup(ItemGroup.BREWING));
		register(379, "brewing_stand", new PlaceableItem(Blocks.BREWING_STAND).setTranslationKey("brewingStand").setItemGroup(ItemGroup.BREWING));
		register(380, "cauldron", new PlaceableItem(Blocks.CAULDRON).setTranslationKey("cauldron").setItemGroup(ItemGroup.BREWING));
		register(381, "ender_eye", new EnderEyeItem().setTranslationKey("eyeOfEnder"));
		register(382, "speckled_melon", new Item().setTranslationKey("speckledMelon").setItemGroup(ItemGroup.BREWING));
		register(383, "spawn_egg", new SpawnEggItem().setTranslationKey("monsterPlacer"));
		register(384, "experience_bottle", new ExperienceBottleItem().setTranslationKey("expBottle"));
		register(385, "fire_charge", new FireChargeItem().setTranslationKey("fireball"));
		register(386, "writable_book", new WritableBookItem().setTranslationKey("writingBook").setItemGroup(ItemGroup.MISC));
		register(387, "written_book", new WrittenBookItem().setTranslationKey("writtenBook").setMaxCount(16));
		register(388, "emerald", new Item().setTranslationKey("emerald").setItemGroup(ItemGroup.MATERIALS));
		register(389, "item_frame", new WallHangableItem(ItemFrameEntity.class).setTranslationKey("frame"));
		register(390, "flower_pot", new PlaceableItem(Blocks.FLOWER_POT).setTranslationKey("flowerPot").setItemGroup(ItemGroup.DECORATIONS));
		register(391, "carrot", new CropItem(3, 0.6F, Blocks.CARROTS, Blocks.FARMLAND).setTranslationKey("carrots"));
		register(392, "potato", new CropItem(1, 0.3F, Blocks.POTATOES, Blocks.FARMLAND).setTranslationKey("potato"));
		register(393, "baked_potato", new FoodItem(5, 0.6F, false).setTranslationKey("potatoBaked"));
		register(
			394,
			"poisonous_potato",
			new FoodItem(2, 0.3F, false).method_11371(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.6F).setTranslationKey("potatoPoisonous")
		);
		register(395, "map", new EmptyMapItem().setTranslationKey("emptyMap"));
		register(396, "golden_carrot", new FoodItem(6, 1.2F, false).setTranslationKey("carrotGolden").setItemGroup(ItemGroup.BREWING));
		register(397, "skull", new SkullItem().setTranslationKey("skull"));
		register(398, "carrot_on_a_stick", new CarrotOnAStickItem().setTranslationKey("carrotOnAStick"));
		register(399, "nether_star", new NetherStarItem().setTranslationKey("netherStar").setItemGroup(ItemGroup.MATERIALS));
		register(400, "pumpkin_pie", new FoodItem(8, 0.3F, false).setTranslationKey("pumpkinPie").setItemGroup(ItemGroup.FOOD));
		register(401, "fireworks", new FireworkItem().setTranslationKey("fireworks"));
		register(402, "firework_charge", new FireworkChargeItem().setTranslationKey("fireworksCharge").setItemGroup(ItemGroup.MISC));
		register(403, "enchanted_book", new EnchantedBookItem().setMaxCount(1).setTranslationKey("enchantedBook"));
		register(404, "comparator", new PlaceableItem(Blocks.UNPOWERED_COMPARATOR).setTranslationKey("comparator").setItemGroup(ItemGroup.REDSTONE));
		register(405, "netherbrick", new Item().setTranslationKey("netherbrick").setItemGroup(ItemGroup.MATERIALS));
		register(406, "quartz", new Item().setTranslationKey("netherquartz").setItemGroup(ItemGroup.MATERIALS));
		register(407, "tnt_minecart", new MinecartItem(AbstractMinecartEntity.Type.TNT).setTranslationKey("minecartTnt"));
		register(408, "hopper_minecart", new MinecartItem(AbstractMinecartEntity.Type.HOPPER).setTranslationKey("minecartHopper"));
		register(409, "prismarine_shard", new Item().setTranslationKey("prismarineShard").setItemGroup(ItemGroup.MATERIALS));
		register(410, "prismarine_crystals", new Item().setTranslationKey("prismarineCrystals").setItemGroup(ItemGroup.MATERIALS));
		register(411, "rabbit", new FoodItem(3, 0.3F, true).setTranslationKey("rabbitRaw"));
		register(412, "cooked_rabbit", new FoodItem(5, 0.6F, true).setTranslationKey("rabbitCooked"));
		register(413, "rabbit_stew", new StewItem(10).setTranslationKey("rabbitStew"));
		register(414, "rabbit_foot", new Item().setTranslationKey("rabbitFoot").setItemGroup(ItemGroup.BREWING));
		register(415, "rabbit_hide", new Item().setTranslationKey("rabbitHide").setItemGroup(ItemGroup.MATERIALS));
		register(416, "armor_stand", new ArmorStandItem().setTranslationKey("armorStand").setMaxCount(16));
		register(417, "iron_horse_armor", new Item().setTranslationKey("horsearmormetal").setMaxCount(1).setItemGroup(ItemGroup.MISC));
		register(418, "golden_horse_armor", new Item().setTranslationKey("horsearmorgold").setMaxCount(1).setItemGroup(ItemGroup.MISC));
		register(419, "diamond_horse_armor", new Item().setTranslationKey("horsearmordiamond").setMaxCount(1).setItemGroup(ItemGroup.MISC));
		register(420, "lead", new LeadItem().setTranslationKey("leash"));
		register(421, "name_tag", new NameTagItem().setTranslationKey("nameTag"));
		register(
			422, "command_block_minecart", new MinecartItem(AbstractMinecartEntity.Type.COMMAND_BLOCK).setTranslationKey("minecartCommandBlock").setItemGroup(null)
		);
		register(423, "mutton", new FoodItem(2, 0.3F, true).setTranslationKey("muttonRaw"));
		register(424, "cooked_mutton", new FoodItem(6, 0.8F, true).setTranslationKey("muttonCooked"));
		register(425, "banner", new BannerItem().setTranslationKey("banner"));
		register(426, "end_crystal", new EndCrystalItem());
		register(427, "spruce_door", new WoodenDoorItem(Blocks.SPRUCE_DOOR).setTranslationKey("doorSpruce"));
		register(428, "birch_door", new WoodenDoorItem(Blocks.BIRCH_DOOR).setTranslationKey("doorBirch"));
		register(429, "jungle_door", new WoodenDoorItem(Blocks.JUNGLE_DOOR).setTranslationKey("doorJungle"));
		register(430, "acacia_door", new WoodenDoorItem(Blocks.ACACIA_DOOR).setTranslationKey("doorAcacia"));
		register(431, "dark_oak_door", new WoodenDoorItem(Blocks.DARK_OAK_DOOR).setTranslationKey("doorDarkOak"));
		register(432, "chorus_fruit", new ChorusFruitItem(4, 0.3F).alwaysEdible().setTranslationKey("chorusFruit").setItemGroup(ItemGroup.MATERIALS));
		register(433, "chorus_fruit_popped", new Item().setTranslationKey("chorusFruitPopped").setItemGroup(ItemGroup.MATERIALS));
		register(434, "beetroot", new FoodItem(1, 0.6F, false).setTranslationKey("beetroot"));
		register(435, "beetroot_seeds", new SeedItem(Blocks.BEETROOTS, Blocks.FARMLAND).setTranslationKey("beetroot_seeds"));
		register(436, "beetroot_soup", new StewItem(6).setTranslationKey("beetroot_soup"));
		register(437, "dragon_breath", new Item().setItemGroup(ItemGroup.BREWING).setTranslationKey("dragon_breath").setRecipeRemainder(item2));
		register(438, "splash_potion", new SplashPotionItem().setTranslationKey("splash_potion"));
		register(439, "spectral_arrow", new SpectralArrowItem().setTranslationKey("spectral_arrow"));
		register(440, "tipped_arrow", new TippedArrowItem().setTranslationKey("tipped_arrow"));
		register(441, "lingering_potion", new LingeringPotionItem().setTranslationKey("lingering_potion"));
		register(442, "shield", new ShieldItem().setTranslationKey("shield"));
		register(443, "elytra", new ElytraItem().setTranslationKey("elytra"));
		register(444, "spruce_boat", new BoatItem(BoatEntity.Type.SPRUCE));
		register(445, "birch_boat", new BoatItem(BoatEntity.Type.BIRCH));
		register(446, "jungle_boat", new BoatItem(BoatEntity.Type.JUNGLE));
		register(447, "acacia_boat", new BoatItem(BoatEntity.Type.ACACIA));
		register(448, "dark_oak_boat", new BoatItem(BoatEntity.Type.DARK_OAK));
		register(449, "totem_of_undying", new Item().setTranslationKey("totem").setMaxCount(1).setItemGroup(ItemGroup.COMBAT));
		register(450, "shulker_shell", new Item().setTranslationKey("shulkerShell").setItemGroup(ItemGroup.MATERIALS));
		register(452, "iron_nugget", new Item().setTranslationKey("ironNugget").setItemGroup(ItemGroup.MATERIALS));
		register(2256, "record_13", new MusicDiscItem("13", Sounds.RECORD_13).setTranslationKey("record"));
		register(2257, "record_cat", new MusicDiscItem("cat", Sounds.RECORD_CAT).setTranslationKey("record"));
		register(2258, "record_blocks", new MusicDiscItem("blocks", Sounds.RECORD_BLOCKS).setTranslationKey("record"));
		register(2259, "record_chirp", new MusicDiscItem("chirp", Sounds.RECORD_CHIRP).setTranslationKey("record"));
		register(2260, "record_far", new MusicDiscItem("far", Sounds.RECORD_FAR).setTranslationKey("record"));
		register(2261, "record_mall", new MusicDiscItem("mall", Sounds.RECORD_MALL).setTranslationKey("record"));
		register(2262, "record_mellohi", new MusicDiscItem("mellohi", Sounds.RECORD_MELLOHI).setTranslationKey("record"));
		register(2263, "record_stal", new MusicDiscItem("stal", Sounds.RECORD_STAL).setTranslationKey("record"));
		register(2264, "record_strad", new MusicDiscItem("strad", Sounds.RECORD_STRAD).setTranslationKey("record"));
		register(2265, "record_ward", new MusicDiscItem("ward", Sounds.RECORD_WARD).setTranslationKey("record"));
		register(2266, "record_11", new MusicDiscItem("11", Sounds.RECORD_11).setTranslationKey("record"));
		register(2267, "record_wait", new MusicDiscItem("wait", Sounds.RECORD_WAIT).setTranslationKey("record"));
	}

	private static void registerBlockItem(Block block) {
		registerBlockItem(block, new BlockItem(block));
	}

	protected static void registerBlockItem(Block block, Item blockItem) {
		register(Block.getIdByBlock(block), Block.REGISTRY.getIdentifier(block), blockItem);
		BLOCK_ITEMS.put(block, blockItem);
	}

	private static void register(int id, String name, Item item) {
		register(id, new Identifier(name), item);
	}

	private static void register(int id, Identifier name, Item item) {
		REGISTRY.add(id, name, item);
	}

	public static enum ToolMaterialType {
		WOOD(0, 59, 2.0F, 0.0F, 15),
		STONE(1, 131, 4.0F, 1.0F, 5),
		IRON(2, 250, 6.0F, 2.0F, 14),
		DIAMOND(3, 1561, 8.0F, 3.0F, 10),
		GOLD(0, 32, 12.0F, 0.0F, 22);

		private final int miningLevel;
		private final int maxDurability;
		private final float speedMultiplier;
		private final float attackMultiplier;
		private final int enchantability;

		private ToolMaterialType(int j, int k, float f, float g, int l) {
			this.miningLevel = j;
			this.maxDurability = k;
			this.speedMultiplier = f;
			this.attackMultiplier = g;
			this.enchantability = l;
		}

		public int getMaxDurability() {
			return this.maxDurability;
		}

		public float getMiningSpeedMultiplier() {
			return this.speedMultiplier;
		}

		public float getAttackMultiplier() {
			return this.attackMultiplier;
		}

		public int getMiningLevel() {
			return this.miningLevel;
		}

		public int getEnchantability() {
			return this.enchantability;
		}

		public Item getRepairIngredient() {
			if (this == WOOD) {
				return Item.fromBlock(Blocks.PLANKS);
			} else if (this == STONE) {
				return Item.fromBlock(Blocks.COBBLESTONE);
			} else if (this == GOLD) {
				return Items.GOLD_INGOT;
			} else if (this == IRON) {
				return Items.IRON_INGOT;
			} else {
				return this == DIAMOND ? Items.DIAMOND : null;
			}
		}
	}
}
