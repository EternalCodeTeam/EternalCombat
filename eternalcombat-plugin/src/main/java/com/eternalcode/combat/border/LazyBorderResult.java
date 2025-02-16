package com.eternalcode.combat.border;

import dev.rollczi.litecommands.shared.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
class LazyBorderResult implements BorderResult {

    private final List<Lazy<List<BorderPoint>>> borderPoints = new ArrayList<>();

    void addLazyBorderPoints(Lazy<List<BorderPoint>> supplier) {
        borderPoints.add(supplier);
    }

    boolean isEmpty() {
        return borderPoints.isEmpty();
    }

    @Override
    public Set<BorderPoint> collect() {
        return borderPoints.stream()
            .flatMap(result -> result.get().stream())
            .collect(Collectors.toUnmodifiableSet());
    }

}
