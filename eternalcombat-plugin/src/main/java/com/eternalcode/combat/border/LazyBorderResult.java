package com.eternalcode.combat.border;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class LazyBorderResult implements BorderResult {

    private final List<Supplier<List<BorderPoint>>> borderPoints = new ArrayList<>();

    void addLazyBorderPoints(Supplier<List<BorderPoint>> supplier) {
        borderPoints.add(supplier);
    }

    boolean isEmpty() {
        return borderPoints.isEmpty();
    }

    @Override
    public Stream<BorderPoint> parallelStream() {
        if (borderPoints.isEmpty()) {
            return Stream.empty();
        }

        Iterable<BorderPoint> iterable = () -> new LazyBorderResultIterator();
        return StreamSupport.stream(iterable.spliterator(), true);
    }

    private class LazyBorderResultIterator implements Iterator<BorderPoint> {
        private int currentSupplierIndex = 0;
        private Iterator<BorderPoint> currentIterator = borderPoints.get(0).get().iterator();

        @Override
        public boolean hasNext() {
            if (currentIterator.hasNext()) {
                return true;
            }

            if (currentSupplierIndex >= borderPoints.size() - 1) {
                return false;
            }

            currentSupplierIndex++;
            currentIterator = borderPoints.get(currentSupplierIndex).get().iterator();
            return currentIterator.hasNext();

        }

        @Override
        public BorderPoint next() {
            return currentIterator.next();
        }
    }

}
