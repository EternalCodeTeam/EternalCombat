package com.eripe14.combatlog.config;


import net.dzikoysk.cdn.source.Resource;

import java.io.File;

public interface ReloadableConfig {

    Resource resource(File folder);

}

