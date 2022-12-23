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
* 보통 엔티티에는 해당 데이터의 생성시간과 수정시간을 포함한다. 이때 반복적인 코드를 모든 테이블에 포함 시키지 않기 위해서는   
@MappedSuperclass, JPA Auditing을 사용하면 된다.   

### 4장 : 머스테치로 화면 구성하기
* 템플릿 엔진이란 지정된 템플릿 양식과 데이터가 합쳐져 HTML 문서를 출력하는 소프트웨어를 이야기한다. 
* 템플릿 엔진에는 서버 템플릿 엔진과 클라이언트 템플릿 엔진이 있다.  
서버 템플릿 엔진 : 서버에서 Java 코드로 문자열을 만든 뒤 이 문자열을 HTML로 변환하여 브라우저로 전달한다. (Jsp, Freemarker, Thymeleaf)           
클라이언트 템플릿 엔진 : 서버에서는 Json, Xml 형식으로 데이터만 전달하고 클라이언트에서 조립한다. (Vue.js, React.js)
* layout을 만들 때 페이지 로딩속도를 높이기 위해 css header에 js는 footer에 두었다. HTML은 위에서부터 코드가 실행되기 때문에  
head가 다 실행되고 body가 실행된다. js의 용량이 크면 클 수록 body의 실행이 늦어지기 때문에 js는 body의 하단에 두는 것이 좋다.  
반면에 css는 화면을 그리는 역할이므로 head에서 부르는 것이 좋다. 그렇지 않으면 css가 적용되지 않은 깨진 화면을 사용자가 볼 수 있기 때문이다.  
* 규모가 있는 프로젝트에서의 데이터 조회는 FK의 조인, 복잡한 조건 등으로 Entity 클래스만으로 처리하기 어려워 조회용 프레임워크를 추가로 사용한다.   
대표적인 예로 querydsl, jooq, MyBatis가 있는데 타입 안전성이 보장되고 레퍼런스가 많은 querydsl 사용을 추천한다.  
등록/수정/삭제는 Spring Data Jpa를 통해 진행한다.   

### 5장 : 스프링 시큐리티와 OAuth 2.0으로 로그인 기능 구현하기

* 인터셉터, 필터 기반의 보안 기능을 구현하는 것보다 스프링 시큐리티를 통해 구현하는 것을 더 적극적으로 권장하고 있다.  
* 로그인 기능을 직접 구현하려면 로그인 시 보안, 회원가입시 이메일/전화번호 인증, 비밀번호 찾기, 비밀번호 변경, 회원정보 변경 등을 전부 구현해야 한다.  
* 스프링 부트 1.5 방식에서는 application.properties, application.yml 설정시 url 주소를 모두 명시해야 하지만 2.0 방식에서는   
client 인증 정보만 입력하면 된다.     
* 스프링 시큐리티에서는 권한 코드에 항상 ROLE_이 앞에 있어야만 한다.  
* @EnableWebSecurity : Spring Security 설정들을 활성화 시켜준다.   
csrf().disable().headers().frameOptions().disable() : h2-console 화면을 사용하기 위해 해당 옵션들을 disable 한다.  
authorizeRequests : URL별 권한 관리를 설정하는 옵션의 시작점, 이것을 선언해야 antMatchers 옵션을 사용할 수 있다.  
antMatchers : 권한 관리 대상을 지정하는 옵션이다.  
anyRequest : 설정된 값들 이외 나머지 URL들을 나타낸다. authenticated()를 추가하면 인증된 사용자들에게만 허용하는 것이다.  
logout().logoutSuccessUrl("/") : 로그아웃 성공시 / 주소로 이동한다.  
oauth2Login : OAuth2 로그인 기능에 대한 여러 설정의 진입점  
userInfoEndPoint : OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당한다.  
userService : 소셜 로그인 성공시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록한다.  
* 스프링 시큐리티 사용을 위해 SecurityConfig, CustomOAuth2UserService, OAuthAttributes, SessionUser 4개의 클래스를  
구현하였다. SecurityConfig에는 설정 코드를 작성하고 CustomOAuth2UserService는 소셜 로그인 이후 가져온 사용자의 정보들을 기반으로  
가입 및 정보수정, 세션 저장등의 기능을 지원한다. OAuthAttributes는 OAuth2UserService를 통해 가져온 OAuth2의 attribute를 담을 클래스이다.   
* User 클래스를 사용하지 않고 SessionUser Dto를 새로 만든 이유는 User 클래스는 엔티티이기 때문에 언제 다른 엔티티와 관계가 형성될지 모른다.  
따라서 성능 이슈, 부수 효과가 발생할 확률이 높기 때문에 직렬화 기능을 가진 세션 Dto를 하나 추가로 만들었다.  
* 지금까지 만든 서비스는 애플리케이션을 재실행하면 로그인이 풀린다. 세션이 내장 톰캣의 메모리에 저장되기 때문이다. 내장 톰캣처럼  
애플리케이션 실행 시 실행되는 구조에선 항상 초기화가 된다. 이외에도 한가지 문제가 더 있는데 2대 이상의 서버에서 서비스를 하고 있다면  
톰캣마다 세션 동기화 설정을 해야만 한다. 이를 해결하기위해서 실무에서는 세션 저장소에 대해 다음의 3가지 중 한 가지를 선택한다.  
1. 톰캣 세션을 사용한다  
톰캣에 세션이 저장되기 때문에 2대 이상의 WAS가 구동되는 환경에서는 톰캣들 간의 세션 공유를 위한 추가 설정이 필요하다.  
2. MySQL과 같은 데이터베이스를 세션 저장소로 사용한다.  
여러 WAS간의 공용 세션을 사용할 수 있는 가장 쉬운 방법이지만 로그인 요청마다 DB IO가 발생하여 성능상 이슈가 발생한다.  
따라서 로그인 요청이 많이 없는 백오피스, 사내 시스템 용도에서 사용한다.  
3. Redis, Memcached와 같은 메모리 DB를 세션 저장소로 사용한다. B2C 서비스에서 가장 많이 사용하는 방식이고 실제 서비스로 사용하기 위해서는  
Embedded Redis와 같은 방식이 아닌 외부 메모리 서버가 필요하다.  


