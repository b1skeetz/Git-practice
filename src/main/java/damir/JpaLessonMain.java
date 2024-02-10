package damir;

import damir.entity.*;
import jakarta.persistence.*;

import java.util.List;

public class JpaLessonMain {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        /*Product product = manager.find(Product.class, 1);
        System.out.println(product.getName() + " - " + product.getCategory() + " - " + product.getPrice());

        Category category = manager.find(Category.class, 1);

        System.out.println(category.toString());
        List<Product> productList = category.getProducts();
        for (Product prod : productList) {
            System.out.println("- " + prod.getName() + ": " + prod.getPrice());
        }*/



        try {
            /*
            manager.getTransaction().begin();
            Category category1 = new Category();
            category1.setName("Смартфоны");
            manager.persist(category1);
            manager.getTransaction().commit();*/

            TypedQuery<Product> productTypedQuery = manager.createQuery("SELECT p FROM Product p", Product.class);
            List<Product> products = productTypedQuery.getResultList();
            products.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }


        manager.close();
        factory.close();
    }
}
