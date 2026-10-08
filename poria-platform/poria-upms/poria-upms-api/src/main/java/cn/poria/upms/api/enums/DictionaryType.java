package cn.poria.upms.api.enums;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class DictionaryType {

    private int id;

    private String name;

    public static final DictionaryType SYSTEM = new DictionaryType(0, "系统字典");

    public static final DictionaryType CUSTOMER = new DictionaryType(1, "用户字典");

    public static final List<DictionaryType> ALL;

    private DictionaryType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String getName(int id) {
        for (DictionaryType type : ALL) {
            if (type.getId() == id) {
                return type.getName();
            }
        }

        return "未知";
    }

    static {
        ALL = Arrays.asList(SYSTEM, CUSTOMER);
    }
}
