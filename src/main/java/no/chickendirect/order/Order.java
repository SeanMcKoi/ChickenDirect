package no.chickendirect.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.chickendirect.customer.Customer;
import no.chickendirect.orderitem.OrderItem;

import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @NotNull
    @PositiveOrZero
    private BigDecimal totalPrice;
    
    @NotNull
    @PositiveOrZero
    private BigDecimal shippingCharge;
    
    private Boolean isShipped;

    @NotBlank
    private String shippingStreet;
    
    @NotBlank
    private String shippingCity;
    
    @NotBlank
    private String shippingPostalCode;
    
    @NotBlank
    private String shippingCountry;

    @NotBlank
    private String customerName;

    @NotBlank
    private String customerEmail;

    @NotBlank
    private String customerPhone;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    public void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}