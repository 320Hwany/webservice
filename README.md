## 스프링 부트와 AWS로 혼자 구현하는 웹서비스(이동욱) 책을 읽고 정리한 내용

### 2장 : 스프링 부트에서 테스트 코드 작성하기  

* @SpringBootApplication으로 인해 스프링 부트의 자동 설정, 스프링 Bean 읽기와 생성을 모두 자동으로 설정한다.  
* 특히나 @SpringBootApplication이 있는 위치부터 설정을 읽어가기 때문에 항상 프로젝트의 최상단에 위치해야 한다.  
* main 메소드에서 실행하는 SpringApplication.run으로 인해 내장 WAS를 실행한다.  
그러므로 항상 서버에 톰캣을 설치할 필요가 없게 되고 스프링 부트로 만들어진 Jar 파일로 실행하면 된다.   
* 내장 WAS 사용을 권장하는 이유 : 언제 어디서나 같은 환경에서 스프링 부트를 배포할 수 있다.  

테스트 코드 작성은 Junit4를 사용하였다. @RestController로 "hello"를 Json으로 반환하는 HelloController에 대한 테스트 만들기  
```
@RunWith(SpringRunner.class) // 스프링 부트 테스트와 Junit 사이를 연결한다.  
@WebMvcTest // spring mvc 에 집중할 수 있는 어노테이션 
public class HelloControllerTest {

    @Autowired
    private MockMvc mvc;  // 웹 API 를 테스트할 때 사용한다. 이 클래스를 통해 HTTP GET, POST 등에 대한 API 테스트 가능

    @Test
    public void hello가_리턴된다() throws Exception {
        String hello = "hello";

        mvc.perform(get("/hello"))  // MockMVc 를 통해 HTTP GET 요청을 한다.
                .andExpect(status().isOk()) // HTTP status 검증
                .andExpect(content().string(hello)); // 응답 본문의 내용 검증
    }
}
```

### 3장 : 스프링 부트에서 JPA로 데이터베이스 다뤄보자

* JPA를 사용하면 개발자가 객체지향적으로 프로그래밍을 하고 SQL에 종속적인 개발을 하지 않아도 된다. 패러다임 불일치 문제를 해결한다.   
* 인터페이스인 JPA를 사용하기 위해서는 구현체가 필요하다. 대표적으로 Hibernate, EclipseLink가 있지만 Spring에서 JPA를 사용할 때는  
이 구현체들을 직접 다루지 않는다. 구현체들을 좀 더 쉽게 사용하고자 추상화시킨 Spring Data JPA라는 모듈을 이용해 JPA 기술을 다룬다.  
* Spring Data JPA의 장점에는 구현체 교체의 용이성, 저장소 교체의 용이성이 있다.  
구현체 교체의 용이성 : Hibernate가 언젠가 수명을 다해서 새로운 JPA 구현체로 바꿔야할 때 Spring Data JPA를 사용하면 아주 쉽게 교체할 수 있다.  
Spring Data JPA 내부에서 구현체 매핑을 지원해주기 때문이다.  
저장소 교체의 용이성 : 관계형 데이터베이스 외에 다른 저장소로 쉽게 교체할 수 있다.만약에 트래픽이 많아져 관계형 데이터베이스로는 도저히 감당이 안될때  
MongoDB로 교체가 필요하다면 Spring Data JPA에서 Spring Data MongoDB로 의존성만 교체하면 된다. 왜냐하면 Spring Data의 하위 프로젝트들은  
기본적인 CRUD의 인터페이스(save, findAll, findOne 등)가 같기 때문이다.   
* 필자는 어노테이션 순서를 주요 어노테이션을 클래스에 가깝게 둡니다 -> 앞으로 이렇게 하는 것이 좋은 방법인 것 같다  
* Builder를 사용하게 되면 어느 필드에 어떤 값을 채워야할 지 명확하게 인지할 수 있다.   
* Spring Data JPA의 인터페이스 중 save는 id 값이 없으면 insert, id 값이 있으면 update 쿼리가 실행된다.  
* 스프링 부트에서는 여러 설정들을 application.properties, application.yml 등의 파일로 한 줄의 코드로 설정할 수 있도록 지원하고 권장한다.  
* layer를 Web(controller), Service, Repository, Dto, Domain 5가지로 나눈다면 비즈니스 처리를 담당해야할 곳은 바로 Domain이다.  
비즈니스 로직이 Service에 있지 않고 Service는 단지 트랜잭션, 도메인 기능 간의 순서를 보장하는 layer이다.   
-> 중요한 로직은 도메인 각각에 있도록 하는 방법이 나중에 수정해야할 때도 더 편한 방법인 것 같다. 다음에는 이렇게 설계해보자  
* Entity 클래스는 데이터베이스와 맞닿은 핵심 클래스로 Entity 클래스를 기준으로 테이블이 생성되고 스키마가 변경된다.   
따라서 절대로 Entity 클래스를 Request/Response 클래스로 사용해서는 안된다. Request/Response 용 Dto를 만들어야 한다.  
* 트랜잭션 안에서 데이터베이스에서 데이터를 가져오면 데이터는 영속성 컨텍스트가 유지된 상태이다. 트랜잭션이 끝나는 시점에 변경분을 반영하는  
더티체킹이 일어나기 때문에 별도로 Update 쿼리를 날릴 필요가 없다.  
* 보통 엔티티에는 해당 데이터의 생성시간과 수정시간을 포함한다. 이때 반복적인 코드를 모든 테이블에 포함 시키지 않기 위해서는 JPA Auditing을 사용하면 된다.  

### 4장 : 머스테치로 화면 구성하기
* 템플릿 엔진이란 지정된 템플릿 양식과 데이터가 합쳐져 HTML 문서를 출력하는 소프트웨어를 이야기한다. 
* 템플릿 엔진에는 서버 템플릿 엔진과 클라이언트 템플릿 엔진이 있다.  
서버 템플릿 엔진 : 서버에서 Java 코드로 문자열을 만든 뒤 이 문자열을 HTML로 변환하여 브라우저로 전달한다. (Jsp, Freemarker, Thymeleaf)           
클라이언트 템플릿 엔진 : 서버에서는 Json, Xml 형식으로 데이터만 전달하고 클라이언트에서 조립한다. (Vue.js, React.js)
* layout을 만들 때 페이지 로딩속도를 높이기 위해 css header에 js는 footer에 두었다. HTML은 위에서부터 코드가 실행되기 때문에  
head가 다 실행되고 body가 실행된다. js의 용량이 크면 클 수록 body의 실행이 늦어지기 때문에 js는 body의 하단에 두는 것이 좋다.  
반면에 css는 화면을 그리는 역할이므로 head에서 부르는 것이 좋다. 그렇지 않으면 css가 적용되지 않은 깨진 화면을 사용자가 볼 수 있기 때문이다.  
