package com.cun.cyberguard.web.dto;

import java.util.Arrays;
import java.util.function.Function;

public record OpcionDto(String valor, String etiqueta) {

    public static <E extends Enum<E>> java.util.List<OpcionDto> de(E[] valores, Function<E, String> etiqueta) {
        return Arrays.stream(valores).map(valor -> new OpcionDto(valor.name(), etiqueta.apply(valor))).toList();
    }
}
