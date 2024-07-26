package com.beyond.ordersystem.member.repository;

import com.beyond.ordersystem.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    // 아래 선언이 없어도 Service 에서 Page 로 선언해서 쓴다면 알아서 Page 로 변환해준다 !
    Page<Member> findAll(Pageable pageable); // List<Member> 로 쓰고싶으면 이 선언이 있어도 pageable 을 안 써주면 된다.
}
