package ar.edu.unlp.info.bd2.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.repositories.DBliveryException;

import ar.edu.unlp.info.bd2.repositories.ProductRepository;
import ar.edu.unlp.info.bd2.repositories.SupplierRepository;
import ar.edu.unlp.info.bd2.repositories.UserRepository;
import ar.edu.unlp.info.bd2.repositories.OrderRepository;


public class SpringDataDBliveryService implements DBliveryService, DBliveryStatisticsService { 


	@Autowired
	UserRepository userRepo;
	
	@Autowired
	SupplierRepository supplierRepo;
	
	@Autowired
	ProductRepository productRepo;
	
	@Autowired
	OrderRepository orderRepo;

@Override
@Transactional
public Product createProduct(String name, Float price, Float weight, Supplier supplier) {
	// TODO Auto-generated method stub
	Product p = new Product(name, price, weight, supplier);
	return productRepo.save(p);
}

@Override
@Transactional
public Product createProduct(String name, Float price, Float weight, Supplier supplier, Date date) {
	// TODO Auto-generated method stub
	Product p = new Product(name, price, weight, supplier, date);
	return productRepo.save(p);
}

@Override
@Transactional
public Supplier createSupplier(String name, String cuil, String address, Float coordX, Float coordY) {
	// TODO Auto-generated method stub
	Supplier s = new Supplier(name, cuil, address, coordX, coordY);
	return supplierRepo.save(s);
}

@Override
@Transactional
public User createUser(String email, String password, String username, String name, Date dateOfBirth) {
	// TODO Auto-generated method stub
	User user = new User(email, password, username, name, dateOfBirth);
	userRepo.save(user);
	return user;
}

@Override
@Transactional
public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException {
	
 		Optional<Product> prod = productRepo.findById(id);
 		if (!prod.isPresent() ) {
 			throw new DBliveryException("The product does not exist");
 		}

 		Product prd = prod.get();
 		prd.addPrice(price, startDate);

 		// aca es return productRepo.update(prd); ????????????????????????????????
 		return productRepo.save(prd);  //save??
 		
}

@Override
@Transactional
public Optional<User> getUserById(Long id) {
	
		return userRepo.findById(id);
}

@Override
@Transactional
public Optional<User> getUserByEmail(String email) {
	
	return userRepo.findByEmail(email);
}

@Override
@Transactional
public Optional<User> getUserByUsername(String username) {
	
	return userRepo.findByUsername(username);
}

@Override
@Transactional
public Optional<Order> getOrderById(Long id) {
	return orderRepo.findById(id);
}

@Override
@Transactional
public Order createOrder(Date dateOfOrder, String address, Float coordX, Float coordY, User client) {
	Order order = new Order(dateOfOrder, address, coordX, coordY, client);
	return orderRepo.save(order);
}

@Override
@Transactional
public Order addProduct(Long order, Long quantity, Product product) throws DBliveryException {
	Optional<Order> op = this.getOrderById(order);

	if (!op.isPresent() ) {
		throw new DBliveryException("The order doesnt exists");
	}

	Order o = op.get();
	//traemos el producto de la base
	Optional<Product> ppe = productRepo.findById(product.getId());
	Product este = ppe.get();
	
	
	ProductOrder po = new ProductOrder(quantity, este);
	o.addProductOrder(po);

	return orderRepo.save(o);
}

@Override
@Transactional
public Order deliverOrder(Long order, User deliveryUser) throws DBliveryException {
	Optional<Order> op = this.getOrderById(order);

	if(!op.isPresent() ){
		throw new DBliveryException("Order does not exist");
	}
		
	if (!this.canDeliver(order)) {
		throw new DBliveryException("The order can't be delivered");
	} 

	Order o = op.get();

	o.setDeliveryUser(deliveryUser);
	OrderStatus sent = new OrderStatus("Sent");
	o.addOrderStatus(sent); //Este metodo setea el actualState!

	return orderRepo.save(o);
}



@Override
@Transactional
public Order cancelOrder(Long order) throws DBliveryException {
	Optional<Order> op = this.getOrderById(order);

	if(!op.isPresent() ){
		throw new DBliveryException("Order does not exist");
	}

	if (!this.canCancel(order) ) {
		throw new DBliveryException("The order can't be cancelled");
	}

	Order o = op.get();

	OrderStatus cancelled = new OrderStatus("Cancelled");
	o.addOrderStatus(cancelled);
	 
	 return orderRepo.save(o);

}



@Override
@Transactional
public Order finishOrder(Long order) throws DBliveryException {
	Optional<Order> op = this.getOrderById(order);

	if(!op.isPresent() ){
		throw new DBliveryException("Order does not exist");
	}

	if (!this.canFinish(order) ) {
		throw new DBliveryException("The order can't be delivered");
	}

	Order o = op.get();

	OrderStatus delivered = new OrderStatus("Delivered");
	o.addOrderStatus(delivered);
	 
	 return orderRepo.save(o);
	
}


@Override
@Transactional
public Order deliverOrder(Long order, User deliveryUser, Date date) throws DBliveryException {
	Optional<Order> op = this.getOrderById(order);
	
	
	if(!op.isPresent() ){
		throw new DBliveryException("Order does not exist");
	}
		
	if (!this.canDeliver(order)) {
		throw new DBliveryException("The order can't be delivered");
	} 
	
	Order o = op.get();

	o.setDeliveryUser(deliveryUser);
	//OrderStatus sent = new OrderStatus("Sent", date);
	o.addOrderStatus("Sent", date);
	//o.calcularPrecioTotal();
	return orderRepo.save(o);

}

@Override
@Transactional
public Order finishOrder(Long order, Date date) throws DBliveryException {
	Optional<Order> op = this.getOrderById(order);
	
	
	if(!op.isPresent() ){
		throw new DBliveryException("Order does not exist");
	}

	if (!this.canFinish(order) ) {
		throw new DBliveryException("The order can't be delivered");
	}
	
	Order o = op.get();

	//OrderStatus delivered = new OrderStatus("Delivered", date);
	o.addOrderStatus("Delivered", date);
	
	return orderRepo.save(o);
    	
}



@Override
@Transactional
public Order cancelOrder(Long order, Date date) throws DBliveryException {
	Optional<Order> op = this.getOrderById(order);
	
	
	if(!op.isPresent() ){
		throw new DBliveryException("Order does not exist");
	}

	if (!this.canCancel(order) ) {
		throw new DBliveryException("The order can't be cancelled");
	}
	
	Order o = op.get();

	//OrderStatus cancelled = new OrderStatus("Cancelled", date);
	//o.calcularPrecioTotal();
	o.addOrderStatus("Cancelled", date);
	 
	return orderRepo.save(o);
}



@Override
@Transactional
public boolean canCancel(Long order) throws DBliveryException {
	Optional<Order> o = this.getOrderById(order);
	if (o.isPresent()) {
		Order o1 = o.get();
		if(o1.getActualState().getStatus().equals("Pending") ) {
			return true;
		}
	}
	else {
		throw new DBliveryException("The order doesnt exist");
	}
	return false;
}



@Override
@Transactional
public boolean canFinish(Long id) throws DBliveryException {
	Optional<Order> o = this.getOrderById(id);
	if (o.isPresent()) {
		Order o1 = o.get();
		if (o1.getActualState().getStatus().equals("Sent") ) {
			return true;
		}
	}
	else {
		throw new DBliveryException("The order doesnt exist");
	}
	return false;
}



@Override
@Transactional
public boolean canDeliver(Long order) throws DBliveryException {
	Optional<Order> o = this.getOrderById(order);
	if (o.isPresent()) {
		Order order1 = o.get();
		if (order1.getProducts().size() >= 1) {
			if (this.getActualStatus(order).getStatus().equals("Pending") ) {
				return true;
			} else {
				throw new DBliveryException("The order can't be delivered");
			}
		}
	}
	return false;
}



@Override
@Transactional
public OrderStatus getActualStatus(Long order) {
	Optional<Order> op = this.getOrderById(order);
	if (!op.isPresent()) {
		return null;
	}
	//La olden existe por lo tanto si o si tiene un estado
	
	Order o = op.get();
	
	OrderStatus os = o.getActualState();
	return os;
}

@Override
public Product getMaxWeigth() {
	return productRepo.findFirstByOrderByWeigthDesc().get();
}

@Override
public List<Order> getAllOrdersMadeByUser(String username) {
	return orderRepo.findOrderByClient(username);
}

@Override
public List<Order> getPendingOrders() {
	return orderRepo.findOrderByStatus("Pending");
}

@Override
public List<Order> getSentOrders() {
	return orderRepo.findOrderByStatus("Sent");
}


@Override
public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
	return orderRepo.findOrdersBetweenDates(startDate, endDate);
}

@Override
public List<Order> getDeliveredOrdersForUser(String username) {
	return orderRepo.findOrdersByUsername(username);
}


@Override
public List<Product> getProductsOnePrice() {
	return productRepo.findOnePrice();
}


@Override
public List<Product> getSoldProductsOn(Date day) {
	return productRepo.findSoldProductOn(day);
}

@Override
public Optional<Product> getProductById(Long id) {
	// TODO Auto-generated method stub
	return null;
}

@Override
@Transactional
public List<Product> getProductsByName(String name) {
	return productRepo.findByNameContaining(name);
}



}
