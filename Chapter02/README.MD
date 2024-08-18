# 도메인 헥사곤으로 비즈니스 모델링
## 엔티티를 활용한 문제영역 모델링
### 도메인 엔티티의 순수성
문제영역 모델링의 주요 초점은 최대한 실제 시나리오를 코드로 변환하는 것, 비즈니스 요구사항을 이해하고 코드로 변환하는데 실패하면 결과적으로 비용의 손실이 발생할 수 밖에 없다

문제영역 모델링의 핵심은 엔티티를 만드는 것이다. 엔티티가 비즈니스 요구사항과 밀접한 관계를 가져야 하기 때문에, 비즈니스 관련 코드와 기술 관련 코드가 혼동되는 것을 방지 해야한다.(기술적: 소프트웨어 맥락에서만 존재하고 의미가 있는 것)

헥사고날 아키텍처는 전통적인 비즈니스 문제를 해결하려는 프로젝트에 초점이 맞춰져 있기 때문에, 새로운 개발 프레임워크와 같은 순수하게 기술적인 프로젝트에는 최선의 접근 방법이 아닐 수도 있다.

도메인 엔티티는 비즈니스 관심사만 처리한다는 점에서 순수해야 한다.

### 적절하지 않은 엔티티 구성
비즈니스 규칙, 비즈니스 데이터 두 요소는 엔티티의 특징을 결정한다. 비즈니스 데이터만 표현하는 데이터베이스 엔티티 객체와 유사하게 모델링된 엔티티 클래스를 보는 건 드문 일이 아니다.

모델링하려는 엔티티에 본질적이지 않은 로직으로 엔티티 클래스에 과부하를 주면 안된다. 이러한 오퍼레이션에 대해서는 서비스를 통해 수용할 수 있다.

이전 `Router`클래스에서는 라우터들을 필터링하고 나열하기 위해 retrieveRouter 메서드를 만들었다.

```java
public static List<Router> retrieveRouter(List<Router> routers, Predicate<Router> predicate) {
    return routers.stream()
            .filter(predicate)
            .collect(Collectors.<Router>toList());
}
```
실제 세계에서 이 동작은 라우터의 본질적인 특성으로 볼 수 없다. 이는 서비스를 통해 수용해야 한다.
```java
public class RouterSearch {

    public static List<Router> retrieveRouter(List<Router> routers, Predicate<Router> predicate) {
        return routers.stream()
                .filter(predicate)
                .collect(Collectors.<Router>toList());
    }
}
```
도메인 서비스 클래스 `RouterSearch`에서 이를 수용한다. 이제 `retrieveRouter`메서드는 도메인 헥사곤과 다른 헥사곤에 있는 다양한 객체들이 서비스로 사용할 수 있다.

`isCore`, `isEdge`, `filterRouterByType`와 같은 도메인 제약사항 메서드는 Router 엔티티 클래스에 계속 유지한다.

### UUID를 이용한 식별자 정의
식별자의 중복 생성 및 방지를 위해 데이터베이스 시퀀스에 의존하는 것은 책임을 위임하는 것이며, 중요 부분이 외부와 결합하게 된다. 분리 방법 중 하나는 UUID(universally unique identifier)를 사용하는 것이다.

단점으로는 시퀀스는 정수형태지만 UUID는 문자열이라 리소스를 더 소비한다. 또한 인덱스 관리에 상당한 영향을 줄 수 있다. 이처럼 기술에 구애받지 않는 것 대신 컴퓨터 리소스가 비용으로 들어간다.

엔티티 ID는 한번 정의하고 나면 불변이 되어야 한다. 이러한 불변 속성은 엔티티 ID 속성을 `값 객체`로 모델링하기에 적합한 후보로 만든다.
```java
public class RouterId {

    private final UUID id;

    public RouterId(UUID id) {
        this.id = id;
    }

    public static RouterId withId(String id) {
        return new RouterId(UUID.fromString(id));
    }

    public static RouterId withoutId() {
        return new RouterId(UUID.randomUUID());
    }
}
```

도메인 비즈니스의 모든 것이 ID를 갖는 것은 아니므로, 풍부한 도메인 모델을 생성하기 위해서는 엔티티만으로는 충분하지 않다. ID 없는 객체를 표현하기 위한 방법으로 `값 객체`를 이용한다.

## 값 객체를 통한 서술력 향상
문제 영역을 모델링하기 위해 내장 타입만 사용하는 것은 충분하지 않다. 비즈니스 본질과 목적을 정확하게 전달하기 위해 우리는 내장타입과 사용자 정의 타입 또한 `값 객체`로 감싸야 한다.

값 객체는 다음과 같은 특성을 기본으로 한다.
* 값 객체는 불변이다.
* 값 객체는 식별자를 갖지 않는다.
* 폐기 할 수 있어야 한다.
* 엔티티나 다른 객체 타입을 구성하는 데 쉽게 교체 가능한 객체여야 한다.

값 객체로 엔티티를 구성하면 좋은 예를 알아보자.

```java
public class Event implements Comparable<Event> {
    private EventId id;
    private OffsetDateTime timestamp;
    private String protocol;
    private String activity;
}
```

activity 필드의 내용을 보면 출발지 호스트와 목적지 호스트를 알 수 있다고 해보자. `source.1234 > destination.5555` 이와 같이 표현 된다고 했을때 `>` 기호로 출발지와 목적지를 구분 하고 있다.
이와 같은 방식은 클라이언트에 부담을 주게 된다.
```java
var srcHost = event.getActivity().split(">")[0];
```
이를 값 객체로 개선 하면 해결 할 수 있다.

```java
public class Activity {
    private String description;
    private final String srcHost;
    private final String dstHost;

    public Activity(String description) {
        this.description = description;
        String[] split = description.split(">");
        this.srcHost = split[0];
        this.dstHost = split[1];
    }
    
    public String retrieveSrcHost() {
        return this.srcHost;
    }
}

public class Event implements Comparable<Event> {
    private EventId id;
    private OffsetDateTime timestamp;
    private String protocol;
    private Activity activity;
}
```
그럼 클라이언트의 코드가 명확해지고 표현력도 좋아진다. 또한 임의의 데이터 처리도 필요가 없어진다.
```java
var srcHost = event.getActivity().retrieveSrcHost();
```