package com.ecommerce.library.repository;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.model.Pet;
import com.ecommerce.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    @Query("select p from Product  p where p.is_activated = true and p.is_deleted = false")
    Page<Product> pageProduct(Pageable pageable);
    @Query("select p from Product p where p.description like %?1% or p.name like %?1% and p.is_activated = true and p.is_deleted = false")
    Page<Product> searchProducts(String keyword,Pageable pageable);
    @Query("select p from Product p where p.description like %?1% or p.name like %?1% and p.is_activated = true and p.is_deleted = false")
    List<Product>searchProductsList(String keyword);
    @Query(value = "select * from products WHERE  products.is_activated = 1 AND products.is_deleted = 0 " +
            "order by sold_quantity desc LIMIT 12 ", nativeQuery = true)
    List<Product> findTrendingProducts();
    @Query(value = "select * from products as pd where category_id = ?1 and pd.is_activated = 1 and pd.is_deleted = 0", nativeQuery = true)
    List<Product>findByCategoryId(Long category_id);
    @Query(value = "select * from products inner join categories ON products.category_id = categories.category_id where categories.pet_id = ?1 " +
            "and products.is_activated = 1 and products.is_deleted = 0", nativeQuery = true)
    List<Product> findByPetId(Long pet_id);
    @Query(value = "select pd.category_id,pd.name,pd.image,pd.is_activated," +
            "pd.is_deleted,pd.sold_quantity,pd.sale_price,pd.product_id,pd.cost_price,pd.current_quantity,pd.description" +
            " from products as pd inner join categories as c ON pd.category_id = c.category_id where c.slug = ?1 " +
            "and pd.is_activated = 1 and pd.is_deleted = 0", nativeQuery = true)
    List<Product>findAllProductsByCategorySlug(String slug);
    @Query(value = "select pd.category_id,pd.name,pd.image,pd.is_activated," +
            "pd.is_deleted,pd.sold_quantity,pd.sale_price,pd.product_id,pd.cost_price,pd.current_quantity,pd.description" +
            " from products as pd inner join categories as c ON pd.category_id = c.category_id where c.slug = ?1 " +
            "and pd.is_activated = 1 and pd.is_deleted = 0 order by pd.sale_price desc ", nativeQuery = true)
    List<Product>findByCategorySlugDesc(String slug);
    @Query(value = "select pd.category_id,pd.name,pd.image,pd.is_activated," +
            "pd.is_deleted,pd.sold_quantity,pd.sale_price,pd.product_id,pd.cost_price,pd.current_quantity,pd.description" +
            " from products as pd inner join categories as c ON pd.category_id = c.category_id where c.slug = ?1 " +
            "and pd.is_activated = 1 and pd.is_deleted = 0 order by pd.sale_price asc", nativeQuery = true)
    List<Product>findByCategorySlugAsc(String slug);
    @Query(value = "select pd.category_id,pd.name,pd.image,pd.is_activated,pd.is_deleted,pd.sold_quantity," +
            "pd.sale_price,pd.product_id,pd.cost_price,pd.current_quantity,pd.description" +
            " from products as pd inner join categories as c " +
            "ON pd.category_id = c.category_id " +
            "inner join pet as p " +
            "on p.pet_id = c.pet_id " +
            "where p.slug = ?1 and pd.is_activated = 1 and pd.is_deleted = 0", nativeQuery = true)
    List<Product>findAllProductsByPetSlug(String slug);
    @Query(value = "select pd.category_id,pd.name,pd.image,pd.is_activated,pd.is_deleted,pd.sold_quantity," +
            "pd.sale_price,pd.product_id,pd.cost_price,pd.current_quantity,pd.description" +
            " from products as pd inner join categories as c " +
            "ON pd.category_id = c.category_id " +
            "inner join pet as p " +
            "on p.pet_id = c.pet_id " +
            "where p.slug = ?1 and pd.is_activated = 1 and pd.is_deleted = 0 order by pd.sale_price asc", nativeQuery = true)
    List<Product>findByPetSlugAsc(String slug);
    @Query(value = "select pd.category_id,pd.name,pd.image,pd.is_activated,pd.is_deleted,pd.sold_quantity," +
            "pd.sale_price,pd.product_id,pd.cost_price,pd.current_quantity,pd.description" +
            " from products as pd inner join categories as c " +
            "ON pd.category_id = c.category_id " +
            "inner join pet as p " +
            "on p.pet_id = c.pet_id " +
            "where p.slug = ?1 and pd.is_activated = 1 and pd.is_deleted = 0 order by pd.sale_price desc", nativeQuery = true)
    List<Product>findByPetSlugDesc(String slug);
    @Query("select p from Product  p where p.is_activated=true and p.is_deleted=false order by p.costPrice desc ")
    List<Product> filterHighPrice();
    @Query("select p from Product  p where p.is_activated=true and p.is_deleted=false order by p.costPrice asc ")
    List<Product> filterLowPrice();

}
