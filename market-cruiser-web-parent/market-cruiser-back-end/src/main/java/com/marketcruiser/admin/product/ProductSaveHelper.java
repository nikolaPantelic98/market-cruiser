package com.marketcruiser.admin.product;

import com.marketcruiser.admin.AmazonS3Util;
import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.entity.product.ProductImage;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The ProductSaveHelper class is a helper class for saving and updating product entities in the database.
 */
public class ProductSaveHelper {

    /**
     * Sets the name of the main image for a product based on the uploaded file.
     *
     * @param mainImageMultipart The multipart file containing the main image.
     * @param product The product entity to be updated.
     */
    static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    /**
     * Sets the names of the extra images for a product based on the uploaded files.
     *
     * @param extraImageMultiparts The array of multipart files containing the extra images.
     * @param product The product entity to be updated.
     */
    static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        if (extraImageMultiparts.length > 0) {
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    /**
     * Saves the uploaded main image and extra images to disk.
     *
     * @param mainImageMultipart The multipart file containing the main image.
     * @param extraImageMultiparts The array of multipart files containing the extra images.
     * @param savedProduct The saved product entity.
     * @throws IOException If there is an error saving the images to disk.
     */
    static void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            String uploadDir = "product-images/" + savedProduct.getProductId();

            List<String> listObjectKeys = AmazonS3Util.listFolder(uploadDir + "/");

            for (String objectKey : listObjectKeys) {
                if (!objectKey.contains("/extras/")) {
                    AmazonS3Util.deleteFile(objectKey);
                }
            }

            AmazonS3Util.uploadFile(uploadDir, fileName, mainImageMultipart.getInputStream());
        }

        if (extraImageMultiparts.length > 0) {
            String uploadDir = "product-images/" + savedProduct.getProductId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
            }
        }
    }

    /**
     * Helper method that sets the product details for the given product based on the provided arrays of names and values.
     * @param detailIDs The array of detail IDs.
     * @param detailNames The array of detail names.
     * @param detailValues The array of detail values.
     * @param product The product entity to be updated.
     */
    static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];
            Long id = Long.parseLong(detailIDs[count]);

            if (id != 0) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }

    /**
     * Sets the names for the existing extra images for a product.
     *
     * @param imageIDs The array of image IDs.
     * @param imageNames The array of image names.
     * @param product The product entity to be updated.
     */
    static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;

        Set<ProductImage> images = new HashSet<>();
        for (int count = 0; count < imageIDs.length; count++) {
            Long id = Long.parseLong(imageIDs[count]);
            String name = imageNames[count];
            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);
    }

    /**
     * Deletes the extra images that were removed on the form.
     *
     * @param product The product entity to be updated.
     */
    static void deleteExtraImagesWereRemovedOnForm(Product product) {
        String extraImageDir = "product-images/" + product.getProductId() + "/extras";
        List<String> listObjectKeys = AmazonS3Util.listFolder(extraImageDir);

        for (String objectKey : listObjectKeys) {
            int lastIndexOfSlash = objectKey.lastIndexOf("/");
            String fileName = objectKey.substring(lastIndexOfSlash + 1, objectKey.length());

            if (!product.containsImageName(fileName)) {
                AmazonS3Util.deleteFile(objectKey);
            }
        }
    }
}
