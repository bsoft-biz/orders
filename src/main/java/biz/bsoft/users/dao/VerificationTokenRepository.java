package biz.bsoft.users.dao;

import biz.bsoft.users.model.VerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Integer> {
    VerificationToken findByToken(String token);
}
