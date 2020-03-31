package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="product")
public class Product {
	
	public Product(String name, Float weigth, Float price, Supplier supplier) {
		super();
		this.name = name;
		this.weigth = weigth;
		this.price = price;
		this.supplier = supplier;
		this.addPrice(price);
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="productId")
    private Long id;
	
	@Column(name="name", nullable=false)
	private String name;
	
	@Column(name="weigth", nullable=false)
	private Float weigth;
	
	@Column(name="price", nullable=false)
	private Float price;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "productId")
	private List<HistoryPrice> historyPrice = new ArrayList<>();
	
	@OneToOne
    @JoinColumn(name="supplierId")
    private Supplier supplier;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getWeigth() {
		return weigth;
	}

	public void setWeigth(Float weigth) {
		this.weigth = weigth;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public void addPrice(Float price) {
		HistoryPrice hp = new HistoryPrice(price);
		this.setHistoryPrice(hp);
	}
	
	public List<HistoryPrice> getPrices() {
		return historyPrice;
	}

	public void setHistoryPrice(HistoryPrice historyPrice) {
		this.historyPrice.add(historyPrice);
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Long getId() {
		return id;
	}

}
