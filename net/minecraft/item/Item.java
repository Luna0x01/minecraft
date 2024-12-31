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
import net.minecraft.class_3187;
import net.minecraft.class_3543;
import net.minecraft.class_3545;
import net.minecraft.class_3546;
import net.minecraft.class_3547;
import net.minecraft.class_3548;
import net.minecraft.class_3550;
import net.minecraft.class_3552;
import net.minecraft.class_3553;
import net.minecraft.class_3556;
import net.minecraft.class_3558;
import net.minecraft.class_3559;
import net.minecraft.class_3560;
import net.minecraft.class_3564;
import net.minecraft.class_4079;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.TooltipContext;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class Item implements Itemable {
	public static final Map<Block, Item> BLOCK_ITEMS = Maps.newHashMap();
	private static final ItemPropertyGetter GETTER_DAMAGED = (itemStack, world, livingEntity) -> itemStack.isDamaged() ? 1.0F : 0.0F;
	private static final ItemPropertyGetter GETTER_DAMAGE = (itemStack, world, livingEntity) -> MathHelper.clamp(
			(float)itemStack.getDamage() / (float)itemStack.getMaxDamage(), 0.0F, 1.0F
		);
	private static final ItemPropertyGetter GETTER_HAND = (itemStack, world, livingEntity) -> livingEntity != null
				&& livingEntity.getDurability() != HandOption.RIGHT
			? 1.0F
			: 0.0F;
	private static final ItemPropertyGetter GETTER_COOLDOWN = (itemStack, world, livingEntity) -> livingEntity instanceof PlayerEntity
			? ((PlayerEntity)livingEntity).getItemCooldownManager().getCooldownProgress(itemStack.getItem(), 0.0F)
			: 0.0F;
	protected static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	protected static Random RANDOM = new Random();
	private final Map<Identifier, ItemPropertyGetter> PROPERTIES = Maps.newHashMap();
	protected final ItemGroup itemGroup;
	private final Rarity itemCategory;
	private final int maxCount;
	private final int maxDamage;
	private final Item recipeRemainder;
	@Nullable
	private String translationKey;

	public static int getRawId(Item item) {
		return item == null ? 0 : Registry.ITEM.getRawId(item);
	}

	public static Item byRawId(int id) {
		return Registry.ITEM.getByRawId(id);
	}

	@Deprecated
	public static Item fromBlock(Block block) {
		Item item = (Item)BLOCK_ITEMS.get(block);
		return item == null ? Items.AIR : item;
	}

	public Item(Item.Settings settings) {
		this.addProperty(new Identifier("lefthanded"), GETTER_HAND);
		this.addProperty(new Identifier("cooldown"), GETTER_COOLDOWN);
		this.itemGroup = settings.group;
		this.itemCategory = settings.rarity;
		this.recipeRemainder = settings.recipeRemainder;
		this.maxDamage = settings.maxDamage;
		this.maxCount = settings.maxStackSize;
		if (this.maxDamage > 0) {
			this.addProperty(new Identifier("damaged"), GETTER_DAMAGED);
			this.addProperty(new Identifier("damage"), GETTER_DAMAGE);
		}
	}

	@Nullable
	public ItemPropertyGetter getProperty(Identifier identifier) {
		return (ItemPropertyGetter)this.PROPERTIES.get(identifier);
	}

	public boolean hasProperties() {
		return !this.PROPERTIES.isEmpty();
	}

	public boolean postProcessNbt(NbtCompound nbt) {
		return false;
	}

	public boolean beforeBlockBreak(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		return true;
	}

	@Override
	public Item getItem() {
		return this;
	}

	public final void addProperty(Identifier identifier, ItemPropertyGetter itemPropertyGetter) {
		this.PROPERTIES.put(identifier, itemPropertyGetter);
	}

	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
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

	public final int getMaxCount() {
		return this.maxCount;
	}

	public final int getMaxDamage() {
		return this.maxDamage;
	}

	public boolean isDamageable() {
		return this.maxDamage > 0;
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

	public Text method_16080() {
		return new TranslatableText(this.getTranslationKey());
	}

	protected String computeTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("item", Registry.ITEM.getId(this));
		}

		return this.translationKey;
	}

	public String getTranslationKey() {
		return this.computeTranslationKey();
	}

	public String getTranslationKey(ItemStack stack) {
		return this.getTranslationKey();
	}

	public boolean shouldSyncNbtToClient() {
		return true;
	}

	@Nullable
	public final Item getRecipeRemainder() {
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

	public void appendTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext tooltipContext) {
	}

	public Text getDisplayName(ItemStack stack) {
		return new TranslatableText(this.getTranslationKey(stack));
	}

	public boolean hasEnchantmentGlint(ItemStack stack) {
		return stack.hasEnchantments();
	}

	public Rarity getRarity(ItemStack stack) {
		if (!stack.hasEnchantments()) {
			return this.itemCategory;
		} else {
			switch (this.itemCategory) {
				case COMMON:
				case UNCOMMON:
					return Rarity.RARE;
				case RARE:
					return Rarity.EPIC;
				case EPIC:
				default:
					return this.itemCategory;
			}
		}
	}

	public boolean isEnchantable(ItemStack stack) {
		return this.getMaxCount() == 1 && this.isDamageable();
	}

	@Nullable
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
		return world.method_3615(vec3d, vec3d2, liquid ? class_4079.SOURCE_ONLY : class_4079.NEVER, false, false);
	}

	public int getEnchantability() {
		return 0;
	}

	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.canAddTo(group)) {
			stacks.add(new ItemStack(this));
		}
	}

	protected boolean canAddTo(ItemGroup group) {
		ItemGroup itemGroup = this.getItemGroup();
		return itemGroup != null && (group == ItemGroup.SEARCH || group == itemGroup);
	}

	@Nullable
	public final ItemGroup getItemGroup() {
		return this.itemGroup;
	}

	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return false;
	}

	public Multimap<String, AttributeModifier> method_6326(EquipmentSlot equipmentSlot) {
		return HashMultimap.create();
	}

	public static void setup() {
		registerBlockItem(Blocks.AIR, new class_3056(Blocks.AIR, new Item.Settings()));
		register(Blocks.STONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GRANITE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.POLISHED_GRANITE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DIORITE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.POLISHED_DIORITE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ANDESITE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.POLISHED_ANDESITE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GRASS_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DIRT, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.COARSE_DIRT, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PODZOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.COBBLESTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.OAK_PLANKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SPRUCE_PLANKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BIRCH_PLANKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.JUNGLE_PLANKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ACACIA_PLANKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DARK_OAK_PLANKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.OAK_SAPLING, ItemGroup.DECORATIONS);
		register(Blocks.SPRUCE_SAPLING, ItemGroup.DECORATIONS);
		register(Blocks.BIRCH_SAPLING, ItemGroup.DECORATIONS);
		register(Blocks.JUNGLE_SAPLING, ItemGroup.DECORATIONS);
		register(Blocks.ACACIA_SAPLING, ItemGroup.DECORATIONS);
		register(Blocks.DARK_OAK_SAPLING, ItemGroup.DECORATIONS);
		register(Blocks.BEDROCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SAND, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.RED_SAND, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GRAVEL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GOLD_ORE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.IRON_ORE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.COAL_ORE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.OAK_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SPRUCE_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BIRCH_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.JUNGLE_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ACACIA_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DARK_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_SPRUCE_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_BIRCH_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_JUNGLE_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_ACACIA_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_DARK_OAK_LOG, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_SPRUCE_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_BIRCH_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_JUNGLE_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_ACACIA_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STRIPPED_DARK_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SPRUCE_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BIRCH_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.JUNGLE_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ACACIA_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DARK_OAK_WOOD, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.OAK_LEAVES, ItemGroup.DECORATIONS);
		register(Blocks.SPRUCE_LEAVES, ItemGroup.DECORATIONS);
		register(Blocks.BIRCH_LEAVES, ItemGroup.DECORATIONS);
		register(Blocks.JUNGLE_LEAVES, ItemGroup.DECORATIONS);
		register(Blocks.ACACIA_LEAVES, ItemGroup.DECORATIONS);
		register(Blocks.DARK_OAK_LEAVES, ItemGroup.DECORATIONS);
		register(Blocks.SPONGE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.WET_SPONGE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LAPIS_LAZULI_ORE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LAPIS_LAZULI_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DISPENSER, ItemGroup.REDSTONE);
		register(Blocks.SANDSTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CHISELED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CUT_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.NOTE_BLOCK, ItemGroup.REDSTONE);
		register(Blocks.POWERED_RAIL, ItemGroup.field_17160);
		register(Blocks.DETECTOR_RAIL, ItemGroup.field_17160);
		register(Blocks.STICKY_PISTON, ItemGroup.REDSTONE);
		register(Blocks.COBWEB, ItemGroup.DECORATIONS);
		register(Blocks.GRASS, ItemGroup.DECORATIONS);
		register(Blocks.FERN, ItemGroup.DECORATIONS);
		register(Blocks.DEAD_BUSH, ItemGroup.DECORATIONS);
		register(Blocks.SEAGRASS, ItemGroup.DECORATIONS);
		register(Blocks.SEA_PICKLE, ItemGroup.DECORATIONS);
		register(Blocks.PISTON, ItemGroup.REDSTONE);
		register(Blocks.WHITE_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ORANGE_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.MAGENTA_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIGHT_BLUE_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.YELLOW_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIME_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PINK_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GRAY_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIGHT_GRAY_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CYAN_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PURPLE_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BLUE_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BROWN_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GREEN_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.RED_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BLACK_WOOL, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DANDELION, ItemGroup.DECORATIONS);
		register(Blocks.POPPY, ItemGroup.DECORATIONS);
		register(Blocks.BLUE_ORCHID, ItemGroup.DECORATIONS);
		register(Blocks.ALLIUM, ItemGroup.DECORATIONS);
		register(Blocks.AZURE_BLUET, ItemGroup.DECORATIONS);
		register(Blocks.RED_TULIP, ItemGroup.DECORATIONS);
		register(Blocks.ORANGE_TULIP, ItemGroup.DECORATIONS);
		register(Blocks.WHITE_TULIP, ItemGroup.DECORATIONS);
		register(Blocks.PINK_TULIP, ItemGroup.DECORATIONS);
		register(Blocks.OXEYE_DAISY, ItemGroup.DECORATIONS);
		register(Blocks.BROWN_MUSHROOM, ItemGroup.DECORATIONS);
		register(Blocks.RED_MUSHROOM, ItemGroup.DECORATIONS);
		register(Blocks.GOLD_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.IRON_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SPRUCE_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BIRCH_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.JUNGLE_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ACACIA_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DARK_OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STONE_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PETRIFIED_OAK_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.COBBLESTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STONE_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.NETHER_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.QUARTZ_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.RED_SANDSTONE_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PURPUR_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PRISMARINE_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PRISMARINE_BRICK_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DARK_PRISMARINE_SLAB, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SMOOTH_QUARTZ, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SMOOTH_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SMOOTH_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SMOOTH_STONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BRICKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.TNT, ItemGroup.REDSTONE);
		register(Blocks.BOOKSHELF, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.MOSSY_COBBLESTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.OBSIDIAN, ItemGroup.BUILDING_BLOCKS);
		register(new class_3559(Blocks.TORCH, Blocks.WALL_TORCH, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(Blocks.END_ROD, ItemGroup.DECORATIONS);
		register(Blocks.CHORUS_PLANT, ItemGroup.DECORATIONS);
		register(Blocks.CHORUS_FLOWER, ItemGroup.DECORATIONS);
		register(Blocks.PURPUR_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PURPUR_PILLAR, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PURPUR_STAIRS, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(Blocks.SPAWNER);
		register(Blocks.WOODEN_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CHEST, ItemGroup.DECORATIONS);
		register(Blocks.DIAMOND_ORE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DIAMOND_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CRAFTING_TABLE, ItemGroup.DECORATIONS);
		register(Blocks.FARMLAND, ItemGroup.DECORATIONS);
		register(Blocks.FURNACE, ItemGroup.DECORATIONS);
		register(Blocks.LADDER, ItemGroup.DECORATIONS);
		register(Blocks.RAIL, ItemGroup.field_17160);
		register(Blocks.COBBLESTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LEVER, ItemGroup.REDSTONE);
		register(Blocks.STONE_PRESSURE_PLATE, ItemGroup.REDSTONE);
		register(Blocks.OAK_PRESSURE_PLATE, ItemGroup.REDSTONE);
		register(Blocks.SPRUCE_PRESSURE_PLATE, ItemGroup.REDSTONE);
		register(Blocks.BIRCH_PRESSURE_PLATE, ItemGroup.REDSTONE);
		register(Blocks.JUNGLE_PRESSURE_PLATE, ItemGroup.REDSTONE);
		register(Blocks.ACACIA_PRESSURE_PLATE, ItemGroup.REDSTONE);
		register(Blocks.DARK_OAK_PRESSURE_PLATE, ItemGroup.REDSTONE);
		register(Blocks.REDSTONE_ORE, ItemGroup.BUILDING_BLOCKS);
		register(new class_3559(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, new Item.Settings().setGroup(ItemGroup.REDSTONE)));
		register(Blocks.STONE_BUTTON, ItemGroup.REDSTONE);
		register(Blocks.SNOW, ItemGroup.DECORATIONS);
		register(Blocks.ICE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SNOW_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CACTUS, ItemGroup.DECORATIONS);
		register(Blocks.CLAY, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.JUKEBOX, ItemGroup.DECORATIONS);
		register(Blocks.OAK_FENCE, ItemGroup.DECORATIONS);
		register(Blocks.SPRUCE_FENCE, ItemGroup.DECORATIONS);
		register(Blocks.BIRCH_FENCE, ItemGroup.DECORATIONS);
		register(Blocks.JUNGLE_FENCE, ItemGroup.DECORATIONS);
		register(Blocks.ACACIA_FENCE, ItemGroup.DECORATIONS);
		register(Blocks.DARK_OAK_FENCE, ItemGroup.DECORATIONS);
		register(Blocks.PUMPKIN, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CARVED_PUMPKIN, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.NETHERRACK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SOULSAND, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GLOWSTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.JACK_O_LANTERN, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.OAK_TRAPDOOR, ItemGroup.REDSTONE);
		register(Blocks.SPRUCE_TRAPDOOR, ItemGroup.REDSTONE);
		register(Blocks.BIRCH_TRAPDOOR, ItemGroup.REDSTONE);
		register(Blocks.JUNGLE_TRAPDOOR, ItemGroup.REDSTONE);
		register(Blocks.ACACIA_TRAPDOOR, ItemGroup.REDSTONE);
		register(Blocks.DARK_OAK_TRAPDOOR, ItemGroup.REDSTONE);
		register(Blocks.INFESTED_STONE, ItemGroup.DECORATIONS);
		register(Blocks.INFESTED_COBBLESTONE, ItemGroup.DECORATIONS);
		register(Blocks.INFESTED_STONE_BRICKS, ItemGroup.DECORATIONS);
		register(Blocks.INFESTED_MOSSY_STONE_BRICKS, ItemGroup.DECORATIONS);
		register(Blocks.INFESTED_CRACKED_STONE_BRICKS, ItemGroup.DECORATIONS);
		register(Blocks.INFESTED_CHISELED_STONE_BRICKS, ItemGroup.DECORATIONS);
		register(Blocks.STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.MOSSY_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CRACKED_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CHISELED_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BROWN_MUSHROOM_BLOCK, ItemGroup.DECORATIONS);
		register(Blocks.RED_MUSHROOM_BLOCK, ItemGroup.DECORATIONS);
		register(Blocks.MUSHROOM_STEM, ItemGroup.DECORATIONS);
		register(Blocks.IRON_BARS, ItemGroup.DECORATIONS);
		register(Blocks.GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.MELON_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.VINE, ItemGroup.DECORATIONS);
		register(Blocks.OAK_FENCE_GATE, ItemGroup.REDSTONE);
		register(Blocks.SPRUCE_FENCE_GATE, ItemGroup.REDSTONE);
		register(Blocks.BIRCH_FENCE_GATE, ItemGroup.REDSTONE);
		register(Blocks.JUNGLE_FENCE_GATE, ItemGroup.REDSTONE);
		register(Blocks.ACACIA_FENCE_GATE, ItemGroup.REDSTONE);
		register(Blocks.DARK_OAK_FENCE_GATE, ItemGroup.REDSTONE);
		register(Blocks.BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.STONE_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.MYCELIUM, ItemGroup.BUILDING_BLOCKS);
		register(new LilyPadItem(Blocks.LILY_PAD, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(Blocks.NETHER_BRICKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.NETHER_BRICK_FENCE, ItemGroup.DECORATIONS);
		register(Blocks.NETHER_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ENCHANTING_TABLE, ItemGroup.DECORATIONS);
		register(Blocks.END_PORTAL_FRAME, ItemGroup.DECORATIONS);
		register(Blocks.END_STONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.END_STONE_BRICKS, ItemGroup.BUILDING_BLOCKS);
		register(new BlockItem(Blocks.DRAGON_EGG, new Item.Settings().setRarity(Rarity.EPIC)));
		register(Blocks.REDSTONE_LAMP, ItemGroup.REDSTONE);
		register(Blocks.SANDSTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.EMERALD_ORE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ENDERCHEST, ItemGroup.DECORATIONS);
		register(Blocks.TRIPWIRE_HOOK, ItemGroup.REDSTONE);
		register(Blocks.EMERALD_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SPRUCE_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BIRCH_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.JUNGLE_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(new class_3553(Blocks.COMMAND_BLOCK, new Item.Settings().setRarity(Rarity.EPIC)));
		register(new BlockItem(Blocks.BEACON, new Item.Settings().setGroup(ItemGroup.MISC).setRarity(Rarity.RARE)));
		register(Blocks.COBBLESTONE_WALL, ItemGroup.DECORATIONS);
		register(Blocks.MOSSY_COBBLESTONE_WALL, ItemGroup.DECORATIONS);
		register(Blocks.OAK_BUTTON, ItemGroup.REDSTONE);
		register(Blocks.SPRUCE_BUTTON, ItemGroup.REDSTONE);
		register(Blocks.BIRCH_BUTTON, ItemGroup.REDSTONE);
		register(Blocks.JUNGLE_BUTTON, ItemGroup.REDSTONE);
		register(Blocks.ACACIA_BUTTON, ItemGroup.REDSTONE);
		register(Blocks.DARK_OAK_BUTTON, ItemGroup.REDSTONE);
		register(Blocks.ANVIL, ItemGroup.DECORATIONS);
		register(Blocks.CHIPPED_ANVIL, ItemGroup.DECORATIONS);
		register(Blocks.DAMAGED_ANVIL, ItemGroup.DECORATIONS);
		register(Blocks.TRAPPED_CHEST, ItemGroup.REDSTONE);
		register(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, ItemGroup.REDSTONE);
		register(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, ItemGroup.REDSTONE);
		register(Blocks.DAYLIGHT_DETECTOR, ItemGroup.REDSTONE);
		register(Blocks.REDSTONE_BLOCK, ItemGroup.REDSTONE);
		register(Blocks.NETHER_QUARTZ_ORE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.HOPPER, ItemGroup.REDSTONE);
		register(Blocks.CHISELED_QUARTZ_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.QUARTZ_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.QUARTZ_PILLAR, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.QUARTZ_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ACTIVATOR_RAIL, ItemGroup.field_17160);
		register(Blocks.DROPPER, ItemGroup.REDSTONE);
		register(Blocks.WHITE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ORANGE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.MAGENTA_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIGHT_BLUE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.YELLOW_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIME_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PINK_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GRAY_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIGHT_GRAY_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CYAN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PURPLE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BLUE_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BROWN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GREEN_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.RED_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BLACK_TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(Blocks.BARRIER);
		register(Blocks.IRON_TRAPDOOR, ItemGroup.REDSTONE);
		register(Blocks.HAY_BALE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.WHITE_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.ORANGE_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.MAGENTA_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.LIGHT_BLUE_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.YELLOW_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.LIME_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.PINK_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.GRAY_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.LIGHT_GRAY_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.CYAN_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.PURPLE_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.BLUE_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.BROWN_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.GREEN_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.RED_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.BLACK_CARPET, ItemGroup.DECORATIONS);
		register(Blocks.TERRACOTTA, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.COAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PACKED_ICE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ACACIA_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DARK_OAK_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SLIME_BLOCK, ItemGroup.DECORATIONS);
		register(Blocks.GRASS_PATH, ItemGroup.DECORATIONS);
		register(new class_3548(Blocks.SUNFLOWER, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3548(Blocks.LILAC, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3548(Blocks.ROSE_BUSH, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3548(Blocks.PEONY, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3548(Blocks.TALL_GRASS, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3548(Blocks.LARGE_FERN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(Blocks.WHITE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ORANGE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.MAGENTA_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIGHT_BLUE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.YELLOW_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIME_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PINK_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GRAY_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIGHT_GRAY_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CYAN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PURPLE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BLUE_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BROWN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GREEN_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.RED_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BLACK_STAINED_GLASS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.WHITE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.ORANGE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.MAGENTA_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.YELLOW_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.LIME_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.PINK_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.GRAY_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.CYAN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.PURPLE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.BLUE_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.BROWN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.GREEN_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.RED_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.BLACK_STAINED_GLASS_PANE, ItemGroup.DECORATIONS);
		register(Blocks.PRISMARINE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PRISMARINE_BRICKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DARK_PRISMARINE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PRISMARINE_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PRISMARINE_BRICK_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DARK_PRISMARINE_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.SEA_LANTERN, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CHISELED_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CUT_RED_SANDSTONE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.RED_SANDSTONE_STAIRS, ItemGroup.BUILDING_BLOCKS);
		register(new class_3553(Blocks.REPEATING_COMMAND_BLOCK, new Item.Settings().setRarity(Rarity.EPIC)));
		register(new class_3553(Blocks.CHAIN_COMMAND_BLOCK, new Item.Settings().setRarity(Rarity.EPIC)));
		register(Blocks.MAGMA_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.NETHER_WART_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.RED_NETHER_BRICKS, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BONE_BLOCK, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(Blocks.STRUCTURE_VOID);
		register(Blocks.OBSERVER, ItemGroup.REDSTONE);
		register(new BlockItem(Blocks.SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.WHITE_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.ORANGE_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.MAGENTA_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.LIGHT_BLUE_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.YELLOW_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.LIME_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.PINK_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.GRAY_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.LIGHT_GRAY_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.CYAN_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.PURPLE_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.BLUE_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.BROWN_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.GREEN_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.RED_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BlockItem(Blocks.BLACK_SHULKER_BOX, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(Blocks.WHITE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.ORANGE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.MAGENTA_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.YELLOW_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.LIME_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.PINK_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.GRAY_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.CYAN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.PURPLE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.BLUE_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.BROWN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.GREEN_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.RED_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.BLACK_GLAZED_TERRACOTTA, ItemGroup.DECORATIONS);
		register(Blocks.WHITE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ORANGE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.MAGENTA_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIGHT_BLUE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.YELLOW_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIME_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PINK_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GRAY_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIGHT_GRAY_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CYAN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PURPLE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BLUE_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BROWN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GREEN_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.RED_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BLACK_CONCRETE, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.WHITE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.ORANGE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.MAGENTA_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIGHT_BLUE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.YELLOW_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIME_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PINK_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GRAY_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.LIGHT_GRAY_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.CYAN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.PURPLE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BLUE_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BROWN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.GREEN_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.RED_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BLACK_CONCRETE_POWDER, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.TURTLE_EGG, ItemGroup.MISC);
		register(Blocks.DEAD_TUBE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DEAD_BRAIN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DEAD_BUBBLE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DEAD_FIRE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.DEAD_HORN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.TUBE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BRAIN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.BUBBLE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.FIRE_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.HORN_CORAL_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register(Blocks.TUBE_CORAL, ItemGroup.DECORATIONS);
		register(Blocks.BRAIN_CORAL, ItemGroup.DECORATIONS);
		register(Blocks.BUBBLE_CORAL, ItemGroup.DECORATIONS);
		register(Blocks.FIRE_CORAL, ItemGroup.DECORATIONS);
		register(Blocks.HORN_CORAL, ItemGroup.DECORATIONS);
		register(Blocks.DEAD_BRAIN_CORAL, ItemGroup.DECORATIONS);
		register(Blocks.DEAD_BUBBLE_CORAL, ItemGroup.DECORATIONS);
		register(Blocks.DEAD_FIRE_CORAL, ItemGroup.DECORATIONS);
		register(Blocks.DEAD_HORN_CORAL, ItemGroup.DECORATIONS);
		register(Blocks.DEAD_TUBE_CORAL, ItemGroup.DECORATIONS);
		register(new class_3559(Blocks.TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3559(Blocks.BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3559(Blocks.BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3559(Blocks.FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3559(Blocks.HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3559(Blocks.DEAD_TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3559(Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3559(Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3559(Blocks.DEAD_FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(new class_3559(Blocks.DEAD_HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(Blocks.BLUE_ICE, ItemGroup.BUILDING_BLOCKS);
		register(new BlockItem(Blocks.CONDUIT, new Item.Settings().setGroup(ItemGroup.MISC).setRarity(Rarity.RARE)));
		register(new class_3548(Blocks.IRON_DOOR, new Item.Settings().setGroup(ItemGroup.REDSTONE)));
		register(new class_3548(Blocks.OAK_DOOR, new Item.Settings().setGroup(ItemGroup.REDSTONE)));
		register(new class_3548(Blocks.SPRUCE_DOOR, new Item.Settings().setGroup(ItemGroup.REDSTONE)));
		register(new class_3548(Blocks.BIRCH_DOOR, new Item.Settings().setGroup(ItemGroup.REDSTONE)));
		register(new class_3548(Blocks.JUNGLE_DOOR, new Item.Settings().setGroup(ItemGroup.REDSTONE)));
		register(new class_3548(Blocks.ACACIA_DOOR, new Item.Settings().setGroup(ItemGroup.REDSTONE)));
		register(new class_3548(Blocks.DARK_OAK_DOOR, new Item.Settings().setGroup(ItemGroup.REDSTONE)));
		register(Blocks.REPEATER, ItemGroup.REDSTONE);
		register(Blocks.COMPARATOR, ItemGroup.REDSTONE);
		register(new class_3553(Blocks.STRUCTURE_BLOCK, new Item.Settings().setRarity(Rarity.EPIC)));
		register("turtle_helmet", new ArmorItem(class_3543.TURTLE, EquipmentSlot.HEAD, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("scute", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("iron_shovel", new ShovelItem(ToolMaterials.IRON, 1.5F, -3.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("iron_pickaxe", new PickaxeItem(ToolMaterials.IRON, 1, -2.8F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("iron_axe", new AxeItem(ToolMaterials.IRON, 6.0F, -3.1F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("flint_and_steel", new FlintAndSteelItem(new Item.Settings().setMaxDamage(64).setGroup(ItemGroup.TOOLS)));
		register("apple", new FoodItem(4, 0.3F, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("bow", new BowItem(new Item.Settings().setMaxDamage(384).setGroup(ItemGroup.COMBAT)));
		register("arrow", new ArrowItem(new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("coal", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("charcoal", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("diamond", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("iron_ingot", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("gold_ingot", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("iron_sword", new SwordItem(ToolMaterials.IRON, 3, -2.4F, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("wooden_sword", new SwordItem(ToolMaterials.WOOD, 3, -2.4F, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("wooden_shovel", new ShovelItem(ToolMaterials.WOOD, 1.5F, -3.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("wooden_pickaxe", new PickaxeItem(ToolMaterials.WOOD, 1, -2.8F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("wooden_axe", new AxeItem(ToolMaterials.WOOD, 6.0F, -3.2F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("stone_sword", new SwordItem(ToolMaterials.STONE, 3, -2.4F, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("stone_shovel", new ShovelItem(ToolMaterials.STONE, 1.5F, -3.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("stone_pickaxe", new PickaxeItem(ToolMaterials.STONE, 1, -2.8F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("stone_axe", new AxeItem(ToolMaterials.STONE, 7.0F, -3.2F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("diamond_sword", new SwordItem(ToolMaterials.DIAMOND, 3, -2.4F, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("diamond_shovel", new ShovelItem(ToolMaterials.DIAMOND, 1.5F, -3.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("diamond_pickaxe", new PickaxeItem(ToolMaterials.DIAMOND, 1, -2.8F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("diamond_axe", new AxeItem(ToolMaterials.DIAMOND, 5.0F, -3.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("stick", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("bowl", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("mushroom_stew", new StewItem(6, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.FOOD)));
		register("golden_sword", new SwordItem(ToolMaterials.GOLD, 3, -2.4F, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("golden_shovel", new ShovelItem(ToolMaterials.GOLD, 1.5F, -3.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("golden_pickaxe", new PickaxeItem(ToolMaterials.GOLD, 1, -2.8F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("golden_axe", new AxeItem(ToolMaterials.GOLD, 6.0F, -3.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("string", new class_3560(new Item.Settings().setGroup(ItemGroup.MISC)));
		register("feather", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("gunpowder", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("wooden_hoe", new HoeItem(ToolMaterials.WOOD, -3.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("stone_hoe", new HoeItem(ToolMaterials.STONE, -2.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("iron_hoe", new HoeItem(ToolMaterials.IRON, -1.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("diamond_hoe", new HoeItem(ToolMaterials.DIAMOND, 0.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("golden_hoe", new HoeItem(ToolMaterials.GOLD, -3.0F, new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("wheat_seeds", new SeedItem(Blocks.WHEAT, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("wheat", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("bread", new FoodItem(5, 0.6F, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("leather_helmet", new DyeableArmorItem(class_3543.LEATHER, EquipmentSlot.HEAD, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("leather_chestplate", new DyeableArmorItem(class_3543.LEATHER, EquipmentSlot.CHEST, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("leather_leggings", new DyeableArmorItem(class_3543.LEATHER, EquipmentSlot.LEGS, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("leather_boots", new DyeableArmorItem(class_3543.LEATHER, EquipmentSlot.FEET, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("chainmail_helmet", new ArmorItem(class_3543.CHAIN, EquipmentSlot.HEAD, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("chainmail_chestplate", new ArmorItem(class_3543.CHAIN, EquipmentSlot.CHEST, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("chainmail_leggings", new ArmorItem(class_3543.CHAIN, EquipmentSlot.LEGS, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("chainmail_boots", new ArmorItem(class_3543.CHAIN, EquipmentSlot.FEET, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("iron_helmet", new ArmorItem(class_3543.IRON, EquipmentSlot.HEAD, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("iron_chestplate", new ArmorItem(class_3543.IRON, EquipmentSlot.CHEST, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("iron_leggings", new ArmorItem(class_3543.IRON, EquipmentSlot.LEGS, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("iron_boots", new ArmorItem(class_3543.IRON, EquipmentSlot.FEET, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("diamond_helmet", new ArmorItem(class_3543.DIAMOND, EquipmentSlot.HEAD, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("diamond_chestplate", new ArmorItem(class_3543.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("diamond_leggings", new ArmorItem(class_3543.DIAMOND, EquipmentSlot.LEGS, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("diamond_boots", new ArmorItem(class_3543.DIAMOND, EquipmentSlot.FEET, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("golden_helmet", new ArmorItem(class_3543.GOLD, EquipmentSlot.HEAD, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("golden_chestplate", new ArmorItem(class_3543.GOLD, EquipmentSlot.CHEST, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("golden_leggings", new ArmorItem(class_3543.GOLD, EquipmentSlot.LEGS, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("golden_boots", new ArmorItem(class_3543.GOLD, EquipmentSlot.FEET, new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("flint", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("porkchop", new FoodItem(3, 0.3F, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("cooked_porkchop", new FoodItem(8, 0.8F, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("painting", new WallHangableItem(PaintingEntity.class, new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register("golden_apple", new AppleItem(4, 1.2F, false, new Item.Settings().setGroup(ItemGroup.FOOD).setRarity(Rarity.RARE)).alwaysEdible());
		register("enchanted_golden_apple", new class_3550(4, 1.2F, false, new Item.Settings().setGroup(ItemGroup.FOOD).setRarity(Rarity.EPIC)).alwaysEdible());
		register("sign", new SignItem(new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS)));
		Item item = new BucketItem(Fluids.EMPTY, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.MISC));
		register("bucket", item);
		register("water_bucket", new BucketItem(Fluids.WATER, new Item.Settings().setRecipeRemainder(item).setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("lava_bucket", new BucketItem(Fluids.LAVA, new Item.Settings().setRecipeRemainder(item).setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("minecart", new MinecartItem(AbstractMinecartEntity.Type.RIDEABLE, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("saddle", new SaddleItem(new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("redstone", new BlockItem(Blocks.REDSTONE_WIRE, new Item.Settings().setGroup(ItemGroup.REDSTONE)));
		register("snowball", new SnowballItem(new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.MISC)));
		register("oak_boat", new BoatItem(BoatEntity.Type.OAK, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("leather", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("milk_bucket", new MilkBucketItem(new Item.Settings().setRecipeRemainder(item).setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("pufferfish_bucket", new class_3552(EntityType.PUFFERFISH, Fluids.WATER, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("salmon_bucket", new class_3552(EntityType.SALMON, Fluids.WATER, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("cod_bucket", new class_3552(EntityType.COD, Fluids.WATER, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("tropical_fish_bucket", new class_3552(EntityType.TROPICAL_FISH, Fluids.WATER, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("brick", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("clay_ball", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register(Blocks.SUGAR_CANE, ItemGroup.MISC);
		register(Blocks.KELP, ItemGroup.MISC);
		register(Blocks.DRIED_KELP_BLOCK, ItemGroup.BUILDING_BLOCKS);
		register("paper", new Item(new Item.Settings().setGroup(ItemGroup.MISC)));
		register("book", new BookItem(new Item.Settings().setGroup(ItemGroup.MISC)));
		register("slime_ball", new Item(new Item.Settings().setGroup(ItemGroup.MISC)));
		register("chest_minecart", new MinecartItem(AbstractMinecartEntity.Type.CHEST, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("furnace_minecart", new MinecartItem(AbstractMinecartEntity.Type.FURNACE, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("egg", new EggItem(new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.MATERIALS)));
		register("compass", new CompassItem(new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("fishing_rod", new FishingRodItem(new Item.Settings().setMaxDamage(64).setGroup(ItemGroup.TOOLS)));
		register("clock", new ClockItem(new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("glowstone_dust", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("cod", new FishItem(FishItem.FishType.COD, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("salmon", new FishItem(FishItem.FishType.SALMON, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("tropical_fish", new FishItem(FishItem.FishType.TROPICAL_FISH, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("pufferfish", new FishItem(FishItem.FishType.PUFFERFISH, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("cooked_cod", new FishItem(FishItem.FishType.COD, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("cooked_salmon", new FishItem(FishItem.FishType.SALMON, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("ink_sac", new DyeItem(DyeColor.BLACK, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("rose_red", new DyeItem(DyeColor.RED, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("cactus_green", new DyeItem(DyeColor.GREEN, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("cocoa_beans", new class_3546(DyeColor.BROWN, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("lapis_lazuli", new DyeItem(DyeColor.BLUE, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("purple_dye", new DyeItem(DyeColor.PURPLE, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("cyan_dye", new DyeItem(DyeColor.CYAN, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("light_gray_dye", new DyeItem(DyeColor.LIGHT_GRAY, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("gray_dye", new DyeItem(DyeColor.GRAY, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("pink_dye", new DyeItem(DyeColor.PINK, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("lime_dye", new DyeItem(DyeColor.LIME, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("dandelion_yellow", new DyeItem(DyeColor.YELLOW, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("light_blue_dye", new DyeItem(DyeColor.LIGHT_BLUE, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("magenta_dye", new DyeItem(DyeColor.MAGENTA, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("orange_dye", new DyeItem(DyeColor.ORANGE, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("bone_meal", new class_3545(DyeColor.WHITE, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("bone", new Item(new Item.Settings().setGroup(ItemGroup.MISC)));
		register("sugar", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register(new BlockItem(Blocks.CAKE, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.FOOD)));
		register(new BedItem(Blocks.WHITE_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.ORANGE_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.MAGENTA_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.LIGHT_BLUE_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.YELLOW_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.LIME_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.PINK_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.GRAY_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.LIGHT_GRAY_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.CYAN_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.PURPLE_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.BLUE_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.BROWN_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.GREEN_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.RED_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register(new BedItem(Blocks.BLACK_BED, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.DECORATIONS)));
		register("cookie", new FoodItem(2, 0.1F, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("filled_map", new FilledMapItem(new Item.Settings()));
		register("shears", new ShearsItem(new Item.Settings().setMaxDamage(238).setGroup(ItemGroup.TOOLS)));
		register("melon_slice", new FoodItem(2, 0.3F, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("dried_kelp", new FoodItem(1, 0.3F, false, new Item.Settings().setGroup(ItemGroup.FOOD)).method_16065());
		register("pumpkin_seeds", new SeedItem(Blocks.PUMPKIN_STEM, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("melon_seeds", new SeedItem(Blocks.MELON_STEM, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("beef", new FoodItem(3, 0.3F, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("cooked_beef", new FoodItem(8, 0.8F, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register(
			"chicken",
			new FoodItem(2, 0.3F, true, new Item.Settings().setGroup(ItemGroup.FOOD)).method_11371(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.3F)
		);
		register("cooked_chicken", new FoodItem(6, 0.6F, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register(
			"rotten_flesh",
			new FoodItem(4, 0.1F, true, new Item.Settings().setGroup(ItemGroup.FOOD)).method_11371(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F)
		);
		register("ender_pearl", new EnderPearlItem(new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.MISC)));
		register("blaze_rod", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("ghast_tear", new Item(new Item.Settings().setGroup(ItemGroup.BREWING)));
		register("gold_nugget", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("nether_wart", new SeedItem(Blocks.NETHER_WART, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("potion", new PotionItem(new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.BREWING)));
		Item item2 = new GlassBottleItem(new Item.Settings().setGroup(ItemGroup.BREWING));
		register("glass_bottle", item2);
		register(
			"spider_eye",
			new FoodItem(2, 0.8F, false, new Item.Settings().setGroup(ItemGroup.FOOD)).method_11371(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 1.0F)
		);
		register("fermented_spider_eye", new Item(new Item.Settings().setGroup(ItemGroup.BREWING)));
		register("blaze_powder", new Item(new Item.Settings().setGroup(ItemGroup.BREWING)));
		register("magma_cream", new Item(new Item.Settings().setGroup(ItemGroup.BREWING)));
		register(Blocks.BREWING_STAND, ItemGroup.BREWING);
		register(Blocks.CAULDRON, ItemGroup.BREWING);
		register("ender_eye", new EnderEyeItem(new Item.Settings().setGroup(ItemGroup.MISC)));
		register("glistering_melon_slice", new Item(new Item.Settings().setGroup(ItemGroup.BREWING)));
		register("bat_spawn_egg", new class_3558(EntityType.BAT, 4996656, 986895, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("blaze_spawn_egg", new class_3558(EntityType.BLAZE, 16167425, 16775294, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("cave_spider_spawn_egg", new class_3558(EntityType.CAVE_SPIDER, 803406, 11013646, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("chicken_spawn_egg", new class_3558(EntityType.CHICKEN, 10592673, 16711680, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("cod_spawn_egg", new class_3558(EntityType.COD, 12691306, 15058059, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("cow_spawn_egg", new class_3558(EntityType.COW, 4470310, 10592673, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("creeper_spawn_egg", new class_3558(EntityType.CREEPER, 894731, 0, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("dolphin_spawn_egg", new class_3558(EntityType.DOLPHIN, 2243405, 16382457, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("donkey_spawn_egg", new class_3558(EntityType.DONKEY, 5457209, 8811878, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("drowned_spawn_egg", new class_3558(EntityType.DROWNED, 9433559, 7969893, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("elder_guardian_spawn_egg", new class_3558(EntityType.ELDER_GUARDIAN, 13552826, 7632531, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("enderman_spawn_egg", new class_3558(EntityType.ENDERMAN, 1447446, 0, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("endermite_spawn_egg", new class_3558(EntityType.ENDERMITE, 1447446, 7237230, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("evoker_spawn_egg", new class_3558(EntityType.EVOKER, 9804699, 1973274, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("ghast_spawn_egg", new class_3558(EntityType.GHAST, 16382457, 12369084, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("guardian_spawn_egg", new class_3558(EntityType.GUARDIAN, 5931634, 15826224, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("horse_spawn_egg", new class_3558(EntityType.HORSE, 12623485, 15656192, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("husk_spawn_egg", new class_3558(EntityType.HUSK, 7958625, 15125652, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("llama_spawn_egg", new class_3558(EntityType.LLAMA, 12623485, 10051392, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("magma_cube_spawn_egg", new class_3558(EntityType.MAGMA_CUBE, 3407872, 16579584, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("mooshroom_spawn_egg", new class_3558(EntityType.MOOSHROOM, 10489616, 12040119, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("mule_spawn_egg", new class_3558(EntityType.MULE, 1769984, 5321501, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("ocelot_spawn_egg", new class_3558(EntityType.OCELOT, 15720061, 5653556, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("parrot_spawn_egg", new class_3558(EntityType.PARROT, 894731, 16711680, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("phantom_spawn_egg", new class_3558(EntityType.PHANTOM, 4411786, 8978176, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("pig_spawn_egg", new class_3558(EntityType.PIG, 15771042, 14377823, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("polar_bear_spawn_egg", new class_3558(EntityType.POLAR_BEAR, 15921906, 9803152, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("pufferfish_spawn_egg", new class_3558(EntityType.PUFFERFISH, 16167425, 3654642, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("rabbit_spawn_egg", new class_3558(EntityType.RABBIT, 10051392, 7555121, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("salmon_spawn_egg", new class_3558(EntityType.SALMON, 10489616, 951412, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("sheep_spawn_egg", new class_3558(EntityType.SHEEP, 15198183, 16758197, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("shulker_spawn_egg", new class_3558(EntityType.SHULKER, 9725844, 5060690, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("silverfish_spawn_egg", new class_3558(EntityType.SILVERFISH, 7237230, 3158064, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("skeleton_spawn_egg", new class_3558(EntityType.SKELETON, 12698049, 4802889, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("skeleton_horse_spawn_egg", new class_3558(EntityType.SKELETON_HORSE, 6842447, 15066584, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("slime_spawn_egg", new class_3558(EntityType.SLIME, 5349438, 8306542, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("spider_spawn_egg", new class_3558(EntityType.SPIDER, 3419431, 11013646, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("squid_spawn_egg", new class_3558(EntityType.SQUID, 2243405, 7375001, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("stray_spawn_egg", new class_3558(EntityType.STRAY, 6387319, 14543594, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("tropical_fish_spawn_egg", new class_3558(EntityType.TROPICAL_FISH, 15690005, 16775663, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("turtle_spawn_egg", new class_3558(EntityType.TURTLE, 15198183, 44975, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("vex_spawn_egg", new class_3558(EntityType.VEX, 8032420, 15265265, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("villager_spawn_egg", new class_3558(EntityType.VILLAGER, 5651507, 12422002, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("vindicator_spawn_egg", new class_3558(EntityType.VINDICATOR, 9804699, 2580065, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("witch_spawn_egg", new class_3558(EntityType.WITCH, 3407872, 5349438, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("wither_skeleton_spawn_egg", new class_3558(EntityType.WITHER_SKELETON, 1315860, 4672845, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("wolf_spawn_egg", new class_3558(EntityType.WOLF, 14144467, 13545366, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("zombie_spawn_egg", new class_3558(EntityType.ZOMBIE, 44975, 7969893, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("zombie_horse_spawn_egg", new class_3558(EntityType.ZOMBIE_HORSE, 3232308, 9945732, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("zombie_pigman_spawn_egg", new class_3558(EntityType.ZOMBIE_PIGMAN, 15373203, 5009705, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("zombie_villager_spawn_egg", new class_3558(EntityType.ZOMBIE_VILLAGER, 5651507, 7969893, new Item.Settings().setGroup(ItemGroup.MISC)));
		register("experience_bottle", new ExperienceBottleItem(new Item.Settings().setGroup(ItemGroup.MISC).setRarity(Rarity.UNCOMMON)));
		register("fire_charge", new FireChargeItem(new Item.Settings().setGroup(ItemGroup.MISC)));
		register("writable_book", new WritableBookItem(new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("written_book", new WrittenBookItem(new Item.Settings().setMaxStackSize(16)));
		register("emerald", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("item_frame", new class_3556(new Item.Settings().setGroup(ItemGroup.DECORATIONS)));
		register(Blocks.FLOWER_POT, ItemGroup.DECORATIONS);
		register("carrot", new CropItem(3, 0.6F, Blocks.CARROTS, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("potato", new CropItem(1, 0.3F, Blocks.POTATOES, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("baked_potato", new FoodItem(5, 0.6F, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register(
			"poisonous_potato",
			new FoodItem(2, 0.3F, false, new Item.Settings().setGroup(ItemGroup.FOOD)).method_11371(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.6F)
		);
		register("map", new EmptyMapItem(new Item.Settings().setGroup(ItemGroup.MISC)));
		register("golden_carrot", new FoodItem(6, 1.2F, false, new Item.Settings().setGroup(ItemGroup.BREWING)));
		register(new class_3559(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, new Item.Settings().setGroup(ItemGroup.DECORATIONS).setRarity(Rarity.UNCOMMON)));
		register(
			new class_3559(
				Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, new Item.Settings().setGroup(ItemGroup.DECORATIONS).setRarity(Rarity.UNCOMMON)
			)
		);
		register(new SkullItem(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, new Item.Settings().setGroup(ItemGroup.DECORATIONS).setRarity(Rarity.UNCOMMON)));
		register(new class_3559(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, new Item.Settings().setGroup(ItemGroup.DECORATIONS).setRarity(Rarity.UNCOMMON)));
		register(new class_3559(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, new Item.Settings().setGroup(ItemGroup.DECORATIONS).setRarity(Rarity.UNCOMMON)));
		register(new class_3559(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, new Item.Settings().setGroup(ItemGroup.DECORATIONS).setRarity(Rarity.UNCOMMON)));
		register("carrot_on_a_stick", new CarrotOnAStickItem(new Item.Settings().setMaxDamage(25).setGroup(ItemGroup.field_17160)));
		register("nether_star", new NetherStarItem(new Item.Settings().setGroup(ItemGroup.MATERIALS).setRarity(Rarity.UNCOMMON)));
		register("pumpkin_pie", new FoodItem(8, 0.3F, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("firework_rocket", new FireworkItem(new Item.Settings().setGroup(ItemGroup.MISC)));
		register("firework_star", new FireworkChargeItem(new Item.Settings().setGroup(ItemGroup.MISC)));
		register("enchanted_book", new EnchantedBookItem(new Item.Settings().setMaxStackSize(1).setRarity(Rarity.UNCOMMON)));
		register("nether_brick", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("quartz", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("tnt_minecart", new MinecartItem(AbstractMinecartEntity.Type.TNT, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("hopper_minecart", new MinecartItem(AbstractMinecartEntity.Type.HOPPER, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("prismarine_shard", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("prismarine_crystals", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("rabbit", new FoodItem(3, 0.3F, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("cooked_rabbit", new FoodItem(5, 0.6F, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("rabbit_stew", new StewItem(10, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.FOOD)));
		register("rabbit_foot", new Item(new Item.Settings().setGroup(ItemGroup.BREWING)));
		register("rabbit_hide", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("armor_stand", new ArmorStandItem(new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS)));
		register("iron_horse_armor", new Item(new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("golden_horse_armor", new Item(new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("diamond_horse_armor", new Item(new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC)));
		register("lead", new LeadItem(new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("name_tag", new NameTagItem(new Item.Settings().setGroup(ItemGroup.TOOLS)));
		register("command_block_minecart", new MinecartItem(AbstractMinecartEntity.Type.COMMAND_BLOCK, new Item.Settings().setMaxStackSize(1)));
		register("mutton", new FoodItem(2, 0.3F, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("cooked_mutton", new FoodItem(6, 0.8F, true, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register(
			"white_banner", new BannerItem(Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS))
		);
		register(
			"orange_banner", new BannerItem(Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS))
		);
		register(
			"magenta_banner", new BannerItem(Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS))
		);
		register(
			"light_blue_banner",
			new BannerItem(Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS))
		);
		register(
			"yellow_banner", new BannerItem(Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS))
		);
		register("lime_banner", new BannerItem(Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS)));
		register("pink_banner", new BannerItem(Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS)));
		register("gray_banner", new BannerItem(Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS)));
		register(
			"light_gray_banner",
			new BannerItem(Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS))
		);
		register("cyan_banner", new BannerItem(Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS)));
		register(
			"purple_banner", new BannerItem(Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS))
		);
		register("blue_banner", new BannerItem(Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS)));
		register(
			"brown_banner", new BannerItem(Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS))
		);
		register(
			"green_banner", new BannerItem(Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS))
		);
		register("red_banner", new BannerItem(Blocks.RED_BANNER, Blocks.RED_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS)));
		register(
			"black_banner", new BannerItem(Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER, new Item.Settings().setMaxStackSize(16).setGroup(ItemGroup.DECORATIONS))
		);
		register("end_crystal", new EndCrystalItem(new Item.Settings().setGroup(ItemGroup.DECORATIONS).setRarity(Rarity.RARE)));
		register("chorus_fruit", new ChorusFruitItem(4, 0.3F, new Item.Settings().setGroup(ItemGroup.MATERIALS)).alwaysEdible());
		register("popped_chorus_fruit", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("beetroot", new FoodItem(1, 0.6F, false, new Item.Settings().setGroup(ItemGroup.FOOD)));
		register("beetroot_seeds", new SeedItem(Blocks.BEETROOTS, new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("beetroot_soup", new StewItem(6, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.FOOD)));
		register("dragon_breath", new Item(new Item.Settings().setRecipeRemainder(item2).setGroup(ItemGroup.BREWING).setRarity(Rarity.UNCOMMON)));
		register("splash_potion", new SplashPotionItem(new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.BREWING)));
		register("spectral_arrow", new SpectralArrowItem(new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("tipped_arrow", new TippedArrowItem(new Item.Settings().setGroup(ItemGroup.COMBAT)));
		register("lingering_potion", new LingeringPotionItem(new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.BREWING)));
		register("shield", new ShieldItem(new Item.Settings().setMaxDamage(336).setGroup(ItemGroup.COMBAT)));
		register("elytra", new ElytraItem(new Item.Settings().setMaxDamage(432).setGroup(ItemGroup.field_17160).setRarity(Rarity.UNCOMMON)));
		register("spruce_boat", new BoatItem(BoatEntity.Type.SPRUCE, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("birch_boat", new BoatItem(BoatEntity.Type.BIRCH, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("jungle_boat", new BoatItem(BoatEntity.Type.JUNGLE, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("acacia_boat", new BoatItem(BoatEntity.Type.ACACIA, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("dark_oak_boat", new BoatItem(BoatEntity.Type.DARK_OAK, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.field_17160)));
		register("totem_of_undying", new Item(new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.COMBAT).setRarity(Rarity.UNCOMMON)));
		register("shulker_shell", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("iron_nugget", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("knowledge_book", new class_3187(new Item.Settings().setMaxStackSize(1)));
		register("debug_stick", new class_3547(new Item.Settings().setMaxStackSize(1)));
		register("music_disc_13", new MusicDiscItem(1, Sounds.MUSIC_DISC_13, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE)));
		register(
			"music_disc_cat", new MusicDiscItem(2, Sounds.MUSIC_DISC_CAT, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE))
		);
		register(
			"music_disc_blocks", new MusicDiscItem(3, Sounds.MUSIC_DISC_BLOCKS, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE))
		);
		register(
			"music_disc_chirp", new MusicDiscItem(4, Sounds.MUSIC_DISC_CHIRP, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE))
		);
		register(
			"music_disc_far", new MusicDiscItem(5, Sounds.MUSIC_DISC_FAR, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE))
		);
		register(
			"music_disc_mall", new MusicDiscItem(6, Sounds.MUSIC_DISC_MALL, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE))
		);
		register(
			"music_disc_mellohi",
			new MusicDiscItem(7, Sounds.MUSIC_DISC_MELLOHI, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE))
		);
		register(
			"music_disc_stal", new MusicDiscItem(8, Sounds.MUSIC_DISC_STAL, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE))
		);
		register(
			"music_disc_strad", new MusicDiscItem(9, Sounds.MUSIC_DISC_STRAD, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE))
		);
		register(
			"music_disc_ward", new MusicDiscItem(10, Sounds.MUSIC_DISC_WARD, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE))
		);
		register("music_disc_11", new MusicDiscItem(11, Sounds.MUSIC_DISC_11, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE)));
		register(
			"music_disc_wait", new MusicDiscItem(12, Sounds.MUSIC_DISC_WAIT, new Item.Settings().setMaxStackSize(1).setGroup(ItemGroup.MISC).setRarity(Rarity.RARE))
		);
		register("trident", new class_3564(new Item.Settings().setMaxDamage(250).setGroup(ItemGroup.COMBAT)));
		register("phantom_membrane", new Item(new Item.Settings().setGroup(ItemGroup.BREWING)));
		register("nautilus_shell", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS)));
		register("heart_of_the_sea", new Item(new Item.Settings().setGroup(ItemGroup.MATERIALS).setRarity(Rarity.UNCOMMON)));
	}

	private static void registerBlockItem(Block block) {
		register(new BlockItem(block, new Item.Settings()));
	}

	private static void register(Block block, ItemGroup itemGroup) {
		register(new BlockItem(block, new Item.Settings().setGroup(itemGroup)));
	}

	private static void register(BlockItem blockItem) {
		registerBlockItem(blockItem.getBlock(), blockItem);
	}

	protected static void registerBlockItem(Block block, Item blockItem) {
		method_16074(Registry.BLOCK.getId(block), blockItem);
	}

	private static void register(String string, Item item) {
		method_16074(new Identifier(string), item);
	}

	private static void method_16074(Identifier identifier, Item item) {
		if (item instanceof BlockItem) {
			((BlockItem)item).method_16015(BLOCK_ITEMS, item);
		}

		Registry.ITEM.add(identifier, item);
	}

	public ItemStack getDefaultStack() {
		return new ItemStack(this);
	}

	public boolean method_16075(Tag<Item> tag) {
		return tag.contains(this);
	}

	public static class Settings {
		private int maxStackSize = 64;
		private int maxDamage;
		private Item recipeRemainder;
		private ItemGroup group;
		private Rarity rarity = Rarity.COMMON;

		public Item.Settings setMaxStackSize(int maxStackSize) {
			if (this.maxDamage > 0) {
				throw new RuntimeException("Unable to have damage AND stack.");
			} else {
				this.maxStackSize = maxStackSize;
				return this;
			}
		}

		public Item.Settings setMaxDamageIfAbsent(int maxDamage) {
			return this.maxDamage == 0 ? this.setMaxDamage(maxDamage) : this;
		}

		private Item.Settings setMaxDamage(int maxDamage) {
			this.maxDamage = maxDamage;
			this.maxStackSize = 1;
			return this;
		}

		public Item.Settings setRecipeRemainder(Item recipeRemainder) {
			this.recipeRemainder = recipeRemainder;
			return this;
		}

		public Item.Settings setGroup(ItemGroup group) {
			this.group = group;
			return this;
		}

		public Item.Settings setRarity(Rarity rarity) {
			this.rarity = rarity;
			return this;
		}
	}
}
