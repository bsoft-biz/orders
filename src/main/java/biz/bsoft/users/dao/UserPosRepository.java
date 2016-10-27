package biz.bsoft.users.dao;

import biz.bsoft.users.model.UserPos;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Created by vbabin on 19.10.2016.
 */
public interface UserPosRepository extends CrudRepository<UserPos, Integer> {
    List<UserPos> findByUser_Username(String username);
}
