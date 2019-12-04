package ru.geekbrains.javaone.lesson05.hw;

public class Horse extends Animal {
    private static final float RUN_MAX = 10000;
    private static final float JUMP_MAX = 3.5f;
    private static final float SWIM_MAX = 400;

    private float maxRunDistance = 1500;
    private float maxJumpHeigth = 3;
    private float maxSwimDistance = 100;

    public Horse(String name, String color) {
        super(name, color);
    }

    public Horse(String name, String color, float maxRunDistance) {
        super(name, color);
        setMaxRunDistance(maxRunDistance);
    }

    public Horse(String name, String color,
               float maxRunDistance, float maxJumpHeigth) {
        super(name, color);
        setMaxRunDistance(maxRunDistance);
        setMaxJumpHeigth(maxJumpHeigth);
    }

    public Horse(String name, String color, float maxRunDistance,
               float maxJumpHeigth, float maxSwimDistance) {
        super(name, color);
        setMaxRunDistance(maxRunDistance);
        setMaxJumpHeigth(maxJumpHeigth);
        setMaxSwimDistance(maxSwimDistance);
    }

    public void setMaxRunDistance(float maxRunDistance) {
        this.maxRunDistance =
                maxRunDistance > RUN_MAX ? RUN_MAX : maxRunDistance;
    }

    public void setMaxJumpHeigth(float maxJumpHeigth) {
        this.maxJumpHeigth =
                maxJumpHeigth > JUMP_MAX ? JUMP_MAX : maxJumpHeigth;
    }

    public void setMaxSwimDistance(float maxSwimDistance) {
        this.maxSwimDistance =
                maxSwimDistance > SWIM_MAX ? SWIM_MAX : maxSwimDistance;
    }

    @Override
    public String run(float distance) {
        if (distance > maxRunDistance)
            return name + " runs only for "
                    + maxRunDistance + " meters";
        else
            return super.run(distance);
    }

    @Override
    public String jump(float heigth) {
        if (heigth > maxJumpHeigth)
            return name + " jumps only for "
                    + maxJumpHeigth + " meters high";
        else
            return super.jump(heigth);
    }

    @Override
    public String swim(float distance) {
        if (distance > maxSwimDistance)
            return name + " swims only for "
                    + maxSwimDistance + " meters";
        else
            return super.swim(distance);
    }
}
