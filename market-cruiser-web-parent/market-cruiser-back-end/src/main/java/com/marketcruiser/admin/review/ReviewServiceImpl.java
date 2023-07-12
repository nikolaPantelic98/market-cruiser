package com.marketcruiser.admin.review;

import com.marketcruiser.common.entity.Review;
import com.marketcruiser.common.exception.ReviewNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ReviewServiceImpl implements ReviewService{

    public static final int REVIEWS_PER_PAGE = 10;

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }


    @Override
    public Page<Review> listReviewsByPage(int pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, REVIEWS_PER_PAGE, sort);

        if (keyword != null) {
            return reviewRepository.findAll(keyword, pageable);
        }

        return reviewRepository.findAll(pageable);
    }

    @Override
    public Review getReviewById(Long reviewId) throws ReviewNotFoundException {
        try {
            return reviewRepository.findById(reviewId).get();
        } catch (NoSuchElementException exception) {
            throw new ReviewNotFoundException("Could not find any reviews with ID " + reviewId);
        }
    }

    @Override
    public void saveReview(Review reviewInForm) {
        Review reviewInDB = reviewRepository.findById(reviewInForm.getReviewId()).get();
        reviewInDB.setHeadline(reviewInForm.getHeadline());
        reviewInDB.setComment(reviewInDB.getComment());

        reviewRepository.save(reviewInDB);
    }

    @Override
    public void deleteReview(Long reviewId) throws ReviewNotFoundException {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException("Could not find any reviews with ID " + reviewId);
        }

        reviewRepository.deleteById(reviewId);
    }
}
