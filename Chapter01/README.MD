# 헥사고날 아키텍쳐란

포트와 어댑터 아키텍쳐라고도 불림

### 메인 아이디어

- 비즈니스 코드를 기술 코드로부터 분리하는 것
- 비즈니스 코드에 피해를 주지 않고 기술 코드 변경이 가능해야 한다
- 핵심 도메인 로직을 외부 인터페이스(입출력, DB, 사용자 인터페이스 등)으로 부터 분리함으로 달성

### 도메인 헥사곤

소프트웨어가 해결하기를 원하는 핵심 문제를 설명하는 요소의 결합, 이러한 요소들은 기술에 구애 받지 않는 형태로 표현되어야 함

- 구성 요소
    - 엔티티 - 식별자를 할당할 수 있는 것
    - 값 객체 - 엔티티들을 합성하기 위한 불변 객체

### 애플리케이션 헥사곤

도메인 헥사곤에서 나오는 비즈니스 규칙을 사용, 처리, 조정

비즈니스 측면과 기술 측면 사이에서 양쪽과 상호작용하는 중개자 역할

### 프레임워크 헥사곤

애플리케이션 기능의 노출 방법을 결정, 예를 들면 REST, gRPC 엔드포인트 정의

## 도메인 헥사곤

실 세계 문제를 이해하고 모델링하는 활동

스타트업 같은 개발자나 도메인 전문가 모두 시스템의 목적에 대한 명확한 비전을 갖고 있지 않거나,

문제 영역이 존재하고 도메인 전문가들이 분명하게 알고 있는 상황에서 문제 영역을 엔티티, 값 객체로의 변환 방법을 파악하지 못하면 잘못된 가정을 기반으로 구축되게 될 것이다.

이러한 점은 소프트웨어가 간단하게 시작해서, 기반 코드가 커짐에 따라 기술 부채가 누적되고 유지 보수가 더 어려워지는 이유 중 하나로 여겨진다.

### 엔티티

- **식별자 (Identifier)**: 엔티티는 고유한 식별자를 가지고 있으며, 이 식별자를 통해 다른 엔티티와 구분된다. 동일한 엔티티는 시간이 지나도 동일한 식별자를 유지한다.
- **변경 가능성 (Mutability)**: 엔티티는 시간이 지남에 따라 상태가 변할 수 있다. 이는 엔티티의 특성이 변할 수 있다는 것을 의미한다.
- **생명 주기 (Lifecycle)**: 엔티티는 생성되고, 변경되며, 소멸되는 생명 주기를 가진다.

예) 사용자 엔티티는 사용자 ID와 같은 고유 식별자를 가지며, 이름, 이메일과 같은 변하는 속성을 지닌다. 시간이 지남에 따라 사용자의 특성이 변할 수 있다.

### 값 객체

객체의 정체성보다 속성에 관심을 갖는 경우 코드의 표현력을 보완하는데 도움을 준다.

- **식별자가 없음 (No Identifier)**: 값 객체는 고유한 식별자를 가지지 않는다. 자체 속성 값으로 동일성 판단을 한다.
- **불변성 (Immutability)**: 값 객체를 사용해 엔티티를 구성할 수 있으므로 도메인 전체에서 예상치 못한 불일치를 방지 하기 위해 불변으로 해야한다.
- **동등성 (Equality by Value)**: 두 값 객체가 동일한 속성 값을 가지면, 두 값 객체는 동일한 것으로 간주한다.

## 애플리케이션 헥사곤

애플리케이션 특화 작업을 추상적으로 처리하는 곳, 기술 관심사를 직접 다루지 않기 때문에 추상적인 것을 이야기한다. 도메인 비즈니스 규칙에 기반한 소프트웨어 사용자의 의도와 기능을 표현한다.

### 유스케이스

도메인 제약사항을 지원하기 위해 시스템의 동작을 소프트웨어 영역 내에 존재하는 애플리케이션 특화 오퍼레이션을 통해 나타낸다.

엔티티 및 다른 유스케이스와 직접 상호작용하고 그것을 유연한 컴포넌트로 만들 수 있다.

자바에서는 인터페이스로 정의된 추상화로 나타낸다.

```java
public interface RouterViewUseCase {
    List<Router> getRouters(Predicate<Router> filter);
}
```

### 입력 포트

