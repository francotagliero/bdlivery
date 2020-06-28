package ar.edu.unlp.info.bd2.repositories;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ar.edu.unlp.info.bd2.model.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
	List<Product> findByNameContaining(String name);
	
	Optional<Product> findFirstByOrderByWeigthDesc();
	
	@Query("select p from Product p where p.historyPrice.size = 1")
	List<Product> findOnePrice();
	
	@Query("select po.product from Order o join o.productOrders po where o.dateOfOrder=:day")
	List<Product> findSoldProductOn(@Param("day") Date day);
	

}