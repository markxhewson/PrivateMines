package tech.markxhewson.mines.util;

import lombok.Getter;

@Getter
public enum EnchantmentsEnum {

    DIG_SPEED("Efficiency");

    private final String actualEnchantmentName;

    EnchantmentsEnum(String actualEnchantmentName) {
        this.actualEnchantmentName = actualEnchantmentName;
    }

}
