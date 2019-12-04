package ru.geekbrains.javaone.lesson05.hw;

public class Dog extends Animal {
    private static final float RUN_MAX = 2000;
    private static final float JUMP_MAX = 1.5f;
    private static final float SWIM_MAX = 200;

    protected float maxRunDistance = 500;
    private float maxJumpHeigth = 0.5f;
    private float maxSwimDistance = 10;

    public Dog(String name, String color) { super(name, color); }

    public Dog(String name, String color, float maxRunDistance) {
        super(name, color);
        setMaxRunDistance(maxRunDistance);
    }

    public Dog(String name, String color,
               float maxRunDistance, float maxJumpHeigth) {
        super(name, color);
        setMaxRunDistance(maxRunDistance);
        setMaxJumpHeigth(maxJumpHeigth);
    }

    public Dog(String name, String color, float maxRunDistance,
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
        else if (maxRunDistance/distance >= 2)
            return name + " runs for "
                    + distance + " meters and runs back :)";
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
