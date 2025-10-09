package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.block.entity.AlchemyTableBlockEntity;
import net.willowins.animewitchery.block.entity.model.AlchemyTableModel;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the Alchemy Table model and any orbiting items above it.
 */
public class AlchemyTableRenderer extends GeoBlockRenderer<AlchemyTableBlockEntity> {
    private final ItemRenderer itemRenderer;

    public AlchemyTableRenderer(BlockEntityRendererFactory.Context context) {
        super(new AlchemyTableModel());
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void renderFinal(MatrixStack matrices, AlchemyTableBlockEntity table, BakedGeoModel model,
                            VertexConsumerProvider vertexConsumers, VertexConsumer buffer, float tickDelta,
                            int light, int overlay, float red, float green, float blue, float alpha) {
        super.renderFinal(matrices, table, model, vertexConsumers, buffer, tickDelta, light, overlay, red, green, blue, alpha);

        if (table == null || table.getWorld() == null) return;

        // === 🔮 Auto-refresh client visuals if crafting just completed ===
        if (!table.isProcessing() && table.getWorld().isClient) {
            // Ask the world to re-request fresh BlockEntity data
            table.getWorld().updateListeners(table.getPos(), table.getCachedState(), table.getCachedState(), 3);
        }

        // === Floating output and orbiting input visuals ===
        ItemStack resultItem = table.getStack(0);
        if (!resultItem.isEmpty()) {
            renderFloatingResultItem(resultItem, table, tickDelta, matrices, vertexConsumers, light, overlay);
        }

        renderOrbitingIngredients(table, tickDelta, matrices, vertexConsumers, light, overlay);
    }


    private void renderFloatingResultItem(ItemStack stack, AlchemyTableBlockEntity table, float tickDelta,
                                          MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                          int light, int overlay) {
        matrices.push();

        matrices.translate(0.5, 1.5, 0.5);

        float time = (table.getWorld().getTime() + tickDelta) * 0.05f;
        float yOffset = MathHelper.sin(time) * 0.08f;
        matrices.translate(0, yOffset, 0);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 20));
        matrices.scale(1.4f, 1.4f, 1.4f);

        itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, table.getWorld(), 0);

        matrices.pop();
    }

    private void renderOrbitingIngredients(AlchemyTableBlockEntity table, float tickDelta, MatrixStack matrices,
                                           VertexConsumerProvider vertexConsumers, int light, int overlay) {
        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ItemStack stack = table.getStack(i);
            if (!stack.isEmpty()) ingredients.add(stack);
        }

        if (ingredients.isEmpty()) return;

        var world = table.getWorld();
        double time = world.getTime() + tickDelta;

        // Orbit parameters
        double baseSpeed = table.isProcessing() ? 0.07 : 0.02;
        double orbitSpeed = baseSpeed * (1.0 + ingredients.size() * 0.05);
        double orbitRadius = table.isProcessing() ? 1.0 : 1.25;

        int count = ingredients.size();
        double angleStep = (Math.PI * 2) / count;

        for (int i = 0; i < count; i++) {
            ItemStack stack = ingredients.get(i);
            if (stack.isEmpty()) continue;

            double angle = (time * orbitSpeed) + (i * angleStep);

            // Each ingredient has its own phase-shifted vertical oscillation
            double bobPhase = (i * 37.42) % (2 * Math.PI); // arbitrary constant for variation
            double bobSpeed = 0.06 + (i % 3) * 0.01;       // some slower, some faster
            double bobOffset = Math.sin((time * bobSpeed) + bobPhase) * 0.08;

            // Height baseline
            double baseHeight = 0.85 + 0.25;
            double height = baseHeight + bobOffset;

            // Orbit position
            double x = Math.cos(angle) * orbitRadius;
            double z = Math.sin(angle) * orbitRadius;

            matrices.push();
            matrices.translate(0.5 + x, height, 0.5 + z);

            // Face outward
            float facing = (float) (-angle + Math.PI / 2);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(facing));

            // Add gentle spin
            float spin = (float) ((time + i * 10) * 0.04f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(spin));

            // Small stylish tilt
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(15));

            // Scale
            matrices.scale(1.5f, 1.5f, 1.5f);

            itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, world, 0);
            matrices.pop();
        }
    }
}