package com.ruinscraft.motd;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public interface MotdStorage {

    Random MOTD_RANDOM = new Random();

    Callable<List<String>> getFirstLines();

    Callable<List<String>> getSecondLineAnnouncements();

    Callable<List<String>> getSecondLineTagLines();

    Callable<List<String>> getSecondLineTips();

    default Callable<String> getRandomFirstLine() {
        return () -> {
            List<String> firstLines = getFirstLines().call();
            if (firstLines.isEmpty()) return "";
            return firstLines.get(MOTD_RANDOM.nextInt(firstLines.size()));
        };
    }

    default Callable<String> getRandomSecondLine() {
        return () -> {
            List<String> secondLineAnnouncements = getSecondLineAnnouncements().call();

            if (!secondLineAnnouncements.isEmpty()) {
                return secondLineAnnouncements.get(MOTD_RANDOM.nextInt(secondLineAnnouncements.size()));
            }

            boolean showTip = MOTD_RANDOM.nextBoolean();

            if (showTip) {
                List<String> secondLineTips = getSecondLineTips().call();
                if (secondLineTips.isEmpty()) return "";
                return secondLineTips.get(MOTD_RANDOM.nextInt(secondLineTips.size()));
            } else {
                List<String> secondLineTagLines = getSecondLineTagLines().call();
                if (secondLineTagLines.isEmpty()) return "";
                return secondLineTagLines.get(MOTD_RANDOM.nextInt(secondLineTagLines.size()));
            }
        };
    }

    default void shutdown() {}

}
