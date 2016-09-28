package biz.bsoft.users.dao;

import biz.bsoft.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by vbabin on 15.08.2016.
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail (String email);
    User findByUsername (String username);

//    @Override
//    void delete(User user);
}
