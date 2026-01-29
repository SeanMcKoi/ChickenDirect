package no.chickendirect.config;

import net.datafaker.Faker;
import no.chickendirect.address.Address;
import no.chickendirect.address.AddressRepository;
import no.chickendirect.customer.Customer;
import no.chickendirect.customer.CustomerRepository;
import no.chickendirect.order.Order;
import no.chickendirect.order.OrderRepository;
import no.chickendirect.orderitem.OrderItem;
import no.chickendirect.product.Product;
import no.chickendirect.product.ProductRepository;
import no.chickendirect.productstatus.ProductStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Profile("dev")
@Component
public class FakeDataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    private final Faker faker = new Faker(Locale.forLanguageTag("nb-NO"));
    private final Random random = new Random();

    private final List<String> norwegianCities = List.of(
            "Oslo", "Bergen", "Trondheim", "Stavanger", "Sandnes", "Kristiansand",
            "Drammen", "Fredrikstad", "Tromsø", "Ålesund", "Sarpsborg", "Skien"
    );

    public FakeDataSeeder(CustomerRepository customerRepository,
                          AddressRepository addressRepository,
                          ProductRepository productRepository,
                          OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {

        if (customerRepository.count() > 10 || productRepository.count() > 10) {
            System.out.println("FakeDataSeeder: data exists, skipping seeding.");
            return;
        }

        System.out.println("FakeDataSeeder: generating fake data...");

        for (int i = 0; i < 20; i++) {

            Customer c = new Customer();

            String fullName = faker.name().fullName();
            c.setName(fullName);
            c.setEmail(generateEmailFromName(fullName));
            c.setPhone(norwegianPhoneNumber());

            customerRepository.save(c);

            Address a = new Address();
            a.setStreet(faker.address().streetName() + " " + faker.number().numberBetween(1, 120));
            a.setCity(norwegianCities.get(random.nextInt(norwegianCities.size())));
            a.setPostalCode(norwegianPostalCode());
            a.setCountry("Norway");
            a.setCustomer(c);

            addressRepository.save(a);
        }

        List<Customer> customers = customerRepository.findAll();

        Map<String, BigDecimal> chickenPrices = Map.ofEntries(
                Map.entry("Live Chicken - Layer Hen", BigDecimal.valueOf(299)),
                Map.entry("Live Chicken - Broiler", BigDecimal.valueOf(249)),
                Map.entry("Live Chicken - Free-Range Hen", BigDecimal.valueOf(349)),
                Map.entry("Live Chicken - Rooster", BigDecimal.valueOf(199)),
                Map.entry("Live Chicken - Organic Broiler", BigDecimal.valueOf(399)),

                Map.entry("Chicken Breast - Fresh", BigDecimal.valueOf(89)),
                Map.entry("Chicken Breast - Frozen", BigDecimal.valueOf(69)),
                Map.entry("Chicken Thighs - Fresh", BigDecimal.valueOf(79)),
                Map.entry("Chicken Thighs - Frozen", BigDecimal.valueOf(59)),
                Map.entry("Chicken Wings - Fresh", BigDecimal.valueOf(49)),
                Map.entry("Chicken Wings - Frozen", BigDecimal.valueOf(39)),
                Map.entry("Chicken Drumsticks - Fresh", BigDecimal.valueOf(55)),
                Map.entry("Chicken Drumsticks - Frozen", BigDecimal.valueOf(45)),
                Map.entry("Whole Chicken - Fresh", BigDecimal.valueOf(129)),
                Map.entry("Whole Chicken - Frozen", BigDecimal.valueOf(99)),
                Map.entry("Chicken Mince - Fresh", BigDecimal.valueOf(85)),
                Map.entry("Chicken Cutlets - Fresh", BigDecimal.valueOf(95)),
                Map.entry("Chicken Strips - Fresh", BigDecimal.valueOf(89)),
                Map.entry("Chicken Fillets - Frozen", BigDecimal.valueOf(75)),
                Map.entry("Marinated Chicken Pieces - Fresh", BigDecimal.valueOf(109))
        );

        Map<String, Integer> chickenQuantities = Map.ofEntries(
                Map.entry("Live Chicken - Layer Hen", 20),
                Map.entry("Live Chicken - Broiler", 30),
                Map.entry("Live Chicken - Free-Range Hen", 15),
                Map.entry("Live Chicken - Rooster", 10),
                Map.entry("Live Chicken - Organic Broiler", 12),

                Map.entry("Chicken Breast - Fresh", 80),
                Map.entry("Chicken Breast - Frozen", 100),
                Map.entry("Chicken Thighs - Fresh", 70),
                Map.entry("Chicken Thighs - Frozen", 90),
                Map.entry("Chicken Wings - Fresh", 120),
                Map.entry("Chicken Wings - Frozen", 150),
                Map.entry("Chicken Drumsticks - Fresh", 60),
                Map.entry("Chicken Drumsticks - Frozen", 80),
                Map.entry("Whole Chicken - Fresh", 40),
                Map.entry("Whole Chicken - Frozen", 50),
                Map.entry("Chicken Mince - Fresh", 55),
                Map.entry("Chicken Cutlets - Fresh", 50),
                Map.entry("Chicken Strips - Fresh", 65),
                Map.entry("Chicken Fillets - Frozen", 70),
                Map.entry("Marinated Chicken Pieces - Fresh", 45)
        );

        List<String> productNames = new ArrayList<>(chickenPrices.keySet());

        for (int i = 0; i < 30; i++) {
            Product p = new Product();

            String name = productNames.get(random.nextInt(productNames.size()));

            p.setName(name);
            p.setDescription(
                    name.startsWith("Live")
                            ? "Healthy live chicken suitable for farming or egg production."
                            : "Quality chicken meat, ideal for cooking."
            );

            p.setPrice(chickenPrices.get(name));

            int initialQuantity = chickenQuantities.getOrDefault(name, 0);
            p.setQuantityOnHand(initialQuantity);

            if (initialQuantity > 0) {
                p.setStatus(ProductStatus.IN_STOCK);
            } else {
                p.setStatus(ProductStatus.OUT_OF_STOCK);
            }

            productRepository.save(p);
        }

        List<Product> products = productRepository.findAll();
        List<Address> allAddresses = addressRepository.findAll();

        for (int i = 0; i < 40; i++) {

            boolean anyInStock = products.stream().anyMatch(p -> p.getQuantityOnHand() > 0);
            if (!anyInStock) {
                break;
            }

            Customer c = customers.get(random.nextInt(customers.size()));

            Address shippingAddress = allAddresses.stream()
                    .filter(a -> a.getCustomer().getId().equals(c.getId()))
                    .findFirst()
                    .orElseThrow();

            Order order = new Order();
            order.setCustomer(c);
            order.setCustomerName(c.getName());
            order.setCustomerEmail(c.getEmail());
            order.setCustomerPhone(c.getPhone());
            order.setShippingStreet(shippingAddress.getStreet());
            order.setShippingCity(shippingAddress.getCity());
            order.setShippingPostalCode(shippingAddress.getPostalCode());
            order.setShippingCountry(shippingAddress.getCountry());
            order.setShippingCharge(BigDecimal.valueOf(99));
            order.setIsShipped(random.nextBoolean());

            BigDecimal itemsTotal = BigDecimal.ZERO;

            int orderItems = faker.number().numberBetween(1, 4);
            boolean hasItems = false;

            for (int j = 0; j < orderItems; j++) {

                List<Product> availableProducts = products.stream()
                        .filter(p -> p.getQuantityOnHand() > 0)
                        .toList();

                if (availableProducts.isEmpty()) {
                    break;
                }

                Product p = availableProducts.get(random.nextInt(availableProducts.size()));

                int maxQty = p.getQuantityOnHand();
                int requestedQty = faker.number().numberBetween(1, 6);
                int qty = Math.min(requestedQty, maxQty);

                if (qty <= 0) {
                    continue;
                }

                OrderItem item = new OrderItem();
                item.setProduct(p);
                item.setQuantity(qty);

                order.addOrderItem(item);
                hasItems = true;

                itemsTotal = itemsTotal.add(p.getPrice().multiply(BigDecimal.valueOf(qty)));

                p.setQuantityOnHand(p.getQuantityOnHand() - qty);

                if (p.getQuantityOnHand() == 0) {
                    p.setStatus(ProductStatus.OUT_OF_STOCK);
                }

                productRepository.save(p);
            }

            if (!hasItems) {
                continue;
            }

            order.setTotalPrice(itemsTotal.add(order.getShippingCharge()));

            orderRepository.save(order);
        }

        System.out.println("FakeDataSeeder: finished.");
    }


    private String norwegianPhoneNumber() {
        int firstDigit = random.nextBoolean() ? 9 : 4;

        String rest = String.format("%07d", random.nextInt(10_000_000));

        return String.format("+47 %d%s %s %s",
                firstDigit,
                rest.substring(0, 2),
                rest.substring(2, 4),
                rest.substring(4)
        );
    }

    private String norwegianPostalCode() {
        return String.format("%04d", random.nextInt(9999) + 1);
    }

    private String generateEmailFromName(String name) {
        String local = name.toLowerCase()
                .replace("æ", "ae")
                .replace("ø", "o")
                .replace("å", "a")
                .replace(" ", ".")
                .replaceAll("[^a-z.]", "");

        local = local.replaceAll("\\.+", ".");

        local = local.replaceAll("^\\.+", "");
        local = local.replaceAll("\\.+$", "");

        if (local.isEmpty()) {
            local = "user" + random.nextInt(1_000_000);
        }

        List<String> domains = List.of(
                "gmail.com",
                "outlook.com",
                "hotmail.com",
                "live.no",
                "icloud.com",
                "yahoo.com"
        );

        String domain = domains.get(random.nextInt(domains.size()));

        return local + "@" + domain;
    }

}
