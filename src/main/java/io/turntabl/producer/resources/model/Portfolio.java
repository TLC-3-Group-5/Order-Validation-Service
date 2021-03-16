package io.turntabl.producer.resources.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

//@Table
//@Entity(name="Portfolio")
public class Portfolio {
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

    private String name;

    private String email;

    @ManyToOne
    @JoinColumn(name="client_id")
    @JsonBackReference
    private Client client;

    @OneToMany(mappedBy = "portfolio")
    @JsonManagedReference
    private List<OwnedStock> ownedStocks;

    @JsonManagedReference
    @OneToMany(mappedBy = "portfolio")

    private List<Orders> orders;

    public List<OwnedStock> getOwnedStocks() {
        return ownedStocks;
    }

    public void setOwnedStocks(List<OwnedStock> ownedStocks) {
        this.ownedStocks = ownedStocks;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public Portfolio() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
