package a_silly_cat.modulargolems_weaponry.init;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

import com.tterrag.registrate.util.entry.RegistryEntry;
import a_silly_cat.modulargolems_weaponry.modifier.KillstreakModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;
import a_silly_cat.modulargolems_weaponry.modifier.ProficiencyModifier;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class MgweModifier {
    public static final RegistryEntry<KillstreakModifier> Kill_streak;
    public static final RegistryEntry<ProficiencyModifier> PROFICIENCY_TEMPLATE;
    static {
        Kill_streak = reg("kill_streak",KillstreakModifier::new
        ,"kill_streak",
        "");
        PROFICIENCY_TEMPLATE = reg("proficiency_template", () -> new ProficiencyModifier("", 1, 0.0, 0, 0.0f), "proficiency_template", "A proficiency template.");
    }
    public static void register() {
        // 仅用于触发类加载，执行 static 块
    }
}



