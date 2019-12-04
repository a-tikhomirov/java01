package ru.geekbrains.javaone.lesson05.hw;

public class Cat extends Animal {
    private static final float RUN_MAX = 500;
    private static final float JUMP_MAX = 4.5f;

    private float maxRunDistance = 200;
    private float maxJumpHeigth = 2;

    public Cat(String name, String color) {
        super(name, color);
    }

    public Cat(String name, String color, float maxRunDistance) {
        super(name, color);
        setMaxRunDistance(maxRunDistance);
    }

    public Cat(String name, String color,
               float maxRunDistance, float maxJumpHeigth) {
        super(name, color);
        setMaxRunDistance(maxRunDistance);
        setMaxJumpHeigth(maxJumpHeigth);
    }

    public void setMaxRunDistance(float maxRunDistance) {
        this.maxRunDistance =
                maxRunDistance > RUN_MAX ? RUN_MAX : maxRunDistance;
    }

    public void setMaxJumpHeigth(float maxJumpHeigth) {
        this.maxJumpHeigth =
                maxJumpHeigth > JUMP_MAX ? JUMP_MAX : maxJumpHeigth;
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
    protected String swim(float distance) {
        return name + " looks very dissapointed" ;
    }
}
