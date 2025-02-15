package com.eternalcode.combat.border;

import java.util.stream.Stream;

public interface BorderResult {

    BorderResult EMPTY = () -> Stream.empty();

    Stream<BorderPoint> stream();

}
