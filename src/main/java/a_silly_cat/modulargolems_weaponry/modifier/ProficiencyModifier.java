// 文件路径: src/main/java/a_silly_cat/modulargolems_weaponry/modifier/ProficiencyModifier.java
package a_silly_cat.modulargolems_weaponry.modifier;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class ProficiencyModifier extends GolemModifier {

    private final String modifierId;
    private final double attackPerLevel;
    private final int thresholdLevel;
    private final float bonusDamageMultiplier;

    public ProficiencyModifier(String id, int maxLevel, double attackPerLevel, int thresholdLevel, float bonusDamageMultiplier) {
        super(StatFilterType.ATTACK, maxLevel);
        this.modifierId = id;
        this.attackPerLevel = attackPerLevel;
        this.thresholdLevel = thresholdLevel;
        this.bonusDamageMultiplier = bonusDamageMultiplier;
    }

    @Override
    public Component getTooltip(int level) {
        // 实现与之前类似，但需要根据实际需要调整
        return super.getTooltip(level);
    }

    @Override
    public List<MutableComponent> getDetail(int level) {
        // 返回正确的类型 List<MutableComponent>
        List<MutableComponent> details = new ArrayList<>();
        details.add(Component.translatable("modifier.mgwe.proficiency.detail", modifierId, attackPerLevel * level));
        if (level >= thresholdLevel) {
            details.add(Component.translatable("modifier.mgwe.proficiency.bonus_detail", bonusDamageMultiplier * 100));
        }
        return details;
    }

    @Override
    public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
        // 1. 添加固定的攻击力加成
        cache.addDealtModifier(DamageModifier.add((float) (attackPerLevel * level)));

        // 2. 如果等级达到阈值，添加额外的百分比伤害加成
        if (level >= thresholdLevel) {
            cache.addDealtModifier(DamageModifier.multTotal(1 + bonusDamageMultiplier));
        }
    }

    // Getter方法
    public String getModifierId() { return modifierId; }
    public double getAttackPerLevel() { return attackPerLevel; }
    public int getThresholdLevel() { return thresholdLevel; }
    public float getBonusDamageMultiplier() { return bonusDamageMultiplier; }
}