package net.sweenus.simplyskills.events;

import me.fzzyhmstrs.amethyst_core.compat.spell_power.SpCompat;
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spell_power.api.SpellSchool;
import net.sweenus.simplyskills.abilities.AbilityLogic;

import java.util.Set;

public class AmethystImbuementEvent {

    public static void registerAIEvents() {
        SpCompat.INSTANCE.getAFTER_CAST().register(AmethystImbuementEvent::onSpellPowerCast);
    }

    // This method will be called when a spell power cast event occurs
    private static void onSpellPowerCast(Level world, LivingEntity user, ItemStack stack, ScepterAugment spell, Set<? extends SpellSchool> schools) {

        if (user instanceof  Player player) {
            //System.out.println("ScepterAugment to string is: " + spell.toString());
            AbilityLogic.onSpellCastEffects(player, null, null, schools);
        }
    }

}