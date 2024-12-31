package net.minecraft.util;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.ScoreText;
import net.minecraft.text.SelectorText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ChatSerializer {
	public static Text process(CommandSource source, Text test, Entity entity) throws CommandException {
		Text text;
		if (test instanceof ScoreText) {
			ScoreText scoreText = (ScoreText)test;
			String string = scoreText.getName();
			if (PlayerSelector.method_4091(string)) {
				List<Entity> list = PlayerSelector.method_10866(source, string, Entity.class);
				if (list.size() != 1) {
					throw new EntityNotFoundException("commands.generic.selector.notFound", string);
				}

				Entity entity2 = (Entity)list.get(0);
				if (entity2 instanceof PlayerEntity) {
					string = entity2.getTranslationKey();
				} else {
					string = entity2.getEntityName();
				}
			}

			String string2 = entity != null && string.equals("*") ? entity.getTranslationKey() : string;
			text = new ScoreText(string2, scoreText.getObjective());
			((ScoreText)text).setScore(scoreText.computeValue());
			((ScoreText)text).method_12607(source);
		} else if (test instanceof SelectorText) {
			String string3 = ((SelectorText)test).getPattern();
			text = PlayerSelector.method_6362(source, string3);
			if (text == null) {
				text = new LiteralText("");
			}
		} else if (test instanceof LiteralText) {
			text = new LiteralText(((LiteralText)test).getRawString());
		} else {
			if (!(test instanceof TranslatableText)) {
				return test;
			}

			Object[] objects = ((TranslatableText)test).getArgs();

			for (int i = 0; i < objects.length; i++) {
				Object object = objects[i];
				if (object instanceof Text) {
					objects[i] = process(source, (Text)object, entity);
				}
			}

			text = new TranslatableText(((TranslatableText)test).getKey(), objects);
		}

		Style style = test.getStyle();
		if (style != null) {
			text.setStyle(style.deepCopy());
		}

		for (Text text6 : test.getSiblings()) {
			text.append(process(source, text6, entity));
		}

		return text;
	}
}
