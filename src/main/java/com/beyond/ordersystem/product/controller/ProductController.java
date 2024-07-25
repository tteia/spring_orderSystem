package com.beyond.ordersystem.product.controller;

import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.dto.ProductResDto;
import com.beyond.ordersystem.product.dto.ProductSaveReqDto;
import com.beyond.ordersystem.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")                 // @RequestBody 쓰면 안 됨. @ModelAttribute (생략 가능) 쓰거나 아래처럼 json 두 번 받기.
//    public ResponseEntity<?> productCreate(@RequestPart ProductSaveReqDto createDto, @RequestPart MultipartFile productImage){
    public ResponseEntity<?> productCreate(ProductSaveReqDto createDto){
        Product product = productService.productCreate(createDto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "상품이 성공적으로 등록 되었습니다.", product.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public List<ProductResDto> productList(){
        return null;
    }

}
