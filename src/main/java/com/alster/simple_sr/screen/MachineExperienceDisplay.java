package com.alster.simple_sr.screen;

public final class MachineExperienceDisplay {
    private MachineExperienceDisplay() { }
    public static int level(int experience) {
        int level = 0;
        while (experience >= pointsForNextLevel(level)) { experience -= pointsForNextLevel(level); level++; }
        return level;
    }
    public static float progress(int experience) {
        int level = 0;
        while (experience >= pointsForNextLevel(level)) { experience -= pointsForNextLevel(level); level++; }
        return Math.min(1.0F, (float) experience / pointsForNextLevel(level));
    }
    private static int pointsForNextLevel(int level) { return level <= 15 ? 2 * level + 7 : level <= 30 ? 5 * level - 38 : 9 * level - 158; }
}
