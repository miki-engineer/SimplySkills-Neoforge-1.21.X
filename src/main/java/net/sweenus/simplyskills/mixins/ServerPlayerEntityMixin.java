package net.sweenus.simplyskills.mixins;

import net.neoforged.fml.ModList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.puffish.skillsmod.api.Category;
import net.puffish.skillsmod.api.Experience;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.abilities.*;
import net.sweenus.simplyskills.abilities.compat.ProminenceInternalAbilities;
import net.sweenus.simplyskills.abilities.compat.SimplySwordsGemEffects;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static net.puffish.skillsmod.api.SkillsAPI.getCategory;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin {

    @Unique
    private boolean simplyskills$fallPassivesTriggered;

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    public void simplyskills$damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player)(Object)this;
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.isAlive()) {

            //Effect Ghostwalk
            if (player.hasEffect(EffectRegistry.GHOSTWALK)) {
                cir.setReturnValue(false);
                player.level().playSound(null, player, SoundRegistry.FX_SKILL_BACKSTAB,
                        SoundSource.PLAYERS, 1, 1);
            }

            //Curse Torment
            if (AscendancyAbilities.tormentEffect(player, source, amount))
                cir.setReturnValue(false);

            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.warriorHeavyArmorMastery, serverPlayer)
                    || HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.warriorMediumArmorMastery, serverPlayer)) {
                WarriorAbilities.passiveWarriorArmorMastery(player);
            }

            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.warriorSpellbreaker, player)) {
                WarriorAbilities.passiveWarriorSpellbreaker(player);
            }

            if (HelperMethods.isUnlocked("simplyskills:rogue",
                    SkillReferencePosition.rogueSmokeBomb, serverPlayer)) {
                RogueAbilities.passiveRogueSmokeBomb(player);
            }

            //Passive Initiate Hasty
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.initiateHasty, player)) {
                InitiateAbilities.passiveInitiateHasty(player);
            }


            //Effect Rampage
            if (HelperMethods.isUnlocked("simplyskills:berserker",
                    SkillReferencePosition.berserkerSpecialisationRampage, serverPlayer)) {
                AbilityEffects.effectBerserkerRampage(player);
            }

            //Effect Stealth
            if (player.hasEffect(EffectRegistry.STEALTH) && !player.hasEffect(EffectRegistry.BARRIER)) {
                WayfarerAbilities.passiveWayfarerBreakStealth(null, player, true, false);
            }

            //Passive Rage
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.berserkerPath, serverPlayer)) {
                if (!player.isInvulnerableTo(source))
                    HelperMethods.incrementStatusEffect(player, EffectRegistry.RAGE, 300, 1, 99);
            }

            //Cleric Signature Anoint Weapon Undying
            if (HelperMethods.isUnlocked("simplyskills:cleric",
                    SkillReferencePosition.clericSpecialisationAnointWeaponUndying, player)
                    && player.hasEffect(EffectRegistry.ANOINTED)
                    && ModList.get().isLoaded("paladins")) {
                ClericAbilities.signatureClericAnointWeaponUndying(player);
            }

            //Necromancer Death Warden
            if (HelperMethods.isUnlocked("simplyskills:necromancer",
                    SkillReferencePosition.necromancerSpecialisationDeathWarden, player) && player.getHealth() < (player.getMaxHealth() / 2)) {
                NecromancerAbilities.effectDeathWarden(player);
            }


            //Ascendancy effects
            AscendancyAbilities.boneArmorEffect(serverPlayer);


            //Prom effects
            ProminenceAbilities.boneArmorEffect(serverPlayer);

        }
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float simplyskills$damageResult(float amount){
        Player player = (Player) (Object) this;
        if (player.hasEffect(EffectRegistry.RAGE))
            return amount;
        //Prom Melody of Safety Protection
        if (player.hasEffect(EffectRegistry.MELODYOFPROTECTION)) {
            return ProminenceAbilities.melodyOfProtection(amount);
        }
        return amount;
    }

    @Inject(at = @At("HEAD"), method = "trackStartFallingPosition")
    public void simplyskills$tickFallStartPos(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer)(Object)this;
        float slowfallActivateDistance = SimplySkills.initiateConfig.passiveInitiateSlowFallDistanceToActivate;
        float goliathActivateDistance = SimplySkills.warriorConfig.passiveWarriorGoliathFallDistance;

        if (!simplyskills$fallPassivesTriggered && HelperMethods.isUnlocked("simplyskills:tree",
                SkillReferencePosition.initiateSlowfall, player)
                && player.fallDistance > slowfallActivateDistance && !player.hasEffect(MobEffects.SLOW_FALLING)) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false, true));
            simplyskills$fallPassivesTriggered = true;
        }

        if (!simplyskills$fallPassivesTriggered && HelperMethods.isUnlocked("simplyskills:tree",
                SkillReferencePosition.warriorGoliath, player)
                && player.fallDistance > goliathActivateDistance && !player.hasEffect(MobEffects.SLOW_FALLING)) {
            simplyskills$fallPassivesTriggered = true;
            WarriorAbilities.passiveWarriorGoliath(player);
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.warriorBound, player)) {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 80, 2, false, false, true));
                player.addEffect(new MobEffectInstance(EffectRegistry.RAGINGJAVELIN, 80, 0, false, false, true));
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void simplyskills$tick(CallbackInfo ci) {
        Player player = (Player)(Object)this;
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.isAlive()) {

            AbilityEffects.tickRangerArrowRain(serverPlayer);

            if (player.onGround())
                simplyskills$fallPassivesTriggered = false;

            if (player.hasEffect(EffectRegistry.STEALTH)) {
                player.setInvisible(player.hasEffect(EffectRegistry.STEALTH));
            }

            //Passive Wayfarer Stealth
            if (player.tickCount % 10 == 0 && WayfarerAbilities.passiveWayfarerStealth(player)) {
                player.addEffect(new MobEffectInstance(EffectRegistry.STEALTH, 20, 0, false, false, true));
            }

            //Passive Warrior Death Defy
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.warriorDeathDefy, player)) {
                WarriorAbilities.passiveWarriorDeathDefy(player);
            }

            //Passive Warrior Carnage
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.warriorCarnage, player)) {
                WarriorAbilities.passiveWarriorCarnage(player);
            }

            //Passive Wayfarer Sneak
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.wayfarerSneak, player)
                    && player.isShiftKeyDown() && player.tickCount % 10 == 0) {
                int sneakSpeedAmplifier = SimplySkills.wayfarerConfig.passiveWayfarerSneakSpeedAmplifier;
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15, sneakSpeedAmplifier, false, false, true));
                if (player.hasEffect(EffectRegistry.STEALTH))
                    HelperMethods.incrementStatusEffect(player, EffectRegistry.MIGHT, 15, 1, 22);
            }
            //Passive Wayfarer Guarding
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.wayfarerGuarding, player)) {
                WayfarerAbilities.passiveWayfarerGuarding(player);
            }
            //Passive Area Strip
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.initiateNullification, player)) {
                InitiateAbilities.passiveInitiateNullification(player);
            }
            //Passive Initiate Lightning Rod
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.initiateLightningRod, player)) {
                InitiateAbilities.passiveInitiateLightningRod(player);
            }
            //Passive Initiate Attuned
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.initiateAttuned, player)) {
                InitiateAbilities.passiveInitiateAttuned(player);
            }

            //Passive Ranger Reveal
            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerReveal, player)) {
                RangerAbilities.passiveRangerReveal(player);
            }
            //Passive Ranger Bonded
            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerBonded, player)) {
                RangerAbilities.passiveRangerBonded(player);
            }
            //Passive Ranger Tamer
            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerTamer, player)) {
                RangerAbilities.passiveRangerTamer(player);
            }
            //Passive Ranger Trained
            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerTrained, player)) {
                RangerAbilities.passiveRangerTrained(player);
            }
            //Passive Ranger Incognito
            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerIncognito, player)) {
                RangerAbilities.passiveRangerIncognito(player);
            }

            //Passive Berserker Sword Mastery
            if (HelperMethods.isUnlocked("simplyskills:berserker",
                    SkillReferencePosition.berserkerSwordMastery, player)) {
                BerserkerAbilities.passiveBerserkerSwordMastery(player);
            }
            //Passive Berserker Axe Mastery
            if (HelperMethods.isUnlocked("simplyskills:berserker",
                    SkillReferencePosition.berserkerAxeMastery, player)) {
                BerserkerAbilities.passiveBerserkerAxeMastery(player);
            }
            //Passive Berserker Ignore Pain
            if (HelperMethods.isUnlocked( "simplyskills:berserker",
                    SkillReferencePosition.berserkerIgnorePain, player)) {
                BerserkerAbilities.passiveBerserkerIgnorePain(player);
            }
            //Passive Berserker Recklessness
            if (HelperMethods.isUnlocked("simplyskills:berserker",
                    SkillReferencePosition.berserkerPath, player)) {
                BerserkerAbilities.passiveBerserkerRecklessness(player);
            }
            //Passive Berserker Challenge
            if (HelperMethods.isUnlocked("simplyskills:berserker",
                    SkillReferencePosition.berserkerChallenge, player)) {
                BerserkerAbilities.passiveBerserkerChallenge(player);
            }
            //Passive Bulwark Shield Mastery
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.bulwarkShieldMastery, player)) {
                WarriorAbilities.passiveWarriorShieldMastery(player);
            }
            //Passive Wayfarer Slender && Initiate Frail
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.roguePath, player)
                    || HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.rangerPath, player)
                    || HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.wizardPath, player)) {
                WayfarerAbilities.passiveWayfarerSlender(player);
            }
            //Passive Backstab Stealth
            if (HelperMethods.isUnlocked("simplyskills:rogue",
                    SkillReferencePosition.rogueBackstab, player)) {
                RogueAbilities.passiveRogueBackstabStealth(player);
            }
            //Wizard Frost Volley Effect
            if (HelperMethods.isUnlocked("simplyskills:wizard",
                    SkillReferencePosition.wizardSpecialisationIceCometVolley, player)) {
                AbilityEffects.effectWizardFrostVolley(player);
            }
            //Wizard Arcane Volley Effect
            if (HelperMethods.isUnlocked("simplyskills:wizard",
            SkillReferencePosition.wizardSpecialisationArcaneBoltVolley, player)) {
                AbilityEffects.effectWizardArcaneVolley(player);
            }
            //Wizard Meteoric Wrath Effect
            if (HelperMethods.isUnlocked("simplyskills:wizard",
                    SkillReferencePosition.wizardSpecialisationMeteorShowerWrath, player)) {
                AbilityEffects.effectWizardMeteoricWrath(player);
            }
            //Rogue Fan of Blades Effect
            if (HelperMethods.isUnlocked("simplyskills:rogue",
                    SkillReferencePosition.rogueSpecialisationEvasionFanOfBlades, player)) {
                AbilityEffects.effectRogueFanOfBlades(player);
            }
            //Crusader Aegis
            if (HelperMethods.isUnlocked("simplyskills:crusader",
                    SkillReferencePosition.crusaderAegis, player)
                    && ModList.get().isLoaded("paladins")) {
                CrusaderAbilities.passiveCrusaderAegis(player);
            }
            //Crusader Divine Adjudication
            if (ModList.get().isLoaded("paladins")) {
                CrusaderAbilities.effectDivineAdjudication(player);
            }
            //Wizard Lightning Orb (Buff)
            if (HelperMethods.isUnlocked("simplyskills:wizard",
                    SkillReferencePosition.wizardSpecialisationStaticDischargeLightningOrb, player)) {
                WizardAbilities.signatureWizardLightningOrbBuff(player);
            }
            //Cleric Altruism
            if (HelperMethods.isUnlocked("simplyskills:cleric",
                    SkillReferencePosition.clericAltruism, player)
                    && ModList.get().isLoaded("paladins")) {
                ClericAbilities.passiveClericAltruism(player);
            }

            // Golden Aegis
            if (HelperMethods.isUnlocked("simplyskills:ascendancy",
                    SkillReferencePosition.ascendancyRighteousShield, player )
                    && AscendancyAbilities.getAscendancyPoints(player) > 29 && player.tickCount %400 == 0) {
                AscendancyAbilities.goldenAegis(player);
            }
            if (ModList.get().isLoaded("prominent")
                    && HelperMethods.isUnlocked("puffish_skills:prom",
                    SkillReferencePosition.ascendancyRighteousShield, player)
                    && ProminenceAbilities.getAscendancyPoints(player) > 29 && player.tickCount %400 == 0) {
                AscendancyAbilities.goldenAegis(player);
            }

            // Necromancer Winterborn
            if (HelperMethods.isUnlocked("simplyskills:necromancer",
                    SkillReferencePosition.necromancerSpecialisationWinterborn, player)
                    && player.tickCount %200 == 0) {
                NecromancerAbilities.effectNecromancerWinterborn(player);
            }

            NecromancerAbilities.effectPlague(player);

            if (ModList.get().isLoaded("prominent")) {
                ProminenceAbilities.warriorsDevotion(player);
                if (ModList.get().isLoaded("immersive_melodies"))
                    ProminenceInternalAbilities.bardAbility(player);
            }

            // Tick Gem effects
            if (ModList.get().isLoaded("simplyswords")) {
                SimplySwordsGemEffects.spellforged(player);
                SimplySwordsGemEffects.soulshock(player);
            }

            //Passive Initiate Lightning Rod
            if (HelperMethods.isUnlocked("simplyskills:tree",
                    SkillReferencePosition.initiateLightningRod, player)
                    && player.tickCount %40 == 0 && player.hasEffect(EffectRegistry.SOULSHOCK)) {
                SignatureAbilities.castSpellEngineAOE(player, "simplyskills:lightning_rod", 5, 100, false, true);
            }

        }
    }

    @Inject(at = @At("HEAD"), method = "die")
    public void simplyskills$onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer)(Object)this;
        AbilityEffects.clearRangerArrowRain(player);
        HelperMethods.treeResetOnDeath(player);
    }


    @Inject(at = @At("HEAD"), method = "attack")
    public void simplyskills$attack(Entity target,CallbackInfo ci) {
        Player player = (Player)(Object)this;
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.isAlive()) {
            if (target.isAttackable()) {
                if (!target.skipAttackInteraction(player)) {

                    // Moved logic to AbilityLogic, so that it can be activated via melee spells
                    AbilityLogic.doMeleeOnHit(serverPlayer, target);

                }
            }
        }
    }

}
