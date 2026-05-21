package a_silly_cat.modulargolems_weaponry.init;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

import com.tterrag.registrate.util.entry.RegistryEntry;
import a_silly_cat.modulargolems_weaponry.modifier.KillstreakModifier;

public class GoarModifier {
    public static final RegistryEntry<KillstreakModifier> Kill_streak;
    static {
        Kill_streak = reg("Kill_streak",KillstreakModifier::new
        ,"kill_streak",
        "");
    }
}



