package net.willowins.animewitchery.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShockwaveParticle extends ExplosionLargeParticle {
    private final Quaternionf initialRotation;

    public ShockwaveParticle(ClientWorld world, double x, double y, double z, double velocityMultiplier, SpriteProvider spriteProvider, Vector3f direction) {
        super(world, x, y, z, velocityMultiplier, spriteProvider);
        this.maxAge = 24;
        this.scale = 8.0F;
        this.gravityStrength = 0.0F;
        this.velocityX = 0.5*direction.x;
        this.velocityY = 0.5*direction.y;
        this.velocityZ = 0.5*direction.z;
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.alpha = 0.5F;
        this.setSpriteForAge(spriteProvider);

        // Rotate quad's normal (0,0,1) to match the spawn look direction
        Vector3f baseNormal = new Vector3f(0, 0, 1);
        Vector3f target = new Vector3f(direction);
        if (target.lengthSquared() < 1e-6f) {
            target.set(0, 0, 1);
        } else {
            target.normalize();
        }
        this.initialRotation = new Quaternionf().rotateTo(baseNormal, target);
    }

    @Override
    public float getSize(float tickDelta) {
        float progress = ((float)this.age + tickDelta) / (float)this.maxAge;
        return this.scale * MathHelper.clamp(progress, 0.0F, 1.0F);
    }

    @Override
    public void buildGeometry(VertexConsumer buffer, Camera camera, float tickDelta) {
        Vec3d camPos = camera.getPos();
        float px = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camPos.getX());
        float py = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camPos.getY());
        float pz = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camPos.getZ());

        Vector3f[] quad = new Vector3f[] {
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F,  1.0F, 0.0F),
                new Vector3f( 1.0F,  1.0F, 0.0F),
                new Vector3f( 1.0F, -1.0F, 0.0F)
        };

        float size = getSize(tickDelta);
        for (Vector3f v : quad) {
            v.rotate(initialRotation);
            v.mul(size);
            v.add(px, py, pz);
        }

        float minU = getMinU();
        float maxU = getMaxU();
        float minV = getMinV();
        float maxV = getMaxV();
        int light = getBrightness(tickDelta);
        float alphaFade = MathHelper.lerp((float)this.age / this.maxAge, 0.5F, 0.0F);

        buffer.vertex(quad[0].x(), quad[0].y(), quad[0].z()).texture(maxU, maxV).color(red, green, blue, alphaFade).light(light).next();
        buffer.vertex(quad[1].x(), quad[1].y(), quad[1].z()).texture(maxU, minV).color(red, green, blue, alphaFade).light(light).next();
        buffer.vertex(quad[2].x(), quad[2].y(), quad[2].z()).texture(minU, minV).color(red, green, blue, alphaFade).light(light).next();
        buffer.vertex(quad[3].x(), quad[3].y(), quad[3].z()).texture(minU, maxV).color(red, green, blue, alphaFade).light(light).next();
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            // Get spawn orientation from the client's camera forward vector
            MinecraftClient mc = MinecraftClient.getInstance();
            Camera cam = mc.gameRenderer.getCamera();
            Quaternionf camRot = cam.getRotation();
            Vector3f look = new Vector3f(0, 0, 1);
            camRot.transform(look);

            return new ShockwaveParticle(world, x, y, z, vx, this.spriteProvider, look);
        }
    }
}