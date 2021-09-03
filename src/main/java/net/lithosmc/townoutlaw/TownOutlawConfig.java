package net.lithosmc.townoutlaw;

import me.Silverwolfg11.CommentConfig.annotations.Comment;
import me.Silverwolfg11.CommentConfig.annotations.ConfigVersion;
import me.Silverwolfg11.CommentConfig.annotations.Node;
import me.Silverwolfg11.CommentConfig.annotations.SerializableConfig;
import me.Silverwolfg11.CommentConfig.node.ParentConfigNode;
import me.Silverwolfg11.CommentConfig.serialization.ClassDeserializer;
import me.Silverwolfg11.CommentConfig.serialization.ClassSerializer;
import me.Silverwolfg11.CommentConfig.serialization.NodeSerializer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@ConfigVersion(1.0)
@SerializableConfig
@Comment({"---####--- Town Outlaw Config ----###---",
          "Developed by Silverwolfg11", ""})
public class TownOutlawConfig {

    @Node("disable-flight")
    @Comment("Disable flight while a resident is in an outlawed town?")
    private boolean disableFlight = true;

    public boolean disableFlight() {
        return disableFlight;
    }

    @Node("outlaw-jail-time")
    @Comment({"How long should a player be jailed for if they die in an outlawed town?",
               "Set to 0 to disable.",
               "(in seconds)"})
    private long jailTime = 5 * 60;

    public long getJailTime() {
        return jailTime;
    }

    @Node("outlaw-jail-protect-time")
    @Comment({"How long should a player be protected (prevented) from being jailed again",
            "if they die in an outlaw town?",
            "Set to 0 to disable.",
            "(in seconds)"})
    private long jailProtectTime = 2 * 60;

    public long getJailProtectTime() {
        return jailProtectTime;
    }

    @Node({"hooks", "disable-god"})
    @Comment({"Disable god mode while a resident is in an outlawed town?", "Requires the Essentials plugin!"})
    private boolean disableGod = true;

    public boolean disableGod() {
        return disableGod;
    }

    public static TownOutlawConfig loadConfig(File directory, Logger errorLogger) throws IOException {

        if (!directory.exists())
            directory.mkdir();

        File configFile = new File(directory, "config.yml");

        NodeSerializer serializer = new NodeSerializer();
        if (!configFile.exists()) {
            configFile.createNewFile();
            // Use save config mapping
            TownOutlawConfig config = new TownOutlawConfig();
            ParentConfigNode node = ClassSerializer.serializeClass(config);
            serializer.serializeToFile(configFile, node);
            return config;
        }
        else {
            ClassDeserializer deserializer = new ClassDeserializer();
            deserializer.setErrorLogger(errorLogger);
            return deserializer.deserializeClassAndUpdate(configFile, TownOutlawConfig.class, serializer);
        }

    }
}
