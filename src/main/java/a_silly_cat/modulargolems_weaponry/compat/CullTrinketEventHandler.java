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
import dev.xkmc.golemmagicka.content.entity.GolemMagicData;

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
    /**
     * Golem Magicka 兼容模块
     *
     * 修复思路：
     * 1. 通过 GolemMagicData.getMagicData() 获取傀儡的 MagicData，直接为傀儡回蓝。
     * 2. 在 LivingHurtEvent 中，用 attacked 的持久化数据来判断增伤，而非依赖玩家。
     */
    static class GolemCompatHandler {
        private static final Map<UUID, Boolean> GOLEM_CASTING_STATE = new HashMap<>();

        /**
         * 监听玩家周围的傀儡施法
         */
        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
            Player player = event.player;

            // 获取玩家周围的所有傀儡
            for (var golem : player.level().getEntitiesOfClass(AbstractGolemEntity.class, player.getBoundingBox().inflate(32))) {
                // 检查是否为 Golem Magicka 傀儡
                if (!(golem instanceof IGolemMagicka magicka)) continue;
                // 检查是否是玩家的傀儡
                if (!player.getUUID().equals(getGolemOwnerId(golem))) continue;

                GolemMagicData golemMagicData = magicka.magicka$getGolemMagicData();
                MagicData magicData = golemMagicData.getMagicData(); // 关键修复：获取傀儡的魔力数据

                boolean isCasting = magicData.isCasting();
                boolean wasCasting = GOLEM_CASTING_STATE.getOrDefault(golem.getUUID(), false);

                if (isCasting && !wasCasting) {
                    // 1. 为傀儡恢复魔力
                    float maxMana = (float) golem.getAttributeValue(AttributeRegistry.MAX_MANA.get());
                    float currentMana = magicData.getMana();
                    float newMana = Math.min(currentMana + 20.0F, maxMana);
                    magicData.setMana(newMana);

                    // 2. 在傀儡身上标记下次攻击增伤，而非玩家
                    golem.getPersistentData().putLong("CullNextHitBonus", golem.level().getGameTime());
                }
                GOLEM_CASTING_STATE.put(golem.getUUID(), isCasting);
            }
        }

        /**
         * 监听傀儡造成的伤害，触发增伤
         */
        @SubscribeEvent
        public void onLivingHurt(LivingHurtEvent event) {
            // 判断攻击来源是否为实体
            if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

            // 关键修复：直接检查攻击者（傀儡）身上的增伤标记
            if (!(attacker instanceof AbstractGolemEntity<?, ?>)) return;

            long currentTime = attacker.level().getGameTime();
            long bonusTime = attacker.getPersistentData().getLong("CullNextHitBonus");

            if (bonusTime == 0 || currentTime - bonusTime > 40) return; // 2秒内有效

            // 移除标记并应用伤害增幅
            attacker.getPersistentData().remove("CullNextHitBonus");
            event.setAmount(event.getAmount() * 1.2F);
        }

        /**
         * 获取傀儡的主人 UUID
         */
        private static UUID getGolemOwnerId(LivingEntity entity) {
            if (entity instanceof AbstractGolemEntity<?, ?> golem) {
                return golem.getOwnerUUID();
            }
            // 备用反射
            try {
                var method = entity.getClass().getMethod("getOwnerUUID");
                return (UUID) method.invoke(entity);
            } catch (Exception ignored) {}
            return null;
        }
    }
}