package ar.edu.unlp.info.bd2.model;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@BsonDiscriminator
public class Order implements PersistentObject{

    @BsonId
    private ObjectId objectId;

    private Date dateOfOrder;

    private String address;

    @BsonIgnore
    private Float coordX; //Si no ponemos El ignore no funca!

    private Point position;

    @BsonIgnore
    private Float coordY; //Si no ponemos El ignore no funca!

    private Float amount;

    private User client;

    private User deliveryUser;

    private OrderStatus actualState;

    private List<ProductOrder> productOrders = new ArrayList<ProductOrder>();

    private List<OrderStatus> collectionOrderStatus = new ArrayList<OrderStatus>();

    /*--------------------------------------------------------------*/

    public Order() {}

    public Order(Date dateOfOrder, String address, Float coordX, Float coordY, User client) {
        this.dateOfOrder = dateOfOrder;
        this.address = address;
        this.coordX = coordX;
        this.coordY = coordY;
        this.amount = 0F;
        this.client = client;

        this.deliveryUser = null;
        this.actualState = new OrderStatus("Pending", this, dateOfOrder); //NUEVO! NO LO TENIAMOS EN CUENTA!
        this.productOrders = new ArrayList<>();
        this.collectionOrderStatus = new ArrayList<>();
        this.addOrderStatus(this.actualState); // q te parece??

        Position position1 = new Position(coordX, coordY);

        this.position = new Point(position1);

    }

    public Date getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(Date dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getCoordX() {
        return coordX;
    }

    public void setCoordX(Float coordX) {
        this.coordX = coordX;
    }

    public Float getCoordY() {
        return coordY;
    }

    public void setCoordY(Float coordY) {
        this.coordY = coordY;
    }

    public Object getClient() {
        return this.client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public User getDeliveryUser() {
        return deliveryUser;
    }

    public void setDeliveryUser(User deliveryUser) {
        this.deliveryUser = deliveryUser;
    }

    public OrderStatus getActualState() {
        return actualState;
    }

    public void setActualState(OrderStatus actualState) {
        this.actualState = actualState;
    }

    public List<ProductOrder> getProducts() { //Diferente al set por como esta en DBliveryServiceTestCase
        return productOrders;
    }

    public void setProductOrders(List<ProductOrder> productOrders) {
        this.productOrders = productOrders;
        for(int i= 0; i < productOrders.size(); i++){
            this.actAmountPO(productOrders.get(i));
        }

    }

    public List<ProductOrder> getProductOrders() {
        return this.productOrders;
    }

    public void addProductOrder(ProductOrder po) {
        this.productOrders.add(po);
        this.actAmountPO(po);
    }

    public void actAmountPO(ProductOrder po) {
        Float price=po.getProduct().getPriceAt(getDateOfOrder()) * po.getQuantity();
        if (price == 0F) {
            price = po.getProduct().getPrice() * po.getQuantity();
        }
        this.amount = this.amount + price;
    }

    public List<OrderStatus> getStatus() { //Diferente al set por como esta en DBliveryServiceTestCase
        return collectionOrderStatus;
    }

    public void setCollectionOrderStatus(List<OrderStatus> collectionOrderStatus) {
        this.collectionOrderStatus = collectionOrderStatus;
    }

    public List<OrderStatus> getCollectionOrderStatus(){
        return this.collectionOrderStatus;
    }

    public void addOrderStatus(OrderStatus orderStatus) {

        this.collectionOrderStatus.add(orderStatus);
        this.actualState = orderStatus;

    }

    public void addOrderStatus(OrderStatus orderStatus, Date date) {
        if(this.collectionOrderStatus.size() >= 1) {
            OrderStatus lastOrderStatus = this.collectionOrderStatus.get(this.collectionOrderStatus.size()-1);
            lastOrderStatus.setEndDate(date); //Falta volver a agregar al array?
        }
        this.actualState = orderStatus;

        this.collectionOrderStatus.add(orderStatus);

    }

    public void addOrderStatus(String status, Date date) {
        OrderStatus os = new OrderStatus(status, this, date);

        if(this.collectionOrderStatus.size() >= 1) {
            OrderStatus lastOrderStatus = this.collectionOrderStatus.get(this.collectionOrderStatus.size()-1);
            lastOrderStatus.setEndDate(date); //Falta volver a agregar al array?
        }
        this.actualState = os;

        this.collectionOrderStatus.add(os);

    }

    public Float getAmount() {
        return this.amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float calcularPrecioTotal() {
        Float total = 0F;
        for(int i=0; i < this.productOrders.size(); i++) {
            total =(Float) (total + (this.getProducts().get(i).getProduct().getPriceAt(getDateOfOrder()) ) * ( this.getProducts().get(i).getQuantity()));
        }
        return total;

    }

    @Override
    public ObjectId getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;

    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }


}