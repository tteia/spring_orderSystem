package com.beyond.ordersystem.member.service;

import com.beyond.ordersystem.member.dto.MemberSaveReqDto;
import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired // 생성자 1개일 때는 생략 가능.
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member memberCreate(MemberSaveReqDto createDto){
        Member member = createDto.toEntity();
        Member success = memberRepository.save(member);
        return success;
    }

}
