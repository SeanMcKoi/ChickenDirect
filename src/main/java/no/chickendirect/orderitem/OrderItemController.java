package no.chickendirect.orderitem;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.chickendirect.orderitem.dto.OrderItemRequest;
import no.chickendirect.orderitem.dto.OrderItemResponse;

@RestController
@RequestMapping("api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItemResponse createOrderItem(@Valid @RequestBody OrderItemRequest request) {
        return orderItemService.createOrderItem(request);
    }

    @GetMapping("{id}")
    public OrderItemResponse getOrderItem(@PathVariable Long id) {
        return orderItemService.getOrderItem(id);
    }

    @GetMapping
    public List<OrderItemResponse> getAllOrderItems() {
        return orderItemService.getAllOrderItems();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
    }
}
