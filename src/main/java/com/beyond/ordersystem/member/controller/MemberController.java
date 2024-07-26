package com.beyond.ordersystem.member.controller;

import com.beyond.ordersystem.common.auth.JwtTokenProvider;
import com.beyond.ordersystem.common.dto.CommonErrorDto;
import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.member.dto.MemberLoginDto;
import com.beyond.ordersystem.member.dto.MemberResDto;
import com.beyond.ordersystem.member.dto.MemberSaveReqDto;
import com.beyond.ordersystem.member.repository.MemberRepository;
import com.beyond.ordersystem.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RequestMapping("/member")
@RestController
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MemberController(MemberService memberService, MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate (@Valid @RequestBody MemberSaveReqDto createDto) {
        memberService.memberCreate(createDto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "회원 가입 성공 !", createDto);
        ResponseEntity<CommonResDto> result = new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        return result;
    }

    @GetMapping("/list")
    public ResponseEntity<?> memberList(Pageable pageable){
        Page<MemberResDto> listDto = memberService.memberList(pageable);
        memberService.memberList(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "회원 목록 조회 성공 !", listDto);
        ResponseEntity<CommonResDto> result = new ResponseEntity<>(commonResDto, HttpStatus.OK);
        return result;
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto dto){
        // email, password 가 일치하는지 검증
        Member member = memberService.login(dto);
        // 일치할 경우 accessToken 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        // 생성된 토큰을 CommonResDto 에 담아 사용자에게 return.
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "로그인 성공 !", loginInfo);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
