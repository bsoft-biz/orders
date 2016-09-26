package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.ProductionTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface ProductionTimeRepository extends JpaRepository<ProductionTime, Integer> {
    List<ProductionTime> findByItemGroup_Id(Integer groupId);
}
