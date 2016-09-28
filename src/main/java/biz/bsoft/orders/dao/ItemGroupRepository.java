package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.ItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface ItemGroupRepository extends CrudRepository<ItemGroup, Integer> {
}
