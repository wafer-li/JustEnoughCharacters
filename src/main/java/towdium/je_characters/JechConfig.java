package towdium.je_characters;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import towdium.je_characters.core.JechCore;
import towdium.je_characters.util.FeedFetcher;
import towdium.je_characters.util.VersionChecker;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Author:  Towdium
 * Created: 2016/6/14.
 */

public class JechConfig {
    public static Configuration config;
    public static Object empty;

    public static void preInit(File location) {
        config = new Configuration(new File(location, "config/JustEnoughCharacters.cfg"), JechCore.VERSION);
        config.load();
        handleFormerVersion();
        initProperties();
        setValue();
        config.save();
        fetchOnline();
    }

    public static void fetchOnline() {
        Thread t = new Thread(() ->
                FeedFetcher.fetch((s, r) -> {
                    HashSet<String> buf = new HashSet<>();
                    Collections.addAll(buf, EnumItems.ListAdditionalStringMatch.getProperty().getStringList());
                    buf.addAll(s);
                    EnumItems.ListAdditionalStringMatch.getProperty()
                            .set(buf.stream().sorted().collect(Collectors.toList()).toArray(new String[]{}));
                    buf.clear();
                    Collections.addAll(buf, EnumItems.ListAdditionalRegExpMatch.getProperty().getStringList());
                    buf.addAll(r);
                    EnumItems.ListAdditionalRegExpMatch.getProperty()
                            .set(buf.stream().sorted().collect(Collectors.toList()).toArray(new String[]{}));
                    config.save();
                }));
        t.setPriority(Thread.MIN_PRIORITY);
        t.run();
    }


    public static void setValue() {
        EnumItems.ListDefaultRegExpMatch.getProperty().set(((String[]) EnumItems.ListDefaultRegExpMatch.getDefault()));
        EnumItems.ListDefaultStringMatch.getProperty().set(((String[]) EnumItems.ListDefaultStringMatch.getDefault()));
    }

    public static void handleFormerVersion() {
        if (VersionChecker.checkVersion(config.getLoadedConfigVersion(), "1.12.0-1.10.0").toInt() < 0) {
            JechCore.LOG.info("Low version detected. Disabling radical.");
            EnumItems.EnableRadicalMode.getProperty().set(false);
        }
    }

    public static void initProperties() {
        for (EnumItems item : EnumItems.values()) {
            item.init();
        }
    }

    public static void save() {
        config.save();
    }

    public enum EnumItems {
        ListAdditionalStringMatch,
        ListAdditionalRegExpMatch,
        ListDefaultStringMatch,
        ListDefaultRegExpMatch,
        ListDumpClass,
        ListMethodBlacklist,
        EnableRadicalMode,
        EnableJEI;


        public String getComment() {
            switch (this) {
                case ListAdditionalStringMatch:
                    return "Give a list of methods to transform, of which uses \"String.contains\" to match strings.\n" +
                            "The format is \"full.class.path$InnerClass:methodName\"";
                case ListAdditionalRegExpMatch:
                    return "Give a list of methods to transform, of which uses regular expression to match strings.\n" +
                            "The format is \"full.class.path$InnerClass:methodName\"";
                case ListDefaultStringMatch:
                    return "Default list of methods to transform, of which uses \"String.contains\" to match strings.\n" +
                            "This list is maintained by the mod and will have no effect if you change it.";
                case ListDefaultRegExpMatch:
                    return "Default list of methods to transform, of which uses regular expression to match strings.\n" +
                            "This list is maintained by the mod and will have no effect if you change it.";
                case ListDumpClass:
                    return "Dump all the methods in this class into LOG.";
                case ListMethodBlacklist:
                    return "Put the strings in default list here to disable transform for certain method";
                case EnableRadicalMode:
                    return "Set to false to disable radical mode transform.\n" +
                            "When in radical mode, this mod will try to change every instance of \"contains\",\n" +
                            "so every mod using this method will be supported, while this could lead to\n" +
                            "unexpected problems and slow down the launching speed.";
                case EnableJEI:
                    return "In the 1.11 version of JEI, the text filtering mechanics has been changed to a prefix tree, \n" +
                            "which is incompatible with Chinese pinyin system. So I have to entirely change the behavior.\n" +
                            "Specifically, I'm using the old JEI's cached filter and inject it in.\n" +
                            "If anything wired happens, try to disable it.";
            }
            return "";
        }

