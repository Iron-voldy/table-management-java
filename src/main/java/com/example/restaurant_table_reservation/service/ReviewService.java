package com.example.restaurant_table_reservation.service;

import com.example.restaurant_table_reservation.model.Review;
import com.example.restaurant_table_reservation.utils.ReviewFileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReviewService {
    private List<Review> reviewList;
    private Gson gson = new Gson();

    public ReviewService() {
        loadReviews();
    }

    private void loadReviews() {
        try {
            String json = ReviewFileUtils.readFile();
            Type listType = new TypeToken<List<Review>>(){}.getType();
            reviewList = gson.fromJson(json, listType);
            if (reviewList == null) reviewList = new ArrayList<>();
            System.out.println("Loaded Reviews: " + reviewList);
        } catch (Exception e) {
            System.err.println("Failed to load reviews: " + e.getMessage());
            reviewList = new ArrayList<>();
        }
    }

    private void saveReviews() {
        try {
            ReviewFileUtils.writeFile(gson.toJson(reviewList));
            System.out.println("Reviews saved successfully to review.json");
        } catch (Exception e) {
            System.err.println("Failed to save reviews: " + e.getMessage());
        }
    }

    public List<Review> getAllReviews() {
        return reviewList;
    }

    public void addReview(String customerName, String email, String message, int rating) {
        int id = reviewList.isEmpty() ? 1 : reviewList.get(reviewList.size() - 1).getId() + 1;
        reviewList.add(new Review(id, customerName, email, message, rating, null));
        saveReviews();
    }

    public void deleteReview(int id) {
        reviewList.removeIf(review -> review.getId() == id);
        saveReviews();
    }

    public Review getReviewById(int id) {
        for (Review review : reviewList) {
            if (review.getId() == id) {
                return review;
            }
        }
        return null;
    }

    public void updateReview(int id, String customerName, String email, String message, int rating, String adminReply) {
        for (Review review : reviewList) {
            if (review.getId() == id) {
                review.setCustomerName(customerName);
                review.setEmail(email);
                review.setMessage(message);
                review.setRating(rating);
                review.setAdminReply(adminReply);
                saveReviews();
                break;
            }
        }
    }
}