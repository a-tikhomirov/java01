package ru.geekbrains.javaone.lesson05.hw;

public class Lesson05HW {
    private static void printPetsInfo(Animal[] pets) {
        for (int i = 1; i <= pets.length; i++)
        {
            System.out.printf("Our %d pet is %s. Name: %s\t Color: %s\n",
                    i, pets[i-1].getClass().getSimpleName(), pets[i-1].getName(), pets[i-1].getColor());
        }
    }

    private static void makeActivity(Animal[] pets, float runDistanse, float jumpHeight, float swimDistance) {
        for (Animal pet : pets) {
            System.out.printf("Ask %s to run for %.1f meters:\n", pet.name, runDistanse);
            System.out.println(pet.run(runDistanse));
            System.out.printf("Ask %s to jump %.1f meters high:\n", pet.name, jumpHeight);
            System.out.println(pet.jump(jumpHeight));
            System.out.printf("Ask %s to swim for %.1f meters:\n", pet.name, swimDistance);
            System.out.println(pet.swim(swimDistance));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Animal[] pets = {
                new Dog("Charlie", "Dark-brown"),
                new Dog("Nessy", "White", 800, 1, 200),
                new Horse("Caesarus","Light-brown"),
                new Horse("Aphina","Black", 10000, 4),
                new Bird("Nevilichka","Yellow-gray"),
                new Cat("Baiyun","Gray"),
                new HunterDog("Laika", "Black", 3000)
        };

        printPetsInfo(pets);
        System.out.println("\n");
        makeActivity(pets, 400, 3, 15);
        System.out.println("\n");
        makeActivity(pets, 3000, 4, 300);

    }
}
