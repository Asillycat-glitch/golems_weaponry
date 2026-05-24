package a_silly_cat.modulargolems_weaponry.modifier;

import a_silly_cat.modulargolems_weaponry.init.MgweModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

public class ArmorDistributionModifier extends GolemModifier {

    public ArmorDistributionModifier() {
        // 为所有类型的傀儡生效，最大等级1
        super(StatFilterType.HEALTH, 1);
    }

    @Override
    public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
        // 注册装备分发 AI 目标，优先级设为 0 (最高)
        addGoal.accept(0, new ArmorDistributionGoal(entity, lv));
    }
}