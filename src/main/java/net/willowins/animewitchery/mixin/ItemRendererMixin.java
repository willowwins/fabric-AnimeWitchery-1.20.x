package net.willowins.animewitchery.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.item.ModItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow @Final private ItemModels models;

    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useNeedleModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModItems.NEEDLE) && renderMode != ModelTransformationMode.GUI) {
            return ((ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(AnimeWitchery.MOD_ID, "needle3d", "inventory"));
        }
        if (stack.isOf(ModItems.HEALING_STAFF) && (renderMode == ModelTransformationMode.GUI||renderMode == ModelTransformationMode.GROUND)) {
            return ((ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(AnimeWitchery.MOD_ID, "healing_staff_2d", "inventory"));
        }
        if (stack.isOf(ModItems.RAILGUNNER_HELMET) && (renderMode == ModelTransformationMode.GUI||renderMode == ModelTransformationMode.GROUND||
                renderMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND||renderMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND||
                renderMode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND||renderMode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND)){
            return ((ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(AnimeWitchery.MOD_ID, "railgunner_helmet_2d", "inventory"));
        }
        if (stack.isOf(ModItems.RAILGUNNER_CHESTPLATE) && (renderMode == ModelTransformationMode.GUI||renderMode == ModelTransformationMode.GROUND||
                renderMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND||renderMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND||
                renderMode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND||renderMode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND)){
            return ((ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(AnimeWitchery.MOD_ID, "railgunner_chestplate_2d", "inventory"));
        }
        if (stack.isOf(ModItems.RAILGUNNER_LEGGINGS) && (renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND||
                renderMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND||renderMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND||
                renderMode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND||renderMode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND)){
            return ((ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(AnimeWitchery.MOD_ID, "railgunner_leggings_2d", "inventory"));
        }
        if (stack.isOf(ModItems.RAILGUNNER_BOOTS) && (renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND||
                renderMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND||renderMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND||
                renderMode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND||renderMode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND)){
            return ((ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(AnimeWitchery.MOD_ID, "railgunner_boots_2d", "inventory"));
        }
        if (stack.isOf(ModBlocks.BINDING_SPELL.asItem()) && (renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND||
                renderMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND||renderMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND||
                renderMode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND||renderMode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND||
                renderMode == ModelTransformationMode.FIXED)){
            return ((ItemRendererAccessor) this).mccourse$getModels().getModelManager().getModel(new ModelIdentifier(AnimeWitchery.MOD_ID, "binding_spell_2d", "inventory"));
        }
        return value;
    }
}