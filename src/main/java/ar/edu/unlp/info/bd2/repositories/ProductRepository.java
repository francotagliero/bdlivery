package ar.edu.unlp.info.bd2.repositories;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import ar.edu.unlp.info.bd2.model.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
	List<Product> findByNameContaining(String name);
	Optional<Product> findFirstByOrderByWeigthDesc();
	

}