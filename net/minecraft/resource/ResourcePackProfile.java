package net.minecraft.resource;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackProfile implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final PackResourceMetadata BROKEN_PACK_META = new PackResourceMetadata(
		new TranslatableText("resourcePack.broken_assets").formatted(new Formatting[]{Formatting.field_1061, Formatting.field_1056}),
		SharedConstants.getGameVersion().getPackVersion()
	);
	private final String name;
	private final Supplier<ResourcePack> packGetter;
	private final Text displayName;
	private final Text description;
	private final ResourcePackCompatibility compatibility;
	private final ResourcePackProfile.InsertionPosition position;
	private final boolean alwaysEnabled;
	private final boolean pinned;

	@Nullable
	public static <T extends ResourcePackProfile> T of(
		String string, boolean bl, Supplier<ResourcePack> supplier, ResourcePackProfile.Factory<T> factory, ResourcePackProfile.InsertionPosition insertionPosition
	) {
		try {
			ResourcePack resourcePack = (ResourcePack)supplier.get();
			Throwable var6 = null;

			ResourcePackProfile var8;
			try {
				PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.READER);
				if (bl && packResourceMetadata == null) {
					LOGGER.error(
						"Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!"
					);
					packResourceMetadata = BROKEN_PACK_META;
				}

				if (packResourceMetadata == null) {
					LOGGER.warn("Couldn't find pack meta for pack {}", string);
					return null;
				}

				var8 = factory.create(string, bl, supplier, resourcePack, packResourceMetadata, insertionPosition);
			} catch (Throwable var19) {
				var6 = var19;
				throw var19;
			} finally {
				if (resourcePack != null) {
					if (var6 != null) {
						try {
							resourcePack.close();
						} catch (Throwable var18) {
							var6.addSuppressed(var18);
						}
					} else {
						resourcePack.close();
					}
				}
			}

			return (T)var8;
		} catch (IOException var21) {
			LOGGER.warn("Couldn't get pack info for: {}", var21.toString());
			return null;
		}
	}

	public ResourcePackProfile(
		String string,
		boolean bl,
		Supplier<ResourcePack> supplier,
		Text text,
		Text text2,
		ResourcePackCompatibility resourcePackCompatibility,
		ResourcePackProfile.InsertionPosition insertionPosition,
		boolean bl2
	) {
		this.name = string;
		this.packGetter = supplier;
		this.displayName = text;
		this.description = text2;
		this.compatibility = resourcePackCompatibility;
		this.alwaysEnabled = bl;
		this.position = insertionPosition;
		this.pinned = bl2;
	}

	public ResourcePackProfile(
		String string,
		boolean bl,
		Supplier<ResourcePack> supplier,
		ResourcePack resourcePack,
		PackResourceMetadata packResourceMetadata,
		ResourcePackProfile.InsertionPosition insertionPosition
	) {
		this(
			string,
			bl,
			supplier,
			new LiteralText(resourcePack.getName()),
			packResourceMetadata.getDescription(),
			ResourcePackCompatibility.from(packResourceMetadata.getPackFormat()),
			insertionPosition,
			false
		);
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public Text getDescription() {
		return this.description;
	}

	public Text getInformationText(boolean bl) {
		return Texts.bracketed(new LiteralText(this.name))
			.styled(
				style -> style.setColor(bl ? Formatting.field_1060 : Formatting.field_1061)
						.setInsertion(StringArgumentType.escapeIfRequired(this.name))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new LiteralText("").append(this.displayName).append("\n").append(this.description)))
			);
	}

	public ResourcePackCompatibility getCompatibility() {
		return this.compatibility;
	}

	public ResourcePack createResourcePack() {
		return (ResourcePack)this.packGetter.get();
	}

	public String getName() {
		return this.name;
	}

	public boolean isAlwaysEnabled() {
		return this.alwaysEnabled;
	}

	public boolean isPinned() {
		return this.pinned;
	}

	public ResourcePackProfile.InsertionPosition getInitialPosition() {
		return this.position;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof ResourcePackProfile)) {
			return false;
		} else {
			ResourcePackProfile resourcePackProfile = (ResourcePackProfile)object;
			return this.name.equals(resourcePackProfile.name);
		}
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public void close() {
	}

	@FunctionalInterface
	public interface Factory<T extends ResourcePackProfile> {
		@Nullable
		T create(
			String string,
			boolean bl,
			Supplier<ResourcePack> supplier,
			ResourcePack resourcePack,
			PackResourceMetadata packResourceMetadata,
			ResourcePackProfile.InsertionPosition insertionPosition
		);
	}

	public static enum InsertionPosition {
		field_14280,
		field_14281;

		public <T, P extends ResourcePackProfile> int insert(List<T> list, T object, Function<T, P> function, boolean bl) {
			ResourcePackProfile.InsertionPosition insertionPosition = bl ? this.inverse() : this;
			if (insertionPosition == field_14281) {
				int i;
				for (i = 0; i < list.size(); i++) {
					P resourcePackProfile = (P)function.apply(list.get(i));
					if (!resourcePackProfile.isPinned() || resourcePackProfile.getInitialPosition() != this) {
						break;
					}
				}

				list.add(i, object);
				return i;
			} else {
				int j;
				for (j = list.size() - 1; j >= 0; j--) {
					P resourcePackProfile2 = (P)function.apply(list.get(j));
					if (!resourcePackProfile2.isPinned() || resourcePackProfile2.getInitialPosition() != this) {
						break;
					}
				}

				list.add(j + 1, object);
				return j + 1;
			}
		}

		public ResourcePackProfile.InsertionPosition inverse() {
			return this == field_14280 ? field_14281 : field_14280;
		}
	}
}
