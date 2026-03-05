package com.eternalcode.combat.config.implementation;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class DurationFormatSettings extends OkaeriConfig {

    @Comment({
        "# Pattern used to format durations.",
        "# Placeholders:",
        "#  %d{singular|plural} - days",
        "#  %h{singular|plural} - hours",
        "#  %m{singular|plural} - minutes",
        "#  %s{singular|plural} - seconds",
        "# Example:",
        "#  %d{day|days} %h{hour|hours} %m{minute|minutes} %s{second|seconds}"
    })
    public String pattern = "%d{day|days} %h{hour|hours} %m{minute|minutes} %s{second|seconds}";

    @Comment({
        "# Separator used between duration parts.",
        "# Example result:",
        "#  1 hour, 2 minutes, 5 seconds"
    })
    public String separator = ", ";

    @Comment({
        "# Separator used before the last duration part.",
        "# Example result:",
        "#  1 hour, 2 minutes and 5 seconds"
    })
    public String lastSeparator = " and ";

    @Comment({"# Text displayed when duration is zero or negative."})
    public String zero = "<1 second";
}
