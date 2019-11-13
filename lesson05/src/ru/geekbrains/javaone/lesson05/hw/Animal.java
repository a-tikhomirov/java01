package ru.geekbrains.javaone.lesson05.hw;

public abstract class Animal {
    protected String name;
    protected String color;

    protected Animal(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    protected String run(int distance) {
        return name + " runs " + distance + " meters";
    }

    protected String jump(int heigth) {
        return name + " jumps " + heigth + " meters high";
    }

    protected String swim(int distance) {
        return name + " swims " + distance + " meters";
    }
}
