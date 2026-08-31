package net.sweenus.simplyskills;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.neoforged.fml.ModList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.puffish.skillsmod.api.Category;
import net.puffish.skillsmod.api.SkillsAPI;
import net.sweenus.simplyskills.config.*;
import net.sweenus.simplyskills.client.SimplySkillsClient;
import net.sweenus.simplyskills.abilities.AbilityEffects;
import net.sweenus.simplyskills.abilities.RangerAbilities;
import net.sweenus.simplyskills.abilities.RogueAbilities;
import net.sweenus.simplyskills.entities.DreadglareEntity;
import net.sweenus.simplyskills.entities.GreaterDreadglareEntity;
import net.sweenus.simplyskills.entities.WraithEntity;
import net.sweenus.simplyskills.network.KeybindPacket;
import net.sweenus.simplyskills.network.ModPacketHandler;
import net.sweenus.simplyskills.mixins.LivingEntityAccessor;
import net.sweenus.simplyskills.registry.*;
import net.sweenus.simplyskills.rewards.PassiveSkillReward;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Mod(SimplySkills.MOD_ID)
public class SimplySkills {
    public static final String MOD_ID = "simplyskills";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static List<String> specialisations = new ArrayList<>();
    public static GeneralConfig generalConfig;
    public static WayfarerConfig wayfarerConfig;
    public static WarriorConfig warriorConfig;
    public static InitiateConfig initiateConfig;
    public static BerserkerConfig berserkerConfig;
    public static WizardConfig wizardConfig;
    public static SpellbladeConfig spellbladeConfig;
    public static RogueConfig rogueConfig;
    public static RangerConfig rangerConfig;
    public static CrusaderConfig crusaderConfig;
    public static ClericConfig clericConfig;
    public static NecromancerConfig necromancerConfig;
    public static MiscConfig miscConfig;

    private static void setSpecialisations() {
        specialisations.add("simplyskills:rogue");
        specialisations.add("simplyskills:ranger");
        specialisations.add("simplyskills:berserker");
        specialisations.add("simplyskills:wizard");
        specialisations.add("simplyskills:spellblade");
        specialisations.add("simplyskills:crusader");
        specialisations.add("simplyskills:cleric");
        specialisations.add("simplyskills:necromancer");
    }
    public static String[] getSpecialisations() {return new String[] {
            "simplyskills:rogue",
            "simplyskills:ranger",
            "simplyskills:berserker",
            "simplyskills:wizard",
            "simplyskills:spellblade",
            "simplyskills:crusader",
            "simplyskills:cleric",
            "simplyskills:necromancer"
    };}

    public static List<String> getSpecialisationsAsArray() {
        return specialisations;
    }

    public SimplySkills(IEventBus modEventBus) {
        modEventBus.addListener(EffectRegistry::registerEffects);
        modEventBus.addListener(SoundRegistry::registerSounds);
        modEventBus.addListener(ItemRegistry::registerItems);
        modEventBus.addListener(EntityRegistry::registerEntities);
        onInitialize();
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(ModPacketHandler::register);
        if (FMLEnvironment.dist == Dist.CLIENT)
            SimplySkillsClient.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(this::onEffectAdded);
        NeoForge.EVENT_BUS.addListener(this::onEffectRemoved);
        NeoForge.EVENT_BUS.addListener(this::onEffectExpired);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(this::onLivingIncomingDamage);
    }

