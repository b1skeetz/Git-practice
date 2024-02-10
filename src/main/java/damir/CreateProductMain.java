package damir;

import damir.entity.Category;
import damir.entity.Product;
import damir.entity.PropValues;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Scanner;

public class CreateProductMain {
    public static void main(String[] args) {
        // TypedQuery
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        Scanner scanner = new Scanner(System.in);
        boolean isCategoryValidStatus = false;

        System.out.println("-- Все категории для выбора: --");
        TypedQuery<Category> categoryTypedQuery = manager.createQuery("SELECT c FROM Category c", Category.class);
        List<Category> categories = categoryTypedQuery.getResultList();
        for (int i = 0; i < categories.size(); i++) {
            System.out.println(i + 1 + " - " + categories.get(i).getName());
        }
        System.out.println("-- ========================= --");

        int categoryIndex;
        Category category = null;
        while (!isCategoryValidStatus) {
            System.out.print("Выберите ID категории для товара: ");
            categoryIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (categoryIndex < categories.size() && categoryIndex > 0) {
                category = categories.get(categoryIndex);
                isCategoryValidStatus = true;
            } else {
                System.out.println("Внимание: Выберите существующую категорию из списка!");
            }
        }

        System.out.print("Введите название товара: ");
        String productName = scanner.nextLine();

        boolean isPriceValidStatus = false;
        int productPrice = 0;
        while(!isPriceValidStatus){
            try{
                System.out.print("Введите цену: ");
                productPrice = Integer.parseInt(scanner.nextLine());
                if(productPrice >= 0){
                    isPriceValidStatus = true;
                } else {
                    System.out.println("Внимание: Введите допустимое значение цены!");
                }
            } catch (NumberFormatException e){
                System.out.println("Внимание: Текстовая информация не допускается! Ввод символов запрещен!");
            }
        }

        try {
            manager.getTransaction().begin();
            Product product = new Product();
            product.setName(productName);
            product.setPrice(productPrice);
            product.setCategory(category);
            manager.persist(product);

            for (int i = 0; i < category.getProperties().size(); i++) {
                PropValues propValue = new PropValues();
                System.out.print(category.getProperties().get(i).getName() + ": ");
                String property = scanner.nextLine();
                propValue.setValue(property);
                propValue.setProduct(manager.find(Product.class, product.getId()));
                propValue.setProperty(category.getProperties().get(i));
                manager.persist(propValue);
            }

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
