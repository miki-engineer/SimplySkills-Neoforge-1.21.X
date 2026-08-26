package net.sweenus.simplyskills.abilities;

import net.neoforged.fml.ModList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.effects.instance.SimplyStatusEffectInstance;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

import java.util.Comparator;

public class AscendancyAbilities {

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

    public static boolean righteousHammers(Player player) {
        player.addEffect(new MobEffectInstance(EffectRegistry.RIGHTEOUSHAMMERS,
                800, 1 + (getAscendancyPoints(player) / 10), false, false, true));
        return true;
    }

    public static boolean boneArmor(Player player) {
        player.addEffect(new MobEffectInstance(EffectRegistry.BONEARMOR,
                800, 3 + (getAscendancyPoints(player) / 10), false, false, true));
        return true;
    }
    public static void boneArmorEffect(ServerPlayer player) {
            if (HelperMethods.isUnlocked("simplyskills:ascendancy", SkillReferencePosition.ascendancyBoneArmor, player) && player.hasEffect(EffectRegistry.BONEARMOR))
                HelperMethods.decrementStatusEffect(player, EffectRegistry.BONEARMOR);
    }

    public static boolean cyclonicCleave(Player player) {
        if (ModList.get().isLoaded("prominent"))
            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:cyclonic_cleave_prom", 3, player, null);
        else SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:cyclonic_cleave", 3, player, null);

        return true;
    }

    public static boolean magicCircle(Player player) {
        player.addEffect(new MobEffectInstance(EffectRegistry.MAGICCIRCLE,
                240 + (getAscendancyPoints(player)), 0, false, false, true));
        player.addEffect(new MobEffectInstance(EffectRegistry.IMMOBILIZE,
                25, 0, false, false, true));
        player.level().playSound(null, player, SoundRegistry.SPELL_RADIANT_HIT,
                SoundSource.PLAYERS, 0.2f, 0.9f);
        return true;
    }
    public static boolean magicCircleEffect(Player player) {
        return getAscendancyPoints(player) > 29 && player.hasEffect(EffectRegistry.MAGICCIRCLE);
    }

    public static boolean arcaneSlash(Player player) {
        SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:arcane_slash", 3, player, null);
        player.level().playSound(null, player, SoundRegistry.SPELL_SLASH,
                SoundSource.PLAYERS, 0.4f, 1.1f);
        if (getAscendancyPoints(player) > 9)
            HelperMethods.incrementStatusEffect(player, EffectRegistry.ARCANEATTUNEMENT, 60, 1+(getAscendancyPoints(player) / 10), 19);

        return true;
    }

