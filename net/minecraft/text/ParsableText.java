package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public interface ParsableText {
	MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException;
}
