package com.beyond.ordersystem.member.dto;

import com.beyond.ordersystem.common.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResDto {
    private Long id;
    private String name;
    private String email;
    private Address address;
}
