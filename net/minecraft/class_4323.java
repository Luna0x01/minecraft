package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4323 {
	private static final Logger field_21246 = LogManager.getLogger();
	private static final Map<Class<?>, class_4323.class_4324<?>> field_21247 = Maps.newHashMap();
	private static final Map<Identifier, class_4323.class_4324<?>> field_21248 = Maps.newHashMap();

	public static <T extends ArgumentType<?>> void method_19899(Identifier identifier, Class<T> class_, class_4322<T> arg) {
		if (field_21247.containsKey(class_)) {
			throw new IllegalArgumentException("Class " + class_.getName() + " already has a serializer!");
		} else if (field_21248.containsKey(identifier)) {
			throw new IllegalArgumentException("'" + identifier + "' is already a registered serializer!");
		} else {
			class_4323.class_4324<T> lv = new class_4323.class_4324<>(class_, arg, identifier);
			field_21247.put(class_, lv);
			field_21248.put(identifier, lv);
		}
	}

	public static void method_19892() {
		class_4329.method_19910();
		method_19899(new Identifier("minecraft:entity"), class_4062.class, new class_4062.class_4063());
		method_19899(new Identifier("minecraft:game_profile"), class_4073.class, new class_4326(class_4073::method_17988));
		method_19899(new Identifier("minecraft:block_pos"), class_4252.class, new class_4326(class_4252::method_19358));
		method_19899(new Identifier("minecraft:column_pos"), class_4257.class, new class_4326(class_4257::method_19369));
		method_19899(new Identifier("minecraft:vec3"), class_4287.class, new class_4326(class_4287::method_19562));
		method_19899(new Identifier("minecraft:vec2"), class_4284.class, new class_4326(class_4284::method_19539));
		method_19899(new Identifier("minecraft:block_state"), class_4229.class, new class_4326(class_4229::method_19207));
		method_19899(new Identifier("minecraft:block_predicate"), class_4220.class, new class_4326(class_4220::method_19107));
		method_19899(new Identifier("minecraft:item_stack"), class_4310.class, new class_4326(class_4310::method_19698));
		method_19899(new Identifier("minecraft:item_predicate"), class_4313.class, new class_4326(class_4313::method_19718));
		method_19899(new Identifier("minecraft:color"), class_3991.class, new class_4326(class_3991::method_17647));
		method_19899(new Identifier("minecraft:component"), class_4009.class, new class_4326(class_4009::method_17711));
		method_19899(new Identifier("minecraft:message"), class_4102.class, new class_4326(class_4102::method_18091));
		method_19899(new Identifier("minecraft:nbt"), class_4119.class, new class_4326(class_4119::method_18393));
		method_19899(new Identifier("minecraft:nbt_path"), class_4124.class, new class_4326(class_4124::method_18432));
		method_19899(new Identifier("minecraft:objective"), class_4151.class, new class_4326(class_4151::method_18520));
		method_19899(new Identifier("minecraft:objective_criteria"), class_4159.class, new class_4326(class_4159::method_18598));
		method_19899(new Identifier("minecraft:operation"), class_4164.class, new class_4326(class_4164::method_18683));
		method_19899(new Identifier("minecraft:particle"), class_4168.class, new class_4326(class_4168::method_18780));
		method_19899(new Identifier("minecraft:rotation"), class_4271.class, new class_4326(class_4271::method_19435));
		method_19899(new Identifier("minecraft:scoreboard_slot"), class_4196.class, new class_4326(class_4196::method_18938));
		method_19899(new Identifier("minecraft:score_holder"), class_4186.class, new class_4186.class_4189());
		method_19899(new Identifier("minecraft:swizzle"), class_4275.class, new class_4326(class_4275::method_19445));
		method_19899(new Identifier("minecraft:team"), class_4209.class, new class_4326(class_4209::method_18997));
		method_19899(new Identifier("minecraft:item_slot"), class_4202.class, new class_4326(class_4202::method_18948));
		method_19899(new Identifier("minecraft:resource_location"), class_4181.class, new class_4326(class_4181::method_18904));
		method_19899(new Identifier("minecraft:mob_effect"), class_4114.class, new class_4326(class_4114::method_18275));
		method_19899(new Identifier("minecraft:function"), class_4308.class, new class_4326(class_4308::method_19691));
		method_19899(new Identifier("minecraft:entity_anchor"), class_4048.class, new class_4326(class_4048::method_17865));
		method_19899(new Identifier("minecraft:int_range"), class_4173.class_4176.class, new class_4173.class_4176.class_4177());
		method_19899(new Identifier("minecraft:float_range"), class_4173.class_4174.class, new class_4173.class_4174.class_4175());
		method_19899(new Identifier("minecraft:item_enchantment"), class_4078.class, new class_4326(class_4078::method_17997));
		method_19899(new Identifier("minecraft:entity_summon"), class_4069.class, new class_4326(class_4069::method_17944));
		method_19899(new Identifier("minecraft:dimension"), class_4030.class, new class_4326(class_4030::method_17821));
	}

	@Nullable
	private static class_4323.class_4324<?> method_19898(Identifier identifier) {
		return (class_4323.class_4324<?>)field_21248.get(identifier);
	}

	@Nullable
	private static class_4323.class_4324<?> method_19895(ArgumentType<?> argumentType) {
		return (class_4323.class_4324<?>)field_21247.get(argumentType.getClass());
	}

	public static <T extends ArgumentType<?>> void method_19897(PacketByteBuf packetByteBuf, T argumentType) {
		class_4323.class_4324<T> lv = (class_4323.class_4324<T>)method_19895(argumentType);
		if (lv == null) {
			field_21246.error("Could not serialize {} ({}) - will not be sent to client!", argumentType, argumentType.getClass());
			packetByteBuf.writeIdentifier(new Identifier(""));
		} else {
			packetByteBuf.writeIdentifier(lv.field_21251);
			lv.field_21250.method_19890(argumentType, packetByteBuf);
		}
	}

	@Nullable
	public static ArgumentType<?> method_19896(PacketByteBuf packetByteBuf) {
		Identifier identifier = packetByteBuf.readIdentifier();
		class_4323.class_4324<?> lv = method_19898(identifier);
		if (lv == null) {
			field_21246.error("Could not deserialize {}", identifier);
			return null;
		} else {
			return lv.field_21250.method_19891(packetByteBuf);
		}
	}

	private static <T extends ArgumentType<?>> void method_19893(JsonObject jsonObject, T argumentType) {
		class_4323.class_4324<T> lv = (class_4323.class_4324<T>)method_19895(argumentType);
		if (lv == null) {
			field_21246.error("Could not serialize argument {} ({})!", argumentType, argumentType.getClass());
			jsonObject.addProperty("type", "unknown");
		} else {
			jsonObject.addProperty("type", "argument");
			jsonObject.addProperty("parser", lv.field_21251.toString());
			JsonObject jsonObject2 = new JsonObject();
			lv.field_21250.method_19889(argumentType, jsonObject2);
			if (jsonObject2.size() > 0) {
				jsonObject.add("properties", jsonObject2);
			}
		}
	}

	public static <S> JsonObject method_19894(CommandDispatcher<S> commandDispatcher, CommandNode<S> commandNode) {
		JsonObject jsonObject = new JsonObject();
		if (commandNode instanceof RootCommandNode) {
			jsonObject.addProperty("type", "root");
		} else if (commandNode instanceof LiteralCommandNode) {
			jsonObject.addProperty("type", "literal");
		} else if (commandNode instanceof ArgumentCommandNode) {
			method_19893(jsonObject, ((ArgumentCommandNode)commandNode).getType());
		} else {
			field_21246.error("Could not serialize node {} ({})!", commandNode, commandNode.getClass());
			jsonObject.addProperty("type", "unknown");
		}

		JsonObject jsonObject2 = new JsonObject();

		for (CommandNode<S> commandNode2 : commandNode.getChildren()) {
			jsonObject2.add(commandNode2.getName(), method_19894(commandDispatcher, commandNode2));
		}

		if (jsonObject2.size() > 0) {
			jsonObject.add("children", jsonObject2);
		}

		if (commandNode.getCommand() != null) {
			jsonObject.addProperty("executable", true);
		}

		if (commandNode.getRedirect() != null) {
			Collection<String> collection = commandDispatcher.getPath(commandNode.getRedirect());
			if (!collection.isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (String string : collection) {
					jsonArray.add(string);
				}

				jsonObject.add("redirect", jsonArray);
			}
		}

		return jsonObject;
	}

	static class class_4324<T extends ArgumentType<?>> {
		public final Class<T> field_21249;
		public final class_4322<T> field_21250;
		public final Identifier field_21251;

		private class_4324(Class<T> class_, class_4322<T> arg, Identifier identifier) {
			this.field_21249 = class_;
			this.field_21250 = arg;
			this.field_21251 = identifier;
		}
	}
}
