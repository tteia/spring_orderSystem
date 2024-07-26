package com.beyond.ordersystem.ordering.service;

import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.member.repository.MemberRepository;
import com.beyond.ordersystem.ordering.domain.OrderDetail;
import com.beyond.ordersystem.ordering.domain.Ordering;
import com.beyond.ordersystem.ordering.dto.OrderListResDto;
import com.beyond.ordersystem.ordering.dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.repository.OrderDetailRepository;
import com.beyond.ordersystem.ordering.repository.OrderingRepository;
import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderingService{
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository, OrderDetailRepository orderDetailRepository) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Ordering orderCreate(OrderSaveReqDto dto) {

        // 방법 1. 조금 더 쉬운 방식
        // Ordering 생성 : member_id, status
//        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(() -> new EntityNotFoundException("회 원 없 음 ."));
//        Ordering ordering = orderingRepository.save(dto.toEntity(member));
//
//        // OrderDetail 생성 : order_id, product_id, quantity
//        for (OrderSaveReqDto.OrderDto orderDto : dto.getOrderDtos()) {
//            Product product = productRepository.findById(orderDto.getProductId()).orElseThrow(() -> new EntityNotFoundException("회 원 없 음 ."));
//            int quantity = orderDto.getProductCount();
//            OrderDetail orderDetail = OrderDetail.builder()
//                    .product(product)
//                    .quantity(quantity)
//                    .ordering(ordering)
//                    .build();
//            orderDetailRepository.save(orderDetail);
//        }
//        return ordering;


//        // 방법 2. JPA 최적화 방식
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(() -> new EntityNotFoundException("회 원 없 음 ."));
        Ordering ordering = Ordering.builder()
                .member(member)
//                .orderDetails() > 아직 없어서 세팅할 수가 없음! 주문을 해야 디테일이 생기니까 . .
                .build();
        for (OrderSaveReqDto.OrderDto orderDto : dto.getOrderDtos()) {
            Product product = productRepository.findById(orderDto.getProductId()).orElseThrow(() -> new EntityNotFoundException("회 원 없 음 ."));
            int quantity = orderDto.getProductCount();
            if(product.getStockQuantity() < quantity){
                throw new IllegalArgumentException("재고가 부족합니다. 주문량을 확인해주세요.");
            }
            product.updateStockQuantity(quantity); // 변경 감지(더티 체킹)로 인해 별도의 save 불필요함.
            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(quantity)
                    .ordering(ordering)
                    .build();
            // 방법 1 과의 차이점. Repository 에 save 하는 게 아님.
            // save 를 뒤에서 해줘도 JPA 에서 알아서 먼저 실행시켜준다.
            ordering.getOrderDetails().add(orderDetail);
        }
        Ordering savedOrder = orderingRepository.save(ordering);
        return savedOrder   ;
    }

    public List<OrderListResDto> orderList() {
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for (Ordering ordering : orderings) {
            orderListResDtos.add(ordering.fromEntity());
        }
        return orderListResDtos;
    }
}
