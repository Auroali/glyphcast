package com.auroali.glyphcast.common.entities;

import com.auroali.glyphcast.client.LightTracker;
import com.auroali.glyphcast.common.config.GCClientConfig;
import com.auroali.glyphcast.common.network.client.ClientPacketHandler;
import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.registry.GCEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
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
        if(getDeltaMovement().length() <= 0.1d) {
            this.remove(RemovalReason.KILLED);
            return;
        }

        if(!level.isClientSide)
            igniteBlock();

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);

        if (!blockstate.isAir()) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for(AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.remove(RemovalReason.KILLED);
                        break;
                    }
                }
            }
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            level.playSound(null, this, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 1.0f);
            this.remove(RemovalReason.KILLED);
        }


        if(level.isClientSide) {
            if(GCClientConfig.CLIENT.fireEmitsLight.get())
                LightTracker.update(this, 7);
            double speed = getDeltaMovement().length() / 5;
            ClientPacketHandler.spawnParticles(new SpawnParticlesMessage(ParticleTypes.FLAME, 0.12, 15, position().add(0, 0.25, 0),getDeltaMovement(), speed ));
            ClientPacketHandler.spawnParticles(new SpawnParticlesMessage(ParticleTypes.SOUL_FIRE_FLAME,0.02, 15, position().add(0, 0.25, 0),getDeltaMovement(), speed));
            ClientPacketHandler.spawnParticles(new SpawnParticlesMessage(ParticleTypes.SMOKE, 0.02, 20, position().add(0, 0.25, 0),getDeltaMovement(), speed));
        }

        EntityHitResult result = findHitEntity(position(), position().add(getDeltaMovement()));
        if(result != null && result.getType() == HitResult.Type.ENTITY) {
            this.onHitEntity(result);
        }

        double newX = getX() + this.getDeltaMovement().x;
        double newY = getY() + this.getDeltaMovement().y;
        double newZ = getZ() + this.getDeltaMovement().z;
        this.setPos(newX, newY, newZ);

        setDeltaMovement(getDeltaMovement().scale(0.8f));
        this.checkInsideBlocks();
    }

    void igniteBlock() {
        var result = level.clip(new ClipContext(this.position(), this.position().add(this.getDeltaMovement()), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
        if(result.getType() != HitResult.Type.MISS) {
            BlockPos pos = result.getBlockPos().relative(result.getDirection());
            BlockState state = level.getBlockState(result.getBlockPos());
            // If the block can be lit, we light it
            if(state.hasProperty(BlockStateProperties.LIT) && !state.getValue(BlockStateProperties.LIT)) {
                level.setBlockAndUpdate(result.getBlockPos(), state.setValue(BlockStateProperties.LIT, true));
                this.remove(RemovalReason.KILLED);
                return;
            }
            // Otherwise we try to burn it
            if(BaseFireBlock.canBePlacedAt(level, pos, result.getDirection()))
                level.setBlockAndUpdate(result.getBlockPos().relative(result.getDirection()), BaseFireBlock.getState(level, pos));
        }
    }
    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level, this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if(ownedBy(pResult.getEntity()))
            return;
        pResult.getEntity().setSecondsOnFire(4);
        pResult.getEntity().hurt(DamageSource.IN_FIRE, 8);
        this.remove(RemovalReason.KILLED);
    }

    @Override
    protected void defineSynchedData() {

    }
}
