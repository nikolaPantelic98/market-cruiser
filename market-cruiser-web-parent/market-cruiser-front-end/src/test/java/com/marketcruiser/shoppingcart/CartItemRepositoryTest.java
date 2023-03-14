package com.marketcruiser.shoppingcart;

import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void testSaveItem() {
        Long customerId = 12L;
        Long productId = 40L;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem newItem = new CartItem();
        newItem.setCustomer(customer);
        newItem.setProduct(product);
        newItem.setQuantity(1);

        CartItem savedItem = cartItemRepository.save(newItem);

        assertThat(savedItem.getCartItemId()).isGreaterThan(0);
    }

    @Test
    public void testSaveItem2() {
        Long customerId = 13L;
        Long productId = 40L;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem item1 = new CartItem();
        item1.setCustomer(customer);
        item1.setProduct(product);
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setCustomer(new Customer(customerId));
        item2.setProduct(new Product(productId));
        item2.setQuantity(3);

        Iterable<CartItem> iterable = cartItemRepository.saveAll(List.of(item1, item2));

        assertThat(iterable).size().isGreaterThan(0);
    }

    @Test
    public void testFindCartItemByCustomer() {
        Long customerId = 13L;
        List<CartItem> listItems = cartItemRepository.findCartItemByCustomer(new Customer(customerId));

        listItems.forEach(System.out::println);

        assertThat(listItems.size()).isEqualTo(2);
    }

    @Test
    public void testFindCartItemByCustomerAndProduct() {
        Long customerId = 12L;
        Long productId = 40L;

        CartItem item = cartItemRepository.findCartItemByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item).isNotNull();

        System.out.println(item);
    }

    @Test
    public void testUpdateQuantity() {
        Long customerId = 12L;
        Long productId = 40L;
        Integer quantity = 4;

        cartItemRepository.updateQuantity(quantity, customerId, productId);

        CartItem item = cartItemRepository.findCartItemByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item.getQuantity()).isEqualTo(4);
    }

    @Test
    public void testDeleteCartItemByCustomerAndProduct() {
        Long customerId = 13L;
        Long productId = 40L;

        cartItemRepository.deleteCartItemByCustomerAndProduct(customerId, productId);

        CartItem item = cartItemRepository.findCartItemByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item).isNull();
    }
}
