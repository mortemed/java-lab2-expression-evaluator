package ru.vsu.polichnoy.lab2;

import java.util.Scanner;

/**
 * Консольная программа для демонстрации разбора и вычисления выражений.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        while (running) {
            printMenu();

            String command = readLine(scanner, "Выберите команду: ");

            switch (command) {
                case "1":
                    System.out.println("Вычисление выражений будет реализовано на следующих этапах. :3 ");
                    break;
                case "2":
                    printPlannedFeatures();
                    break;
                case "0":
                    running = false;
                    System.out.println("Работа программы завершена.");
                    break;
                default:
                    System.out.println("Неизвестная команда.");
            }

            System.out.println();
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=== Разбор и вычисление выражения ===");
        System.out.println("1. Ввести выражение и вычислить значение");
        System.out.println("2. Показать поддерживаемый и планируемый функционал");
        System.out.println("0. Выйти");
    }

    private static void printPlannedFeatures() {
        System.out.println("Планируемый функционал:");
        System.out.println("- целые и дробные числа");
        System.out.println("- операции +, -, *, /, ^");
        System.out.println("- скобки");
        System.out.println("- унарный плюс и унарный минус");
        System.out.println("- переменные с запросом значения");
        System.out.println("- функции sin, cos, tan, sqrt, abs, ln, log");
        System.out.println("- константы pi и e");
        System.out.println("- сообщения об ошибках при некорректном выражении.");
    }

    private static String readLine(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}