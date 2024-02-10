package damir;

import damir.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Scanner;

public class FillPropValues {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите ID категории: ");
        Long categoryId = Long.valueOf(scanner.nextLine());

        System.out.print("Введите ID продукта: ");
        Long productId = Long.valueOf(scanner.nextLine());

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            Category category = manager.find(Category.class, categoryId);
            for (int i = 0; i < category.getProperties().size(); i++) {
                PropValues propValue = new PropValues();
                System.out.print(category.getProperties().get(i).getName() + ": ");
                String property = scanner.nextLine();
                propValue.setValue(property);
                propValue.setProduct(manager.find(Product.class, productId));
                propValue.setProperty(category.getProperties().get(i));
                manager.persist(propValue);
            }
            manager.getTransaction().commit();
        }catch (Exception e){
            System.out.println(e.getMessage());
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }

        factory.close();
        manager.close();
    }
}
