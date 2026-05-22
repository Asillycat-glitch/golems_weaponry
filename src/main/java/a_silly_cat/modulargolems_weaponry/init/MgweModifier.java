package a_silly_cat.modulargolems_weaponry.init;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

import com.tterrag.registrate.util.entry.RegistryEntry;
import a_silly_cat.modulargolems_weaponry.modifier.KillstreakModifier;

public class MgweModifier {
    public static final RegistryEntry<KillstreakModifier> Kill_streak;
    // 移除 ProficiencyModifier 注册
    // public static final RegistryEntry<ProficiencyModifier> PROFICIENCY_TEMPLATE;

    static {
        Kill_streak = reg("kill_streak", KillstreakModifier::new, "kill_streak", "");
    }

    public static void register() {
        // 仅用于触发类加载
    }
}


