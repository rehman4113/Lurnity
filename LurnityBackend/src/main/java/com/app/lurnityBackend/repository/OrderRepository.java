package com.app.lurnityBackend.repository;

import com.app.lurnityBackend.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    // Find orders by userId
    List<Order> findByUserId(String userId);

    // Find orders by courseId
    List<Order> findByCourseId(String courseId);
}
