package net.sweenus.simplyskills.abilities.compat;

import net.neoforged.fml.ModList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.sweenus.simplyswords.power.GemPowerComponent;
import net.sweenus.simplyswords.registry.ComponentTypeRegistry;
import net.sweenus.simplyskills.registry.EffectRegistry;
import net.sweenus.simplyskills.registry.SoundRegistry;

public class SimplySwordsGemEffects {

    private static String getNetherPower(net.minecraft.world.item.ItemStack stack) {
        GemPowerComponent component = stack.getOrDefault(ComponentTypeRegistry.GEM_POWER.get(), GemPowerComponent.DEFAULT);
        return component.hasNetherPower() ? component.netherPower().getPath() : "";
    }

    public static boolean passVersionCheck() {
        if (ModList.get().isLoaded("simplyswords")) {
            if (ModList.get().getModContainerById("simplyswords").isPresent()) {
                String blacklistedVersion1 = "1.50";
                String blacklistedVersion2 = "1.48";
                String version = ModList.get().getModContainerById("simplyswords").get().getModInfo().getVersion().toString();
                //System.out.println("Comparing current Simply Swords version: " + version + " against blacklisted versions: " + blacklistedVersion1 + " & " + blacklistedVersion2);
                if (version.contains(blacklistedVersion1) || version.contains(blacklistedVersion2)) {
                    //System.out.println("Detected BLACKLISTED version of Simply Swords");
                    return false;
                }
            }
            return true;
        }
        return  false;
    }


    public static void doGenericAbilityGemEffects(Player user) {

        if (ModList.get().isLoaded("simplyswords") && passVersionCheck()) {

            // Used for non-specialisation specific effects that proc on signature ability use

            String mainHandNetherEffect = "";
            String offHandNetherEffect = "";

            if (user.getMainHandItem().getItem() instanceof SwordItem)
                mainHandNetherEffect = getNetherPower(user.getMainHandItem());
            if (user.getOffhandItem().getItem() instanceof SwordItem)
                offHandNetherEffect = getNetherPower(user.getOffhandItem());
            String allNetherEffects = offHandNetherEffect + mainHandNetherEffect;

            // Chance to gain 5 stacks of precision on ability use
            if (allNetherEffects.contains("precise")) {
                int procChance = SimplySwordsRequiredMethods.preciseChance;
                if (user.getRandom().nextInt(100) < procChance) {
                    user.addEffect(new MobEffectInstance(EffectRegistry.PRECISION, 200, 5, false, false, true));
                    doSound(user);
                }
            }

            // Chance to gain 2 stacks of might on ability use
            if (allNetherEffects.contains("mighty")) {
                int procChance = SimplySwordsRequiredMethods.mightyChance;
                if (user.getRandom().nextInt(100) < procChance) {
                    user.addEffect(new MobEffectInstance(EffectRegistry.MIGHT, 200, 3, false, false, true));
                    doSound(user);
                }
            }

            // Chance to gain stealth on ability use
            if (allNetherEffects.contains("stealthy")) {
                int procChance = SimplySwordsRequiredMethods.stealthyChance;
                if (user.getRandom().nextInt(100) < procChance) {
                    user.addEffect(new MobEffectInstance(EffectRegistry.STEALTH, 600, 0, false, false, true));
                    doSound(user);
                }
            }
        }
    }

    // Socket checking
    public static boolean doSignatureGemEffects(Player user, String nether_power) {

        if (ModList.get().isLoaded("simplyswords") && passVersionCheck()) {

            String mainHandNetherEffect = "";
            String offHandNetherEffect = "";

            if (user.getMainHandItem().getItem() instanceof SwordItem)
                mainHandNetherEffect = getNetherPower(user.getMainHandItem());
            if (user.getOffhandItem().getItem() instanceof SwordItem && !nether_power.contains("spellforged"))
                offHandNetherEffect = getNetherPower(user.getOffhandItem());
            String allNetherEffects = offHandNetherEffect + mainHandNetherEffect;

            return allNetherEffects.contains(nether_power);
        }
        return false;
    }

    public static void doSound(Player user) {
        user.level().playSound(null, user, SoundRegistry.FX_UI_UNLOCK3,
                SoundSource.PLAYERS, 1f, 1.6f);
    }



    // Specific effects

    // Renewed - Chance to significantly reduce cooldown
    public static int renewed(Player player, int cooldown, int minimumCD) {
        if (SimplySwordsGemEffects.doSignatureGemEffects(player, "renewed")) {
            int procChance = SimplySwordsRequiredMethods.renewedChance;
            if (player.getRandom().nextInt(100) < procChance) {
                doSound(player);
                return minimumCD;
            }
        }
        return cooldown;
    }

    // Accelerant - Berserkers signature ability Berserking, no longer provides stacks of Berserking but has a reduced base cooldown.
    public static int accelerant(Player player, int cooldown, int minimumCD) {
        if (SimplySwordsGemEffects.doSignatureGemEffects(player, "accelerant")) {
            doSound(player);
            return (cooldown - 12000);
        }
        return cooldown;
    }

    // Chance to gain a stack of Barrier whenever you cast a spell
    public static void spellshield(Player player) {
        if (SimplySwordsGemEffects.doSignatureGemEffects(player, "spellshield")) {
            int procChance = SimplySwordsRequiredMethods.spellshieldChance;
            if (player.getRandom().nextInt(100) < procChance) {
                player.addEffect(new MobEffectInstance(EffectRegistry.BARRIER, 100, 0, false, false, true));
                doSound(player);
            }
        }
    }

    // When in mainhand, grants + 1 to all Spell Power
    public static void spellforged(Player player) {
        if (player.tickCount %20 == 0 && SimplySwordsGemEffects.doSignatureGemEffects(player, "spellforged"))
            player.addEffect(new MobEffectInstance(EffectRegistry.SPELLFORGED, 25, 0, false, false, true));
    }

    // When in main or offhand, grants + 2 to Soul & Lightning Spell Power
    public static void soulshock(Player player) {
        if (player.tickCount %20 == 0 && SimplySwordsGemEffects.doSignatureGemEffects(player, "soulshock"))
            player.addEffect(new MobEffectInstance(EffectRegistry.SOULSHOCK, 25, 0, false, false, true));
    }

    // Chance on spell hit to drop a banner that periodically grants precision & spellforged
    public static void spellStandard(Player user) {
        if (doSignatureGemEffects(user, "spell_Standard")) {
            SimplySwordsRequiredMethods.spawnSpellStandard(user);
        }
    }

    // Drop a banner at the end of your charge, revealing enemies and granting might to allies
    public static void warStandard(Player user) {
        if (doSignatureGemEffects(user, "war_standard")) {
            SimplySwordsRequiredMethods.spawnWarStandard(user);
            doSound(user);
        }
    }

    //Chance to remove Revealed stacks on Evasion proc
    public static void deception(Player user) {
        if (doSignatureGemEffects(user, "deception")) {
            int chance = SimplySwordsRequiredMethods.deceptionChance;
            if (user.getRandom().nextInt(100) < chance && user.hasEffect(EffectRegistry.REVEALED)) {
                user.removeEffect(EffectRegistry.REVEALED);
                doSound(user);
            }
        }
    }


}
