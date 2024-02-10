package damir;

import damir.entity.Category;
import damir.entity.Product;
import damir.entity.PropValues;
import damir.entity.Property;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UpdateProductMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите ID товара для обновления информации: ");
        Long productId = Long.valueOf(scanner.nextLine());

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        Product product = manager.find(Product.class, productId);
        Category category = product.getCategory();

        System.out.println(product);
        System.out.print("Введите новое название продукта: ");
        String newName = scanner.nextLine();
        if (newName.equals("")) {
            newName = product.getName();
        }
        Integer newPrice = 0;
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

            TypedQuery<Property> propertiesTypedQuery = manager.createQuery("select prop from Property prop where prop.category.id = ?1", Property.class);
            propertiesTypedQuery.setParameter(1, category.getId());
            List<Property> propertiesList = propertiesTypedQuery.getResultList();

            for (int i = 0; i < product.getCategory().getProperties().size(); i++) {

                /*TypedQuery<PropValues> doesPropValueExist = manager.createQuery("select pv from PropValues pv where " +
                        "pv.property.id = " + propertiesList.get(i).getId() + " and pv.product.id = " + product.getId(), PropValues.class);*/
                TypedQuery<PropValues> doesPropValueExist = manager.createQuery(
                        "select pv from PropValues pv where pv.property.id = ?1 and pv.product.id = ?2", PropValues.class);
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
                    /*Query updateIfExists = manager.createQuery(
                            "update PropValues pv set pv.value = ?1 where pv.product = ?2 and pv.property = ?3"
                    );
                    updateIfExists.setParameter(1, newPropValue);
                    updateIfExists.setParameter(2, neededPV.getProduct());
                    updateIfExists.setParameter(3, neededPV.getProperty());
                    updateIfExists.executeUpdate();*/
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
            throw new RuntimeException(e);
        }
        factory.close();
        manager.close();
    }
}
