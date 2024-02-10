package damir;

import damir.entity.Category;
import damir.entity.Product;
import damir.entity.PropValues;
import damir.entity.Property;
import jakarta.persistence.*;

import java.util.List;
import java.util.Scanner;

public class Functions {
    private final Scanner scanner = new Scanner(System.in);
    private final EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
    private final EntityManager manager = factory.createEntityManager();

    public boolean createCategory(){
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
            for (String s : propertiesArray) {
                Property property = new Property();
                property.setName(s);
                property.setCategory(category);
                manager.persist(property);
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            manager.getTransaction().rollback();
            return false;
        }
        manager.close();
        factory.close();
        return true;
    }

    public boolean createProduct(){
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
            return false;
        }

        factory.close();
        manager.close();
        return true;
    }

    public boolean deleteProduct(){
        System.out.print("Введите ID товара для удаления: ");
        Long productId = Long.valueOf(scanner.nextLine());

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
            return false;
        }

        factory.close();
        manager.close();
        return true;
    }

    public int fillProperties(){
        System.out.print("Введите ID категории: ");
        Long categoryId = Long.valueOf(scanner.nextLine());

        System.out.print("Введите ID продукта: ");
        Long productId = Long.valueOf(scanner.nextLine());

        int counter = 0;

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
                counter++;
                manager.persist(propValue);
            }
            manager.getTransaction().commit();
        }catch (Exception e){
            System.out.println(e.getMessage());
            manager.getTransaction().rollback();
            return 0;
        }

        factory.close();
        manager.close();
        return counter;
    }

    public boolean increasePrice(int percent){
        System.out.println("Список всех категорий: ");
        TypedQuery<Category> categoryTypedQuery = manager.createQuery("select c from Category c", Category.class);
        List<Category> categories = categoryTypedQuery.getResultList();
        for (int i = 0; i < categories.size(); i++) {
            System.out.println(i + 1 + ") " + categories.get(i).getName());
        }

        System.out.print("Выберите категорию: ");
        int selectedCategory = Integer.parseInt(scanner.nextLine()) - 1;
        Category category = categories.get(selectedCategory);

        TypedQuery<Product> productTypedQuery = manager.createQuery("select p from Product p where p.category.id = ?1",
                Product.class);
        productTypedQuery.setParameter(1, category.getId());
        List<Product> products = productTypedQuery.getResultList();

        try {
            manager.getTransaction().begin();
            Query priceUpdate = manager.createQuery("update Product p set p.price = p.price + (p.price * ?1 / 100)");
            priceUpdate.setParameter(1, percent);
            priceUpdate.executeUpdate();
            manager.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            manager.getTransaction().rollback();
            return false;
        }
        factory.close();
        manager.close();
        return true;
    }

    public boolean updateProduct(){
        System.out.print("Введите ID товара для обновления информации: ");
        Long productId = Long.valueOf(scanner.nextLine());

        Product product = manager.find(Product.class, productId);
        Category category = product.getCategory();

        System.out.println(product);
        System.out.print("Введите новое название продукта: ");
        String newName = scanner.nextLine();
        if (newName.equals("")) {
            newName = product.getName();
        }
        Integer newPrice;
        try {
            System.out.print("Введите новую цену: ");
            newPrice = Integer.valueOf(scanner.nextLine());
            if (newPrice == 0) {
                newPrice = product.getPrice();
            }
        } catch (NumberFormatException e) {
            newPrice = product.getPrice();
        }


        product.setName(newName);
        product.setPrice(newPrice);

        try {
            manager.getTransaction().begin();
            manager.persist(product);

            TypedQuery<Property> propertiesTypedQuery = manager.createQuery("select prop from Property prop " +
                    "where prop.category.id = ?1", Property.class);
            propertiesTypedQuery.setParameter(1, category.getId());
            List<Property> propertiesList = propertiesTypedQuery.getResultList();

            for (int i = 0; i < product.getCategory().getProperties().size(); i++) {
                TypedQuery<PropValues> doesPropValueExist = manager.createQuery(
                        "select pv from PropValues pv where pv.property.id = ?1 and pv.product.id = ?2",
                        PropValues.class);
                doesPropValueExist.setParameter(1, propertiesList.get(i).getId());
                doesPropValueExist.setParameter(2, product.getId());

                PropValues neededPV;
                try { // Если нашел
                    neededPV = doesPropValueExist.getSingleResult();
                    System.out.println(neededPV.getProperty().getName() + ": " + neededPV.getValue());
                    System.out.print("Введите новое значение свойства: ");
                    String newPropValue = scanner.nextLine();
                    if (newPropValue.equals("")) {
                        newPropValue = neededPV.getValue();
                    }

                    product.getPropValues().get(i).setValue(newPropValue);
                    manager.persist(product.getPropValues().get(i));
                } catch (NoResultException e) { // Не нашел
                    neededPV = new PropValues();
                    neededPV.setProduct(product);
                    neededPV.setProperty(product.getCategory().getProperties().get(i));
                    System.out.println(neededPV.getProperty().getName() + ": " + neededPV.getValue());
                    System.out.print("Введите значение свойства: ");
                    String newPropValue = scanner.nextLine();
                    neededPV.setValue(newPropValue);
                    manager.persist(neededPV);
                }
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            manager.getTransaction().rollback();
            return false;
        }
        factory.close();
        manager.close();
        return true;
    }
}
