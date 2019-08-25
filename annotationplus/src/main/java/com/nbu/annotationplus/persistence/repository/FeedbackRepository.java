package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
