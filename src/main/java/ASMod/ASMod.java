package ASMod;

import ASMod.util.IDCheckDontTouchPls;
import ASMod.util.TextureLoader;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

@SpireInitializer
public class ASMod implements
        EditStringsSubscriber,
        PostInitializeSubscriber
{

    public static final Logger logger = LogManager.getLogger(ASMod.class.getName());
    private static String modID;

    public static Properties ASModDefaultSettings = new Properties();

    private static final String MODNAME = "Ascension Unlocker";
    private static final String AUTHOR = "squeeny";
    private static final String DESCRIPTION = "Unlocks A20 for all characters, modded or otherwise.";

    public static final String BADGE_IMAGE = "ASModResources/images/Badge.png";

    public static String makeAudioPath(String resourcePath) {
        return getModID() + "Resources/audio/" + resourcePath;
    }

    public ASMod() {
        logger.info("Subscribe to BaseMod hooks");
        BaseMod.subscribe(this);
        setModID("ASMod");
        logger.info("Done subscribing");

        logger.info("Done adding mod settings");
    }

    public static void setModID(String ID) {
        Gson coolG = new Gson();
        InputStream in = ASMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json");
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class);
        logger.info("You are attempting to set your mod ID as: " + ID);
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) {
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION);
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) {
            modID = EXCEPTION_STRINGS.DEFAULTID;
        } else {
            modID = ID;
        }
        logger.info("Success! ID is " + modID);
    }

    public static String getModID() {
        return modID;
    }

    private static void pathCheck() {
        Gson coolG = new Gson();
        InputStream in = ASMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json");
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class);
        String packageName = ASMod.class.getPackage().getName();
        FileHandle resourcePathExists = Gdx.files.internal(getModID() + "Resources");
        if (!modID.equals(EXCEPTION_STRINGS.DEVID)) {
            if (!packageName.equals(getModID())) {
                throw new RuntimeException(EXCEPTION_STRINGS.PACKAGE_EXCEPTION + getModID());
            }
            if (!resourcePathExists.exists()) {
                throw new RuntimeException(EXCEPTION_STRINGS.RESOURCE_FOLDER_EXCEPTION + getModID() + "Resources");
            }
        }
    }

    @SuppressWarnings("unused")
    public static void initialize() {
        logger.info("========================= Initializing ASMod  =========================");
        ASMod asmod = new ASMod();
        logger.info("========================= a fleeting dream =========================");

        try {

            int i;
            int j;
            logger.info("Setting default values.");
            SpireConfig config = new SpireConfig("asmod", "ASModConfig", ASModDefaultSettings);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void receivePostInitialize() {

        logger.info("Loading badge image and mod options");
        try {
            CreatePanel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unlockAscensionLevels(20);

    }

    @Override
    public void receiveEditStrings() {

        logger.info("Beginning to edit strings for mod with ID: " + getModID());

        BaseMod.loadCustomStringsFile(UIStrings.class,
                getModID() + "Resources/localization/eng/ASMod-Ui-Strings.json");

        logger.info("Strings are done.");
    }

    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }


    private void CreatePanel() throws IOException {
        SpireConfig spireConfig = new SpireConfig("asmod", "ASModConfig");
        ModPanel settingsPanel = new ModPanel();
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("CreatePanelMokou"));
        String[] TEXT = uiStrings.TEXT;
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
    }

    public static void unlockAscensionLevels(int level) {

        try {
            ArrayList<Prefs> prefs = CardCrawlGame.characterManager.getAllPrefs();
            for (Prefs pref : prefs) {
                UnlockTracker.unlockAchievement("ASCEND_0");
                if (pref.getInteger("WIN_COUNT", 0) == 0)
                    pref.putInteger("WIN_COUNT", 1);
                if (level < 1) {
                    level = 1;
                } else if (level > 20) {
                    level = 20;
                }
                pref.putInteger("ASCENSION_LEVEL", level);
                BaseMod.logger.info("ASCENSION LEVEL IS NOW: " + level);
                pref.flush();
            }
        } catch (Exception Exception) {
            BaseMod.logger.info( " NOT FOUND");
        }
    }

}
