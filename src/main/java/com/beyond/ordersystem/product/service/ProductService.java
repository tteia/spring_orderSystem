package com.beyond.ordersystem.product.service;

import com.beyond.ordersystem.common.service.StockInventoryService;
import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.dto.ProductResDto;
import com.beyond.ordersystem.product.dto.ProductSaveReqDto;
import com.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductService{

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final ProductRepository productRepository;
    private final S3Client s3Client;
    private final StockInventoryService stockInventoryService;

    @Autowired
    public ProductService(ProductRepository productRepository, S3Client s3Client, StockInventoryService stockInventoryService) {
        this.productRepository = productRepository;
        this.s3Client = s3Client;
        this.stockInventoryService = stockInventoryService;
    }

    public Product productCreate(ProductSaveReqDto createDto){
        MultipartFile image = createDto.getProductImage();
        Product product = null;
        try {
            product = productRepository.save(createDto.toEntity());
            byte[] bytes = image.getBytes();
//            Path path = Paths.get("/Users/tteia/Desktop/tmp/", UUID.randomUUID() + "_" + image.getOriginalFilename());  // UUID = 난수 값. 파일명이 겹치지 않게 해줌.
            Path path = Paths.get("/Users/tteia/Desktop/tmp/", product.getId() + "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString());

            if(createDto.getName().contains("sale")){
                stockInventoryService.increaseStock(product.getId(), createDto.getStockQuantity());
            }
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패 !"); // 트랜잭션 처리를 위해 예외 잡아주기
        }
        return product;
    }

    public Page<ProductResDto> productList(Pageable pageable){
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(a->a.fromEntity());

    }

    public Product productAwsCreate(ProductSaveReqDto createDto){
        MultipartFile image = createDto.getProductImage();
        Product product = null;
        try {
            product = productRepository.save(createDto.toEntity());
            byte[] bytes = image.getBytes();
            String fileName = product.getId() + "_" + image.getOriginalFilename();
            Path path = Paths.get("/Users/tteia/Desktop/tmp/", fileName);

            // local pc 에 임시 저장.
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            // aws 에 pc 저장 파일을 업로드.
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();                                                                      // 실습 시 확인을 위해 .fromFile 해줬는데 .fromByte 로 바로 해줘도 됨 !
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
            String s3Path = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm(); // 우리 파일 이름이 아니라 s3 관련 이름으로 나옴.
            product.updateImagePath(s3Path);
            // https://tteia-file.s3.ap-northeast-2.amazonaws.com/A1.png
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패 !"); // 트랜잭션 처리를 위해 예외 잡아주기
        }
        return product;
    }
}
