
package com.tenco.bank.dto;

import lombok.Data;

@Data
public class KakaoProfile {

    private Long id; // public -> private으로 바꾸기  객체의 속성은 변수로 바꾸는게 아니라 행위로 바꾸는 것이다!!!
    private String connectedAt;
    private Properties properties;
    private KakaoAccount kakaoAccount;

    // 내부 클래스
    // 내부 클래스
    // 내부 클래스
}
