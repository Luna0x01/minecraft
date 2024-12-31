package net.minecraft.sound;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.class_2906;

public class SoundEntry {
	private final List<class_2906> sounds;
	private final boolean replace;
	private final String field_13689;

	public SoundEntry(List<class_2906> list, boolean bl, String string) {
		this.sounds = list;
		this.replace = bl;
		this.field_13689 = string;
	}

	public List<class_2906> getSounds() {
		return this.sounds;
	}

	public boolean canReplace() {
		return this.replace;
	}

	@Nullable
	public String method_7058() {
		return this.field_13689;
	}
}
