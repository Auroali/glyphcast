package com.auroali.glyphcast.common.entities;

import com.auroali.glyphcast.common.registry.GCEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

public class FireEntity extends Projectile {
    public FireEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FireEntity(Level level, double x, double y, double z) {
        super(GCEntities.FIRE.get(), level);
        setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > 1000) {
            this.remove(RemovalReason.KILLED);
            return;
        }
        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);
        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            level.playSound(null, this, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 1.0f);
            this.remove(RemovalReason.KILLED);
        }
        if(level.isClientSide) {
            Vec3 speed = getDeltaMovement().scale(0.5f);
            level.addParticle(ParticleTypes.FLAME, this.getX(), this.getEyeY(), this.getZ(), speed.x, speed.y, speed.z);
        }

        EntityHitResult result = findHitEntity(position(), position().add(getDeltaMovement()));
        if(result != null && result.getType() == HitResult.Type.ENTITY) {
            this.onHitEntity(result);
        }

        if(!level.isClientSide)
            igniteBlock();

        double newX = getX() + this.getDeltaMovement().x;
        double newY = getY() + this.getDeltaMovement().y;
        double newZ = getZ() + this.getDeltaMovement().z;
        this.setPos(newX, newY, newZ);

        this.checkInsideBlocks();
    }

    void igniteBlock() {
        var result = level.clip(new ClipContext(this.position(), this.position().add(this.getDeltaMovement()), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
        if(result.getType() != HitResult.Type.MISS) {
            BlockPos pos = result.getBlockPos().relative(result.getDirection());
            BlockState state = level.getBlockState(result.getBlockPos());
            // If the block can be lit, we light it
            if(state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)) {
                level.setBlockAndUpdate(result.getBlockPos(), state.setValue(BlockStateProperties.LIT, true));
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
    protected void onInsideBlock(BlockState pState) {
        if(!pState.isAir())
            this.remove(RemovalReason.KILLED);
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
