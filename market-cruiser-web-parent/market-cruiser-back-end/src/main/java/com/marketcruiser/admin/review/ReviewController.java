package com.marketcruiser.admin.review;

import com.marketcruiser.common.entity.Review;
import com.marketcruiser.common.exception.ReviewNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ReviewController {

    private final ReviewServiceImpl reviewService;

    @Autowired
    public ReviewController(ReviewServiceImpl reviewService) {
        this.reviewService = reviewService;
    }


    @GetMapping("/reviews")
    public String showFirstPageOfReviews(Model model) {
        return showPageOfReviews(1, model, "reviewTime", "desc", null);
    }

    @GetMapping("/reviews/page/{pageNumber}")
    public String showPageOfReviews(@PathVariable int pageNumber, Model model, @Param("sortField") String sortField,
                                   @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
        Page<Review> page = reviewService.listReviewsByPage(pageNumber, sortField, sortDir, keyword);
        List<Review> listReviews = page.getContent();

        long startCount = (long) (pageNumber - 1) * ReviewServiceImpl.REVIEWS_PER_PAGE + 1;
        long endCount = startCount + ReviewServiceImpl.REVIEWS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listReviews", listReviews);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/reviews");

        return "reviews/reviews";
    }

    @GetMapping("/reviews/detail/{reviewId}")
    public String viewReview(@PathVariable Long reviewId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.getReviewById(reviewId);
            model.addAttribute("review", review);

            return "reviews/review_detail_modal";
        } catch (ReviewNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/reviews";
        }
    }

    @GetMapping("/reviews/edit/{reviewId}")
    public String editReview(@PathVariable Long reviewId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.getReviewById(reviewId);

            model.addAttribute("review", review);
            model.addAttribute("pageTitle", String.format("Edit Review (ID: %d)", review.getReviewId()));

            return "reviews/review_form";
        } catch (ReviewNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/reviews";
        }
    }

    @PostMapping("/reviews/save")
    public String saveReview(Review reviewInForm, RedirectAttributes redirectAttributes) {
        reviewService.saveReview(reviewInForm);
        redirectAttributes.addFlashAttribute("message", "The review ID " + reviewInForm.getReviewId() + " has been updated successfully.");
        return "redirect:/reviews";
    }

    @GetMapping("/reviews/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReview(reviewId);
            redirectAttributes.addFlashAttribute("message", "The review ID " + reviewId + " has been deleted.");
        } catch (ReviewNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/reviews";
    }
}
