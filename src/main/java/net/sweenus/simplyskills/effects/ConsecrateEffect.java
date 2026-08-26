package net.sweenus.simplyskills.effects;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.effects.instance.SimplyStatusEffectInstance;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class ConsecrateEffect extends MobEffect {
    public ConsecrateEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity.onGround() && (livingEntity instanceof Player player)) {

                int radius = SimplySkills.crusaderConfig.signatureCrusaderConsecrationRadius;
                double damageMultiplier = SimplySkills.crusaderConfig.signatureCrusaderConsecrationDMGMultiplier;
                int hitFrequency = SimplySkills.crusaderConfig.signatureCrusaderConsecrationHitFrequency;
                double damage = (SpellPower.getSpellPower(SpellSchools.HEALING, player).baseValue() * damageMultiplier);
                int tauntDuration = SimplySkills.crusaderConfig.signatureCrusaderConsecrationTauntDuration;
                int mightStacks = SimplySkills.crusaderConfig.signatureCrusaderConsecrationMightStacks;
                int mightStacksMax = SimplySkills.crusaderConfig.signatureCrusaderConsecrationMightStacksMax;
                int spellforgedStacks = SimplySkills.crusaderConfig.signatureCrusaderConsecrationSpellforgedStacks;
                int spellforgedStacksMax = SimplySkills.crusaderConfig.signatureCrusaderConsecrationSpellforgedStacksMax;

                AABB box = HelperMethods.createBox(player, radius * 2);
                if (player.tickCount % hitFrequency == 0) {
                    player.heal((float) damage / 5);
                    for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                        if (entities != null) {
                            if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {

                                if (le.isInvertedHealAndHarm() && HelperMethods.isUnlocked("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationConsecrationWard, player))
                                    le.setDeltaMovement((le.getX() - player.getX()) /4,  (le.getY() - player.getY()) /4, (le.getZ() - player.getZ()) /4);

                                le.invulnerableTime = 0;
                                le.hurt(player.damageSources().indirectMagic(player, player), (float) damage);
                                le.invulnerableTime = 1;

                                // Taunt
                                if ((le instanceof Mob me) && HelperMethods.isUnlocked("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationConsecrationTaunt, player)) {
                                    SimplyStatusEffectInstance tauntEffect = new SimplyStatusEffectInstance(
                                            EffectRegistry.TAUNTED, tauntDuration, 0, false,
                                            false, true);
                                    tauntEffect.setSourceEntity(livingEntity);
                                    me.addEffect(tauntEffect);
                                }


                            }
                            if ((entities instanceof LivingEntity le) && !HelperMethods.checkFriendlyFire(le, player)) {
                                le.heal((float) damage / 4);
                                if (HelperMethods.isUnlocked("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationConsecrationMighty, player))
                                    HelperMethods.incrementStatusEffect(le, EffectRegistry.MIGHT, hitFrequency+1, mightStacks, mightStacksMax);
                                if (HelperMethods.isUnlocked("simplyskills:crusader", SkillReferencePosition.crusaderSpecialisationConsecrationSpellforged, player))
                                    HelperMethods.incrementStatusEffect(le, EffectRegistry.SPELLFORGED, hitFrequency+1, spellforgedStacks, spellforgedStacksMax);
                            }
                        }
                    }
                }
                if (player.tickCount % hitFrequency == 0) {
                    HelperMethods.spawnParticlesPlane(
                            player.level(),
                            SpellEngineParticles.magic_holy.type(),
                            player.blockPosition(),
                            radius, 0, 0.4, 0);
                    HelperMethods.spawnParticlesPlane(
                            player.level(),
                            SpellEngineParticles.magic_holy.type(),
                            player.blockPosition(),
                            radius, 0, 0.2, 0);
                    player.level().playSound(null, player, SoundRegistry.SOUNDEFFECT25,
                            SoundSource.PLAYERS, 0.05f, 0.8f);
                }
                if (player.tickCount % hitFrequency-10 == 0) {
                    HelperMethods.spawnParticlesPlane(
                            player.level(),
                            SpellEngineParticles.magic_spell.type(),
                            player.blockPosition(),
                            radius, 0, 0.2, 0);
                    HelperMethods.spawnParticlesPlane(
                            player.level(),
                            SpellEngineParticles.magic_holy.type(),
                            player.blockPosition(),
                            radius, 0, 0.3, 0);
                }
                if (player.tickCount % hitFrequency-5 == 0) {
                    HelperMethods.spawnParticlesPlane(
                            player.level(),
                            SpellEngineParticles.magic_holy.type(),
                            player.blockPosition(),
                            radius, 0, 0.4, 0);
                }
            }
        }
        super.applyEffectTick(livingEntity, amplifier);
        return true;
}

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
