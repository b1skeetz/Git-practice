package damir;

import damir.entity.Category;
import damir.entity.Product;
import jakarta.persistence.*;

import java.util.List;
import java.util.Scanner;

public class PriceIncrease {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Список всех категорий: ");
        TypedQuery<Category> categoryTypedQuery = manager.createQuery("select c from Category c", Category.class);
        List<Category> categories = categoryTypedQuery.getResultList();
        for (int i = 0; i < categories.size(); i++) {
            System.out.println(i + 1 + ") " + categories.get(i).getName());
        }

        System.out.print("Выберите категорию: ");
        int selectedCategory = Integer.parseInt(scanner.nextLine()) - 1;
        Category category = categories.get(selectedCategory);

        TypedQuery<Product> productTypedQuery = manager.createQuery("select p from Product p where p.category.id = ?1", Product.class);
        productTypedQuery.setParameter(1, category.getId());
        List<Product> products = productTypedQuery.getResultList();

        try {
            manager.getTransaction().begin();
            Query priceUpdate = manager.createQuery("update Product p set p.price = p.price + (p.price * ?1 / 100)");
            priceUpdate.setParameter(1, 10);
            /*for (Product product : products) {
                product.setPrice((int) (product.getPrice() * 1.50));
                manager.persist(product);
            }*/
            priceUpdate.executeUpdate();
            manager.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
        factory.close();
        manager.close();
    }
}
