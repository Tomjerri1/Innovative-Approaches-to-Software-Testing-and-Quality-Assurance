package com.example.triangle;

import java.util.Scanner;

/** Console program: reads three sides and outputs the triangle's characteristics. */
public final class Main {

    private static final int SIDES_COUNT = 3;

    private Main() {
    }

    /**
     * Entry point. Side lengths can be passed as arguments or entered via the keyboard.
     * @param args three side lengths (optional); decimal separator is a dot
     */
    public static void main(String[] args) {
        try {
            String[] tokens = args.length == SIDES_COUNT ? args : readSidesFromConsole();
            Triangle triangle = parse(tokens);
            printReport(triangle);
        } catch (NumberFormatException e) {
            System.err.println("Помилка: сторони мають бути числами (наприклад, 3 4.5 5).");
        } catch (IllegalArgumentException e) {
            System.err.println("Помилка: " + e.getMessage());
        }
    }

    private static String[] readSidesFromConsole() {
        System.out.println("Введіть три сторони трикутника через пробіл:");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        String[] tokens = line.split("\\s+");
        if (tokens.length != SIDES_COUNT) {
            throw new IllegalArgumentException("потрібно рівно три числа");
        }
        return tokens;
    }

    private static Triangle parse(String[] tokens) {
        return new Triangle(
                Double.parseDouble(tokens[0]),
                Double.parseDouble(tokens[1]),
                Double.parseDouble(tokens[2]));
    }

    private static void printReport(Triangle triangle) {
        System.out.println(triangle);
        System.out.println("Периметр: " + triangle.getPerimeter());
        System.out.println("Площа: " + triangle.getArea());
        System.out.println("Рівносторонній: " + triangle.isEquilateral());
        System.out.println("Рівнобедрений: " + triangle.isIsosceles());
        System.out.println("Прямокутний: " + triangle.isRight());
    }
}
