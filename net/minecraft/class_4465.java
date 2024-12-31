package net.minecraft;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4465 {
	private static final Logger field_21910 = LogManager.getLogger();
	private static final class_4458 field_21911 = new class_4458(
		new TranslatableText("resourcePack.broken_assets").formatted(new Formatting[]{Formatting.RED, Formatting.ITALIC}), 4
	);
	private final String field_21912;
	private final Supplier<class_4454> field_21913;
	private final Text field_21914;
	private final Text field_21915;
	private final class_4461 field_21916;
	private final class_4465.class_4466 field_21917;
	private final boolean field_21918;
	private final boolean field_21919;

	@Nullable
	public static <T extends class_4465> T method_21359(
		String string, boolean bl, Supplier<class_4454> supplier, class_4465.class_4467<T> arg, class_4465.class_4466 arg2
	) {
		try {
			class_4454 lv = (class_4454)supplier.get();
			Throwable var6 = null;

			class_4465 var8;
			try {
				class_4458 lv2 = lv.method_21329(class_4458.field_21894);
				if (bl && lv2 == null) {
					field_21910.error(
						"Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!"
					);
					lv2 = field_21911;
				}

				if (lv2 == null) {
					field_21910.warn("Couldn't find pack meta for pack {}", string);
					return null;
				}

				var8 = arg.create(string, bl, supplier, lv, lv2, arg2);
			} catch (Throwable var19) {
				var6 = var19;
				throw var19;
			} finally {
				if (lv != null) {
					if (var6 != null) {
						try {
							lv.close();
						} catch (Throwable var18) {
							var6.addSuppressed(var18);
						}
					} else {
						lv.close();
					}
				}
			}

			return (T)var8;
		} catch (IOException var21) {
			field_21910.warn("Couldn't get pack info for: {}", var21.toString());
			return null;
		}
	}

	public class_4465(String string, boolean bl, Supplier<class_4454> supplier, Text text, Text text2, class_4461 arg, class_4465.class_4466 arg2, boolean bl2) {
		this.field_21912 = string;
		this.field_21913 = supplier;
		this.field_21914 = text;
		this.field_21915 = text2;
		this.field_21916 = arg;
		this.field_21918 = bl;
		this.field_21917 = arg2;
		this.field_21919 = bl2;
	}

	public class_4465(String string, boolean bl, Supplier<class_4454> supplier, class_4454 arg, class_4458 arg2, class_4465.class_4466 arg3) {
		this(string, bl, supplier, new LiteralText(arg.method_5899()), arg2.method_21336(), class_4461.method_21344(arg2.method_21337()), arg3, false);
	}

	public Text method_21358() {
		return this.field_21914;
	}

	public Text method_21362() {
		return this.field_21915;
	}

	public Text method_21360(boolean bl) {
		return ChatSerializer.method_20188(new LiteralText(this.field_21912))
			.styled(
				style -> style.setFormatting(bl ? Formatting.GREEN : Formatting.RED)
						.setInsertion(StringArgumentType.escapeIfRequired(this.field_21912))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("").append(this.field_21914).append("\n").append(this.field_21915)))
			);
	}

	public class_4461 method_21363() {
		return this.field_21916;
	}

	public class_4454 method_21364() {
		return (class_4454)this.field_21913.get();
	}

	public String method_21365() {
		return this.field_21912;
	}

	public boolean method_21366() {
		return this.field_21918;
	}

	public boolean method_21367() {
		return this.field_21919;
	}

	public class_4465.class_4466 method_21368() {
		return this.field_21917;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_4465)) {
			return false;
		} else {
			class_4465 lv = (class_4465)object;
			return this.field_21912.equals(lv.field_21912);
		}
	}

	public int hashCode() {
		return this.field_21912.hashCode();
	}

	public static enum class_4466 {
		TOP,
		BOTTOM;

		public <T, P extends class_4465> int method_21370(List<T> list, T object, Function<T, P> function, boolean bl) {
			class_4465.class_4466 lv = bl ? this.method_21369() : this;
			if (lv == BOTTOM) {
				int i;
				for (i = 0; i < list.size(); i++) {
					P lv2 = (P)function.apply(list.get(i));
					if (!lv2.method_21367() || lv2.method_21368() != this) {
						break;
					}
				}

				list.add(i, object);
				return i;
			} else {
				int j;
				for (j = list.size() - 1; j >= 0; j--) {
					P lv3 = (P)function.apply(list.get(j));
					if (!lv3.method_21367() || lv3.method_21368() != this) {
						break;
					}
				}

				list.add(j + 1, object);
				return j + 1;
			}
		}

		public class_4465.class_4466 method_21369() {
			return this == TOP ? BOTTOM : TOP;
		}
	}

	@FunctionalInterface
	public interface class_4467<T extends class_4465> {
		@Nullable
		T create(String string, boolean bl, Supplier<class_4454> supplier, class_4454 arg, class_4458 arg2, class_4465.class_4466 arg3);
	}
}
