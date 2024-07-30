package com.beyond.ordersystem.product.service;

import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.dto.ProductResDto;
import com.beyond.ordersystem.product.dto.ProductSaveReqDto;
import com.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
            Path path = Paths.get("/Users/tteia/Desktop/tmp/", product.getId() + "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString());
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패 !"); // 트랜잭션 처리를 위해 예외 잡아주기
        }
        return product;
    }
}
