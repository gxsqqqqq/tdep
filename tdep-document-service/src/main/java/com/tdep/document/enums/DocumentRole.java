package com.tdep.document.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentRole {

    PARTY("当事人"),
    JUDGE("法官"),
    CLERK("书记员"),
    ADMIN("管理员");

    private final String description;
}
