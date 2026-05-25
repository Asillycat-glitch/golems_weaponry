package a_silly_cat.modulargolems_weaponry.compat;

import a_silly_cat.modulargolems_weaponry.item.proficiency.CullProficiencyTrinketItem;
import dev.xkmc.golemmagicka.content.entity.IGolemMagicka;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CullTrinketEventHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void register() {
        if (!ModList.get().isLoaded("irons_spellbooks")) {
            LOGGER.warn("[MGWE] Iron's Spells not loaded, skipping CullTrinket integration.");
            return;
        }
        MinecraftForge.EVENT_BUS.register(new CullTrinketEventHandler());

        // 软依赖：仅在 Golem Magicka 存在时注册傀儡相关监听
        if (ModList.get().isLoaded("golemmagicka")) {
            MinecraftForge.EVENT_BUS.register(new GolemCompatHandler());
            LOGGER.info("[MGWE] Golem Magicka detected, golem casting support enabled.");
        }
    }

    // ========== 玩家施法 ==========
    @SubscribeEvent
    public void onPlayerSpellCast(SpellOnCastEvent event) {
        applyEffect(event.getEntity());
    }

    // ========== 玩家攻击增伤 ==========
    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        applyBonus(player, event);
    }

    // ========== 核心效果 ==========
    private void applyEffect(Player player) {
        ItemStack trinket = getCullTrinket(player);
        if (trinket.isEmpty() || !CullProficiencyTrinketItem.isMaxLevel(trinket)) return;

        long currentTime = player.level().getGameTime();
        long lastTrigger = trinket.getOrCreateTag().getLong("CullLastTrigger");
        if (currentTime - lastTrigger < 20 * 3) return;
        trinket.getOrCreateTag().putLong("CullLastTrigger", currentTime);

        // 标记下次攻击增伤（傀儡与玩家共用）
        player.getPersistentData().putLong("CullNextHitBonus", currentTime);

        // 回复魔力
        MagicData magicData = MagicData.getPlayerMagicData(player);
        float maxMana = (float) player.getAttributeValue(AttributeRegistry.MAX_MANA.get());
        float newMana = Math.min(magicData.getMana() + 20.0F, maxMana);
        magicData.setMana(newMana);
    }

    private void applyBonus(Player player, LivingHurtEvent event) {
        long currentTime = player.level().getGameTime();
        long bonusTime = player.getPersistentData().getLong("CullNextHitBonus");
        if (bonusTime == 0 || currentTime - bonusTime > 40) return; // 2秒内有效

        player.getPersistentData().remove("CullNextHitBonus");
        event.setAmount(event.getAmount() * 1.2F);
    }

    // ========== Curios 查询 ==========
    private static ItemStack getCullTrinket(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.getStacksHandler("proficiency"))
                .map(stacksHandler -> {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof CullProficiencyTrinketItem) {
                            return stack;
                        }
                    }
                    return ItemStack.EMPTY;
                }).orElse(ItemStack.EMPTY);
    }

    // ======================================================================
    //  Golem Magicka 兼容模块（仅当 golemmagicka 加载时才被注册）
    // ======================================================================
    static class GolemCompatHandler {
        private static final Map<UUID, Boolean> GOLEM_CASTING_STATE = new HashMap<>();

        // 检测傀儡施法开始
        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
            Player player = event.player;

            for (var golem : player.level().getEntitiesOfClass(AbstractGolemEntity.class,
                    player.getBoundingBox().inflate(32))) {
                if (!(golem instanceof IGolemMagicka magicka)) continue;
                if (!player.getUUID().equals(getGolemOwnerId(golem))) continue;

                var magicData = magicka.magicka$getGolemMagicData();
                boolean isCasting = magicData.isCasting();
                boolean wasCasting = GOLEM_CASTING_STATE.getOrDefault(golem.getUUID(), false);

                if (isCasting && !wasCasting) {
                    // 傀儡施法开始，触发主人效果
                    CullTrinketEventHandler handler = new CullTrinketEventHandler();
                    handler.applyEffect(player);
                }

                GOLEM_CASTING_STATE.put(golem.getUUID(), isCasting);
            }
        }

        // 傀儡攻击增伤
        @SubscribeEvent
        public void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
            UUID ownerId = getGolemOwnerId(attacker);
            if (ownerId == null) return;

            Player player = attacker.level().getPlayerByUUID(ownerId);
            if (player == null) return;

            CullTrinketEventHandler handler = new CullTrinketEventHandler();
            handler.applyBonus(player, event);
        }

        private static UUID getGolemOwnerId(LivingEntity entity) {
            if (entity instanceof AbstractGolemEntity<?, ?> golem) {
                return golem.getOwnerUUID();
            }
            // 备用反射（若其它实体也实现了 getOwnerUUID）
            try {
                var method = entity.getClass().getMethod("getOwnerUUID");
                return (UUID) method.invoke(entity);
            } catch (Exception ignored) {}
            return null;
        }
    }
}