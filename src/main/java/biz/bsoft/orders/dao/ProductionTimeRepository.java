package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.ProductionTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface ProductionTimeRepository extends CrudRepository<ProductionTime, Integer> {
    List<ProductionTime> findByItemGroup_Id(Integer groupId);
}
