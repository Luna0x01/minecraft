package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.MathHelper;

public class TextureStitcher {
	private final int mipLevel;
	private final Set<TextureStitcher.Holder> holders = Sets.newHashSetWithExpectedSize(256);
	private final List<TextureStitcher.Slot> slots = Lists.newArrayListWithCapacity(256);
	private int width;
	private int height;
	private final int maxWidth;
	private final int maxHeight;
	private final int maxSize;

	public TextureStitcher(int i, int j, int k, int l) {
		this.mipLevel = l;
		this.maxWidth = i;
		this.maxHeight = j;
		this.maxSize = k;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void add(Sprite sprite) {
		TextureStitcher.Holder holder = new TextureStitcher.Holder(sprite, this.mipLevel);
		if (this.maxSize > 0) {
			holder.fit(this.maxSize);
		}

		this.holders.add(holder);
	}

	public void stitch() {
		TextureStitcher.Holder[] holders = (TextureStitcher.Holder[])this.holders.toArray(new TextureStitcher.Holder[this.holders.size()]);
		Arrays.sort(holders);

		for (TextureStitcher.Holder holder : holders) {
			if (!this.fit(holder)) {
				String string = String.format(
					"Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution resourcepack?",
					holder.getSprite().method_5348(),
					holder.getSprite().getWidth(),
					holder.getSprite().getHeight()
				);
				throw new TextureStitchException(holder, string);
			}
		}

		this.width = MathHelper.smallestEncompassingPowerOfTwo(this.width);
		this.height = MathHelper.smallestEncompassingPowerOfTwo(this.height);
	}

	public List<Sprite> getStitchedSprites() {
		List<TextureStitcher.Slot> list = Lists.newArrayList();

		for (TextureStitcher.Slot slot : this.slots) {
			slot.addAllFilledSlots(list);
		}

		List<Sprite> list2 = Lists.newArrayList();

		for (TextureStitcher.Slot slot2 : list) {
			TextureStitcher.Holder holder = slot2.getTexture();
			Sprite sprite = holder.getSprite();
			sprite.reInitialize(this.width, this.height, slot2.getOriginX(), slot2.getOriginY(), holder.isRotated());
			list2.add(sprite);
		}

		return list2;
	}

	private static int applyMipLevel(int size, int mipLevel) {
		return (size >> mipLevel) + ((size & (1 << mipLevel) - 1) == 0 ? 0 : 1) << mipLevel;
	}

	private boolean fit(TextureStitcher.Holder holder) {
		Sprite sprite = holder.getSprite();
		boolean bl = sprite.getWidth() != sprite.getHeight();

		for (int i = 0; i < this.slots.size(); i++) {
			if (((TextureStitcher.Slot)this.slots.get(i)).add(holder)) {
				return true;
			}

			if (bl) {
				holder.rotate();
				if (((TextureStitcher.Slot)this.slots.get(i)).add(holder)) {
					return true;
				}

				holder.rotate();
			}
		}

		return this.growAndFit(holder);
	}

	private boolean growAndFit(TextureStitcher.Holder holder) {
		int i = Math.min(holder.getWidth(), holder.getHeight());
		int j = Math.max(holder.getWidth(), holder.getHeight());
		int k = MathHelper.smallestEncompassingPowerOfTwo(this.width);
		int l = MathHelper.smallestEncompassingPowerOfTwo(this.height);
		int m = MathHelper.smallestEncompassingPowerOfTwo(this.width + i);
		int n = MathHelper.smallestEncompassingPowerOfTwo(this.height + i);
		boolean bl = m <= this.maxWidth;
		boolean bl2 = n <= this.maxHeight;
		if (!bl && !bl2) {
			return false;
		} else {
			boolean bl3 = bl && k != m;
			boolean bl4 = bl2 && l != n;
			boolean bl5;
			if (bl3 ^ bl4) {
				bl5 = bl3;
			} else {
				bl5 = bl && k <= l;
			}

			TextureStitcher.Slot slot;
			if (bl5) {
				if (holder.getWidth() > holder.getHeight()) {
					holder.rotate();
				}

				if (this.height == 0) {
					this.height = holder.getHeight();
				}

				slot = new TextureStitcher.Slot(this.width, 0, holder.getWidth(), this.height);
				this.width = this.width + holder.getWidth();
			} else {
				slot = new TextureStitcher.Slot(0, this.height, this.width, holder.getHeight());
				this.height = this.height + holder.getHeight();
			}

			slot.add(holder);
			this.slots.add(slot);
			return true;
		}
	}

	public static class Holder implements Comparable<TextureStitcher.Holder> {
		private final Sprite sprite;
		private final int width;
		private final int height;
		private final int mipLevel;
		private boolean rotated;
		private float scale = 1.0F;

		public Holder(Sprite sprite, int i) {
			this.sprite = sprite;
			this.width = sprite.getWidth();
			this.height = sprite.getHeight();
			this.mipLevel = i;
			this.rotated = TextureStitcher.applyMipLevel(this.height, i) > TextureStitcher.applyMipLevel(this.width, i);
		}

		public Sprite getSprite() {
			return this.sprite;
		}

		public int getWidth() {
			int i = this.rotated ? this.height : this.width;
			return TextureStitcher.applyMipLevel((int)((float)i * this.scale), this.mipLevel);
		}

		public int getHeight() {
			int i = this.rotated ? this.width : this.height;
			return TextureStitcher.applyMipLevel((int)((float)i * this.scale), this.mipLevel);
		}

		public void rotate() {
			this.rotated = !this.rotated;
		}

		public boolean isRotated() {
			return this.rotated;
		}

		public void fit(int maxSize) {
			if (this.width > maxSize && this.height > maxSize) {
				this.scale = (float)maxSize / (float)Math.min(this.width, this.height);
			}
		}

		public String toString() {
			return "Holder{width=" + this.width + ", height=" + this.height + '}';
		}

		public int compareTo(TextureStitcher.Holder holder) {
			int i;
			if (this.getHeight() == holder.getHeight()) {
				if (this.getWidth() == holder.getWidth()) {
					return this.sprite.method_5348().toString().compareTo(holder.sprite.method_5348().toString());
				}

				i = this.getWidth() < holder.getWidth() ? 1 : -1;
			} else {
				i = this.getHeight() < holder.getHeight() ? 1 : -1;
			}

			return i;
		}
	}

	public static class Slot {
		private final int originX;
		private final int originY;
		private final int width;
		private final int height;
		private List<TextureStitcher.Slot> subSlots;
		private TextureStitcher.Holder texture;

		public Slot(int i, int j, int k, int l) {
			this.originX = i;
			this.originY = j;
			this.width = k;
			this.height = l;
		}

		public TextureStitcher.Holder getTexture() {
			return this.texture;
		}

		public int getOriginX() {
			return this.originX;
		}

		public int getOriginY() {
			return this.originY;
		}

		public boolean add(TextureStitcher.Holder texture) {
			if (this.texture != null) {
				return false;
			} else {
				int i = texture.getWidth();
				int j = texture.getHeight();
				if (i <= this.width && j <= this.height) {
					if (i == this.width && j == this.height) {
						this.texture = texture;
						return true;
					} else {
						if (this.subSlots == null) {
							this.subSlots = Lists.newArrayListWithCapacity(1);
							this.subSlots.add(new TextureStitcher.Slot(this.originX, this.originY, i, j));
							int k = this.width - i;
							int l = this.height - j;
							if (l > 0 && k > 0) {
								int m = Math.max(this.height, k);
								int n = Math.max(this.width, l);
								if (m >= n) {
									this.subSlots.add(new TextureStitcher.Slot(this.originX, this.originY + j, i, l));
									this.subSlots.add(new TextureStitcher.Slot(this.originX + i, this.originY, k, this.height));
								} else {
									this.subSlots.add(new TextureStitcher.Slot(this.originX + i, this.originY, k, j));
									this.subSlots.add(new TextureStitcher.Slot(this.originX, this.originY + j, this.width, l));
								}
							} else if (k == 0) {
								this.subSlots.add(new TextureStitcher.Slot(this.originX, this.originY + j, i, l));
							} else if (l == 0) {
								this.subSlots.add(new TextureStitcher.Slot(this.originX + i, this.originY, k, j));
							}
						}

						for (TextureStitcher.Slot slot : this.subSlots) {
							if (slot.add(texture)) {
								return true;
							}
						}

						return false;
					}
				} else {
					return false;
				}
			}
		}

		public void addAllFilledSlots(List<TextureStitcher.Slot> list) {
			if (this.texture != null) {
				list.add(this);
			} else if (this.subSlots != null) {
				for (TextureStitcher.Slot slot : this.subSlots) {
					slot.addAllFilledSlots(list);
				}
			}
		}

		public String toString() {
			return "Slot{originX="
				+ this.originX
				+ ", originY="
				+ this.originY
				+ ", width="
				+ this.width
				+ ", height="
				+ this.height
				+ ", texture="
				+ this.texture
				+ ", subSlots="
				+ this.subSlots
				+ '}';
		}
	}
}
