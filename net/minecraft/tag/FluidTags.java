package net.minecraft.tag;

import java.util.Collection;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

public class FluidTags {
	private static TagContainer<Fluid> container = new TagContainer<>(identifier -> false, identifier -> null, "", false, "");
	private static int newestVersion;
	public static final Tag<Fluid> WATER = register("water");
	public static final Tag<Fluid> LAVA = register("lava");

	public static void setContainer(TagContainer<Fluid> tagContainer) {
		container = tagContainer;
		newestVersion++;
	}

	private static Tag<Fluid> register(String id) {
		return new FluidTags.FluidTag(new Identifier(id));
	}

	public static class FluidTag extends Tag<Fluid> {
		private int version = -1;
		private Tag<Fluid> tag;

		public FluidTag(Identifier identifier) {
			super(identifier);
		}

		public boolean contains(Fluid fluid) {
			if (this.version != FluidTags.newestVersion) {
				this.tag = FluidTags.container.getOrCreate(this.getId());
				this.version = FluidTags.newestVersion;
			}

			return this.tag.contains(fluid);
		}

		@Override
		public Collection<Fluid> values() {
			if (this.version != FluidTags.newestVersion) {
				this.tag = FluidTags.container.getOrCreate(this.getId());
				this.version = FluidTags.newestVersion;
			}

			return this.tag.values();
		}

		@Override
		public Collection<Tag.Entry<Fluid>> entries() {
			if (this.version != FluidTags.newestVersion) {
				this.tag = FluidTags.container.getOrCreate(this.getId());
				this.version = FluidTags.newestVersion;
			}

			return this.tag.entries();
		}
	}
}
