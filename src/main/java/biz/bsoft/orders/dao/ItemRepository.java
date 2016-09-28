package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface ItemRepository extends CrudRepository<Item, Integer> {
}