        public String getCategory() {
            switch (this) {
                case ListAdditionalStringMatch:
                    return EnumCategory.General.toString();
                case ListAdditionalRegExpMatch:
                    return EnumCategory.General.toString();
                case ListDefaultStringMatch:
                    return EnumCategory.General.toString();
                case ListDefaultRegExpMatch:
                    return EnumCategory.General.toString();
                case ListDumpClass:
                    return EnumCategory.General.toString();
                case ListMethodBlacklist:
                    return EnumCategory.General.toString();
                case EnableRadicalMode:
                    return EnumCategory.General.toString();
                case EnableJEI:
                    return EnumCategory.General.toString();
            }
            return "";
        }

        public EnumType getType() {
            switch (this) {
                case ListAdditionalStringMatch:
                    return EnumType.ListString;
                case ListAdditionalRegExpMatch:
                    return EnumType.ListString;
                case ListDefaultStringMatch:
                    return EnumType.ListString;
                case ListDefaultRegExpMatch:
                    return EnumType.ListString;
                case ListDumpClass:
                    return EnumType.ListString;
                case ListMethodBlacklist:
                    return EnumType.ListString;
                case EnableRadicalMode:
                    return EnumType.Boolean;
                case EnableJEI:
                    return EnumType.Boolean;
            }
            return EnumType.Error;
        }

        public Object getDefault() {
            switch (this) {
                case ListAdditionalStringMatch:
                    return new String[0];
                case ListAdditionalRegExpMatch:
                    return new String[0];
                case ListDefaultStringMatch:
                    return new String[]{
                            "mezz.jei.ItemFilter$FilterPredicate:stringContainsTokens",
                            "com.raoulvdberge.refinedstorage.gui.grid.filtering.GridFilterName:accepts",
                            "com.raoulvdberge.refinedstorage.gui.grid.filtering.GridFilterTooltip:accepts",
                            "com.raoulvdberge.refinedstorage.gui.grid.filtering.GridFilterName:test",
                            "com.raoulvdberge.refinedstorage.gui.grid.filtering.GridFilterTooltip:test",
                            "com.rwtema.extrautils2.transfernodes.TileIndexer$ContainerIndexer$WidgetItemRefButton:lambda$getRef$0",
                            "crazypants.enderio.machine.invpanel.client.ItemFilter$ModFilter:matches",
                            "crazypants.enderio.machine.invpanel.client.ItemFilter$NameFilter:matches",
                            "vazkii.psi.client.gui.GuiProgrammer:shouldShow",
                            "vazkii.botania.client.gui.lexicon.GuiLexiconIndex:matchesSearch",
                            "de.ellpeck.actuallyadditions.mod.booklet.entry.BookletEntry:fitsFilter",
                            "de.ellpeck.actuallyadditions.mod.booklet.entry.BookletEntry:getChaptersForDisplay",
                            "com.zerofall.ezstorage.gui.client.GuiStorageCore:updateFilteredItems",
                            "io.github.elytra.copo.inventory.ContainerVT:updateSlots",
                            "io.github.elytra.copo.inventory.ContainerTerminal:updateSlots",
                            "mcjty.rftools.blocks.storage.GuiModularStorage:updateList",
                            "net.minecraft.client.gui.inventory.GuiContainerCreative:updateFilteredItems",
                            "appeng.client.gui.implementation.GuiInterfaceTerminal:refreshList",
                            "appeng.client.gui.implementation.GuiInterfaceTerminal:itemStackMatchesSearchTerm"
                    };
                case ListDefaultRegExpMatch:
                    return new String[]{
                            "appeng.client.me.ItemRepo:updateView",
                            "codechicken.nei.ItemList$PatternItemFilter:matches"
                    };
                case ListDumpClass:
                    return new String[0];
                case ListMethodBlacklist:
                    return new String[0];
                case EnableRadicalMode:
                    return false;
                case EnableJEI:
                    return true;
            }
            return JechConfig.empty;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Property init() {
            EnumType type = this.getType();
            if (type != null) {
                switch (this.getType()) {
                    case Boolean:
                        return config.get(this.getCategory(), this.toString(), (Boolean) this.getDefault(), this.getComment());
                    case ListString:
                        return config.get(this.getCategory(), this.toString(), (String[]) this.getDefault(), this.getComment());
                }
                config.getCategory(EnumCategory.General.toString()).get(this.toString());
            }
            return config.get(this.getCategory(), this.toString(), false, this.getComment());
        }

        public Property getProperty() {
            return config.getCategory(EnumCategory.General.toString()).get(this.toString());
        }
    }

    public enum EnumCategory {
        General;

        @Override
        public String toString() {
            switch (this) {
                case General:
                    return "general";
                default:
                    return "";
            }
        }
    }

    public enum EnumType {Boolean, ListString, Error}
}

