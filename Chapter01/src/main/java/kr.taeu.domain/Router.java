package java.kr.taeu.domain;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * RouterType과 RouterId로 구성된 Router 엔티티 클래스
 */
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

    public static List<Router> retrieveRouter(List<Router> routers, Predicate<Router> predicate) {
        return routers.stream()
                .filter(predicate)
                .collect(Collectors.<Router>toList());
    }

    public RouterType getRouterType() {
        return routerType;
    }
}
