package net.sweenus.simplyskills.abilities.compat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyswords.SimplySwords;
import net.sweenus.simplyswords.config.Config;
import net.sweenus.simplyswords.api.SimplySwordsAPI;
import net.sweenus.simplyswords.entity.BattleStandardEntity;

public class SimplySwordsRequiredMethods {

    // Config fetching

    public static int preciseChance = Config.gemPowers.simplySkills.preciseChance.get();
    public static int mightyChance = Config.gemPowers.simplySkills.mightyChance.get();
    public static int renewedChance = Config.gemPowers.simplySkills.renewedChance.get();
    public static int stealthyChance = Config.gemPowers.simplySkills.stealthyChance.get();
    public static int spellshieldChance = Config.gemPowers.simplySkills.spellshieldChance.get();
    public static int leapingChance = Config.gemPowers.simplySkills.leapingChance.get();

    public static int spellStandardChance = 10;
    public static int deceptionChance = 50;




    // API Reliant

    public static void spawnSpellStandard(Player user) {
        AABB box = HelperMethods.createBox(user, 20);
        int chance = SimplySwordsRequiredMethods.spellStandardChance;

        for (Entity entities : user.level().getEntities(user, box, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
            if (entities != null) {
                if (entities instanceof BattleStandardEntity bse) {

                    if (bse.ownerEntity == user && bse.positiveEffect.contains("simplyskills:precision")
                            && bse.positiveEffectSecondary.contains("simplyskills:spellforged"))
                        return;
                }
            }
        }
        if (user.getRandom().nextInt(100) < chance) {
            SimplySwordsAPI.spawnBattleStandard(user, 3, "api",
                    3, -2, "simplyskills:precision",
                    "simplyskills:spellforged", 0,
                    null, null, 0,
                    false, false);
            SimplySwordsGemEffects.doSound(user);
        }
    }

    public static void spawnWarStandard(Player user) {
        SimplySwordsAPI.spawnBattleStandard(user, 3, "api", 3, 3,
                "simplyskills:might", null, 4,
                "simplyskills:revealed", null, 0,
                false, false);

    }

}
