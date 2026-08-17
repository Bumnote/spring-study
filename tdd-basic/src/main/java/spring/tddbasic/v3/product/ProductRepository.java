package spring.tddbasic.v3.product;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
class ProductRepository {

  private final Map<Long, Product> persistence = new HashMap<>();

  private Long sequence = 0L;

  public void save(Product product) {
    product.assignId(++sequence);
    persistence.put(product.getId(), product);
  }
}
