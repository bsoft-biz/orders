package biz.bsoft.users.dao;

import biz.bsoft.users.model.VerificationToken;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by vbabin on 04.12.2016.
 */
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Integer> {
}
