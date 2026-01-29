package no.chickendirect.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.chickendirect.order.dto.OrderRequest;
import no.chickendirect.order.dto.OrderResponse;
import no.chickendirect.order.dto.OrderUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("{id}")
    public OrderResponse updateOrder(@PathVariable Long id, @Valid @RequestBody OrderUpdateRequest request) {
        return orderService.updateOrder(id, request);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
