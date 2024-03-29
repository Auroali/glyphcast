package com.auroali.glyphcast.common.entities;

import com.auroali.glyphcast.client.LightTracker;
import com.auroali.glyphcast.common.config.GCClientConfig;
import com.auroali.glyphcast.common.damage.GCDamageSources;
import com.auroali.glyphcast.common.network.client.ClientPacketHandler;
import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.registry.GCEntities;
import com.auroali.glyphcast.common.registry.GCNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class FireSpellProjectile extends Projectile {

    public static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(FireSpellProjectile.class, EntityDataSerializers.FLOAT);

    public FireSpellProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FireSpellProjectile(Level level, double x, double y, double z) {
        super(GCEntities.FIRE.get(), level);
        setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        if (getDeltaMovement().length() <= 0.1d) {
            this.discard();
            return;
        }

        if (!level.isClientSide)
            igniteBlock();

        handleExtinguish();
        spawnParticles();
        handleEntityHit();
        updatePosition();
        this.checkInsideBlocks();
        spawnParticles();
    }

    private void handleExtinguish() {
        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);
        if (!blockstate.isAir()) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.discard();
                        break;
                    }
                }
            }
        }
        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            level.playSound(null, this, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 1.0f);
            this.discard();
        }
    }

    private void updatePosition() {
        double newX = getX() + this.getDeltaMovement().x;
        double newY = getY() + this.getDeltaMovement().y;
        double newZ = getZ() + this.getDeltaMovement().z;
        this.setPos(newX, newY, newZ);

        setDeltaMovement(getDeltaMovement().scale(0.8f));
    }

    private void handleEntityHit() {
        EntityHitResult result = findHitEntity(position(), position().add(getDeltaMovement()));
        if (result != null && result.getType() == HitResult.Type.ENTITY) {
            this.onHitEntity(result);
        }
    }

    private void spawnParticles() {
        if (level.isClientSide) {
            if (GCClientConfig.CLIENT.fireEmitsLight.get())
                LightTracker.update(this, 7);
            double speed = getDeltaMovement().length() / 5;
            ClientPacketHandler.spawnParticles(new SpawnParticlesMessage(ParticleTypes.FLAME, 0.12, 2, position().add(0, 0.25, 0), getDeltaMovement(), speed));
            ClientPacketHandler.spawnParticles(new SpawnParticlesMessage(ParticleTypes.SOUL_FIRE_FLAME, 0.02, 2, position().add(0, 0.25, 0), getDeltaMovement(), speed));
            ClientPacketHandler.spawnParticles(new SpawnParticlesMessage(ParticleTypes.SMOKE, 0.02, 3, position().add(0, 0.25, 0), getDeltaMovement(), speed));
        }
    }

    void igniteBlock() {
        var result = level.clip(new ClipContext(this.position(), this.position().add(this.getDeltaMovement()), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
        if (result.getType() != HitResult.Type.MISS) {
            BlockPos pos = result.getBlockPos().relative(result.getDirection());
            BlockState state = level.getBlockState(result.getBlockPos());
            // If the block can be lit, we light it
            if (state.hasProperty(BlockStateProperties.LIT) && !state.getValue(BlockStateProperties.LIT)) {
                level.setBlockAndUpdate(result.getBlockPos(), state.setValue(BlockStateProperties.LIT, true));
                this.discard();
                return;
            }
            // Otherwise we try to burn it
            if (BaseFireBlock.canBePlacedAt(level, pos, result.getDirection()))
                level.setBlockAndUpdate(result.getBlockPos().relative(result.getDirection()), BaseFireBlock.getState(level, pos));
        }
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level, this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (ownedBy(pResult.getEntity()))
            return;
        pResult.getEntity().setSecondsOnFire(2);
        if (getOwner() != null)
            pResult.getEntity().hurt(GCDamageSources.burnIndirect(this, getOwner()), 4);
        else
            pResult.getEntity().hurt(GCDamageSources.BURN, 4);
        this.discard();
        if (!level.isClientSide) {
            GCNetwork.sendToNear(level, position(), 32, new SpawnParticlesMessage(ParticleTypes.FLAME, 0.15, 15, position().add(0, 0.25, 0), getDeltaMovement().normalize().scale(-1), 0.05, 0.07));
            GCNetwork.sendToNear(level, position(), 32, new SpawnParticlesMessage(ParticleTypes.SOUL_FIRE_FLAME, 0.15, 15, position().add(0, 0.25, 0), getDeltaMovement().normalize().scale(-1), 0.05, 0.07));
        }
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DAMAGE, 4.0f);
    }
}
