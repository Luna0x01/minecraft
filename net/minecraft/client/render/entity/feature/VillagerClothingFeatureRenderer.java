package net.minecraft.client.render.entity.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

public class VillagerClothingFeatureRenderer<T extends LivingEntity & VillagerDataContainer, M extends EntityModel<T> & ModelWithHat>
	extends FeatureRenderer<T, M>
	implements SynchronousResourceReloadListener {
	private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = Util.make(new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
		int2ObjectOpenHashMap.put(1, new Identifier("stone"));
		int2ObjectOpenHashMap.put(2, new Identifier("iron"));
		int2ObjectOpenHashMap.put(3, new Identifier("gold"));
		int2ObjectOpenHashMap.put(4, new Identifier("emerald"));
		int2ObjectOpenHashMap.put(5, new Identifier("diamond"));
	});
	private final Object2ObjectMap<VillagerType, VillagerResourceMetadata.HatType> villagerTypeToHat = new Object2ObjectOpenHashMap();
	private final Object2ObjectMap<VillagerProfession, VillagerResourceMetadata.HatType> professionToHat = new Object2ObjectOpenHashMap();
	private final ReloadableResourceManager resourceManager;
	private final String entityType;

	public VillagerClothingFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, ReloadableResourceManager reloadableResourceManager, String string) {
		super(featureRendererContext);
		this.resourceManager = reloadableResourceManager;
		this.entityType = string;
		reloadableResourceManager.registerListener(this);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		if (!livingEntity.isInvisible()) {
			VillagerData villagerData = livingEntity.getVillagerData();
			VillagerType villagerType = villagerData.getType();
			VillagerProfession villagerProfession = villagerData.getProfession();
			VillagerResourceMetadata.HatType hatType = this.getHatType(this.villagerTypeToHat, "type", Registry.field_17166, villagerType);
			VillagerResourceMetadata.HatType hatType2 = this.getHatType(this.professionToHat, "profession", Registry.field_17167, villagerProfession);
			M entityModel = this.getContextModel();
			entityModel.setHatVisible(
				hatType2 == VillagerResourceMetadata.HatType.field_17160
					|| hatType2 == VillagerResourceMetadata.HatType.field_17161 && hatType != VillagerResourceMetadata.HatType.field_17162
			);
			Identifier identifier = this.findTexture("type", Registry.field_17166.getId(villagerType));
			renderModel(entityModel, identifier, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0F, 1.0F, 1.0F);
			entityModel.setHatVisible(true);
			if (villagerProfession != VillagerProfession.field_17051 && !livingEntity.isBaby()) {
				Identifier identifier2 = this.findTexture("profession", Registry.field_17167.getId(villagerProfession));
				renderModel(entityModel, identifier2, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0F, 1.0F, 1.0F);
				if (villagerProfession != VillagerProfession.field_17062) {
					Identifier identifier3 = this.findTexture(
						"profession_level", (Identifier)LEVEL_TO_ID.get(MathHelper.clamp(villagerData.getLevel(), 1, LEVEL_TO_ID.size()))
					);
					renderModel(entityModel, identifier3, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0F, 1.0F, 1.0F);
				}
			}
		}
	}

	private Identifier findTexture(String string, Identifier identifier) {
		return new Identifier(identifier.getNamespace(), "textures/entity/" + this.entityType + "/" + string + "/" + identifier.getPath() + ".png");
	}

	public <K> VillagerResourceMetadata.HatType getHatType(
		Object2ObjectMap<K, VillagerResourceMetadata.HatType> object2ObjectMap, String string, DefaultedRegistry<K> defaultedRegistry, K object
	) {
		return (VillagerResourceMetadata.HatType)object2ObjectMap.computeIfAbsent(object, object2 -> {
			try {
				Resource resource = this.resourceManager.getResource(this.findTexture(string, defaultedRegistry.getId(object)));
				Throwable var6 = null;

				VillagerResourceMetadata.HatType var8;
				try {
					VillagerResourceMetadata villagerResourceMetadata = resource.getMetadata(VillagerResourceMetadata.READER);
					if (villagerResourceMetadata == null) {
						return VillagerResourceMetadata.HatType.field_17160;
					}

					var8 = villagerResourceMetadata.getHatType();
				} catch (Throwable var19) {
					var6 = var19;
					throw var19;
				} finally {
					if (resource != null) {
						if (var6 != null) {
							try {
								resource.close();
							} catch (Throwable var18) {
								var6.addSuppressed(var18);
							}
						} else {
							resource.close();
						}
					}
				}

				return var8;
			} catch (IOException var21) {
				return VillagerResourceMetadata.HatType.field_17160;
			}
		});
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		this.professionToHat.clear();
		this.villagerTypeToHat.clear();
	}
}
