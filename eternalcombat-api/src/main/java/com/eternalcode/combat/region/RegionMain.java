package com.eternalcode.combat.region;

import java.util.List;
import java.util.TreeSet;

public class RegionMain {

    public static void main(String[] args) {
        TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        set.addAll(List.of("Rollczi", "Rollczi2", "Rollczi3"));

        if (!set.subSet("Rollczi", "Rollczi").isEmpty()) {
            System.out.println("Rollczi is in the set");
        }

        if (!set.subSet("Rollczi2", "Rollczi2").isEmpty()) {
            System.out.println("Rollczi2 is in the set");
        }
        System.out.println("Rollczi3 is in the set");
    }

}
