package ar.edu.unlp.info.bd2.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.repositories.DBliveryException;
import ar.edu.unlp.info.bd2.repositories.DBliveryRepository;

@Service
public class DBliveryServiceImpl implements DBliveryService, DBliveryStatisticsService {

	private DBliveryRepository repository;

	public DBliveryServiceImpl(DBliveryRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public Product createProduct(String name, Float price, Float weight, Supplier supplier) {
		// TODO Auto-generated method stub
		Product p = new Product(name, price, weight, supplier);
		return repository.storeProduct(p);
	}
	
	@Override
	@Transactional
	public Supplier createSupplier(String name, String cuil, String address, Float coordX, Float coordY) {
		// TODO Auto-generated method stub
		Supplier s = new Supplier(name, cuil, address, coordX, coordY);
		return repository.storeSupplier(s);

	}
	
	@Override
	@Transactional
	public User createUser(String email, String password, String username, String name, Date dateOfBirth) {
		User user = new User(email, password, username, name, dateOfBirth);
		return repository.storeUser(user);
	}
	
	@Override
	@Transactional
	public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException {
		// TODO Auto-generated method stub
		Optional<Product> prod = this.getProductById(id);
		if (!prod.isPresent() ) {
			throw new DBliveryException("The product does not exist");
		}

		Product prd = prod.get();
		prd.addPrice(price, startDate);

		return repository.updateProduct(prd);
		
	}
	
	@Override
	@Transactional
	public Optional<User> getUserById(Long id) {
		Optional<User> u = repository.getUserById(id);

		return u;
	}
	
	@Override
	@Transactional
	public Optional<User> getUserByEmail(String email) {
		Optional<User> u = repository.getUserByEmail(email);

		return u;
	}
	
	@Override
	@Transactional
	public Optional<User> getUserByUsername(String username) {
		Optional<User> u = repository.getUserByUsername(username);

		return u;
	}
	
	@Override
	@Transactional
	public Optional<Product> getProductById(Long id) {
		// TODO Auto-generated method stub
		return this.repository.getProductByid(id);
	}
	
	@Override
	@Transactional
	public Optional<Order> getOrderById(Long id) {
		Optional<Order> o = repository.getOrderById(id);

		return o;
	}
	
	@Override
	@Transactional
	public Order createOrder(Date dateOfOrder, String address, Float coordX, Float coordY, User client) {
		Order order = new Order(dateOfOrder, address, coordX, coordY, client);
		return repository.storeOrder(order);
	}
	
	@Override
	@Transactional
	public Order addProduct(Long order, Long quantity, Product product) throws DBliveryException {
		Optional<Order> op = this.repository.getOrderById(order);

		if (!op.isPresent() ) {
			throw new DBliveryException("The order doesnt exists");
		}

		Order o = op.get();
		//traemos el producto de la base
		Optional<Product> ppe = repository.getProductByid(product.getId());
		Product este = ppe.get();
		
		
		ProductOrder po = new ProductOrder(quantity, este);
		o.addProductOrder(po);

		return repository.updateOrder(o);
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

		return repository.updateOrder(o);
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
		 
		 return repository.updateOrder(o);
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
		 
		 return repository.updateOrder(o);
	}
	
	@Override
	@Transactional
	public boolean canCancel(Long order) throws DBliveryException {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		Optional<Order> o = this.getOrderById(order);
		if (!o.isPresent()) {
			return null;
		}
		//La orden existe por lo tanto si o si tiene un estado
		OrderStatus os = repository.getActualState(order);
		return os;
		
	}
	
	@Override
	@Transactional
	public List<Product> getProductsByName(String name) {
		// TODO Auto-generated method stub
		return repository.getProductByName(name);
	}

	//-------------------------------------------------------------------------------------------
	
	@Override
	@Transactional
	public Product createProduct(String name, Float price, Float weight, Supplier supplier, Date date) {
	
		Product p = new Product(name, price, weight, supplier, date);
		return repository.storeProduct(p);
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
		return repository.updateOrder(o);
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
		 
		return repository.updateOrder(o);
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
		
		return repository.updateOrder(o);
	}

	//METODOS DBliveryStatisticsService.java
	@Override
	public Product getMaxWeigth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getAllOrdersMadeByUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getPendingOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getSentOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getDeliveredOrdersForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getProductsOnePrice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getSoldProductsOn(Date day) {
		// TODO Auto-generated method stub
		return null;
	}

}
