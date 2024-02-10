package damir;

import damir.entity.Category;
import damir.entity.Property;
import jakarta.persistence.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CreateCategoryMain {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        boolean status = false;
        String categoryName = "";

        while (!status) {
            System.out.print("Введите название категории: ");
            categoryName = scanner.nextLine();
            TypedQuery<Category> categoryTypedQuery = manager.createQuery(
                    "select c from Category c where c.name = ?1", Category.class);
            categoryTypedQuery.setParameter(1, categoryName);
            try {
                categoryTypedQuery.getSingleResult();
                System.out.println("Такая категория уже существует! Попробуйте еще раз!");
            }catch (NoResultException e){ // category == null - такой категории нет
                System.out.println("Новое название! Супер :)");
                status = true;
            }
        }

        System.out.print("Введите характеристики категории: ");
        String properties = scanner.nextLine();
        List<String> propertiesArray = List.of(properties.split(", "));

        // Гитары
        // Материал, Размер, Струны
        try {
            manager.getTransaction().begin();
            Category category = new Category();
            category.setName(categoryName);
            manager.persist(category);
            for (int i = 0; i < propertiesArray.size(); i++) {
                Property property = new Property();
                property.setName(propertiesArray.get(i));
                property.setCategory(category);
                manager.persist(property);
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            manager.getTransaction().rollback();
            throw new RuntimeException(e);
        }
        manager.close();
        factory.close();
    }
}
