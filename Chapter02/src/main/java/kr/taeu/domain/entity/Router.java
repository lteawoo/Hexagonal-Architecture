package kr.taeu.domain.entity;

import java.util.function.Predicate;

public class Router {

    private final RouterType routerType;
    private final RouterId routerId;

    public Router(RouterType routerType, RouterId routerId) {
        this.routerType = routerType;
        this.routerId = routerId;
    }

    public static Predicate<Router> filterRouterByType(RouterType routerType) {
        return routerType.equals(RouterType.CORE)
                ? isCore() :
                isEdge();
    }

    private static Predicate<Router> isCore() {
        return router -> router.getRouterType() == RouterType.CORE;
    }

    private static Predicate<Router> isEdge() {
        return router -> router.getRouterType() == RouterType.EDGE;
    }

    public RouterType getRouterType() {
        return routerType;
    }
}
