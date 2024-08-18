package kr.taeu.domain.entity;

public class RouterId {

    private String id;

    public RouterId(String id) {
        this.id = id;
    }

    public static RouterId of (String id) {
        return new RouterId(id);
    }
}
