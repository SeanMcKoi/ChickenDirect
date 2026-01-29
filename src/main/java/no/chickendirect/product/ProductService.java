package no.chickendirect.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.chickendirect.exception.ProductNotFoundException;
import no.chickendirect.product.dto.ProductRequest;
import no.chickendirect.product.dto.ProductResponse;
import no.chickendirect.product.dto.ProductUpdateRequest;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product with name={}", request.name());
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .status(request.status())
                .quantityOnHand(request.quantityOnHand())
                .build();

        Product saved = productRepository.save(product);
        return toProductResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = getProductEntity(id);
        return toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toProductResponse)
                .toList();
    }

    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        log.info("Updating product with id={}", id);
        Product product = getProductEntity(id);
        
        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.status() != null) {
            product.setStatus(request.status());
        }
        if (request.quantityOnHand() != null) {
            product.setQuantityOnHand(request.quantityOnHand());
        }
        
        Product updated = productRepository.save(product);
        return toProductResponse(updated);
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with id={}", id);
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus(),
                product.getQuantityOnHand()
        );
    }
}
