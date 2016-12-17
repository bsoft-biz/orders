package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Integer> {
    List<Item> findByArchive(boolean archive);
}
