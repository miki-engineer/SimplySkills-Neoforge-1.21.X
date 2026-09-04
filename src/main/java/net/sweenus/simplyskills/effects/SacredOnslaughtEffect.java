package net.sweenus.simplyskills.effects;

import net.neoforged.fml.ModList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.paladins.effect.PaladinEffects;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class SacredOnslaughtEffect extends MobEffect {
    public SacredOnslaughtEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity.onGround() && (livingEntity instanceof Player player)) {

                int velocity = SimplySkills.crusaderConfig.signatureCrusaderSacredOnslaughtVelocity;
                int radius = SimplySkills.crusaderConfig.signatureCrusaderSacredOnslaughtRadius;
                double damageMultiplier = SimplySkills.crusaderConfig.signatureCrusaderSacredOnslaughtDMGMultiplier;
                double healing = (SpellPower.getSpellPower(SpellSchools.HEALING, player).baseValue() * damageMultiplier);
                int hitFrequency = 10;
                int stunDuration = SimplySkills.crusaderConfig.signatureCrusaderSacredOnslaughtStunDuration;

                player.setDeltaMovement(livingEntity.getLookAngle().scale(+velocity));
                player.setDeltaMovement(livingEntity.getDeltaMovement().x, 0, livingEntity.getDeltaMovement().z);
                player.hurtMarked = true;
                double damage = (player.getArmorValue() * damageMultiplier);

                AABB box = HelperMethods.createBox(player, radius*2);
                for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                    if (entities != null) {
                        if (entities instanceof LivingEntity le) {
                            if (player.tickCount % hitFrequency == 0) {
                                boolean hostileTarget = HelperMethods.checkFriendlyFireAOE(le, player);
                                if (hostileTarget && player.isBlocking()) {
                                    le.setDeltaMovement((le.getX() - player.getX()) /4,  (le.getY() - player.getY()) /4, (le.getZ() - player.getZ()) /4);
                                    le.hurt(player.damageSources().playerAttack(player), (float) damage);
                                    player.level().playSound(null, player, SoundRegistry.SOUNDEFFECT32,
                                            SoundSource.PLAYERS, 0.6f, 1.0f);

                                    if (HelperMethods.isUnlocked("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationSacredOnslaughtStun, player))
                                        le.addEffect(new MobEffectInstance(PaladinEffects.JUDGEMENT.entry, stunDuration));

                                }
                                if (!hostileTarget
                                        && HelperMethods.isUnlocked("simplyskills:crusader",
                                        SkillReferencePosition.crusaderSpecialisationSacredOnslaughtHeal, player)) {
                                    SignatureAbilities.castSpellEngineIndirectTarget(player, "paladins:divine_protection", 32, le, null);
                                    le.heal((float)healing);
                                }

                                HelperMethods.spawnParticlesPlane(
                                        player.level(),
                                        ParticleTypes.CLOUD,
                                        player.blockPosition(),
                                        radius-2, 0, 1, 0 );
                            }
                        }
                    }
                }
            }
        }
        super.applyEffectTick(livingEntity, amplifier);
            return true;
    }
    public void onEffectRemovedCustom(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (entity instanceof Player player && ModList.get().isLoaded("simplyswords"))
            SimplySwordsGemEffects.warStandard(player);
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
