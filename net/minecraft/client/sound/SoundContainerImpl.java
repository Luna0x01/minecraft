package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.class_2906;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class SoundContainerImpl implements SoundContainer<class_2906> {
	private final List<SoundContainer<class_2906>> field_13704 = Lists.newArrayList();
	private final Random field_13705 = new Random();
	private final Identifier field_13706;
	private final Text field_13707;

	public SoundContainerImpl(Identifier identifier, @Nullable String string) {
		this.field_13706 = identifier;
		this.field_13707 = string == null ? null : new TranslatableText(string);
	}

	@Override
	public int getWeight() {
		int i = 0;

		for (SoundContainer<class_2906> soundContainer : this.field_13704) {
			i += soundContainer.getWeight();
		}

		return i;
	}

	public class_2906 getSound() {
		int i = this.getWeight();
		if (!this.field_13704.isEmpty() && i != 0) {
			int j = this.field_13705.nextInt(i);

			for (SoundContainer<class_2906> soundContainer : this.field_13704) {
				j -= soundContainer.getWeight();
				if (j < 0) {
					return soundContainer.getSound();
				}
			}

			return SoundManager.field_13702;
		} else {
			return SoundManager.field_13702;
		}
	}

	public void method_12549(SoundContainer<class_2906> soundContainer) {
		this.field_13704.add(soundContainer);
	}

	public Identifier method_12550() {
		return this.field_13706;
	}

	@Nullable
	public Text method_12551() {
		return this.field_13707;
	}
}
