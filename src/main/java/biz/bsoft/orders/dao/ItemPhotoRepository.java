package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.ItemPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface ItemPhotoRepository extends JpaRepository<ItemPhoto, Integer> {
    List<ItemPhoto> findByItem_Id (Integer itemId);
}
