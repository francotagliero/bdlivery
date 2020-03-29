package ar.edu.unlp.info.bd2.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="historyPrice")
public class HistoryPrice {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="histoeyPriceId")
    private Long id;
	
	@Column(name = "price", nullable = false)
    private Float price;
	
	@Column(name = "date", nullable = false)
    private Date date;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;
}
