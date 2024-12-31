package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class class_4402 extends class_2925 {
	private final Identifier field_21675;
	private final Set<UUID> field_21676 = Sets.newHashSet();
	private int field_21677;
	private int field_21678 = 100;

	public class_4402(Identifier identifier, Text text) {
		super(text, class_2957.Color.WHITE, class_2957.Division.PROGRESS);
		this.field_21675 = identifier;
		this.setHealth(0.0F);
	}

	public Identifier method_20464() {
		return this.field_21675;
	}

	@Override
	public void method_12768(ServerPlayerEntity player) {
		super.method_12768(player);
		this.field_21676.add(player.getUuid());
	}

	public void method_20469(UUID uUID) {
		this.field_21676.add(uUID);
	}

	@Override
	public void method_12769(ServerPlayerEntity player) {
		super.method_12769(player);
		this.field_21676.remove(player.getUuid());
	}

	@Override
	public void method_21246() {
		super.method_21246();
		this.field_21676.clear();
	}

	public int method_20471() {
		return this.field_21677;
	}

	public int method_20473() {
		return this.field_21678;
	}

	public void method_20465(int i) {
		this.field_21677 = i;
		this.setHealth(MathHelper.clamp((float)i / (float)this.field_21678, 0.0F, 1.0F));
	}

	public void method_20470(int i) {
		this.field_21678 = i;
		this.setHealth(MathHelper.clamp((float)this.field_21677 / (float)i, 0.0F, 1.0F));
	}

	public final Text method_20475() {
		return ChatSerializer.method_20188(this.getTitle())
			.styled(
				style -> style.setFormatting(this.getColor().method_15532())
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(this.method_20464().toString())))
						.setInsertion(this.method_20464().toString())
			);
	}

	public boolean method_20468(Collection<ServerPlayerEntity> collection) {
		Set<UUID> set = Sets.newHashSet();
		Set<ServerPlayerEntity> set2 = Sets.newHashSet();

		for (UUID uUID : this.field_21676) {
			boolean bl = false;

			for (ServerPlayerEntity serverPlayerEntity : collection) {
				if (serverPlayerEntity.getUuid().equals(uUID)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				set.add(uUID);
			}
		}

		for (ServerPlayerEntity serverPlayerEntity2 : collection) {
			boolean bl2 = false;

			for (UUID uUID2 : this.field_21676) {
				if (serverPlayerEntity2.getUuid().equals(uUID2)) {
					bl2 = true;
					break;
				}
			}

			if (!bl2) {
				set2.add(serverPlayerEntity2);
			}
		}

		for (UUID uUID3 : set) {
			for (ServerPlayerEntity serverPlayerEntity3 : this.method_12770()) {
				if (serverPlayerEntity3.getUuid().equals(uUID3)) {
					this.method_12769(serverPlayerEntity3);
					break;
				}
			}

			this.field_21676.remove(uUID3);
		}

		for (ServerPlayerEntity serverPlayerEntity4 : set2) {
			this.method_12768(serverPlayerEntity4);
		}

		return !set.isEmpty() || !set2.isEmpty();
	}

	public NbtCompound method_20476() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", Text.Serializer.serialize(this.title));
		nbtCompound.putBoolean("Visible", this.method_21247());
		nbtCompound.putInt("Value", this.field_21677);
		nbtCompound.putInt("Max", this.field_21678);
		nbtCompound.putString("Color", this.getColor().method_15534());
		nbtCompound.putString("Overlay", this.getDivision().method_15535());
		nbtCompound.putBoolean("DarkenScreen", this.method_12929());
		nbtCompound.putBoolean("PlayBossMusic", this.method_12930());
		nbtCompound.putBoolean("CreateWorldFog", this.method_12931());
		NbtList nbtList = new NbtList();

		for (UUID uUID : this.field_21676) {
			nbtList.add((NbtElement)NbtHelper.fromUuid(uUID));
		}

		nbtCompound.put("Players", nbtList);
		return nbtCompound;
	}

	public static class_4402 method_20466(NbtCompound nbtCompound, Identifier identifier) {
		class_4402 lv = new class_4402(identifier, Text.Serializer.deserializeText(nbtCompound.getString("Name")));
		lv.method_12771(nbtCompound.getBoolean("Visible"));
		lv.method_20465(nbtCompound.getInt("Value"));
		lv.method_20470(nbtCompound.getInt("Max"));
		lv.setColor(class_2957.Color.method_15533(nbtCompound.getString("Color")));
		lv.setDivision(class_2957.Division.method_15536(nbtCompound.getString("Overlay")));
		lv.method_12921(nbtCompound.getBoolean("DarkenScreen"));
		lv.method_12922(nbtCompound.getBoolean("PlayBossMusic"));
		lv.method_12923(nbtCompound.getBoolean("CreateWorldFog"));
		NbtList nbtList = nbtCompound.getList("Players", 10);

		for (int i = 0; i < nbtList.size(); i++) {
			lv.method_20469(NbtHelper.toUuid(nbtList.getCompound(i)));
		}

		return lv;
	}

	public void method_20472(ServerPlayerEntity serverPlayerEntity) {
		if (this.field_21676.contains(serverPlayerEntity.getUuid())) {
			this.method_12768(serverPlayerEntity);
		}
	}

	public void method_20474(ServerPlayerEntity serverPlayerEntity) {
		super.method_12769(serverPlayerEntity);
	}
}
