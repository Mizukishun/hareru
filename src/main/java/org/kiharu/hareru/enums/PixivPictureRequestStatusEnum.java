package org.kiharu.hareru.enums;

/**
 * @Author kiharu
 * @Date 2020-04-29
 */
public enum PixivPictureRequestStatusEnum {
    INIT(0, "初始"),
    SUCCESS(1, "成功"),
    RETRY(2, "待重试"),
    FAIL(3, "失败");

    PixivPictureRequestStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
