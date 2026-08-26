package net.sweenus.simplyskills.abilities.compat;

import immersive_melodies.item.InstrumentItem;
import net.minecraft.core.Holder;
import net.neoforged.fml.ModList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProminenceInternalAbilities {

    public static void bardAbility(Player player) {
        if (!ModList.get().isLoaded("prominent"))
            return;
        if (!HelperMethods.isUnlocked("puffish_skills:prom", SkillReferencePosition.promBardPassive, player))
            return;

        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        int radius = 6;
        int frequency = 30;
        int duration = frequency + 10;

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId.getNamespace().equals("immersive_melodies")) {
            if (!(item instanceof InstrumentItem instrumentItem) || !instrumentItem.isPlaying(stack))
                return;

            String instrument = itemId.getPath();
            if (instrument.equals("bagpipe")) {
                giveAreaBuffs(player, radius, frequency, duration, EffectRegistry.MELODYOFWAR, 0, MobEffects.REGENERATION, 0, null, 0, null, 0);
            } else if (instrument.equals("flute")) {
                giveAreaBuffs(player, radius, frequency, duration, EffectRegistry.MELODYOFSWIFTNESS, 0, MobEffects.DOLPHINS_GRACE, 0, null, 0, null, 0);
            } else if (instrument.equals("didgeridoo")) {
            giveAreaBuffs(player, radius, frequency, duration, EffectRegistry.MELODYOFPROTECTION, 0, MobEffects.DAMAGE_BOOST, 0, null, 0, null, 0);
            } else if (instrument.equals("lute")) {
                giveAreaBuffs(player, radius, frequency, duration, EffectRegistry.MELODYOFSAFETY, 0, null, 0, null, 0, null, 0);
            } else if (instrument.equals("piano")) {
                giveAreaBuffs(player, radius, frequency, duration, EffectRegistry.MELODYOFCONCENTRATION, 0, null, 0, null, 0, null, 0);
            } else if (instrument.equals("triangle")) {
                giveAreaBuffs(player, radius, frequency, duration, EffectRegistry.MELODYOFBLOODLUST, 0, null, 0, null, 0, null, 0);
            } else if (instrument.equals("trumpet")) {
                giveAreaBuffs(player, radius, frequency, duration, EffectRegistry.MELODYOFWAR, 0, MobEffects.REGENERATION, 0, null, 0, null, 0);
            }
            if (player.tickCount %30 ==0)
                HelperMethods.spawnOrbitParticles((ServerLevel) player.level(), player.position(), ParticleTypes.NOTE, 0.5, 6);

        }
    }

    public static void giveAreaBuffs(
            Player player,
            int radius,
            int tickFrequency,
            int buffDuration,
            @Nullable Holder<MobEffect> buffOne,
            int buffOneAmp,
            @Nullable Holder<MobEffect> buffTwo,
            int buffTwoAmp,
            @Nullable Holder<MobEffect> debuffOne,
            int debuffOneAmp,
            @Nullable Holder<MobEffect> debuffTwo,
            int debuffTwoAmp) {


        if (player.tickCount % tickFrequency != 0 && (debuffOne == null && debuffTwo == null)) {
            return;
        }

        if (buffOne != null && player.hasEffect(buffOne)) {
            MobEffectInstance statusEffectInstance = player.getEffect(buffOne);
            if (statusEffectInstance != null) {
                int duration = statusEffectInstance.getDuration();
                buffDuration += duration;
                if (buffDuration > 200) buffDuration = 200;
            }
        }

        AABB box = HelperMethods.createBox(player, radius);
        List<Entity> entities = player.level().getEntities(player, box, e -> e instanceof LivingEntity);


        // Apply buffs or debuffs to the entities
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity le) {
                boolean isFriendly = !HelperMethods.checkFriendlyFire(le, player);

                // Apply buffs
                if (isFriendly) {
                    if (buffOne != null) {
                        le.addEffect(new MobEffectInstance(buffOne, buffDuration, buffOneAmp, false, false, true));
                    }
                    if (buffTwo != null) {
                        le.addEffect(new MobEffectInstance(buffTwo, buffDuration, buffTwoAmp, false, false, true));
                    }
                }

                // Apply debuffs if they are not null
                if (!isFriendly) {
                    if (debuffOne != null) {
                        le.addEffect(new MobEffectInstance(debuffOne, buffDuration, debuffOneAmp, false, false, true));
                        HelperMethods.spawnOrbitParticles((ServerLevel) le.level(), le.position(), ParticleTypes.CRIT, 0.5, 5);
                        HelperMethods.spawnOrbitParticles((ServerLevel) le.level(), le.position(), ParticleTypes.NOTE, 0.5, 4);
                    }
                    if (debuffTwo != null) {
                        le.addEffect(new MobEffectInstance(debuffTwo, buffDuration, debuffTwoAmp, false, false, true));
                        HelperMethods.spawnOrbitParticles((ServerLevel) le.level(), le.position(), ParticleTypes.CRIT, 0.5, 6);
                        HelperMethods.spawnOrbitParticles((ServerLevel) le.level(), le.position(), ParticleTypes.NOTE, 0.5, 5);
                    }
                }
            }
        }
    }


}
