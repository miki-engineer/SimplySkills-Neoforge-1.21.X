package net.sweenus.simplyskills.abilities;

import net.neoforged.fml.ModList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.world.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.compat.ProminenceInternalAbilities;
import net.sweenus.simplyskills.effects.instance.SimplyStatusEffectInstance;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ProminenceAbilities {

    public static int getAscendancyPoints(Player player) {
        if (player instanceof  ServerPlayer serverPlayer) {

            if (ModList.get().isLoaded("prominent")) {
                if (BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse("eldritch_end:corruption")) != null) {
                    int corruptionMaximum = SimplySkills.miscConfig.promCorruptionMax;
                    double corruptionMultiplier = SimplySkills.miscConfig.promCorruptionMulti;
                    return (int) Math.min(((int) player.getAttributeValue(BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse("eldritch_end:corruption")).orElseThrow()) * corruptionMultiplier), corruptionMaximum);
                } // Scale abilities with Corruption in Prominence
            }

            return HelperMethods.countUnlockedSkills("ascendancy", serverPlayer);
        }
        return 0;
    }


    //------- ASCENDANCY ABILITIES --------

    public static boolean boneArmor(Player player) {

        ServerLevel world = (ServerLevel) player.level();
        AABB box = HelperMethods.createBoxHeight(player, 12);
        AtomicInteger count = new AtomicInteger();
        player.level().getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE).stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> player instanceof ServerPlayer)
                .forEach(entity -> {
                    LivingEntity le = (LivingEntity) entity;
                    ServerPlayer playerEntity = (ServerPlayer) player;
                    if (HelperMethods.checkFriendlyFireAOE(le, playerEntity)) {
                        SimplyStatusEffectInstance tauntedEffect = new SimplyStatusEffectInstance(
                                EffectRegistry.TAUNTED, 160 + getAscendancyPoints(player), 0, false,
                                false, true);
                        if (getAscendancyPoints(player) > 29)
                            le.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                                    300 + getAscendancyPoints(player), 1,
                                    false, false, true));
                        tauntedEffect.setSourceEntity(player);
                        le.addEffect(tauntedEffect);
                        count.getAndIncrement();
                    HelperMethods.spawnWaistHeightParticles(world, ParticleTypes.SMOKE, player, le, 20);
                }
            });

        player.level().playSound(null, player, SoundRegistry.MAGIC_SHAMANIC_SPELL_04,
                SoundSource.PLAYERS, 0.2f, 1.0f);

        player.addEffect(new MobEffectInstance(EffectRegistry.BONEARMOR,
                400, Math.min(6, count.get()), false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, Math.min(6, count.get()) , false, false, true));
        return true;
    }
    public static void boneArmorEffect(ServerPlayer player) {
        if (HelperMethods.isUnlocked("puffish_skills:prom", SkillReferencePosition.ascendancyBoneArmor, player) && player.hasEffect(EffectRegistry.BONEARMOR)) {
            MobEffectInstance boneArmorEffect = player.getEffect(EffectRegistry.BONEARMOR);
            if (boneArmorEffect != null) {
                HelperMethods.decrementStatusEffect(player, EffectRegistry.BONEARMOR);
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 2, false, false, true));
            }
        }
    }

    public static void focusEffect(Player player, ResourceLocation spellId) {
        if (HelperMethods.isUnlocked("puffish_skills:prom", SkillReferencePosition.promFocus, player)) {
            if (spellId.toString().contains("archers:barrage")) {
                if (player.getMainHandItem().getItem() instanceof BowItem) {
                    player.addEffect(new MobEffectInstance(EffectRegistry.FOCUS, 220, 0, false, false, true));
                } else if (player.getMainHandItem().getItem() instanceof CrossbowItem) {
                    player.removeEffect(EffectRegistry.REVEALED);
                    HelperMethods.incrementStatusEffect(player, EffectRegistry.MARKSMANSHIP, 80, 8, 15);
                    player.addEffect(new MobEffectInstance(EffectRegistry.STEALTH, 80, 0, false, false, true));
                }
            }
        }
    }

    public static void promTwinstrike(Player player, LivingEntity target) {
        int effectChance = SimplySkills.warriorConfig.passiveWarriorTwinstrikeChance;
        int effectDamage = (int) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damageSource = player.damageSources().playerAttack(player);
        if (HelperMethods.isDualWielding(player))
            effectChance = effectChance * 2;
        if (player.getRandom().nextInt(100) < effectChance) {
            target.invulnerableTime = 0;
            target.hurt(damageSource, effectDamage);
            target.invulnerableTime = 0;
        }
    }

    public static void warriorsDevotion(Player player) {
        if (player.tickCount % 20 == 0 && HelperMethods.isUnlocked("puffish_skills:prom", SkillReferencePosition.promWarriorsDevotion, player)) {
            ItemStack mainhand = player.getMainHandItem();
            ItemStack offhand = player.getOffhandItem();
            if (mainhand.isEmpty() || offhand.isEmpty()) {
                if  (mainhand.getItem() instanceof SwordItem || mainhand.getItem() instanceof AxeItem || offhand.getItem() instanceof SwordItem || offhand.getItem() instanceof AxeItem)
                    player.addEffect(new MobEffectInstance(EffectRegistry.TITANSGRIP, 30, 0, false, false, true));
            }
       }
    }

    public static float melodyOfProtection(float amount) {
        return amount - (amount / 10);
    }

    public static boolean promDissonance(Player player) {
        if (!(player.level() instanceof ServerLevel world)) {
            return false;
        }

        int corruption = AscendancyAbilities.getAscendancyPoints(player);
        int radius = 6;
        int frequency = 20;
        int duration = 160 + corruption;
        int stunDuration = 40 + (corruption / 2);
        Holder<MobEffect> stunEffect = MobEffects.MOVEMENT_SLOWDOWN;

        if (BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse("minecells:stunned")).isPresent())
            stunEffect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse("minecells:stunned")).orElseThrow();

        List<Holder<MobEffect>> statusEffects = player.getActiveEffects().stream()
                .map(MobEffectInstance::getEffect)
                .toList();


        if (statusEffects.contains(EffectRegistry.MELODYOFBLOODLUST)) {
            ProminenceInternalAbilities.giveAreaBuffs(player, radius, frequency, duration, null, 0, null, 0, MobEffects.DIG_SLOWDOWN, 0, MobEffects.WEAKNESS, 0);
        }
        if (statusEffects.contains(EffectRegistry.MELODYOFPROTECTION)) {
            ProminenceInternalAbilities.giveAreaBuffs(player, radius, frequency, duration, null, 0, null, 0, MobEffects.WEAKNESS, 0, null, 1);
            ProminenceInternalAbilities.giveAreaBuffs(player, radius, frequency, 20, null, 0, null, 0, null, 0, MobEffects.HARM, 1);
        }
        if (statusEffects.contains(EffectRegistry.MELODYOFCONCENTRATION)) {
            ProminenceInternalAbilities.giveAreaBuffs(player, radius, frequency, duration, null, 0, null, 0, MobEffects.DIG_SLOWDOWN, 0, MobEffects.MOVEMENT_SLOWDOWN, 0);
        }
        if (statusEffects.contains(EffectRegistry.MELODYOFWAR)) {
            ProminenceInternalAbilities.giveAreaBuffs(player, radius, frequency, duration, null, 0, null, 0, MobEffects.WEAKNESS, 0, MobEffects.WITHER, 1);
        }
        if (statusEffects.contains(EffectRegistry.MELODYOFSAFETY)) {
            ProminenceInternalAbilities.giveAreaBuffs(player, radius, frequency, duration, null, 0, null, 0, MobEffects.WITHER, 3, null, 0);
        }
        if (statusEffects.contains(EffectRegistry.MELODYOFSWIFTNESS)) {
            ProminenceInternalAbilities.giveAreaBuffs(player, radius, frequency, duration, null, 0, null, 0, MobEffects.MOVEMENT_SLOWDOWN, 2, null, 0);
        }

        ProminenceInternalAbilities.giveAreaBuffs(player, radius, frequency, stunDuration, null, 0, null, 0, stunEffect, 0, null, 0);
        HelperMethods.spawnOrbitParticles(world, player.position(), ParticleTypes.NOTE, radius, 20);
        HelperMethods.spawnOrbitParticles(world, player.position(), ParticleTypes.CRIT, radius, 16);
        HelperMethods.spawnOrbitParticles(world, player.position(), ParticleTypes.NOTE, 0.5, 8);
        player.level().playSound(null, player, SoundRegistry.ACTIVATE_PLINTH_01,
                SoundSource.PLAYERS, 0.4f, 1.0f);

        return true;
    }

}
