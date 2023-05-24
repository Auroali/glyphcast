//package com.auroali.glyphcast.common.entities;
//
//import com.auroali.glyphcast.common.items.StaffItem;
//import com.auroali.glyphcast.common.registry.GCEntities;
//import com.auroali.glyphcast.common.registry.GCEntityDataSerializers;
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.syncher.EntityDataAccessor;
//import net.minecraft.network.syncher.EntityDataSerializers;
//import net.minecraft.network.syncher.SynchedEntityData;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.util.Mth;
//import net.minecraft.world.entity.*;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.entity.ai.control.MoveControl;
//import net.minecraft.world.entity.ai.goal.FloatGoal;
//import net.minecraft.world.entity.ai.goal.Goal;
//import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
//import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
//import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
//import net.minecraft.world.entity.ai.navigation.PathNavigation;
//import net.minecraft.world.entity.animal.FlyingAnimal;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.LevelReader;
//import net.minecraft.world.level.block.LeavesBlock;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.pathfinder.BlockPathTypes;
//import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
//import net.minecraft.world.phys.AABB;
//
//import java.util.EnumSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//public class StaffEntity extends Mob implements FlyingAnimal {
//    public static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(StaffEntity.class, EntityDataSerializers.OPTIONAL_UUID);
//    public static final EntityDataAccessor<StaffItem.Variant> VARIANT = SynchedEntityData.defineId(StaffEntity.class, GCEntityDataSerializers.STAFF_VARIANT);
//    private Player cachedOwner;
//
//    public StaffEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
//        super(pEntityType, pLevel);
//        this.moveControl = new StaffEntityMoveControl(this, 10, false);
//    }
//
//    public StaffEntity(Level pLevel, StaffItem.Variant variant, Player owner) {
//        this(GCEntities.STAFF_ENTITY.get(), pLevel);
//        this.entityData.set(VARIANT, variant);
//        setOwner(owner);
//    }
//
//    @Override
//    public Iterable<ItemStack> getArmorSlots() {
//        return List.of();
//    }
//
//    @Override
//    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
//
//    }
//
//    @Override
//    public HumanoidArm getMainArm() {
//        return HumanoidArm.RIGHT;
//    }
//
//    @Override
//    protected PathNavigation createNavigation(Level pLevel) {
//        if (entityData.get(VARIANT).flying()) {
//            FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
//            flyingpathnavigation.setCanOpenDoors(false);
//            flyingpathnavigation.setCanFloat(true);
//            flyingpathnavigation.setCanPassDoors(true);
//            return flyingpathnavigation;
//        }
//        return super.createNavigation(pLevel);
//    }
//
//    @Override
//    protected void registerGoals() {
//        this.goalSelector.addGoal(0, new FloatGoal(this));
//        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 1.0f, 2.0f, 10.0f));
//        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
//    }
//
//    @Override
//    protected AABB makeBoundingBox() {
//        return entityData.get(VARIANT).dimensions().makeBoundingBox(position());
//    }
//
//    public Player getOwner() {
//        if (entityData.get(OWNER).isPresent() && cachedOwner == null && !level.isClientSide)
//            return (Player) ((ServerLevel) level).getEntity(entityData.get(OWNER).get());
//        return cachedOwner;
//    }
//
//    public void setOwner(Player owner) {
//        this.cachedOwner = owner;
//        this.entityData.set(OWNER, Optional.of(owner.getUUID()));
//    }
//
//    @Override
//    protected void defineSynchedData() {
//        super.defineSynchedData();
//        entityData.define(OWNER, Optional.empty());
//        entityData.define(VARIANT, StaffItem.VARIANTS[0]);
//    }
//
//    @Override
//    public void readAdditionalSaveData(CompoundTag nbt) {
//        if (nbt.contains("Owner"))
//            this.entityData.set(OWNER, Optional.of(nbt.getUUID("Owner")));
//        for (StaffItem.Variant variant : StaffItem.VARIANTS) {
//            if (!variant.name().equals(nbt.getString("Variant")))
//                continue;
//
//            this.entityData.set(VARIANT, variant);
//            break;
//        }
//    }
//
//    @Override
//    public float getEyeHeight(Pose pPose) {
//        return getEyeHeight(pPose, entityData.get(VARIANT).dimensions());
//    }
//
//    @Override
//    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
//        return super.getStandingEyeHeight(pPose, entityData.get(VARIANT).dimensions());
//    }
//
//    @Override
//    public void addAdditionalSaveData(CompoundTag tag) {
//        tag.putString("Variant", this.entityData.get(VARIANT).name());
//        entityData.get(OWNER).ifPresent(uuid -> tag.putUUID("Owner", uuid));
//    }
//
//    @Override
//    public boolean isFlying() {
//        return entityData.get(VARIANT).flying() && !onGround;
//    }
//
//
//    public static class FollowOwnerGoal extends Goal {
//        public static final int TELEPORT_WHEN_DISTANCE_IS = 12;
//        private static final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2;
//        private static final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3;
//        private static final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1;
//        private final StaffEntity tamable;
//        private final LevelReader level;
//        private final double speedModifier;
//        private final PathNavigation navigation;
//        private final float stopDistance;
//        private final float startDistance;
//        private LivingEntity owner;
//        private int timeToRecalcPath;
//        private float oldWaterCost;
//
//        public FollowOwnerGoal(StaffEntity pTamable, double pSpeedModifier, float pStartDistance, float pStopDistance) {
//            this.tamable = pTamable;
//            this.level = pTamable.level;
//            this.speedModifier = pSpeedModifier;
//            this.navigation = pTamable.getNavigation();
//            this.startDistance = pStartDistance;
//            this.stopDistance = pStopDistance;
//            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
//            if (!(pTamable.getNavigation() instanceof GroundPathNavigation) && !(pTamable.getNavigation() instanceof FlyingPathNavigation)) {
//                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
//            }
//        }
//
//        /**
//         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
//         * method as well.
//         */
//        public boolean canUse() {
//            LivingEntity livingentity = this.tamable.getOwner();
//            if (livingentity == null) {
//                return false;
//            } else if (livingentity.isSpectator()) {
//                return false;
//            } else if (this.tamable.distanceToSqr(livingentity) < (double) (this.startDistance * this.startDistance)) {
//                return false;
//            } else {
//                this.owner = livingentity;
//                return true;
//            }
//        }
//
//        /**
//         * Returns whether an in-progress EntityAIBase should continue executing
//         */
//        public boolean canContinueToUse() {
//            if (this.navigation.isDone()) {
//                return false;
//            } else {
//                return !(this.tamable.distanceToSqr(this.owner) <= (double) (this.stopDistance * this.stopDistance));
//            }
//        }
//
//        /**
//         * Execute a one shot task or start executing a continuous task
//         */
//        public void start() {
//            this.timeToRecalcPath = 0;
//            this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
//            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
//        }
//
//        /**
//         * Reset the task's internal state. Called when this task is interrupted by another one
//         */
//        public void stop() {
//            this.owner = null;
//            this.navigation.stop();
//            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
//        }
//
//        /**
//         * Keep ticking a continuous task that has already been started
//         */
//        public void tick() {
//            this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float) this.tamable.getMaxHeadXRot());
//            if (--this.timeToRecalcPath <= 0) {
//                this.timeToRecalcPath = this.adjustedTickDelay(10);
//                if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
//                    if (this.tamable.distanceToSqr(this.owner) >= 144.0D) {
//                        this.teleportToOwner();
//                    } else {
//                        this.navigation.moveTo(this.owner, this.speedModifier);
//                    }
//
//                }
//            }
//        }
//
//        private void teleportToOwner() {
//            BlockPos blockpos = this.owner.blockPosition();
//
//            for (int i = 0; i < 10; ++i) {
//                int j = this.randomIntInclusive(-3, 3);
//                int k = this.randomIntInclusive(-1, 1);
//                int l = this.randomIntInclusive(-3, 3);
//                boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
//                if (flag) {
//                    return;
//                }
//            }
//
//        }
//
//        private boolean maybeTeleportTo(int pX, int pY, int pZ) {
//            if (Math.abs((double) pX - this.owner.getX()) < 2.0D && Math.abs((double) pZ - this.owner.getZ()) < 2.0D) {
//                return false;
//            } else if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
//                return false;
//            } else {
//                this.tamable.moveTo((double) pX + 0.5D, pY, (double) pZ + 0.5D, this.tamable.getYRot(), this.tamable.getXRot());
//                this.navigation.stop();
//                return true;
//            }
//        }
//
//        private boolean canTeleportTo(BlockPos pPos) {
//            BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pPos.mutable());
//            if (blockpathtypes != BlockPathTypes.WALKABLE) {
//                return false;
//            } else {
//                BlockState blockstate = this.level.getBlockState(pPos.below());
//                if (!tamable.entityData.get(VARIANT).flying() && blockstate.getBlock() instanceof LeavesBlock) {
//                    return false;
//                } else {
//                    BlockPos blockpos = pPos.subtract(this.tamable.blockPosition());
//                    return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(blockpos));
//                }
//            }
//        }
//
//        private int randomIntInclusive(int pMin, int pMax) {
//            return this.tamable.getRandom().nextInt(pMax - pMin + 1) + pMin;
//        }
//    }
//
//    static class StaffEntityMoveControl extends MoveControl {
//        private final int maxTurn;
//        private final boolean hoversInPlace;
//
//        public StaffEntityMoveControl(StaffEntity staffEntity, int maxTurn, boolean hoversInPlace) {
//            super(staffEntity);
//            this.maxTurn = maxTurn;
//            this.hoversInPlace = hoversInPlace;
//        }
//
//        @Override
//        public void tick() {
//            if (!mob.getEntityData().get(VARIANT).flying()) {
//                super.tick();
//                return;
//            }
//            if (this.operation == Operation.MOVE_TO) {
//                this.operation = Operation.WAIT;
//                this.mob.setNoGravity(true);
//                double d0 = this.wantedX - this.mob.getX();
//                double d1 = this.wantedY - this.mob.getY();
//                double d2 = this.wantedZ - this.mob.getZ();
//                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
//                if (d3 < (double) 2.5000003E-7F) {
//                    this.mob.setYya(0.0F);
//                    this.mob.setZza(0.0F);
//                    return;
//                }
//
//                float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
//                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));
//                float f1;
//                if (this.mob.isOnGround()) {
//                    f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
//                } else {
//                    f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
//                }
//
//                this.mob.setSpeed(f1);
//                double d4 = Math.sqrt(d0 * d0 + d2 * d2);
//                if (Math.abs(d1) > (double) 1.0E-5F || Math.abs(d4) > (double) 1.0E-5F) {
//                    float f2 = (float) (-(Mth.atan2(d1, d4) * (double) (180F / (float) Math.PI)));
//                    this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, (float) this.maxTurn));
//                    this.mob.setYya(d1 > 0.0D ? f1 : -f1);
//                }
//            } else {
//                if (!this.hoversInPlace) {
//                    this.mob.setNoGravity(false);
//                }
//
//                this.mob.setYya(0.0F);
//                this.mob.setZza(0.0F);
//            }
//
//        }
//    }
//}
