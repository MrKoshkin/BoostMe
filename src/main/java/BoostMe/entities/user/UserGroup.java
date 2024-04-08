package BoostMe.entities.user;

public enum UserGroup {
    COMMON_CAN_AUTH("Может авторизоваться в системе"),

    PANEL_SEE_USER_CONTROLLER("Может видеть пользователей системы"),

    REDDIT_ACCOUNT("Пользователь reddit");

    private String label;

    UserGroup(String label) {
        this.label = label;
    }

}