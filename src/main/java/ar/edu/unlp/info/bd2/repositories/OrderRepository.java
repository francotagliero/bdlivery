package ar.edu.unlp.info.bd2.repositories;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import ar.edu.unlp.info.bd2.model.*;


public interface OrderRepository extends CrudRepository<Order, Long>{	
	
	@Query("select o from Order o join o.client cli where cli.username=:cliente" )
	List <Order> findOrderByClient( @Param("cliente") String cliente);
	
	@Query("select o from Order o join o.actualState os where os.status=:state" )
	List <Order> findOrderByStatus( @Param("state") String state);

}