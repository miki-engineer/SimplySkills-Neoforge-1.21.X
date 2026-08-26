package net.sweenus.simplyskills.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElementalImpactEffect extends MobEffect {
    public ElementalImpactEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {

            if (livingEntity.onGround() && (livingEntity instanceof Player player)) {
                int velocity = SimplySkills.spellbladeConfig.signatureSpellbladeElementalImpactVelocity;

                player.setDeltaMovement(livingEntity.getLookAngle().scale(+velocity));
                player.setDeltaMovement(livingEntity.getDeltaMovement().x, 0, livingEntity.getDeltaMovement().z);
                player.hurtMarked = true;
                List<String> list = new ArrayList<>();
                list.add("simplyskills:frost_arrow_rain");
                list.add("simplyskills:fire_arrow_rain");
                list.add("simplyskills:lightning_arrow_rain");
                List<String> list2 = new ArrayList<>();
                list2.add("simplyskills:fire_explosion");
                list2.add("simplyskills:frost_explosion");
                list2.add("simplyskills:lightning_explosion");
                Random rand = new Random();
                String randomSpell = list.get(rand.nextInt(list.size()));
                String randomSpell2 = list2.get(rand.nextInt(list2.size()));
                int radius = SimplySkills.spellbladeConfig.signatureSpellbladeElementalImpactRadius;
                int chance = SimplySkills.spellbladeConfig.signatureSpellbladeElementalImpactChance;
                int slownessDuration = SimplySkills.spellbladeConfig.signatureSpellbladeElementalImpactSlownessDuration;
                int slownessAmplifier = SimplySkills.spellbladeConfig.signatureSpellbladeElementalImpactSlownessAmplifier;

                SignatureAbilities.castSpellEngineAOE(player, randomSpell, radius, chance, true, false);
                SignatureAbilities.castSpellEngineAOE(player, randomSpell2, radius, (int)(chance * 0.35), true, false);

                if (HelperMethods.isUnlocked("simplyskills:spellblade",
                        SkillReferencePosition.spellbladeSpecialisationElementalImpactMagnet, player)){
                    AABB box = HelperMethods.createBox(player, radius*2);
                    for (Entity entities : livingEntity.level().getEntities(livingEntity, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {

                        if (entities != null) {
                            if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player) && !le.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                                le.setDeltaMovement((player.getX() - le.getX()) /4,  (player.getY() - le.getY()) /4, (player.getZ() - le.getZ()) /4);
                                le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, slownessDuration, slownessAmplifier, false, false, true));
                            }
                        }
                    }
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
