package damir;

public class Main {
    public static void main(String[] args) {
        boolean isWork = true;
        while(isWork){
            menu();

        }
    }
    public static void menu(){
        System.out.println("""
                Выберите операцию:\s
                1) Создать категорию
                2) Создать продукт
                3) Удалить продукт
                4) Заполнить свойства
                5) Увеличить цены
                6) Обновить продукт
                """);
    }
}