    public void onInitialize() {

        AutoConfig.register(ConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        generalConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().client;
        wayfarerConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().wayfarer;
        warriorConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().warrior;
        initiateConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().initiate;
        berserkerConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().berserker;
        wizardConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().wizard;
        spellbladeConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().spellblade;
        rogueConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().rogue;
        rangerConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().ranger;
        crusaderConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().crusader;
        clericConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().cleric;
        necromancerConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().necromancer;
        miscConfig = AutoConfig.getConfigHolder(ConfigWrapper.class).getConfig().misc;

        PassiveSkillReward.register();
        setSpecialisations();
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.DREADGLARE, DreadglareEntity.createDreadglareAttributes().build());
        event.put(EntityRegistry.GREATER_DREADGLARE, GreaterDreadglareEntity.createGreaterDreadglareAttributes().build());
        event.put(EntityRegistry.WRAITH, WraithEntity.createWraithAttributes().build());
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            removeLegacyAttributeModifiers(player);
            if (generalConfig.disableDefaultPuffishTrees || ModList.get().isLoaded("prominent")) {
                processPlayer(player);
            }
            ModPacketHandler.sendSignatureAbility(player);
        }
    }

    private void removeLegacyAttributeModifiers(ServerPlayer player) {
        for (AttributeInstance attribute : player.getAttributes().getSyncableAttributes()) {
            attribute.getModifiers().stream()
                    .map(AttributeModifier::id)
                    .filter(id -> id.getNamespace().equals(MOD_ID) && id.getPath().startsWith("modifier_"))
                    .toList()
                    .forEach(attribute::removeModifier);
        }
    }

    private void onEffectAdded(MobEffectEvent.Added event) {
        invokeEffectHook(event.getEffectInstance().getEffect().value(), "onEffectAddedCustom",
                event.getEntity(), event.getEffectInstance().getAmplifier());
    }

    private void onEffectRemoved(MobEffectEvent.Remove event) {
        int amplifier = event.getEffectInstance() == null ? 0 : event.getEffectInstance().getAmplifier();
        invokeEffectHook(event.getEffect().value(), "onEffectRemovedCustom", event.getEntity(), amplifier);
    }

    private void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance() != null)
            invokeEffectHook(event.getEffectInstance().getEffect().value(), "onEffectRemovedCustom",
                    event.getEntity(), event.getEffectInstance().getAmplifier());
    }

    private void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player
                && event.getEntity() != player) {
            if (HelperMethods.isUnlocked("simplyskills:berserker",
                    SkillReferencePosition.berserkerSpecialisationBloodthirsty, player))
                AbilityEffects.effectBerserkerBloodthirsty(player);

            if (HelperMethods.isUnlocked("simplyskills:ranger",
                    SkillReferencePosition.rangerSpecialisationElementalArrowsRenewal, player))
                RangerAbilities.passiveRangerElementalArrowsRenewal(player);

            if (HelperMethods.isUnlocked("simplyskills:rogue",
                    SkillReferencePosition.rogueSpecialisationEvasionFanOfBladesRenewal, player))
                HelperMethods.incrementStatusEffect(player, EffectRegistry.FANOFBLADES, 500, 2, 20);
        }
    }

    private void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.invulnerableTime <= 10
                && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && HelperMethods.isUnlocked("simplyskills:rogue",
                SkillReferencePosition.rogueEvasionMastery, player)
                && !RogueAbilities.passiveRogueEvasionMastery(player)) {
            player.invulnerableTime = 20;
            ((LivingEntityAccessor) player).simplyskills$setLastHurt(event.getAmount());
            event.setCanceled(true);
        }
    }

    private void invokeEffectHook(Object effect, String methodName, net.minecraft.world.entity.LivingEntity entity, int amplifier) {
        try {
            effect.getClass().getMethod(methodName, net.minecraft.world.entity.LivingEntity.class,
                    net.minecraft.world.entity.ai.attributes.AttributeMap.class, int.class)
                    .invoke(effect, entity, entity.getAttributes(), amplifier);
        } catch (NoSuchMethodException ignored) {
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to run effect lifecycle hook", exception);
        }
    }

    private void processPlayer(ServerPlayer player) {
        SkillsAPI.streamUnlockedCategories(player)
                .forEach(category -> processCategory(player, category));
    }

    private void processCategory(ServerPlayer player, Category category) {
        String categoryId = category.getId().toString();
        if (categoryId.equals("puffish_skills:combat") || categoryId.equals("puffish_skills:mining")) {
            SkillsAPI.getCategory(ResourceLocation.parse(categoryId)).ifPresent(categoryObj -> {
                categoryObj.erase(player);
                categoryObj.lock(player);
            });
        } // Remove Simply Skills tree when Prominent is detected
        if (ModList.get().isLoaded("prominent")  && categoryId.equals("simplyskills:tree")) {
            SkillsAPI.getCategory(ResourceLocation.parse(categoryId)).ifPresent(categoryObj -> {
                categoryObj.erase(player);
                categoryObj.lock(player);
            });
        }
    }

}
