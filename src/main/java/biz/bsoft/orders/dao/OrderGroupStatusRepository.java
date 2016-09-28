package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.OrderGroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface OrderGroupStatusRepository extends CrudRepository<OrderGroupStatus,Integer> {
    List<OrderGroupStatus> findByOrder_ClientPos_IdAndOrder_OrderDateAndGroup_Id(Integer clientPosId, LocalDate orderDate, Integer itemGroupId);
}
