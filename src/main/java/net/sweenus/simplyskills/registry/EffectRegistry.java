package net.sweenus.simplyskills.registry;

import net.neoforged.fml.ModList;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.puffish.attributesmod.AttributesMod;
import net.spell_engine.api.effect.ActionImpairing;
import net.spell_engine.api.effect.EntityActionsAllowed;
import net.spell_engine.api.effect.Synchronized;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyskills.SimplySkills;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.sweenus.simplyskills.config.MiscConfig;
import net.sweenus.simplyskills.effects.*;
import net.sweenus.simplyskills.util.HelperMethods;

public class EffectRegistry {
    public static double mightIncrease = 0.10;
    public static double marksmanshipIncrease = 0.10;
    public static double spellforgedIncrease = 0.25;
    public static double soulshockIncrease = 1.0;

    public static Holder<MobEffect> BERSERKING = Holder.direct(new BerserkingEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> BLOODTHIRSTY = Holder.direct(new BloodthirstyEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> RAMPAGE = Holder.direct(new RampageEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> EVASION = Holder.direct(new EvasionEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> SIPHONINGSTRIKES = Holder.direct(new SiphoningStrikesEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> ELEMENTALARROWS = Holder.direct(new ElementalArrowsEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> ARROWRAIN = Holder.direct(new ArrowRainEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> FROSTVOLLEY = Holder.direct(new FrostVolleyEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> ARCANEVOLLEY = Holder.direct(new ArcaneVolleyEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> METEORICWRATH = Holder.direct(new MeteoricWrathEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> STATICCHARGE = Holder.direct(new StaticChargeEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> ELEMENTALIMPACT = Holder.direct(new ElementalImpactEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> ELEMENTALSURGE = Holder.direct(new ElementalSurgeEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> SPELLWEAVER = Holder.direct(new SpellweaverEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> FANOFBLADES = Holder.direct(new FanOfBladesEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> DISENCHANTMENT = Holder.direct(new DisenchantmentEffect(MobEffectCategory.HARMFUL, 3124687));
    public static Holder<MobEffect> BULLRUSH = Holder.direct(new BullrushEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> IMMOBILIZE = Holder.direct(new ImmobilizeEffect(MobEffectCategory.HARMFUL, 3124687));
    public static Holder<MobEffect> LEAPSLAM = Holder.direct(new LeapSlamEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> IMMOBILIZINGAURA = Holder.direct(new ImmobilizingAuraEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> SPELLBREAKING = Holder.direct(new SpellbreakingEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> EARTHSHAKER = Holder.direct(new EarthshakerEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> ARCANEATTUNEMENT = Holder.direct(new ArcaneAttunementEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellSchools.ARCANE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_8b724548dbd94dbf8ad59c0b7757dec5"),
                    0.02,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> FIREATTUNEMENT = Holder.direct(new FireAttunementEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellSchools.FIRE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_5835e9c241824098b9ef23670c46cb4d"),
                    0.02,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> FROSTATTUNEMENT = Holder.direct(new FrostAttunementEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellSchools.FROST.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_caa82c9798744f5e84e437380bf756ec"),
                    0.02,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> LIGHTNINGATTUNEMENT = Holder.direct(new LightningAttunementEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellSchools.LIGHTNING.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_7edc1ac1c6c54a4692e1baf28abea256"),
                    0.02,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> SOULATTUNEMENT = Holder.direct(new SoulAttunementEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellSchools.SOUL.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_45da701ee40a4041bd54f06e283ad7cb"),
                    0.02,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> HOLYATTUNEMENT = Holder.direct(new HolyAttunementEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellSchools.HEALING.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_60125c3e49804cc8b54e037b47185e2b"),
                    0.02,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> PRECISION = Holder.direct(new PrecisionEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellPowerMechanics.CRITICAL_CHANCE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_32a5a12951a64a38b78ee7afb69f9e17"),
                    0.02,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(SpellPowerMechanics.CRITICAL_DAMAGE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_bb6233b1475947d09044d509b4bc6695"),
                    0.02,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> DEATHMARK = Holder.direct(new DeathMarkEffect(MobEffectCategory.HARMFUL, 3124687)
            .addAttributeModifier(AttributesMod.RESISTANCE,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_325dbaa984c54ceaaca188b8dc585c3e"),
                    -0.25,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(AttributesMod.HEALING,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_5e2ff54c96984ba083209ba6e9f2b394"),
                    -0.25,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> MARKSMAN = Holder.direct(new MarksmanEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> STEALTH = Holder.direct(new StealthEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_0e7a848f46db4e129d4a40a5f24683c3"),
                    -0.40,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> MIGHT = Holder.direct(new MightEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_4a4233a03299475582139f10cfb7e795"),
                    mightIncrease,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> MARKSMANSHIP = Holder.direct(new MarksmanshipEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(AttributesMod.RANGED_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_d6702be12e6e44bab32541a7da2ca6b3"),
                    marksmanshipIncrease,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> EXHAUSTION = Holder.direct(new ExhaustionEffect(MobEffectCategory.HARMFUL, 3124687)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_1ce35aec6a4144ffa537f03b76f01664"),
                    -0.01,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .addAttributeModifier(Attributes.ATTACK_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_9410035d58384f51a48ec896e7a7570f"),
                            -0.01,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_1d71599697d041d7ab3c96c5302c9d98"),
                    -0.01,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> REVEALED = Holder.direct(new RevealedEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> BARRIER = Holder.direct(new BarrierEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> SOULSHOCK = Holder.direct(new SoulshockEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellSchools.SOUL.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_f4f57190f82f4283a4b9a898382bcea7"),
                    soulshockIncrease,
                    AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(SpellSchools.LIGHTNING.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_f811acad25424e5a837fa869251162ee"),
                    soulshockIncrease,
                    AttributeModifier.Operation.ADD_VALUE));
    public static Holder<MobEffect> SPELLFORGED = Holder.direct(new SoulshockEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellSchools.SOUL.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_5d6e7b01e11b46ac8a970ec497616982"),
                    spellforgedIncrease,
                    AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(SpellSchools.LIGHTNING.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_dc0cea79ffa64209b851952f60147b2c"),
                    spellforgedIncrease,
                    AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(SpellSchools.HEALING.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_fa5f66cdca9b4da6a4a83444a77156b7"),
                    spellforgedIncrease,
                    AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(SpellSchools.ARCANE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_d7b58e45df8540d9a78b943dc8765f2a"),
                    spellforgedIncrease,
                    AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(SpellSchools.FIRE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_3d183b4b9bfb4d4eb7f0bd8cf65a34fc"),
                    spellforgedIncrease,
                    AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(SpellSchools.FROST.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_5951bed4b0584320851275c1be44bc33"),
                    spellforgedIncrease,
                    AttributeModifier.Operation.ADD_VALUE));
    public static Holder<MobEffect> DIVINEADJUDICATION = Holder.direct(new DivineAdjudicationEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> SACREDONSLAUGHT = Holder.direct(new SacredOnslaughtEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> CONSECRATION = Holder.direct(new ConsecrateEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> TAUNTED = Holder.direct(new TauntedEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> UNDYING = Holder.direct(new UndyingEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> RAGE = Holder.direct(new RageEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_2489075cb5ce40b58a1d3787ad4f4d8b"),
                    +0.005,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_29d236f5e3e14612898b39b916fd771c"),
                    +0.005,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static Holder<MobEffect> OVERLOAD = Holder.direct(new OverloadEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellPowerMechanics.CRITICAL_DAMAGE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_c937f985c57146e58339b4ccf4c15442"),
                    0.45,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(SpellPowerMechanics.CRITICAL_CHANCE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_ad26be8bdb354d0498dbd8943e4ac8be"),
                    0.10,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> BLADESTORM = Holder.direct(new BladestormEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> VITALITYBOND = Holder.direct(new VitalityBondEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> ANOINTED = Holder.direct(new AnointedEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> AGILE = Holder.direct(new AgileEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(AttributesMod.RANGED_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_6fa231ce882a4163b941452ec1e80f39"),
                    0.05,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_24e81380108e488b9d48995f852d8fba"),
                    0.05,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(SpellPowerMechanics.HASTE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_812e38a796084a82a8d76f6c8db2f4d8"),
                    0.05,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_af3a4ed4331b4a51b9a78bad6f6015f0"),
                    0.05,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static Holder<MobEffect> RIGHTEOUSHAMMERS = Holder.direct(new RighteousHammersEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_36c6315a1c2548a798f6f1cfc15b9851"),
                    +0.02,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static Holder<MobEffect> BONEARMOR = Holder.direct(new BoneArmorEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(getPromBloodMagic(),
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_30ac8be8c2cc46a59311334b0cd88b7d"),
                    +1,
                    AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.ARMOR_TOUGHNESS,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_a8c16e8b9d824fe5a0f22b9fe688b4da"),
                    +0.1,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static Holder<MobEffect> MAGICCIRCLE = Holder.direct(new MagicCircleEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(SpellSchools.SOUL.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_962352c08bbb4e09b9b2ce2570558368"),
                    0.3,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(SpellSchools.LIGHTNING.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_c136624581fd4848988e83f7903f80aa"),
                    0.3,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(SpellSchools.HEALING.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_c9efc31650de470f8d111800066e106e"),
                    0.3,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(SpellSchools.ARCANE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_525f0dd65d1f49619a8257ecd7fc4682"),
                    0.3,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(SpellSchools.FIRE.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_4de16daae56c441483a59f957ce0566d"),
                    0.3,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(SpellSchools.FROST.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_ba74d1cf3d6f49049d515e070ae0bd7c"),
                    0.3,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static Holder<MobEffect> CYCLONICCLEAVE = Holder.direct(new CyclonicCleaveEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> ARCANESLASH = Holder.direct(new ArcaneSlashEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> AGONY = Holder.direct(new AgonyEffect(MobEffectCategory.HARMFUL, 3124687));
    public static Holder<MobEffect> TORMENT = Holder.direct(new TormentEffect(MobEffectCategory.HARMFUL, 3124687));
    public static Holder<MobEffect> RAPIDFIRE = Holder.direct(new RapidFireEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> CATACLYSM = Holder.direct(new CataclysmEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> GHOSTWALK = Holder.direct(new GhostwalkEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> SKYWARDSUNDER = Holder.direct(new SkywardSunderEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> RIGHTEOUSSHIELD = Holder.direct(new RighteousShieldEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> SHADOWAURA = Holder.direct(new ShadowAuraEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> GOLDENAEGIS = Holder.direct(new GoldenAegisEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(Attributes.ARMOR,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_c3bf99d1ee9c430792b0660581bfa28a"),
                    0.01,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ARMOR_TOUGHNESS,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_9836b739fb7b404e99279bdf74de1dae"),
                    0.01,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(SpellSchools.HEALING.attributeEntry,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_1ebefacf9a1e4a81938756dd4207a47f"),
                    0.01,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> FOCUS = Holder.direct(new FocusEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(AttributesMod.RANGED_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_127a23085cd74b28b52d71cde1d2a9da"),
                    0.2,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> TITANSGRIP = Holder.direct(new TitansGripEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_53044215cfb04a01a33e3bda11fab913"),
                    SimplySkills.miscConfig.promWarriorsDevotionAttackMulti,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_10d428423eb544c7ba5fa663ba984e66"),
                    -SimplySkills.miscConfig.promWarriorsDevotionAttackSpeedMulti,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static Holder<MobEffect> MELODYOFWAR = Holder.direct(new MelodyOfWarEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> MELODYOFSWIFTNESS = Holder.direct(new MelodyOfSwiftnessEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_606c3744d1f4431f860b0092f1d9c32e"),
                    0.30,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> MELODYOFPROTECTION = Holder.direct(new MelodyOfProtectionEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> MELODYOFSAFETY = Holder.direct(new MelodyOfSafetyEffect(MobEffectCategory.BENEFICIAL, 3124687));
    public static Holder<MobEffect> MELODYOFCONCENTRATION = Holder.direct(new MelodyOfConcentrationEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(Attributes.ATTACK_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_bddd1a507de34e21bb321b562c138927"),
                    0.20,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_6b9d312d3451485295be3f7d8c6ec188"),
                    0.10,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> MELODYOFBLOODLUST = Holder.direct(new MelodyOfBloodlustEffect(MobEffectCategory.BENEFICIAL, 3124687)
            .addAttributeModifier(Attributes.ATTACK_SPEED,
                    ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, "modifier_c717c9f23b184cebaf35455063b25e2a"),
                    0.20,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static Holder<MobEffect> RAGINGJAVELIN = Holder.direct(new RagingJavelinEffect(MobEffectCategory.BENEFICIAL, 3124687));

    public static Holder<MobEffect> registerStatusEffect(String name, Holder<MobEffect> statusEffect) {
        MobEffect registeredEffect = Registry.register(BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(SimplySkills.MOD_ID, name), statusEffect.value());
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(registeredEffect);
    }
    
    public static void registerEffects(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.MOB_EFFECT))
            return;

        Synchronized.configure(STEALTH.value(), true);
        Synchronized.configure(BLADESTORM.value(), true);
        Synchronized.configure(VITALITYBOND.value(), true);
        Synchronized.configure(UNDYING.value(), true);
        Synchronized.configure(ARCANEVOLLEY.value(), true);
        Synchronized.configure(FROSTVOLLEY.value(), true);
        Synchronized.configure(BARRIER.value(), true);
        Synchronized.configure(RAGE.value(), true);
        Synchronized.configure(EVASION.value(), true);
        Synchronized.configure(IMMOBILIZE.value(), true);
        Synchronized.configure(DEATHMARK.value(), true);
        Synchronized.configure(TAUNTED.value(), true);
        Synchronized.configure(MARKSMANSHIP.value(), true);
        Synchronized.configure(RIGHTEOUSHAMMERS.value(), true);
        Synchronized.configure(BONEARMOR.value(), true);
        Synchronized.configure(MAGICCIRCLE.value(), true);
        Synchronized.configure(AGONY.value(), true);
        Synchronized.configure(TORMENT.value(), true);

        ActionImpairing.configure(CYCLONICCLEAVE.value(), EntityActionsAllowed.STUN);
        ActionImpairing.configure(ARCANESLASH.value(), EntityActionsAllowed.INCAPACITATE);
        ActionImpairing.configure(RAPIDFIRE.value(), EntityActionsAllowed.INCAPACITATE);
        ActionImpairing.configure(CATACLYSM.value(), EntityActionsAllowed.STUN);
        ActionImpairing.configure(GHOSTWALK.value(), EntityActionsAllowed.STUN);
        ActionImpairing.configure(SKYWARDSUNDER.value(), EntityActionsAllowed.STUN);
        ActionImpairing.configure(RIGHTEOUSSHIELD.value(), EntityActionsAllowed.INCAPACITATE);

        BERSERKING = registerStatusEffect("berserking", BERSERKING);
        BLOODTHIRSTY = registerStatusEffect("bloodthirsty", BLOODTHIRSTY);
        RAMPAGE = registerStatusEffect("rampage", RAMPAGE);
        EVASION = registerStatusEffect("evasion", EVASION);
        SIPHONINGSTRIKES = registerStatusEffect("siphoning_strikes", SIPHONINGSTRIKES);
        ELEMENTALARROWS = registerStatusEffect("elemental_arrows", ELEMENTALARROWS);
        ARROWRAIN = registerStatusEffect("arrow_rain", ARROWRAIN);
        FROSTVOLLEY = registerStatusEffect("frost_volley", FROSTVOLLEY);
        ARCANEVOLLEY = registerStatusEffect("arcane_volley", ARCANEVOLLEY);
        METEORICWRATH = registerStatusEffect("meteoric_wrath", METEORICWRATH);
        STATICCHARGE = registerStatusEffect("static_charge", STATICCHARGE);
        ELEMENTALSURGE = registerStatusEffect("elemental_surge", ELEMENTALSURGE);
        ELEMENTALIMPACT = registerStatusEffect("elemental_impact", ELEMENTALIMPACT);
        SPELLWEAVER = registerStatusEffect("spellweaver", SPELLWEAVER);
        FANOFBLADES = registerStatusEffect("fanofblades", FANOFBLADES);
        DISENCHANTMENT = registerStatusEffect("disenchantment", DISENCHANTMENT);
        BULLRUSH = registerStatusEffect("bullrush", BULLRUSH);
        IMMOBILIZE = registerStatusEffect("immobilize", IMMOBILIZE);
        LEAPSLAM = registerStatusEffect("leapslam", LEAPSLAM);
        IMMOBILIZINGAURA = registerStatusEffect("immobilizing_aura", IMMOBILIZINGAURA);
        SPELLBREAKING = registerStatusEffect("spellbreaking", SPELLBREAKING);
        EARTHSHAKER = registerStatusEffect("earthshaker", EARTHSHAKER);
        ARCANEATTUNEMENT = registerStatusEffect("arcane_attunement", ARCANEATTUNEMENT);
        HOLYATTUNEMENT = registerStatusEffect("holy_attunement", HOLYATTUNEMENT);
        SOULATTUNEMENT = registerStatusEffect("soul_attunement", SOULATTUNEMENT);
        FIREATTUNEMENT = registerStatusEffect("fire_attunement", FIREATTUNEMENT);
        FROSTATTUNEMENT = registerStatusEffect("frost_attunement", FROSTATTUNEMENT);
        LIGHTNINGATTUNEMENT = registerStatusEffect("lightning_attunement", LIGHTNINGATTUNEMENT);
        PRECISION = registerStatusEffect("precision", PRECISION);
        DEATHMARK = registerStatusEffect("death_mark", DEATHMARK);
        MARKSMAN = registerStatusEffect("marksman", MARKSMAN);
        STEALTH = registerStatusEffect("stealth", STEALTH);
        MIGHT = registerStatusEffect("might", MIGHT);
        EXHAUSTION = registerStatusEffect("exhaustion", EXHAUSTION);
        REVEALED = registerStatusEffect("revealed", REVEALED);
        BARRIER = registerStatusEffect("barrier", BARRIER);
        SOULSHOCK = registerStatusEffect("soulshock", SOULSHOCK);
        SPELLFORGED = registerStatusEffect("spellforged", SPELLFORGED);
        DIVINEADJUDICATION = registerStatusEffect("divine_adjudication", DIVINEADJUDICATION);
        TAUNTED = registerStatusEffect("taunted", TAUNTED);
        UNDYING = registerStatusEffect("undying", UNDYING);
        RAGE = registerStatusEffect("rage", RAGE);
        OVERLOAD = registerStatusEffect("overload", OVERLOAD);
        BLADESTORM = registerStatusEffect("bladestorm", BLADESTORM);
        VITALITYBOND = registerStatusEffect("vitality_bond", VITALITYBOND);
        ANOINTED = registerStatusEffect("anointed", ANOINTED);
        MARKSMANSHIP = registerStatusEffect("marksmanship", MARKSMANSHIP);
        AGILE = registerStatusEffect("agile", AGILE);
        RIGHTEOUSHAMMERS = registerStatusEffect("righteous_hammers", RIGHTEOUSHAMMERS);
        BONEARMOR = registerStatusEffect("bone_armor", BONEARMOR);
        CYCLONICCLEAVE = registerStatusEffect("cyclonic_cleave", CYCLONICCLEAVE);
        MAGICCIRCLE = registerStatusEffect("magic_circle", MAGICCIRCLE);
        ARCANESLASH = registerStatusEffect("arcane_slash", ARCANESLASH);
        AGONY = registerStatusEffect("agony", AGONY);
        TORMENT = registerStatusEffect("torment", TORMENT);
        RAPIDFIRE = registerStatusEffect("rapidfire", RAPIDFIRE);
        CATACLYSM = registerStatusEffect("cataclysm", CATACLYSM);
        GHOSTWALK = registerStatusEffect("ghostwalk", GHOSTWALK);
        SKYWARDSUNDER = registerStatusEffect("skyward_sunder", SKYWARDSUNDER);
        RIGHTEOUSSHIELD = registerStatusEffect("righteous_shield", RIGHTEOUSSHIELD);
        GOLDENAEGIS = registerStatusEffect("golden_aegis", GOLDENAEGIS);
        SHADOWAURA = registerStatusEffect("shadow_aura", SHADOWAURA);
        FOCUS = registerStatusEffect("focus", FOCUS);
        TITANSGRIP = registerStatusEffect("titans_grip", TITANSGRIP);
        MELODYOFWAR = registerStatusEffect("melody_of_war", MELODYOFWAR);
        MELODYOFSWIFTNESS = registerStatusEffect("melody_of_swiftness", MELODYOFSWIFTNESS);
        MELODYOFPROTECTION = registerStatusEffect("melody_of_protection", MELODYOFPROTECTION);
        MELODYOFSAFETY = registerStatusEffect("melody_of_safety", MELODYOFSAFETY);
        MELODYOFCONCENTRATION = registerStatusEffect("melody_of_concentration", MELODYOFCONCENTRATION);
        MELODYOFBLOODLUST = registerStatusEffect("melody_of_bloodlust", MELODYOFBLOODLUST);
        RAGINGJAVELIN = registerStatusEffect("raging_javelin", RAGINGJAVELIN);

        if (ModList.get().isLoaded("paladins")) {
            CONSECRATION = registerStatusEffect("consecration", CONSECRATION);
            SACREDONSLAUGHT = registerStatusEffect("sacred_onslaught", SACREDONSLAUGHT);
        }

    }

    public static Holder<Attribute> getPromBloodMagic() {
        Holder<Attribute> returnAttribute = Attributes.ARMOR;
        if (ModList.get().isLoaded("prominent")) {
            if (BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse("death_knights:blood")) != null) {
                returnAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse("death_knights:blood")).orElseThrow();
            }
        }

        return returnAttribute;
    }


}
