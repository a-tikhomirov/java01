package ru.geekbrains.javaone.lesson05.hw;

public class Lesson05HW {
    private static void printPetsInfo(Animal[] pets) {
        for (int i = 1; i <= pets.length; i++)
        {
            System.out.printf("Our %d pet is %s. Name: %s\t Color: %s\n",
                    i, pets[i-1].getClass().getSimpleName(), pets[i-1].getName(), pets[i-1].getColor());
        }
    }

    private static void makeActivity(Animal[] pets) {
        int runDistanse = 100;
        int jumpHeight = 1;
        int swimDistance = 10;
        for (Animal pet : pets) {
            System.out.println(pet.run(runDistanse));
            System.out.println(pet.jump(jumpHeight));
            System.out.println(pet.swim(swimDistance));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Animal[] pets = {
                new Dog("Charlie", "Dark-brown"),
                new Horse("Caesarus","Light-brown"),
                new Bird("Phoenix","Yellow-red"),
                new Cat("Baiyun","Gray")
        };

        printPetsInfo(pets);
        System.out.println();
        makeActivity(pets);
    }
}