    public static boolean agony(Player player) {
        ServerLevel world = (ServerLevel) player.level();
        AABB box = HelperMethods.createBox(player, 10);
        Entity closestEntity = world.getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE).stream()
                .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(player)))
                .orElse(null);

        if (closestEntity != null) {
            if ((closestEntity instanceof LivingEntity ee)) {
                if (HelperMethods.checkFriendlyFire(ee, player)) {
                    SimplyStatusEffectInstance agonyEffect = new SimplyStatusEffectInstance(
                            EffectRegistry.AGONY, 200 + getAscendancyPoints(player), 0, false,
                            false, true);
                    agonyEffect.setSourceEntity(player);
                    ee.addEffect(agonyEffect);
                    HelperMethods.spawnWaistHeightParticles(world, ParticleTypes.SMOKE, player, ee, 20);
                    player.level().playSound(null, player, SoundRegistry.MAGIC_SHAMANIC_SPELL_04,
                            SoundSource.PLAYERS, 0.2f, 1.0f);

                    return true;
                }
            }
        }
        return false;
    }

    public static void agonyEffect(Player playerAttacker, LivingEntity livingEntity) {
        if (!(livingEntity.getEffect(EffectRegistry.AGONY) instanceof SimplyStatusEffectInstance))
            return;
        SimplyStatusEffectInstance agonyEffect = (SimplyStatusEffectInstance) livingEntity.getEffect(EffectRegistry.AGONY);
        livingEntity.invulnerableTime = 0;
        livingEntity.hurt(playerAttacker.damageSources().indirectMagic(playerAttacker, playerAttacker), (float) (0.1 * HelperMethods.getHighestAttributeValue(playerAttacker)));
        livingEntity.invulnerableTime = 0;

        if (agonyEffect != null) {
            LivingEntity sourceEntity = agonyEffect.getSourceEntity();
            if (sourceEntity instanceof Player sourcePlayer && AscendancyAbilities.getAscendancyPoints(sourcePlayer) > 29)
                playerAttacker.heal((float) (0.1* SpellPower.getSpellPower(SpellSchools.HEALING, sourcePlayer).randomValue()));
        }
    }
    public static boolean torment(Player player) {
        ServerLevel world = (ServerLevel) player.level();
        AABB box = HelperMethods.createBox(player, 10);
        Entity closestEntity = world.getEntities(player, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE).stream()
                .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(player)))
                .orElse(null);

        if (closestEntity != null) {
            if ((closestEntity instanceof LivingEntity ee)) {
                if (HelperMethods.checkFriendlyFire(ee, player)) {
                    SimplyStatusEffectInstance tormentEffect = new SimplyStatusEffectInstance(
                            EffectRegistry.TORMENT, 160 + getAscendancyPoints(player), 0, false,
                            false, true);
                    tormentEffect.setSourceEntity(player);
                    ee.addEffect(tormentEffect);
                    player.level().playSound(null, player, SoundRegistry.MAGIC_SHAMANIC_SPELL_04,
                            SoundSource.PLAYERS, 0.2f, 1.0f);

                    if (getAscendancyPoints(player) > 29) {
                        SimplyStatusEffectInstance tauntedEffect = new SimplyStatusEffectInstance(
                                EffectRegistry.TAUNTED, 160 + getAscendancyPoints(player), 0, false,
                                false, true);
                        tauntedEffect.setSourceEntity(player);
                        ee.addEffect(tauntedEffect);
                    }
                    HelperMethods.spawnWaistHeightParticles(world, ParticleTypes.SMOKE, player, ee, 20);

                    return true;
                }
            }
        }
        return false;
    }

    public static boolean tormentEffect(Player player, DamageSource source, float amount) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            if (attacker.hasEffect(EffectRegistry.TORMENT)) {
                if (!(attacker.getEffect(EffectRegistry.TORMENT) instanceof SimplyStatusEffectInstance))
                    return false;
                SimplyStatusEffectInstance tormentEffect = (SimplyStatusEffectInstance) attacker.getEffect(EffectRegistry.TORMENT);
                if (tormentEffect.getSourceEntity() instanceof Player sourcePlayer && sourcePlayer == player) {
                    attacker.hurt(source, amount);
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean rapidfire(Player player) {
        player.addEffect(new MobEffectInstance(EffectRegistry.RAPIDFIRE, 120+getAscendancyPoints(player), 0, false, false, true));
        return true;
    }

    public static boolean cataclysm(Player player) {
        SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:cataclysm", 3, player, null);
        player.level().playSound(null, player, SoundRegistry.ENERGY_CHARGE,
                SoundSource.PLAYERS, 0.3f, 1.0f);
        return true;
    }

    public static boolean ghostwalk(Player player) {
        SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:ghostwalk", 3, player, null);
        return true;
    }

    public static boolean skywardSunder(Player player) {
        player.addEffect(new MobEffectInstance(EffectRegistry.SKYWARDSUNDER, 45, 0, false, false, true));
        player.level().playSound(null, player, SoundRegistry.SLASH_02,
                SoundSource.PLAYERS, 0.6f, 1.0f);
        return true;
    }

    public static void goldenAegis(Player player) {
        HelperMethods.incrementStatusEffect(player, EffectRegistry.GOLDENAEGIS, 2400, 1, 15 + (getAscendancyPoints(player) / 10));
    }

    public static boolean righteousShield(Player player) {
        if (player.hasEffect(EffectRegistry.GOLDENAEGIS)) {
            SignatureAbilities.castSpellEngineIndirectTarget(player, "simplyskills:righteous_shield", 3, player, null);
            return true;
        }
        return false;
    }

    public static boolean chainbreaker(Player player) {
        HelperMethods.buffSteal(player, player,true, false,true,true);
        player.level().playSound(null, player, SoundRegistry.SPELL_ARCANE_CAST,
                SoundSource.PLAYERS, 0.3f, 1.1f);
        HelperMethods.incrementStatusEffect(player, EffectRegistry.MIGHT, 120, 1+(getAscendancyPoints(player) / 10), 19);
        HelperMethods.incrementStatusEffect(player, EffectRegistry.MARKSMANSHIP, 120, 1+(getAscendancyPoints(player) / 10), 19);
        HelperMethods.spawnParticlesPlane(player.level(), ParticleTypes.POOF, player.blockPosition(), 1, 0, 0.1, 0);
        if (getAscendancyPoints(player) > 29)
            player.addEffect(new MobEffectInstance(EffectRegistry.UNDYING, 120, 0, false, false, true));
        return true;
    }

}