유스케이스 인터페이스를 구현하는 것이 입력 포트의 역할 이다. 애플리케이션 수준에서 유스케이스에 직접 연결되는 컴포넌트이기 때문에 입력 포트는 도메인 용어로 소프트웨어의 의도를 구현할 수  있게 한다.

```java
public class RouterViewInputPort implements RouterViewUseCase {

    private final RouterViewOutputPort routerListOutputPort;

    public RouterViewInputPort(RouterViewOutputPort routerListOutputPort) {
        this.routerListOutputPort = routerListOutputPort;
    }

    @Override
    public List<Router> getRouters(Predicate<Router> filter) {
        var routers = routerListOutputPort.fetchRouters();
        return Router.retrieveRouter(routers, filter);
    }
}
```

위는 유스케이스에 서술된 소프트웨어의 의도를 구현한 입력 포트다. 도메인 제약사항을 사용해 조회하고자 하는 라우터를 필터링한다.

### 출력 포트

유스케이스가 목표를 달성하기 위해 외부 리소스에서 데이터를 가져와야 하는 상황이 있다. 이것이 출력 포트의 역할이다.

유스케이스나 입력 포트가 오퍼레이션을 수행하기 위해 어떤 종류의 데이터를 외부에서 가져와야 하는지를 기술에 구애 받지 않고 설명하는 인터페이스로 표현된다.

이 말은 특정 데이터가 DB에서 오든 파일 시스템에서 오든 신경 쓰지 않는 말이다.

```java
public interface RouterViewOutputPort {
    List<Router> fetchRouters();
}
```

## 프레임워크 헥사곤

소프트웨어와 통신할 수 있는 기술을 결정한다. 통신은 두 가지 형태로 발생할 수 있다.

- 드라이빙 방식(Driving): 입력 어댑터(Input Adapter)를 사용한다.
- 드리븐 방식(Driven): 출력 어댑터(Output Adapter)를 사용한다.

### 드라이빙 오퍼레이션과 입력 어댑터

드라이빙 오퍼레이션은 소프트웨어에 동작을 요청하는 것이다.

- 웹 애플리케이션에서 사용자가 버튼 눌러 폼을 제출하는 경우
- 외부 시스템에서 REST API로 애플리케이션에 데이터를 요청하는 경우
- 외부 시스템에서 MQ로 메세지를 보내는 경우

이러한 API는 외부 엔티티가 시스템과 상호작용하고, 외부 엔티티의 요청을 도메인 애플리케이션으로 변환하는 방법을 정의한다.

드라이빙이라는 단어는 외부 엔티티들이 시스템의 동작을 유도하기 때문에 쓰인다.

다음은 입력 포트 오퍼레이션 중 stdin을 이용하여 호출 하는 입력 어댑터이다.

```java
public class RouterViewCLIAdapter {

    private RouterViewUseCase routerViewUseCase;

    public RouterViewCLIAdapter() {
        setAdapters();
    }

    public List<Router> obtainRelatedRouters(String type) {
        return routerViewUseCase.getRouters(Router.filterRouterByType(RouterType.valueOf(type)));
    }

    private void setAdapters() {
        this.routerViewUseCase = new RouterViewInputPort(RouterViewFileAdapter.getInstance());
    }
}
```

유스케이스 인터페이스를 통해 입력 포트를 사용하는 것이 중요한 포인트이다.

- 명령어를 통해 obtainRelatedRouters를 호출하면 애플리케이션 유스케이스를 호출한다.
- 입력 데이터 캡슐화: 문자열 형태의 type을 RouterType 열거형으로 변환하여 도메인 엔티티로 캡슐화 한다.
- 도메인 로직 호출: 변환된 RouterType을 통해 도메인 제약사항을 다룬다.
- 유스케이스 실행: 필터링된 결과를 routerViewUseCase.getRouters를 통해 비즈니스 로직을 수행한다.

REST 와 같은 다른 통신 형식을 활성화하려면 REST 통신 엔드포인트 노출을 위한 의존성을 포함하는 새로운 REST 어댑터를 생성하면 된다.

### 드리븐 오퍼레이션과 출력 어댑터

애플리케이션에서 트리거되며, 외부에서 소프트웨어 요구사항을 충족시키는 데 필요한 데이터를 가져온다. 일반적으로 드리븐 오퍼레이션은 일부 드라이빙 오퍼레이션에 응답해 발생한다. 출력 어댑터를 통해서 정의되며 이 어댑터는 그것들을 구현하는 출력 포트와 일치해야 한다.

