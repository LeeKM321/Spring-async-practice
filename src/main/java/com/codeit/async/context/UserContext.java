package com.codeit.async.context;

public class UserContext {

    // ThreadLocal: 스레드마다 가지는 독립적인 저장소 (각 스레드가 자기만의 변수를 가진다)
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(String username) {
        currentUser.set(username);
    }

    public static String getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }


}
