package es.maryshopping.backend.catalog.shared.infrastructure.persistence;

import es.maryshopping.backend.catalog.shared.domain.product.image.Image;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="product_image", schema = "catalog")
public class ImageEntity {
    @Id
    UUID id;
    UUID productId;
    String imageMimeType;
    Integer imageOrder;
    Boolean isPrimary;
    byte[] imageBytes;

    public static ImageEntity fromDomain(Image image){
        return new ImageEntity(
                image.id().value(),
                image.productId().value(),
                image.mimeType().value(),
                image.order().value(),
                image.primary().value(),
                image.bytes().value()
        );
    }
}