출력 포트는 애플리케이션 비즈니스를 수행하는 데 필요한 데이터의 종류를 알려주며, 데이터를 어떻게 가져올지 설명하는 것이 출력 어댑터의 역할이다.

- 도메인 로직에서 처리된 데이터를 데이터베이스에 저장하는 경우
- 애플리케이션이 외부 API를 호출하여 데이터를 전송하거나 요청하는 경우
- 도메인 로직이 완료된 후 MQ에 메세지를 보내는 경우

예를 들어 애플리케이션이 오라클 기반 DB로 구성되었다가 Mongo DB로 변경한다고 해보자.  초기에는 오라클 DB와 지속성을 허용하는 출력 어댑터만 가지고 있었다. Mongo와 통신하기 위해 애플리케이션과 도메인 헥사곤은 건들면 안되고, 프레임워크 헥사곤에 출력 어댑터를 생성해야 한다. 입력 어댑터와 출력 어댑터 모두 헥사곤 내부를 가리키고 있기 때문에 이것들을 애플리케이션 및 도메인 헥사곤에 종속되게 만들어 의존성을 역전 시킨다.

드리븐이라는 단어는 헥사고날 애플리케이션 자체에 의해 오퍼레이션이 유도되고 통제되며, 다른 외부 시스템에서 동작을 트리거 하기 때문이다.

```java
public class RouterViewFileAdapter implements RouterViewOutputPort {

    private static RouterViewFileAdapter instance;

    @Override
    public List<Router> fetchRouters() {
        return readFileAsString();
    }

    private static List<Router> readFileAsString() {
        List<Router> routers = new ArrayList<>();
        try (Stream<String> stream = new BufferedReader(
                new InputStreamReader(RouterViewFileAdapter.class.getClassLoader().getResourceAsStream("routers.txt"))).lines()) {
            stream.forEach(line -> {
                String[] routerEntry = line.split(";");
                var id = routerEntry[0];
                var type = routerEntry[1];
                Router router = new Router(RouterType.valueOf(type), RouterId.of(id));
                routers.add(router);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return routers;
    }

    private RouterViewFileAdapter() {

    }

    public static RouterViewFileAdapter getInstance() {
        if (instance == null) {
            instance = new RouterViewFileAdapter();
        }
        return instance;
    }
}
```

애플리케이션이 외부 데이터를 얻는 방법으로 파일에서 얻는 방법을 출력 어댑터가 출력 포트 인터페이스를 구현하여 정의하는 코드다

이로써 외부 입력 → 입력 어댑터 → 입력 포트 → 유스케이스 → 출력 포트 → 출력 어댑터 → 외부 시스템의 흐름을 살펴 보았다.

## 헥사고날 접근 방식의 장점

- 소프트웨어 개발 방법의 표준을 정할 때 도움이 된다.
    - 클래스, 패키지, 코드 구조가 전체적으로 구성되는 방법에 영향을 준다.
- 조직이 소프트웨어를 구성하는 기본 원칙을 수립하는데 도움이 된다.
    - 프로젝트를 전환할 때마다 개발자는 이전 프로젝트에서 획득한 헥사고날 원칙에 대해 이미 알고 있기 때문에 소프트웨어 구조에 대한 이해에 러닝커브가 감소한다.
- 변경과 테스트가 쉽다.

### 변경 허용(Change tolerant)

기술 변화는 상당히 빠른 속도로 이뤄지고 있다. 첨단 기술에 대한 활용 및 선택에 유연하지 않다면 변화에 대한 내성이 없기 때문에 대규모 리팩터링에 리소스(돈, 시간)이 상당한 규모로 소모 될 수 있다.

### 유지보수성(Maintainability)

비즈니스 규칙을 변경해야 하는 경우 유일하게 변경해야 하는 것은 도메인 헥사곤이다. 반면, 아직 애플리케이션에서 지원하지 않는 특정 기술, 프로토콜을 추가 해야하는 경우 프레임워크 헥사곤에서 어댑터만 생성하면 된다.

이러한 관심사의 분리는 소프트웨어 구조를 파악하는데 들어가는 시간 리소스를 충분히 줄여준다.

### 테스트 용이성(Testability)

UI와 데이터베이스 같은 외부 의존성이 없더라고 개발자가 애플리케이션을 테스트할 수 있게 한다. 이는 통합 테스르를 무시하는 것은 아니다.