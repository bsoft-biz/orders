package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.ItemInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface ItemInfoRepository extends JpaRepository<ItemInfo, Integer> {
    List<ItemInfo> findByItem_Id(Integer itemId);
}
