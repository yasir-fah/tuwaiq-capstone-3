package com.fkhrayef.capstone3.Repository;

import com.fkhrayef.capstone3.Model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    
    Subscription findSubscriptionById(Integer id);
}
