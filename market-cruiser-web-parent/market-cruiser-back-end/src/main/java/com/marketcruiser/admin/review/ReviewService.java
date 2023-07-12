package com.marketcruiser.admin.review;

import com.marketcruiser.common.entity.Review;
import com.marketcruiser.common.exception.ReviewNotFoundException;
import org.springframework.data.domain.Page;

public interface ReviewService {

    Page<Review> listReviewsByPage(int pageNumber, String sortField, String sortDir, String keyword);
    Review getReviewById(Long reviewId) throws ReviewNotFoundException;
    void saveReview(Review reviewInForm);
    void deleteReview(Long reviewId) throws ReviewNotFoundException;
}
