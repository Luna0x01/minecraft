package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;

public class class_3458 extends DamageSource {
	protected class_3458() {
		super("netherBed");
		this.setScaledWithDifficulty();
		this.setExplosive();
	}

	@Override
	public Text getDeathMessage(LivingEntity entity) {
		Text text = ChatSerializer.method_20188(new TranslatableText("death.attack.netherBed.link"))
			.styled(
				style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723"))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("MCPE-28723")))
			);
		return new TranslatableText("death.attack.netherBed.message", entity.getName(), text);
	}
}
