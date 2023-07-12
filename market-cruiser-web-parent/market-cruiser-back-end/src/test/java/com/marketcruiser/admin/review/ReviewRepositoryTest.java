package com.marketcruiser.admin.review;

import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.Review;
import com.marketcruiser.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;


    @Test
    public void testCreateReview() {
        Long productId = 44L;
        Product product = new Product(productId);

        Long customerId = 17L;
        Customer customer = new Customer(customerId);

        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setReviewTime(new Date());
        review.setHeadline("Perfect for my needs. Loving it!");
        review.setComment("Nice to have: wireless remote, iOS app, GPS...");
        review.setRating(5);

        Review savedReview = reviewRepository.save(review);

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getReviewId()).isGreaterThan(0);
    }
}
