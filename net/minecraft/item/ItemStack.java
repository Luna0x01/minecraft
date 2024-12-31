package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.class_3462;
import net.minecraft.class_4220;
import net.minecraft.class_4238;
import net.minecraft.class_4488;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ItemStack EMPTY = new ItemStack((Item)null);
	public static final DecimalFormat MODIFIER_FORMAT = createModifierFormat();
	private int count;
	private int pickupTick;
	@Deprecated
	private final Item item;
	private NbtCompound nbt;
	private boolean empty;
	private ItemFrameEntity itemFrame;
	private CachedBlockPosition field_17202;
	private boolean lastDestroyResult;
	private CachedBlockPosition field_17203;
	private boolean lastPlaceOnResult;

	private static DecimalFormat createModifierFormat() {
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
		return decimalFormat;
	}

	public ItemStack(Itemable itemable) {
		this(itemable, 1);
	}

	public ItemStack(Itemable itemable, int i) {
		this.item = itemable == null ? null : itemable.getItem();
		this.count = i;
		this.updateStackStatus();
	}

	private void updateStackStatus() {
		this.empty = false;
		this.empty = this.isEmpty();
	}

	private ItemStack(NbtCompound nbtCompound) {
		Item item = Registry.ITEM.getByIdentifier(new Identifier(nbtCompound.getString("id")));
		this.item = item == null ? Items.AIR : item;
		this.count = nbtCompound.getByte("Count");
		if (nbtCompound.contains("tag", 10)) {
			this.nbt = nbtCompound.getCompound("tag");
			this.getItem().postProcessNbt(nbtCompound);
		}

		if (this.getItem().isDamageable()) {
			this.setDamage(this.getDamage());
		}

		this.updateStackStatus();
	}

	public static ItemStack from(NbtCompound nbt) {
		try {
			return new ItemStack(nbt);
		} catch (RuntimeException var2) {
			LOGGER.debug("Tried to load invalid item: {}", nbt, var2);
			return EMPTY;
		}
	}

	public boolean isEmpty() {
		if (this == EMPTY) {
			return true;
		} else {
			return this.getItem() == null || this.getItem() == Items.AIR ? true : this.count <= 0;
		}
	}

	public ItemStack split(int amount) {
		int i = Math.min(amount, this.count);
		ItemStack itemStack = this.copy();
		itemStack.setCount(i);
		this.decrement(i);
		return itemStack;
	}

	public Item getItem() {
		return this.empty ? Items.AIR : this.item;
	}

	public ActionResult method_16097(ItemUsageContext itemUsageContext) {
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(itemUsageContext.getWorld(), blockPos, false);
		if (playerEntity != null && !playerEntity.abilities.allowModifyWorld && !this.method_16106(itemUsageContext.getWorld().method_16314(), cachedBlockPosition)) {
			return ActionResult.PASS;
		} else {
			Item item = this.getItem();
			ActionResult actionResult = item.useOnBlock(itemUsageContext);
			if (playerEntity != null && actionResult == ActionResult.SUCCESS) {
				playerEntity.method_15932(Stats.USED.method_21429(item));
			}

			return actionResult;
		}
	}

	public float getBlockBreakingSpeed(BlockState state) {
		return this.getItem().getBlockBreakingSpeed(this, state);
	}

	public TypedActionResult<ItemStack> method_11390(World world, PlayerEntity player, Hand hand) {
		return this.getItem().method_13649(world, player, hand);
	}

	public ItemStack method_11388(World world, LivingEntity entity) {
		return this.getItem().method_3367(this, world, entity);
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		Identifier identifier = Registry.ITEM.getId(this.getItem());
		nbt.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
		nbt.putByte("Count", (byte)this.count);
		if (this.nbt != null) {
			nbt.put("tag", this.nbt);
		}

		return nbt;
	}

	public int getMaxCount() {
		return this.getItem().getMaxCount();
	}

	public boolean isStackable() {
		return this.getMaxCount() > 1 && (!this.isDamageable() || !this.isDamaged());
	}

	public boolean isDamageable() {
		if (!this.empty && this.getItem().getMaxDamage() > 0) {
			NbtCompound nbtCompound = this.getNbt();
			return nbtCompound == null || !nbtCompound.getBoolean("Unbreakable");
		} else {
			return false;
		}
	}

	public boolean isDamaged() {
		return this.isDamageable() && this.getDamage() > 0;
	}

	public int getDamage() {
		return this.nbt == null ? 0 : this.nbt.getInt("Damage");
	}

	public void setDamage(int damage) {
		this.getOrCreateNbt().putInt("Damage", Math.max(0, damage));
	}

	public int getMaxDamage() {
		return this.getItem().getMaxDamage();
	}

	public boolean damage(int amount, Random rand, @Nullable ServerPlayerEntity player) {
		if (!this.isDamageable()) {
			return false;
		} else {
			if (amount > 0) {
				int i = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, this);
				int j = 0;

				for (int k = 0; i > 0 && k < amount; k++) {
					if (UnbreakingEnchantment.shouldPreventDamage(this, i, rand)) {
						j++;
					}
				}

				amount -= j;
				if (amount <= 0) {
					return false;
				}
			}

			if (player != null && amount != 0) {
				AchievementsAndCriterions.field_16347.method_14284(player, this, this.getDamage() + amount);
			}

			int l = this.getDamage() + amount;
			this.setDamage(l);
			return l >= this.getMaxDamage();
		}
	}

	public void damage(int amount, LivingEntity entity) {
		if (!(entity instanceof PlayerEntity) || !((PlayerEntity)entity).abilities.creativeMode) {
			if (this.isDamageable()) {
				if (this.damage(amount, entity.getRandom(), entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null)) {
					entity.method_6111(this);
					Item item = this.getItem();
					this.decrement(1);
					if (entity instanceof PlayerEntity) {
						((PlayerEntity)entity).method_15932(Stats.BROKEN.method_21429(item));
					}

					this.setDamage(0);
				}
			}
		}
	}

	public void onEntityHit(LivingEntity entity, PlayerEntity attacker) {
		Item item = this.getItem();
		if (item.onEntityHit(this, entity, attacker)) {
			attacker.method_15932(Stats.USED.method_21429(item));
		}
	}

	public void method_11306(World world, BlockState blockState, BlockPos blockPos, PlayerEntity playerEntity) {
		Item item = this.getItem();
		if (item.method_3356(this, world, blockState, blockPos, playerEntity)) {
			playerEntity.method_15932(Stats.USED.method_21429(item));
		}
	}

	public boolean method_11396(BlockState state) {
		return this.getItem().method_3346(state);
	}

	public boolean method_6329(PlayerEntity player, LivingEntity entity, Hand hand) {
		return this.getItem().method_3353(this, player, entity, hand);
	}

	public ItemStack copy() {
		ItemStack itemStack = new ItemStack(this.getItem(), this.count);
		itemStack.setPickupTick(this.getPickupTick());
		if (this.nbt != null) {
			itemStack.nbt = this.nbt.copy();
		}

		return itemStack;
	}

	public static boolean equalsIgnoreDamage(ItemStack left, ItemStack right) {
		if (left.isEmpty() && right.isEmpty()) {
			return true;
		} else if (left.isEmpty() || right.isEmpty()) {
			return false;
		} else {
			return left.nbt == null && right.nbt != null ? false : left.nbt == null || left.nbt.equals(right.nbt);
		}
	}

	public static boolean equalsAll(ItemStack left, ItemStack right) {
		if (left.isEmpty() && right.isEmpty()) {
			return true;
		} else {
			return !left.isEmpty() && !right.isEmpty() ? left.equalsAll(right) : false;
		}
	}

	private boolean equalsAll(ItemStack stack) {
		if (this.count != stack.count) {
			return false;
		} else if (this.getItem() != stack.getItem()) {
			return false;
		} else {
			return this.nbt == null && stack.nbt != null ? false : this.nbt == null || this.nbt.equals(stack.nbt);
		}
	}

	public static boolean equalsIgnoreNbt(ItemStack left, ItemStack right) {
		if (left == right) {
			return true;
		} else {
			return !left.isEmpty() && !right.isEmpty() ? left.equalsIgnoreNbt(right) : false;
		}
	}

	public static boolean equals(ItemStack stack0, ItemStack stack1) {
		if (stack0 == stack1) {
			return true;
		} else {
			return !stack0.isEmpty() && !stack1.isEmpty() ? stack0.equals(stack1) : false;
		}
	}

	public boolean equalsIgnoreNbt(ItemStack stack) {
		return !stack.isEmpty() && this.getItem() == stack.getItem();
	}

	public boolean equals(ItemStack other) {
		return !this.isDamageable() ? this.equalsIgnoreNbt(other) : !other.isEmpty() && this.getItem() == other.getItem();
	}

	public String getTranslationKey() {
		return this.getItem().getTranslationKey(this);
	}

	public String toString() {
		return this.count + "x" + this.getItem().getTranslationKey();
	}

	public void inventoryTick(World world, Entity entity, int slot, boolean selected) {
		if (this.pickupTick > 0) {
			this.pickupTick--;
		}

		if (this.getItem() != null) {
			this.getItem().inventoryTick(this, world, entity, slot, selected);
		}
	}

	public void onCraft(World world, PlayerEntity player, int amount) {
		player.method_15930(Stats.CRAFTED.method_21429(this.getItem()), amount);
		this.getItem().onCraft(this, world, player);
	}

	public int getMaxUseTime() {
		return this.getItem().getMaxUseTime(this);
	}

	public UseAction getUseAction() {
		return this.getItem().getUseAction(this);
	}

	public void method_11389(World world, LivingEntity livingEntity, int i) {
		this.getItem().method_3359(this, world, livingEntity, i);
	}

	public boolean hasNbt() {
		return !this.empty && this.nbt != null && !this.nbt.isEmpty();
	}

	@Nullable
	public NbtCompound getNbt() {
		return this.nbt;
	}

	public NbtCompound getOrCreateNbt() {
		if (this.nbt == null) {
			this.setNbt(new NbtCompound());
		}

		return this.nbt;
	}

	public NbtCompound getOrCreateNbtCompound(String key) {
		if (this.nbt != null && this.nbt.contains(key, 10)) {
			return this.nbt.getCompound(key);
		} else {
			NbtCompound nbtCompound = new NbtCompound();
			this.addNbt(key, nbtCompound);
			return nbtCompound;
		}
	}

	@Nullable
	public NbtCompound getNbtCompound(String key) {
		return this.nbt != null && this.nbt.contains(key, 10) ? this.nbt.getCompound(key) : null;
	}

	public void removeNbt(String key) {
		if (this.nbt != null && this.nbt.contains(key)) {
			this.nbt.remove(key);
			if (this.nbt.isEmpty()) {
				this.nbt = null;
			}
		}
	}

	public NbtList getEnchantments() {
		return this.nbt != null ? this.nbt.getList("Enchantments", 10) : new NbtList();
	}

	public void setNbt(@Nullable NbtCompound nbt) {
		this.nbt = nbt;
	}

	public Text getName() {
		NbtCompound nbtCompound = this.getNbtCompound("display");
		if (nbtCompound != null && nbtCompound.contains("Name", 8)) {
			try {
				Text text = Text.Serializer.deserializeText(nbtCompound.getString("Name"));
				if (text != null) {
					return text;
				}

				nbtCompound.remove("Name");
			} catch (JsonParseException var3) {
				nbtCompound.remove("Name");
			}
		}

		return this.getItem().getDisplayName(this);
	}

	public ItemStack setCustomName(@Nullable Text name) {
		NbtCompound nbtCompound = this.getOrCreateNbtCompound("display");
		if (name != null) {
			nbtCompound.putString("Name", Text.Serializer.serialize(name));
		} else {
			nbtCompound.remove("Name");
		}

		return this;
	}

	public void removeCustomName() {
		NbtCompound nbtCompound = this.getNbtCompound("display");
		if (nbtCompound != null) {
			nbtCompound.remove("Name");
			if (nbtCompound.isEmpty()) {
				this.removeNbt("display");
			}
		}

		if (this.nbt != null && this.nbt.isEmpty()) {
			this.nbt = null;
		}
	}

	public boolean hasCustomName() {
		NbtCompound nbtCompound = this.getNbtCompound("display");
		return nbtCompound != null && nbtCompound.contains("Name", 8);
	}

	public List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
		List<Text> list = Lists.newArrayList();
		Text text = new LiteralText("").append(this.getName()).formatted(this.getRarity().formatting);
		if (this.hasCustomName()) {
			text.formatted(Formatting.ITALIC);
		}

		list.add(text);
		if (!context.isAdvanced() && !this.hasCustomName() && this.getItem() == Items.FILLED_MAP) {
			list.add(new LiteralText("#" + FilledMapItem.method_16117(this)).formatted(Formatting.GRAY));
		}

		int i = 0;
		if (this.hasNbt() && this.nbt.contains("HideFlags", 99)) {
			i = this.nbt.getInt("HideFlags");
		}

		if ((i & 32) == 0) {
			this.getItem().appendTooltips(this, player == null ? null : player.world, list, context);
		}

		if (this.hasNbt()) {
			if ((i & 1) == 0) {
				NbtList nbtList = this.getEnchantments();

				for (int j = 0; j < nbtList.size(); j++) {
					NbtCompound nbtCompound = nbtList.getCompound(j);
					Enchantment enchantment = Registry.ENCHANTMENT.getByIdentifier(Identifier.fromString(nbtCompound.getString("id")));
					if (enchantment != null) {
						list.add(enchantment.method_16257(nbtCompound.getInt("lvl")));
					}
				}
			}

			if (this.nbt.contains("display", 10)) {
				NbtCompound nbtCompound2 = this.nbt.getCompound("display");
				if (nbtCompound2.contains("color", 3)) {
					if (context.isAdvanced()) {
						list.add(new TranslatableText("item.color", String.format("#%06X", nbtCompound2.getInt("color"))).formatted(Formatting.GRAY));
					} else {
						list.add(new TranslatableText("item.dyed").formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC}));
					}
				}

				if (nbtCompound2.getType("Lore") == 9) {
					NbtList nbtList2 = nbtCompound2.getList("Lore", 8);

					for (int k = 0; k < nbtList2.size(); k++) {
						list.add(new LiteralText(nbtList2.getString(k)).formatted(new Formatting[]{Formatting.DARK_PURPLE, Formatting.ITALIC}));
					}
				}
			}
		}

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			Multimap<String, AttributeModifier> multimap = this.getAttributes(equipmentSlot);
			if (!multimap.isEmpty() && (i & 2) == 0) {
				list.add(new LiteralText(""));
				list.add(new TranslatableText("item.modifiers." + equipmentSlot.getName()).formatted(Formatting.GRAY));

				for (Entry<String, AttributeModifier> entry : multimap.entries()) {
					AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
					double d = attributeModifier.getAmount();
					boolean bl = false;
					if (player != null) {
						if (attributeModifier.getId() == Item.ATTACK_DAMAGE_MODIFIER_UUID) {
							d += player.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).getBaseValue();
							d += (double)EnchantmentHelper.method_16260(this, class_3462.field_16818);
							bl = true;
						} else if (attributeModifier.getId() == Item.ATTACK_SPEED_MODIFIER) {
							d += player.initializeAttribute(EntityAttributes.GENERIC_ATTACK_SPEED).getBaseValue();
							bl = true;
						}
					}

					double f;
					if (attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2) {
						f = d;
					} else {
						f = d * 100.0;
					}

					if (bl) {
						list.add(
							new LiteralText(" ")
								.append(
									new TranslatableText(
										"attribute.modifier.equals." + attributeModifier.getOperation(),
										MODIFIER_FORMAT.format(f),
										new TranslatableText("attribute.name." + (String)entry.getKey())
									)
								)
								.formatted(Formatting.DARK_GREEN)
						);
					} else if (d > 0.0) {
						list.add(
							new TranslatableText(
									"attribute.modifier.plus." + attributeModifier.getOperation(),
									MODIFIER_FORMAT.format(f),
									new TranslatableText("attribute.name." + (String)entry.getKey())
								)
								.formatted(Formatting.BLUE)
						);
					} else if (d < 0.0) {
						f *= -1.0;
						list.add(
							new TranslatableText(
									"attribute.modifier.take." + attributeModifier.getOperation(),
									MODIFIER_FORMAT.format(f),
									new TranslatableText("attribute.name." + (String)entry.getKey())
								)
								.formatted(Formatting.RED)
						);
					}
				}
			}
		}

		if (this.hasNbt() && this.getNbt().getBoolean("Unbreakable") && (i & 4) == 0) {
			list.add(new TranslatableText("item.unbreakable").formatted(Formatting.BLUE));
		}

		if (this.hasNbt() && this.nbt.contains("CanDestroy", 9) && (i & 8) == 0) {
			NbtList nbtList3 = this.nbt.getList("CanDestroy", 8);
			if (!nbtList3.isEmpty()) {
				list.add(new LiteralText(""));
				list.add(new TranslatableText("item.canBreak").formatted(Formatting.GRAY));

				for (int l = 0; l < nbtList3.size(); l++) {
					list.addAll(method_16108(nbtList3.getString(l)));
				}
			}
		}

		if (this.hasNbt() && this.nbt.contains("CanPlaceOn", 9) && (i & 16) == 0) {
			NbtList nbtList4 = this.nbt.getList("CanPlaceOn", 8);
			if (!nbtList4.isEmpty()) {
				list.add(new LiteralText(""));
				list.add(new TranslatableText("item.canPlace").formatted(Formatting.GRAY));

				for (int m = 0; m < nbtList4.size(); m++) {
					list.addAll(method_16108(nbtList4.getString(m)));
				}
			}
		}

		if (context.isAdvanced()) {
			if (this.isDamaged()) {
				list.add(new TranslatableText("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
			}

			list.add(new LiteralText(Registry.ITEM.getId(this.getItem()).toString()).formatted(Formatting.DARK_GRAY));
			if (this.hasNbt()) {
				list.add(new TranslatableText("item.nbt_tags", this.getNbt().getKeys().size()).formatted(Formatting.DARK_GRAY));
			}
		}

		return list;
	}

	private static Collection<Text> method_16108(String string) {
		try {
			class_4238 lv = new class_4238(new StringReader(string), true).method_19300(true);
			BlockState blockState = lv.method_19301();
			Identifier identifier = lv.method_19307();
			boolean bl = blockState != null;
			boolean bl2 = identifier != null;
			if (bl || bl2) {
				if (bl) {
					return Lists.newArrayList(blockState.getBlock().method_16600().formatted(Formatting.DARK_GRAY));
				}

				Tag<Block> tag = BlockTags.getContainer().method_21486(identifier);
				if (tag != null) {
					Collection<Block> collection = tag.values();
					if (!collection.isEmpty()) {
						return (Collection<Text>)collection.stream().map(Block::method_16600).map(text -> text.formatted(Formatting.DARK_GRAY)).collect(Collectors.toList());
					}
				}
			}
		} catch (CommandSyntaxException var8) {
		}

		return Lists.newArrayList(new LiteralText("missingno").formatted(Formatting.DARK_GRAY));
	}

	public boolean hasEnchantmentGlint() {
		return this.getItem().hasEnchantmentGlint(this);
	}

	public Rarity getRarity() {
		return this.getItem().getRarity(this);
	}

	public boolean isEnchantable() {
		return !this.getItem().isEnchantable(this) ? false : !this.hasEnchantments();
	}

	public void addEnchantment(Enchantment enchantment, int level) {
		this.getOrCreateNbt();
		if (!this.nbt.contains("Enchantments", 9)) {
			this.nbt.put("Enchantments", new NbtList());
		}

		NbtList nbtList = this.nbt.getList("Enchantments", 10);
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
		nbtCompound.putShort("lvl", (short)((byte)level));
		nbtList.add((NbtElement)nbtCompound);
	}

	public boolean hasEnchantments() {
		return this.nbt != null && this.nbt.contains("Enchantments", 9) ? !this.nbt.getList("Enchantments", 10).isEmpty() : false;
	}

	public void addNbt(String key, NbtElement value) {
		this.getOrCreateNbt().put(key, value);
	}

	public boolean isInItemFrame() {
		return this.itemFrame != null;
	}

	public void setInItemFrame(@Nullable ItemFrameEntity itemFrame) {
		this.itemFrame = itemFrame;
	}

	@Nullable
	public ItemFrameEntity getItemFrame() {
		return this.empty ? null : this.itemFrame;
	}

	public int getRepairCost() {
		return this.hasNbt() && this.nbt.contains("RepairCost", 3) ? this.nbt.getInt("RepairCost") : 0;
	}

	public void setRepairCost(int cost) {
		this.getOrCreateNbt().putInt("RepairCost", cost);
	}

	public Multimap<String, AttributeModifier> getAttributes(EquipmentSlot slot) {
		Multimap<String, AttributeModifier> multimap;
		if (this.hasNbt() && this.nbt.contains("AttributeModifiers", 9)) {
			multimap = HashMultimap.create();
			NbtList nbtList = this.nbt.getList("AttributeModifiers", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				AttributeModifier attributeModifier = EntityAttributes.fromNbt(nbtCompound);
				if (attributeModifier != null
					&& (!nbtCompound.contains("Slot", 8) || nbtCompound.getString("Slot").equals(slot.getName()))
					&& attributeModifier.getId().getLeastSignificantBits() != 0L
					&& attributeModifier.getId().getMostSignificantBits() != 0L) {
					multimap.put(nbtCompound.getString("AttributeName"), attributeModifier);
				}
			}
		} else {
			multimap = this.getItem().method_6326(slot);
		}

		return multimap;
	}

	public void setAttribute(String attributeName, AttributeModifier modifier, @Nullable EquipmentSlot slot) {
		this.getOrCreateNbt();
		if (!this.nbt.contains("AttributeModifiers", 9)) {
			this.nbt.put("AttributeModifiers", new NbtList());
		}

		NbtList nbtList = this.nbt.getList("AttributeModifiers", 10);
		NbtCompound nbtCompound = EntityAttributes.toNbt(modifier);
		nbtCompound.putString("AttributeName", attributeName);
		if (slot != null) {
			nbtCompound.putString("Slot", slot.getName());
		}

		nbtList.add((NbtElement)nbtCompound);
	}

	public Text toHoverableText() {
		Text text = new LiteralText("").append(this.getName());
		if (this.hasCustomName()) {
			text.formatted(Formatting.ITALIC);
		}

		Text text2 = ChatSerializer.method_20188(text);
		if (!this.empty) {
			NbtCompound nbtCompound = this.toNbt(new NbtCompound());
			text2.formatted(this.getRarity().formatting)
				.styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new LiteralText(nbtCompound.toString()))));
		}

		return text2;
	}

	private static boolean method_16098(CachedBlockPosition cachedBlockPosition, @Nullable CachedBlockPosition cachedBlockPosition2) {
		if (cachedBlockPosition2 == null || cachedBlockPosition.getBlockState() != cachedBlockPosition2.getBlockState()) {
			return false;
		} else if (cachedBlockPosition.getBlockEntity() == null && cachedBlockPosition2.getBlockEntity() == null) {
			return true;
		} else {
			return cachedBlockPosition.getBlockEntity() != null && cachedBlockPosition2.getBlockEntity() != null
				? Objects.equals(cachedBlockPosition.getBlockEntity().toNbt(new NbtCompound()), cachedBlockPosition2.getBlockEntity().toNbt(new NbtCompound()))
				: false;
		}
	}

	public boolean method_16103(class_4488 arg, CachedBlockPosition cachedBlockPosition) {
		if (method_16098(cachedBlockPosition, this.field_17202)) {
			return this.lastDestroyResult;
		} else {
			this.field_17202 = cachedBlockPosition;
			if (this.hasNbt() && this.nbt.contains("CanDestroy", 9)) {
				NbtList nbtList = this.nbt.getList("CanDestroy", 8);

				for (int i = 0; i < nbtList.size(); i++) {
					String string = nbtList.getString(i);

					try {
						Predicate<CachedBlockPosition> predicate = class_4220.method_19107().parse(new StringReader(string)).create(arg);
						if (predicate.test(cachedBlockPosition)) {
							this.lastDestroyResult = true;
							return true;
						}
					} catch (CommandSyntaxException var7) {
					}
				}
			}

			this.lastDestroyResult = false;
			return false;
		}
	}

	public boolean method_16106(class_4488 arg, CachedBlockPosition cachedBlockPosition) {
		if (method_16098(cachedBlockPosition, this.field_17203)) {
			return this.lastPlaceOnResult;
		} else {
			this.field_17203 = cachedBlockPosition;
			if (this.hasNbt() && this.nbt.contains("CanPlaceOn", 9)) {
				NbtList nbtList = this.nbt.getList("CanPlaceOn", 8);

				for (int i = 0; i < nbtList.size(); i++) {
					String string = nbtList.getString(i);

					try {
						Predicate<CachedBlockPosition> predicate = class_4220.method_19107().parse(new StringReader(string)).create(arg);
						if (predicate.test(cachedBlockPosition)) {
							this.lastPlaceOnResult = true;
							return true;
						}
					} catch (CommandSyntaxException var7) {
					}
				}
			}

			this.lastPlaceOnResult = false;
			return false;
		}
	}

	public int getPickupTick() {
		return this.pickupTick;
	}

	public void setPickupTick(int tick) {
		this.pickupTick = tick;
	}

	public int getCount() {
		return this.empty ? 0 : this.count;
	}

	public void setCount(int count) {
		this.count = count;
		this.updateStackStatus();
	}

	public void increment(int amount) {
		this.setCount(this.count + amount);
	}

	public void decrement(int amount) {
		this.increment(-amount);
	}
}
