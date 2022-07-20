# springdata_jpa_test
~~~
Study for spring data jpa
~~~
  
## 정리

~~~
1) JPA (JPQL) 스터디가 선행되어야 한다. Spring data jpa 는 그저 jpa 사용을 더 편리하게 해줄 뿐!

2) 트랜잭션 과부하를 막고 성능을 최적화 하기 위해 <벌크 연산> 과 <Fetch join> 을 적절하게 사용하는 것이 중요하다.
  - [N + 1 문제] 가 발생하게 되면 겉잡을 수 없는 쿼리 개수로 인해 DBA 로부터 욕먹기 쉽상이다.

3) API 응답에 절대로 엔티티를 그대로 반환하면 안된다. 무조건 DTO 사용하기!!
  - 엔티티를 그대로 반환하게 되면 해당 API 를 요청한 곳에서 불필요한 정보까지 받게되므로 효율적이지 못하다.
  - 서버에서 정의한 엔티티가 바뀌게 되면 API 요구사항에 어긋나게 되므로 DTO 를 통해서 API 룰은 지키게 해야 한다.
    (유연한 유지보수를 위해 API 요구사항과 엔티틱 구현은 독립적이어야 한다.)

4) Cross-cutting concern 은 따로 슈퍼 클래스를 통해 관리해야 하자!
  - 엔티티마다 중복되는 필드들은 아래와 같이 따로 베이스 엔티티 클래스를 상속받게 해주면 된다.
-----------------------
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDateTime;

    @LastModifiedDate
    private LocalDateTime lastModifiedDateTime;

}
-----------------------
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

}
-----------------------
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    
    ...
}
-----------------------

5) 영속성 컨텍스트에 저장하고 바로 조회하는 경우를 조심해야 한다.
  - DB 에 반영은 했지만 영속성 컨텍스트에는 데이터 동기화가 발생하지 않아 실제로 조회한 데이터가 다를 수 있다.
  - 벌크 연산 같은 경우는 영속성 컨텍스트를 거치지 않고 직접 DB 를 조회하여 데이터를 가져오기 때문에 특히 조심해야 한다!
  - 만약 데이터 조회와 수정이 같은 코드 컨텍스트에서 수행되어야 하면 flush 와 clear 실행을 통해 반드시 영속성 컨텍스트에 동기화하도록 하자.

6) @Transactional

  - JPA 의 모든 변경은 트랜잭션 안에서 동작해야 한다!
  - 스프링 데이터 JPA 는 변경(등록, 수정, 삭제) 메서드를 트랜잭션 처리한다.
  - 서비스 계층에서 트랜잭션을 시작하지 않으면 Repository 에서 트랜잭션을 시작한다.
  - 서비스 계층에서 트랜잭션을 시작하면 Repository 는 해당 트랜잭션을 전파(Propagation)받아서 사용한다.
  - 그래서 스프링 데이터 JPA 를 사용할 때 트랜잭션이 없어도 데이터 등록, 변경이 가능한 것이다.

> @Transational(readOnly = true)
  - 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에 정의한다.
  - 플러시를 생략해서 약간의 성능 향상을 얻을 수 있다.
  
7) Save
  - 새로운 엔티티 : persist
  - 기존 엔티티 : merge
  
  > merge 는 데이터 업데이트 용도가 아니다.
  > 엔티티 업데이트는 Dirty Checking (변경 감지)를 사용해야 한다.
  > 엔티티가 영속성 상태가 아닐 때 다시 영속성 엔티티로 바꿔줄 때 사용한다.

~~~

