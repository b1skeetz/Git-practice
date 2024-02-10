package damir;

import damir.entity.Category;
import damir.entity.Product;
import damir.entity.PropValues;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Scanner;

public class DeleteProductMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите ID товара для удаления: ");
        Long productId = Long.valueOf(scanner.nextLine());

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        Product product = manager.find(Product.class, productId);
        Category category = product.getCategory();

        try{ //liquibase
            manager.getTransaction().begin();
            for (int i = 0; i < category.getProperties().size(); i++) {
                manager.remove(product.getPropValues().get(i));
            }
            manager.remove(product);
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