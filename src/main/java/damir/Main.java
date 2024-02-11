package damir;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean isWork = true;
        Scanner scanner = new Scanner(System.in);
        while (isWork) {
            menu();
            int operation = Integer.parseInt(scanner.nextLine());
            switch (operation) {
                case 1 -> Functions.createCategory();
                case 2 -> Functions.createProduct();
                case 3 -> Functions.deleteProduct();
                case 4 -> Functions.fillProperties();
                case 5 -> {
                    System.out.print("Введите процент увеличения от 0 до 100: ");
                    int percent = Integer.parseInt(scanner.nextLine());
                    if (percent > 0 && percent <= 100) {
                        Functions.increasePrice(percent);
                    } else {
                        System.out.println("Некорректное значение процента");
                    }
                }
                case 6 -> Functions.updateProduct();
                case 7 -> {
                    System.out.print("Вы уверены что хотите выйти? [y/n]: ");
                    String answer = scanner.nextLine();
                    if(answer.equals("y") || answer.equals("Y")){
                        System.out.println("Завершение работы...");
                        isWork = false;
                    } else if(answer.equals("n") || answer.equals("N")) {
                        System.out.println("Продолжаем...");
                    }
                }
                default -> System.out.println("Введена неверная операция!");
            }
        }
    }

    public static void menu() {
        System.out.print("""
                Выберите операцию:\s
                1) Создать категорию
                2) Создать продукт
                3) Удалить продукт
                4) Заполнить свойства
                5) Увеличить цены
                6) Обновить продукт
                7) Выход
                """);
    }
}