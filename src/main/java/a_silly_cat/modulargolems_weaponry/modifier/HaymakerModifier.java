package a_silly_cat.modulargolems_weaponry.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.BiConsumer;

public class HaymakerModifier extends GolemModifier {

    public static final double MAX_STORED_DAMAGE_PERCENT = 0.5;
    public static final int STORED_DAMAGE_DECAY_DELAY = 80;
    public static final double ATTACK_RANGE = 6.0;
    public static final double ATTACK_ANGLE = 120.0;

    private static boolean eventRegistered = false;

    public HaymakerModifier() {
        super(StatFilterType.HEALTH, 3);
        if (!eventRegistered) {
            MinecraftForge.EVENT_BUS.register(HaymakerGoal.class);
            eventRegistered = true;
        }
    }

    @Override
    public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
        addGoal.accept(1, new HaymakerGoal(entity, lv));
    }
}