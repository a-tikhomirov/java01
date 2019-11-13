package ru.geekbrains.javaone.lesson05.hw;

public class Cat extends Animal {
    public Cat(String name, String color) {
        super(name, color);
    }

    @Override
    protected String swim(int distance) {
        return name + " swims back to land" ;
    }
}
