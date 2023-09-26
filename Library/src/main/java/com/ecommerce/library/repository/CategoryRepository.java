package com.ecommerce.library.repository;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
    @Query(value = "update Category set name = ?1 where id = ?2")
    Category update(String name, Long id);

    @Query(value = "select * from categories where is_activated = true", nativeQuery = true)
    List<Category> findAllByActivatedTrue();

//    @Query(value = "select new com.ecommerce.library.dto.CategoryDto(c.id, c.name, count(p.category.id)) " +
//            "from Category c left join Product p on c.id = p.category.id " +
//            "where c.activated = true and c.deleted = false " +
//            "group by c.id ")
//    List<CategoryDto> getCategoriesAndSize();
    List<Category> findByPetId(Long id);
    @Query(value = "SELECT pdc.* FROM categories AS pdc INNER JOIN products AS pd ON pd.category_id=pdc.category_id GROUP BY pdc.category_id ORDER BY SUM(pd.sold_quantity) DESC LIMIT 0,6",nativeQuery = true)
    public List<Category> listFeatureCategory();
    @Query("select c from Category  c where c.deleted= false and c.activated= true")
    List<Category>getCategoryAndProduct();
}
