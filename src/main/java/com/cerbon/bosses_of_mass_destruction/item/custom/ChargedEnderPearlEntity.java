package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.packet.custom.ChargedEnderPearlS2CPacket;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.cerbons_api.api.network.Dispatcher;
import com.cerbon.cerbons_api.api.static_utilities.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ChargedEnderPearlEntity extends ThrowableItemProjectile {
    private static final double radius = 3.0;
    private static final double impactHeight = 3.0;
    // CERBONs API ClientParticleBuilder depends on SimpleParticle which extends
    // TextureSheetParticle (removed in NeoForge 26.1.2). Particle builders removed.

    public ChargedEnderPearlEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected ChargedEnderPearlEntity(Level level, LivingEntity owner) {
        super(BMDEntities.CHARGED_ENDER_PEARL.get(), owner, level, new ItemStack(BMDItems.CHARGED_ENDER_PEARL.get()));
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return BMDItems.CHARGED_ENDER_PEARL.get();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(result.getEntity().level().damageSources().thrown(this, getOwner()), 0.0f);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!level().isClientSide())
            if (!this.isRemoved()) serverCollision();
    }

    private void serverCollision(){
        teleportEntity(getOwner());
        applyMobEffects(getOwner());
        Dispatcher.sendToClientsLoadingPos(new ChargedEnderPearlS2CPacket(position()), (ServerLevel) level(), position());
        playSound(BMDSounds.CHARGED_ENDER_PEARL.get(), 1.0f, SoundUtils.randomPitch(this.getRandom()) * 0.8f);
        discard();
    }

    private void teleportEntity(Entity entity){
        if (entity instanceof ServerPlayer player){
            if (player.connection.isAcceptingMessages() && player.level() == this.level() && !player.isSleeping()){
                if (player.isPassenger())
                    player.dismountTo(this.getX(), this.getY(), this.getZ());
                else
                    player.teleportTo(this.getX(), this.getY(), this.getZ());

                player.resetFallDistance();
            }
        } else if (entity != null){
            entity.teleportTo(this.getX(), this.getY(), this.getZ());
            entity.resetFallDistance();
        }
    }

    private void applyMobEffects(Entity entity){
        if (entity instanceof LivingEntity livingEntity){
            livingEntity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 120, 1));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0));
        }

        List<LivingEntity> livingEntities = level()
                .getEntitiesOfClass(LivingEntity.class,
                        new AABB(getX(), getY(), getZ(), getX(), getY(), getZ()).inflate(radius * 2, impactHeight * 2, radius * 2),
                        this::isInXzAndYDistance);

        for (LivingEntity entity1 : livingEntities){
            Vec3 direction = position().subtract(entity1.position());
            entity1.knockback(0.4f, direction.x(), direction.z());
        }
    }

    private boolean isInXzAndYDistance(LivingEntity entity){
        return isInXzDistance(entity) && isInYDistance(entity);
    }

    private boolean isInXzDistance(LivingEntity entity){
        Vec3 xzLineToEntity = VecUtils.planeProject(entity.position(), VecUtils.yAxis).subtract(VecUtils.planeProject(this.position(), VecUtils.yAxis));
        double sqXzDistanceTowardEntity = xzLineToEntity.lengthSqr();
        boolean entityCenterInRadius = sqXzDistanceTowardEntity < radius * radius;
        if (entityCenterInRadius) return true;

        Vec3 xzDirectionTowardEntity = xzLineToEntity.normalize();
        Vec3 xzPointTowardEntity = xzDirectionTowardEntity.scale(radius).add(position());
        return entity.getBoundingBox().inflate(0.0, 10.0, 0.0).contains(xzPointTowardEntity);
    }

    private boolean isInYDistance(LivingEntity entity){
        double yBottom = position().y() - 1;
        double yTop = position().y() + impactHeight;
        return entity.getBoundingBox().maxY > yBottom || entity.getBoundingBox().minY < yTop;
    }

    @Override
    public void tick() {
        Entity entity = getOwner();
        if (entity instanceof Player player && !player.isAlive())
            discard();
        else
            super.tick();
    }


    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return super.getAddEntityPacket(entity);
    }

    public static void handlePearlImpact(Vec3 pos){
        spawnTeleportParticles(pos);
        spawnVerticalRods(pos);
    }

    private static void spawnTeleportParticles(Vec3 pos){
        // CERBONs API ClientParticleBuilder depends on SimpleParticle which extends
        // TextureSheetParticle (removed in NeoForge 26.1.2). Particle spawning
        // will need to be re-implemented using the new particle system.
    }

    private static Vec3 rotateAroundPos(
            Vec3 pos,
            int i,
            int age,
            int startingRotation,
            double radius,
            double rotationSpeed
    ){
        Vec3 yOffset = VecUtils.yAxis.scale(i / 15.0);
        Vec3 xzOffset = VecUtils.xAxis.yRot((float) Math.toRadians(age * rotationSpeed + startingRotation));
        return pos.add(yOffset).add(xzOffset.scale(radius));
    }

    private static void spawnVerticalRods(Vec3 pos){
        // Stubbed - particle rendering pending API migration
    }
}
