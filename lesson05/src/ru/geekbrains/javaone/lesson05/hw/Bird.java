package ru.geekbrains.javaone.lesson05.hw;

public class Bird extends Animal {
    public Bird(String name, String color) {
        super(name, color);
    }

    @Override
    protected String run(int distance) {
        return name + " make little jumps for " + distance + " meters";
    }

    @Override
    protected String jump(int heigth) {
        return name + " jumps and... flies away";
    }

    @Override
    protected String swim(int distance) {
        return name + " looks disapproved";
    }
}
