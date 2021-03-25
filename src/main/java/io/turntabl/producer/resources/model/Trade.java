package io.turntabl.producer.resources.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Table
@Entity(name="Trade")
public class Trade {
    @Id
    @SequenceGenerator(
            name= "client_sequence",
            sequenceName = "client_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "client_sequence"
    )
    @Column(
            nullable=false,
            updatable = false
    )
    private Long id;

    private String exchange_order_id;

    private Double price;

    private String product;

    private String exchange;

    private Integer quantity;

    private String side;

    private String status;

    @ManyToOne
    @JoinColumn(name="orders_id")
    @JsonBackReference
    private Orders orders;

    public Trade() {
    }

    public Trade(Long id, String exchange_order_id, Double price, String product, Integer quantity, String side, Orders orders) {
        this.id = id;
        this.exchange_order_id = exchange_order_id;
        this.price = price;
        this.product = product;
        this.quantity = quantity;
        this.side = side;
        this.orders = orders;
    }

    public Long getId() {
        return id;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExchange_order_id() {
        return exchange_order_id;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "id=" + id +
                ", exchange_order_id='" + exchange_order_id + '\'' +
                ", price=" + price +
                ", product='" + product + '\'' +
                ", exchange='" + exchange + '\'' +
                ", quantity=" + quantity +
                ", side='" + side + '\'' +
                ", status='" + status + '\'' +
                ", orders=" + orders +
                '}';
    }

    public void setExchange_order_id(String exchange_order_id) {
        this.exchange_order_id = exchange_order_id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
