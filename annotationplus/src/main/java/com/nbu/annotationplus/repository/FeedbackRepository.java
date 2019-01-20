package com.nbu.annotationplus.repository;

import com.nbu.annotationplus.persistence.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
