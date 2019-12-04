package ru.geekbrains.javaone.lesson05.hw;

public class HunterDog extends Dog {
    private static final float RUN_MAX = 3000;

    public HunterDog(String name, String color) {
        super(name, color);
    }

    public HunterDog(String name, String color, float maxRunDistance) {
        super(name, color);
        setMaxRunDistance(maxRunDistance);
    }

    @Override
    public void setMaxRunDistance(float maxRunDistance) {
        this.maxRunDistance =
                maxRunDistance > RUN_MAX ? RUN_MAX : maxRunDistance;
    }
}
