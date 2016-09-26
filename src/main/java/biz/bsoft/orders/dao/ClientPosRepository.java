package biz.bsoft.orders.dao;

import biz.bsoft.orders.model.ClientPOS;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by vbabin on 25.09.2016.
 */
public interface ClientPosRepository extends JpaRepository<ClientPOS, Integer> {
}
