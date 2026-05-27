package a_silly_cat.modulargolems_weaponry.init;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

import a_silly_cat.modulargolems_weaponry.modifier.ArmorDistributionModifier;
import a_silly_cat.modulargolems_weaponry.modifier.HaymakerModifier; // 新增导入
import com.tterrag.registrate.util.entry.RegistryEntry;
import a_silly_cat.modulargolems_weaponry.modifier.KillstreakModifier;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;

public class MgweModifier {
    public static final RegistryEntry<KillstreakModifier> Kill_streak;
    public static final RegistryEntry<GolemModifier> ARMOR_DISTRIBUTION;
    // 新增注册项
    public static final RegistryEntry<HaymakerModifier> HAYMAKER;

    static {
        Kill_streak = reg("kill_streak", KillstreakModifier::new, "kill_streak", "");
        ARMOR_DISTRIBUTION = reg("armor_distribution", ArmorDistributionModifier::new, "armor_distribution", "");
        HAYMAKER = reg("haymaker", HaymakerModifier::new, "haymaker", "蓄意轰拳"); // 新增注册
    }

    public static void register() {
        // 仅用于触发类加载
    }
}