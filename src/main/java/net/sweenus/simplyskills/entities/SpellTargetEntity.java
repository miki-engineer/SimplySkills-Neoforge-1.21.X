package net.sweenus.simplyskills.entities;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.SynchedEntityData;

public class SpellTargetEntity extends Entity {
    public SpellTargetEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public static final Supplier<EntityType<SpellTargetEntity>> TYPE = Suppliers.memoize(() ->
            EntityType.Builder.of(SpellTargetEntity::new, MobCategory.MISC).build("spell_target_entity"));
    public static int lifetime = 120;

    @Override
    public void baseTick() {
        this.setNoGravity(true);
        if (this.tickCount > lifetime)
            this.discard();
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return false;
    }
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {

    }
}
