package ru.geekbrains.javaone.lesson05.hw;

public abstract class Animal {
    protected String name;
    protected String color;

    protected Animal(String name, String color) {
        this.name = name;
        this.color = color;
    }

    protected String getName() {
        return name;
    }

    protected String getColor() {
        return color;
    }

    protected String run(float distance) {
        return name + " runs " + distance + " meters";
    }

    protected String jump(float heigth) {
        return name + " jumps " + heigth + " meters high";
    }

    protected String swim(float distance) {
        return name + " swims " + distance + " meters";
    }
}
