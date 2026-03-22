package es.maryshopping.backend.catalog.usecases.images.upload_image.infrastructure.rest;

import es.maryshopping.backend.catalog.usecases.images.upload_image.application.UploadImageCommand;
import es.maryshopping.backend.catalog.usecases.images.upload_image.application.UploadImageResult;
import es.maryshopping.backend.catalog.usecases.images.upload_image.application.UploadImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/catalog/products/images")
public class UploadImageController {
    private final UploadImageService uploadImageService;

    public UploadImageController(UploadImageService uploadImageService) {
        this.uploadImageService = uploadImageService;
    }
//Para subir la imagen ,la peticion tiene que ser de tipo multipart/form-data.El fichero se envia por partes por el front.
    @PostMapping(value="", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadImageResponse> proceed(
            @RequestParam("imageFile") MultipartFile imageFile, //SON  LOS BYTES DE LA IMAGEN
            @RequestParam("order") int order,
//            @RequestParam("mimeType") String mimeType, //Lo coge por defecto del nombrte del fichero
            @RequestParam("primary") boolean primary,
            @RequestParam("productId") UUID productId){
        UploadImageCommand command = null; //Inicializamos el comando a null para luego asignarle el valor dentro del bloque
        try {
            command = mapRequestToCommand(imageFile, order, primary, productId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UploadImageResult result = uploadImageService.proceed(command);
        UploadImageResponse response = mapFromResultToResponse(result);
        return ResponseEntity.status(200).body(response);
    }

    private UploadImageCommand mapRequestToCommand(MultipartFile imageFile, int order, boolean primary, UUID productId) throws IOException {
        return UploadImageCommand.builder()
                .bytes(imageFile.getBytes())
                .mimeType(imageFile.getContentType())
                .order(order)
                .primary(primary)
                .productId(productId)
                .build(
                );
    }

    private UploadImageResponse mapFromResultToResponse(UploadImageResult result) {
        return UploadImageResponse.builder()
                .imageId(result.imageId())
                .build();
    }
}
