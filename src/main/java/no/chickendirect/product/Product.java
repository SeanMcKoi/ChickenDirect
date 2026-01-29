package no.chickendirect.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.chickendirect.productstatus.ProductStatus;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;
    
    @NotBlank
    private String description;
    
    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @NotNull
    @PositiveOrZero
    private Integer quantityOnHand;
}