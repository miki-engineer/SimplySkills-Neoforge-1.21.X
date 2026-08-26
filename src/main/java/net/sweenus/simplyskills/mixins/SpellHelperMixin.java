package net.sweenus.simplyskills.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellExecution;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.target.SpellTarget;
import net.sweenus.simplyskills.abilities.AbilityLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
@Mixin(SpellExecution.class)
public class SpellHelperMixin {

    @Inject(at = @At("TAIL"), method = "performSpell")
    private static void simplyskills$performSpell(
            Level world,
            Player player,
            Holder<Spell> spell,
            SpellTarget.SearchResult targets,
            SpellCast.Action action,
            float progress,
            CallbackInfo ci) {

        ResourceLocation spellId = spell.unwrapKey().orElseThrow().location();
        AbilityLogic.onSpellCastEffects(player, targets.entities(), spellId, null);

    }

}
