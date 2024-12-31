package net.minecraft.sound;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.sound.SoundCategory;

public class SoundEntry {
	private final List<SoundEntry.Entry> sounds = Lists.newArrayList();
	private boolean replace;
	private SoundCategory category;

	public List<SoundEntry.Entry> getSounds() {
		return this.sounds;
	}

	public boolean canReplace() {
		return this.replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	public SoundCategory getCategory() {
		return this.category;
	}

	public void setCategory(SoundCategory category) {
		this.category = category;
	}

	public static class Entry {
		private String field_8152;
		private float field_8153 = 1.0F;
		private float field_8154 = 1.0F;
		private int field_8155 = 1;
		private SoundEntry.Entry.SoundEntryType type = SoundEntry.Entry.SoundEntryType.FILE;
		private boolean field_8157 = false;

		public String method_7059() {
			return this.field_8152;
		}

		public void method_7063(String string) {
			this.field_8152 = string;
		}

		public float method_7065() {
			return this.field_8153;
		}

		public void method_7060(float f) {
			this.field_8153 = f;
		}

		public float method_7067() {
			return this.field_8154;
		}

		public void method_7066(float f) {
			this.field_8154 = f;
		}

		public int method_7068() {
			return this.field_8155;
		}

		public void method_7061(int i) {
			this.field_8155 = i;
		}

		public SoundEntry.Entry.SoundEntryType method_7069() {
			return this.type;
		}

		public void method_7062(SoundEntry.Entry.SoundEntryType soundEntryType) {
			this.type = soundEntryType;
		}

		public boolean method_7070() {
			return this.field_8157;
		}

		public void method_7064(boolean bl) {
			this.field_8157 = bl;
		}

		public static enum SoundEntryType {
			FILE("file"),
			EVENT("event");

			private final String name;

			private SoundEntryType(String string2) {
				this.name = string2;
			}

			public static SoundEntry.Entry.SoundEntryType getTypeByName(String name) {
				for (SoundEntry.Entry.SoundEntryType soundEntryType : values()) {
					if (soundEntryType.name.equals(name)) {
						return soundEntryType;
					}
				}

				return null;
			}
		}
	}
}
