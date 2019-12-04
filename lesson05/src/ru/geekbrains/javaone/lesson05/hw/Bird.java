package ru.geekbrains.javaone.lesson05.hw;

public class Bird extends Animal {
    private static final float RUN_MAX = 5.5f;

    private float maxRunDistance = 5;
    private float maxJumpHeigth = 0.2f;

    public Bird(String name, String color) {
        super(name, color);
    }

    public Bird(String name, String color, float maxRunDistance) {
        super(name, color);
        setMaxRunDistance(maxRunDistance);
    }

    public void setMaxRunDistance(float maxRunDistance) {
        this.maxRunDistance =
                maxRunDistance > RUN_MAX ? RUN_MAX : maxRunDistance;
    }

    public String voice() {
        return name + " whistles";
    }

    @Override
    public String run(float distance) {
        return name + " makes little jumps for "
                + ((distance < maxRunDistance) ? distance : maxRunDistance) + " meters";
    }

    @Override
    public String jump(float heigth) {
        if (heigth > maxJumpHeigth)
            return voice();
        else
            return super.jump(heigth);
    }

    @Override
    public String swim(float distance) {
        return name + " flies away";
    }
}
