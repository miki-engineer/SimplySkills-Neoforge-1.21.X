package net.sweenus.simplyskills.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.SignatureAbilities;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElementalSurgeEffect extends MobEffect {
    public ElementalSurgeEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            int frequency = SimplySkills.spellbladeConfig.signatureSpellbladeElementalSurgeFrequency;

            if (livingEntity.tickCount % frequency == 0 && (livingEntity instanceof Player player)) {
                List<String> list = new ArrayList<>();
                list.add("simplyskills:frost_nova");
                list.add("simplyskills:fire_nova");
                list.add("simplyskills:lightning_nova");

                if (HelperMethods.isUnlocked("simplyskills:spellblade",
                        SkillReferencePosition.spellbladeSpecialisationElementalSurgeNoFrost, player))
                    list.remove("simplyskills:frost_nova");
                if (HelperMethods.isUnlocked("simplyskills:spellblade",
                        SkillReferencePosition.spellbladeSpecialisationElementalSurgeNoFire, player))
                    list.remove("simplyskills:fire_nova");
                if (HelperMethods.isUnlocked("simplyskills:spellblade",
                        SkillReferencePosition.spellbladeSpecialisationElementalSurgeNoLightning, player))
                    list.remove("simplyskills:lightning_nova");
                if (list.isEmpty())
                    list.add("simplyskills:arcane_nova");

                Random rand = new Random();
                String randomSpell = list.get(rand.nextInt(list.size()));
                int radius = SimplySkills.spellbladeConfig.signatureSpellbladeElementalSurgeRadius;
                int chance = SimplySkills.spellbladeConfig.signatureSpellbladeElementalSurgeChance;

                SignatureAbilities.castSpellEngineAOE(player, randomSpell, radius, chance, false, false);
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
