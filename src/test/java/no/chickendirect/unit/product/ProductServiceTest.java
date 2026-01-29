package no.chickendirect.unit.product;

import no.chickendirect.product.*;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import no.chickendirect.exception.ProductNotFoundException;
import no.chickendirect.product.dto.ProductRequest;
import no.chickendirect.product.dto.ProductResponse;
import no.chickendirect.product.dto.ProductUpdateRequest;
import no.chickendirect.productstatus.ProductStatus;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_shouldSaveAndReturnResponse() {
        ProductRequest request = new ProductRequest(
                "Chicken Wings",
                "Spicy wings",
                BigDecimal.valueOf(50),
                ProductStatus.IN_STOCK,
                100
        );

        Product savedEntity = Product.builder()
                .id(1L)
                .name("Chicken Wings")
                .description("Spicy wings")
                .price(BigDecimal.valueOf(50))
                .status(ProductStatus.IN_STOCK)
                .quantityOnHand(100)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedEntity);

        ProductResponse response = productService.createProduct(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product toSave = captor.getValue();

        assertEquals("Chicken Wings", toSave.getName());
        assertEquals("Spicy wings", toSave.getDescription());
        assertEquals(BigDecimal.valueOf(50), toSave.getPrice());
        assertEquals(ProductStatus.IN_STOCK, toSave.getStatus());
        assertEquals(100, toSave.getQuantityOnHand());

        assertEquals(1L, response.id());
        assertEquals("Chicken Wings", response.name());
    }

    @Test
    void getProduct_shouldReturnResponse() {
        Product product = Product.builder()
                .id(1L)
                .name("Chicken Wings")
                .description("Spicy wings")
                .price(BigDecimal.valueOf(50))
                .status(ProductStatus.IN_STOCK)
                .quantityOnHand(100)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProduct(1L);

        assertEquals(1L, response.id());
        assertEquals("Chicken Wings", response.name());
    }

    @Test
    void getProduct_notFound_shouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(99L));
    }

    @Test
    void deleteProduct_shouldDeleteIfFound() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_notFound_shouldThrowException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void updateProduct_shouldUpdateAndReturnResponse() {
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Chicken Wings")
                .description("Spicy wings")
                .price(BigDecimal.valueOf(50))
                .status(ProductStatus.IN_STOCK)
                .quantityOnHand(100)
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Chicken Wings")
                .description("Spicy wings")
                .price(BigDecimal.valueOf(45))
                .status(ProductStatus.OUT_OF_STOCK)
                .quantityOnHand(20)
                .build();

        ProductUpdateRequest request = new ProductUpdateRequest(
                null,
                null,
                BigDecimal.valueOf(45),
                ProductStatus.OUT_OF_STOCK,
                20
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponse response = productService.updateProduct(1L, request);

        verify(productRepository).findById(1L);
        verify(productRepository).save(existingProduct);
        
        assertEquals(1L, response.id());
        assertEquals(BigDecimal.valueOf(45), response.price());
        assertEquals(ProductStatus.OUT_OF_STOCK, response.status());
        assertEquals(20, response.quantityOnHand());
    }

    @Test
    void updateProduct_onlyPrice_shouldUpdateOnlyPrice() {
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Chicken Wings")
                .description("Spicy wings")
                .price(BigDecimal.valueOf(50))
                .status(ProductStatus.IN_STOCK)
                .quantityOnHand(100)
                .build();

        ProductUpdateRequest request = new ProductUpdateRequest(
                null,
                null,
                BigDecimal.valueOf(45),
                null,
                null
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        ProductResponse response = productService.updateProduct(1L, request);

        verify(productRepository).findById(1L);
        verify(productRepository).save(existingProduct);
        
        assertEquals(BigDecimal.valueOf(45), existingProduct.getPrice());
        assertEquals(ProductStatus.IN_STOCK, existingProduct.getStatus());
        assertEquals(100, existingProduct.getQuantityOnHand());
    }

    @Test
    void updateProduct_notFound_shouldThrowException() {
        ProductUpdateRequest request = new ProductUpdateRequest(
                null,
                null,
                BigDecimal.valueOf(45),
                ProductStatus.OUT_OF_STOCK,
                20
        );

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(99L, request));
        verify(productRepository, never()).save(any());
    }
}
