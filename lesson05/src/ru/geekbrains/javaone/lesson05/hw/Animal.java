package ru.geekbrains.javaone.lesson05.hw;

public abstract class Animal {
    protected String name;
    protected String color;

    protected Animal(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
