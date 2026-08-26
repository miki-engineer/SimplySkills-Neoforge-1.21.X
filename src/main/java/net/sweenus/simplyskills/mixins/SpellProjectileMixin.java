package net.sweenus.simplyskills.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellExecution;
import net.spell_engine.internals.impact.SpellImpacts;
import net.sweenus.simplyskills.abilities.*;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(SpellProjectile.class)
public abstract class SpellProjectileMixin extends Projectile {

    @Shadow private Spell.ProjectileData.Perks perks;

    @Shadow public float range;

    @Shadow private SpellExecution.ImpactContext context;

    @Shadow private Entity followedTarget;

    @Shadow public abstract void setVelocity(double x, double y, double z, float speed, float spread, float divergence);

    @Shadow public abstract Holder<Spell> getSpellEntry();

    private ResourceLocation simplyskills$getSpellId() {
        return this.getSpellEntry().unwrapKey().orElseThrow().location();
    }

    public SpellProjectileMixin(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void simplyskills$tick(CallbackInfo ci) {

        if (!this.level().isClientSide) {
            if ( this.getSpellEntry() != null && this.getOwner() instanceof ServerPlayer player) {
                SpellProjectile spellProjectile = (SpellProjectile) (Object)this;
                ResourceLocation spellId = simplyskills$getSpellId();

                // Ranger Elemental Artillery
                RangerAbilities.signatureRangerElementalArtillery(player, spellProjectile, spellId, this.context, this.perks);

                //Wizard Lightning Ball
                WizardAbilities.signatureWizardStaticDischargeBall(player, spellProjectile, spellId, this.context, this.perks);
                //Wizard Lightning Orb
                WizardAbilities.signatureWizardLightningOrb(spellProjectile, this.followedTarget, spellId);
                //Cleric Sacred Orb
                ClericAbilities.signatureClericSacredOrbHoming(spellProjectile, spellId);
            }
        }
    }
    @Inject(at = @At("HEAD"), method = "onHitBlock", cancellable = true)
    protected void simplyskills$onBlockHit(CallbackInfo ci) {
        if (!this.level().isClientSide) {
            if (this.getSpellEntry() != null) {
                ResourceLocation spellId = simplyskills$getSpellId();
                String[] spellList =  new String[] {
                        "simplyskills:lightning_ball_homing",
                        "simplyskills:physical_dagger_homing",
                        "simplyskills:sacred_orb_lesser"};
                if (HelperMethods.stringContainsAny(spellId.toString(), spellList))
                    ci.cancel();
            }
        }
    }
    @Inject(at = @At("HEAD"), method = "onHitEntity", cancellable = true)
    protected void simplyskills$onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (!this.level().isClientSide) {
            if (this.getSpellEntry() != null) {
                ResourceLocation spellId = simplyskills$getSpellId();

                if (entityHitResult.getEntity() != null && entityHitResult.getEntity() instanceof LivingEntity livingEntity && getOwner() != null) {
                    if (livingEntity.hasEffect(EffectRegistry.AGONY) && getOwner() instanceof Player playerAttacker) {
                        AscendancyAbilities.agonyEffect(playerAttacker, livingEntity);
                    }
                }

                try {
                    SpellProjectile spellProjectile = (SpellProjectile) (Object) this;
                    ClericAbilities.signatureClericSacredOrbImpact(entityHitResult, spellId, getOwner(), spellProjectile);

                    String[] spellList = new String[]{"simplyskills:lightning_ball_homing", "simplyskills:physical_dagger_homing"};
                    if (HelperMethods.stringContainsAny(spellId.toString(), spellList) && this.getOwner() instanceof ServerPlayer player) {

                        SpellImpacts.projectileImpact(player, this, entityHitResult.getEntity(), this.getSpellEntry(), context.position(entityHitResult.getLocation()));

                        if (HelperMethods.isUnlocked("simplyskills:wizard",
                                SkillReferencePosition.wizardSpecialisationStaticDischargeLightningOrbOnHit, player)) {
                            List<Entity> targets = new ArrayList<Entity>();
                            if (entityHitResult.getEntity() != null) {
                                targets.add(entityHitResult.getEntity());
                                AbilityLogic.onSpellCastEffects(player, targets, spellId, null);
                            }
                        }

                        ci.cancel();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
