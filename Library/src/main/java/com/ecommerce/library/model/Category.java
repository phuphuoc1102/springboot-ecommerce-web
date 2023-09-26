package com.ecommerce.library.model;

import com.ecommerce.library.ultils.StringToUrl;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = {"name","slug"}))
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    private String name;
    @Column(name = "is_activated")
    private boolean activated;
    @Column(name = "is_deleted")
    private boolean deleted;
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private String image;
    @ManyToOne (fetch=FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="pet_id",referencedColumnName = "pet_id")
    private Pet pet;
    private String slug;

    public Category(String name) {
        this.name = name;
        this.activated = true;
        this.deleted = false;
        this.image = null;
        this.slug = StringToUrl.convertSpaceToSlash(StringToUrl.removeAccent(name).toLowerCase());
    }
}