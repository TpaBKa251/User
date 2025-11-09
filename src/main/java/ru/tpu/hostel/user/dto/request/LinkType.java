package ru.tpu.hostel.user.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LinkType {

    TG("Телеграмм"),

    VK("ВК");

    private final String label;

}