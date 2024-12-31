package net.minecraft;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class class_4066 {
	private final BlockPos field_19722;
	private final DyeColor field_19723;
	@Nullable
	private final Text field_19724;

	public class_4066(BlockPos blockPos, DyeColor dyeColor, @Nullable Text text) {
		this.field_19722 = blockPos;
		this.field_19723 = dyeColor;
		this.field_19724 = text;
	}

	public static class_4066 method_17917(NbtCompound nbtCompound) {
		BlockPos blockPos = NbtHelper.toBlockPos(nbtCompound.getCompound("Pos"));
		DyeColor dyeColor = DyeColor.getByTranslationKey(nbtCompound.getString("Color"));
		Text text = nbtCompound.contains("Name") ? Text.Serializer.deserializeText(nbtCompound.getString("Name")) : null;
		return new class_4066(blockPos, dyeColor, text);
	}

	@Nullable
	public static class_4066 method_17916(BlockView blockView, BlockPos blockPos) {
		BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
		if (blockEntity instanceof BannerBlockEntity) {
			BannerBlockEntity bannerBlockEntity = (BannerBlockEntity)blockEntity;
			DyeColor dyeColor = bannerBlockEntity.method_16776(() -> blockView.getBlockState(blockPos));
			Text text = bannerBlockEntity.hasCustomName() ? bannerBlockEntity.method_15541() : null;
			return new class_4066(blockPos, dyeColor, text);
		} else {
			return null;
		}
	}

	public BlockPos method_17915() {
		return this.field_19722;
	}

	public class_3082.class_3083 method_17919() {
		switch (this.field_19723) {
			case WHITE:
				return class_3082.class_3083.BANNER_WHITE;
			case ORANGE:
				return class_3082.class_3083.BANNER_ORANGE;
			case MAGENTA:
				return class_3082.class_3083.BANNER_MAGENTA;
			case LIGHT_BLUE:
				return class_3082.class_3083.BANNER_LIGHT_BLUE;
			case YELLOW:
				return class_3082.class_3083.BANNER_YELLOW;
			case LIME:
				return class_3082.class_3083.BANNER_LIME;
			case PINK:
				return class_3082.class_3083.BANNER_PINK;
			case GRAY:
				return class_3082.class_3083.BANNER_GRAY;
			case LIGHT_GRAY:
				return class_3082.class_3083.BANNER_LIGHT_GRAY;
			case CYAN:
				return class_3082.class_3083.BANNER_CYAN;
			case PURPLE:
				return class_3082.class_3083.BANNER_PURPLE;
			case BLUE:
				return class_3082.class_3083.BANNER_BLUE;
			case BROWN:
				return class_3082.class_3083.BANNER_BROWN;
			case GREEN:
				return class_3082.class_3083.BANNER_GREEN;
			case RED:
				return class_3082.class_3083.BANNER_RED;
			case BLACK:
			default:
				return class_3082.class_3083.BANNER_BLACK;
		}
	}

	@Nullable
	public Text method_17920() {
		return this.field_19724;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4066 lv = (class_4066)object;
			return Objects.equals(this.field_19722, lv.field_19722) && this.field_19723 == lv.field_19723 && Objects.equals(this.field_19724, lv.field_19724);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_19722, this.field_19723, this.field_19724});
	}

	public NbtCompound method_17921() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.put("Pos", NbtHelper.fromBlockPos(this.field_19722));
		nbtCompound.putString("Color", this.field_19723.getTranslationKey());
		if (this.field_19724 != null) {
			nbtCompound.putString("Name", Text.Serializer.serialize(this.field_19724));
		}

		return nbtCompound;
	}

	public String method_17922() {
		return "banner-" + this.field_19722.getX() + "," + this.field_19722.getY() + "," + this.field_19722.getZ();
	}
}
