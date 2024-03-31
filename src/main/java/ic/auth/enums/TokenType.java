package ic.auth.enums;

public enum TokenType {
    ACCESS_TOKEN("access"), REFRESH_TOKEN("refresh");
    private final String name;

    TokenType(String name) {
        this.name = name;
    }
}