# URL Decoder 트러블슈팅 랩

외부 기기(단말기, POS 등)가 보낸 구매 정보 페이로드를 서버에서 복원하는 과정에서 발생한
**`java.net.URLDecoder` 오적용 문제**를 재현하고 해결한 실험 프로젝트.

```
[단말기]  JSON → AES 암호화 → Base64 인코딩 → (URL 인코딩은 선택) → 전송
                                                    │
[서버]    수신 → URL 디코딩 → Base64 디코딩 → AES 복호화 → JSON
                   ①②            ③
```

```java
// 기존 코드 — 형태 구분 없이 무조건 URLDecoder 적용
String decoded = URLDecoder.decode(payload, StandardCharsets.UTF_8); // 💥
```

---

## 1. 문제 원인

### 1.1 증상

| # | 문제 | 증상 |
|---|---|---|
| **1** | `%` 문자 hex 오류 | `"discountRate":"10%"` 에서 `IllegalArgumentException` → 요청 전체 실패 |
| **2** | `+` 문자 공백 치환 | `"customerPhone":"+821012345678"` 이 `" 821012345678"` 로 **조용히** 오염 |
| **3** | Base64 디코딩 실패 | 2번의 연쇄 피해. `Last unit does not have enough valid bits` |
| **4** | `form-urlencoded` 500 | `@RequestBody` 가 본문 대신 파라미터 맵에서 본문을 재구성 → `Required request body is missing` |

1번은 요청이 터지므로 바로 발견되지만 **2번이 더 위험하다.** 예외 없이 통과한 뒤
잘못된 전화번호가 DB에 저장되기 때문이다.

### 1.2 근본 원인 — `URLDecoder` 는 URL 디코더가 아니다

**"URL 인코딩"이라 부르는 규격은 사실 두 개**이고, 이 차이가 버그의 뿌리다.

| 항목 | RFC 3986 (URI 일반) | `x-www-form-urlencoded` (HTML 폼) |
|---|---|---|
| `+` 의 의미 | **리터럴 플러스 문자** | **공백** |
| 공백 표현 | `%20` | `+` (또는 `%20`) |

`java.net.URLDecoder` 는 **후자(폼 규격)의 구현체**다. Javadoc에도 명시되어 있다.

> Decodes a `application/x-www-form-urlencoded` string using a specific encoding scheme.

그런데 우리 페이로드에서 `+` 는 **리터럴 문자**다 — 국제전화번호 접두사(`+8210...`),
Base64 알파벳의 62번 문자, 일반 텍스트(`"C+ 등급 회원"`). `%` 도 마찬가지로
할인율·메모에 자연스럽게 등장하는 리터럴이다.

게다가 `URLDecoder` 는 `%` 뒤 2글자가 16진수가 아니면 예외를 던져 **문자열 전체를 못 쓰게 만든다**(all-or-nothing).

> **정리**: 라이브러리 버그가 아니라 **규격 오적용**이다. 폼 전송이 아닌 페이로드(JSON 원문,
> Base64 문자열)에 폼 디코더를 썼기 때문에 발생했다.

### 1.3 구조적 원인 — 형태를 사전에 알 수 없다

일부 단말기/펌웨어는 **암호화 없이 JSON 원문을 그대로 보낸다.** 즉 컨트롤러가 받는 raw body가
URL 인코딩된 것인지 아닌지를 **미리 알 수 없다.**

따라서 "인코딩되어 있다고 가정하고 무조건 푼다"는 `URLDecoder` 의 계약 자체가 이 자리에 안 맞는다.
필요한 것은 *판별 후 디코딩*이고, 판별이 "디코딩해도 안 깨지나"에 의존하므로
**예외를 던지지 않는 디코더**여야 한다.

> 서버는 인코딩을 하지 않으므로 `URLEncoder` 는 애초에 쓸 자리가 없다. 테스트 픽스처를 만들 때만 등장한다.

### 1.4 `java.util.Base64` 를 그대로 쓸 수 없는 이유

표준 디코더가 실제로 얼마나 엄격한지 측정한 결과다.

```
OK   패딩 없음    'YWJjZGU'        ← 이건 표준 디코더도 허용한다
FAIL 줄바꿈       'YWJj\r\nZGU='   → Illegal base64 character d
FAIL URL-safe     '-_8='           → Illegal base64 character 2d
FAIL 4바이트 정렬 깨짐             → Last unit does not have enough valid bits
```

단말기·중계 구간을 거치며 섞여 들어오는 방언(MIME 줄바꿈, URL-safe 알파벳, 앞뒤 여백)을
표준 디코더는 전부 거부한다. 그렇다고 `getMimeDecoder()` 로 대체할 수도 없다.

- **형태 판별이 망가진다.** MIME 디코더는 알파벳 밖 문자를 *조용히 무시*한다.
  실제로 `{"orderId":"A-12"}` 를 예외 없이 7바이트로 디코딩했다 — JSON을 Base64로 오판한다는 뜻이라
  분기 조건으로 쓸 수 없다.
- **공백을 무조건 제거한다.** [1.5](#15-연쇄-피해--이미-훼손된-채-도착하는-base64)의 복구 전략과 정반대다.
- 표준 API에는 **"던지지 않고 판별"하는 메서드가 없어** try/catch가 호출처마다 흩어진다.

### 1.5 연쇄 피해 — 이미 훼손된 채 도착하는 Base64

우리 코드가 `URLDecoder` 를 쓰지 않더라도, **페이로드가 도달하기 전에 이미 `+` 가 공백으로
바뀌어 있을 수 있다.** 레거시 `URLDecoder` 를 쓰는 앞단 모듈, 서블릿 컨테이너의 폼 파싱,
일부 프록시가 주범이다.

```
원본   Rz73ujo8GtEWroNZM0PoJEW4xL7RVj2/3Kzp365Y0rpO7P+I8vYP...
훼손   Rz73ujo8GtEWroNZM0PoJEW4xL7RVj2/3Kzp365Y0rpO7P I8vYP...
                                                    ↑ '+' 가 공백
```

여기서 **공백을 "제거"하면 길이가 줄어** 4바이트 정렬이 깨진다. 초기 구현이
`value.replaceAll("\\s", "")` 로 공백을 제거하고 있어 **복구 가능한 페이로드를 오히려 하드 실패로 만들고 있었다.**

---

## 2. 해결 과정

### 2.1 기각한 대안

| 대안 | 기각 사유 |
|---|---|
| ❌ try-catch 후 원문 사용 | 문제 1은 막지만 **문제 2를 전혀 못 막는다.** `+` 만 있는 페이로드는 예외가 안 나므로 catch에 걸리지 않고 오염된 결과가 정상처럼 반환된다 — 가장 위험한 선택 |
| ❌ `%` → `%25` 선치환 | 정상 규격을 깨뜨린다. `%7B%22a%22%7D` → 디코딩해도 그대로라 **한 겹도 풀리지 않는다.** `+` 문제도 그대로 |
| ❌ URL 디코딩 제거 | `%2B` `%2F` `%3D` 가 안 풀려 정상 규격(URL 인코딩된 Base64)을 처리할 수 없다 |

**✅ 채택 — 형태 판별 + RFC 3986 관대한 디코더 (2중 방어).**
디코더 자체를 규격에 맞게 고치고, 그 앞단에서 형태를 먼저 판별해 불필요한 디코딩을 건너뛴다.

### 2.2 `SafeUrlDecoder` — `URLDecoder` 대체

- `%` 뒤가 16진수 2자리가 **아니면 리터럴 `%` 로 보존**한다. 어떤 입력에도 예외를 던지지 않는다.
- `+` 는 변환하지 않는다. 공백은 `%20` 으로만 해석한다.
- 연속된 `%XX` 는 **바이트 버퍼에 모아 한 번에 UTF-8 변환**한다. 한 글자씩 변환하면 `%ED%95%9C`(한) 같은 멀티바이트가 깨진다.

```java
SafeUrlDecoder.decode("a%25b%c");  // → "a%b%c"
//                      ^^^          %25 는 '%' 로 디코딩
//                          ^        뒤가 'c' 뿐이라 리터럴 '%' 로 보존
SafeUrlDecoder.decode("a+b%20c");  // → "a+b c"   '+' 는 그대로, %20 만 공백
```

> `Character.digit(c, 16)` 을 쓰면 전각 숫자(`％２５`) 같은 비-ASCII도 16진수로 인정해버린다.
> 판별을 엄격하게 하려고 ASCII만 받는 `hexValue()` 를 직접 구현했다.

### 2.3 디코더를 고치는 것만으로는 부족하다

사용자가 메모에 실제로 `쿠폰코드 100%25` 라고 입력한 경우, `%25` 는 **형태상 완벽히 유효한
이스케이프**라서 관대한 디코더도 이것을 풀어버린다(`→ 100%`). 원본 훼손이다.

**애초에 URL 인코딩되지 않은 데이터에는 디코딩을 시도하면 안 된다**는 뜻이다.
그래서 `PurchasePayloadDecoder` 는 디코딩 **이전에** 형태를 판별하고, JSON 원문이면
URL 디코딩 단계를 통째로 건너뛴다. 판별은 괄호 휴리스틱이 아니라 **파서에게 직접 묻는다.**

```java
private JSONObject parseOrNull(String value) {
    try {
        JSONTokener tokener = new JSONTokener(value);
        JSONObject json = new JSONObject(tokener);
        return tokener.nextClean() == '\0' ? json : null;   // 끝까지 소비했는지 확인
    } catch (JSONException e) {
        return null;
    }
}
```

> **`nextClean()` 검사가 필요한 이유**: `new JSONObject(String)` 은 객체 하나를 읽고 **뒤에 남은
> 내용을 조용히 무시한다.** 스트림에 본문을 직접 write하는 단말기에서 페이로드가 두 번 써져
> `{...}{...}` 가 되면 앞의 하나만 취하고 나머지를 잃는다. 조용한 데이터 손실을 막는 장치다.

Base64 문자열은 `{` 로 시작할 수 없으므로 JSON으로 오인될 여지가 원천적으로 없다.

### 2.4 `Base64Codec` — 방언 흡수 + 공백 복원

**Base64 알파벳(`A–Z a–z 0–9 + / =`)에는 공백이 없다.** 따라서 문자열에 나타난 공백은
`+` 가 치환된 흔적일 가능성이 높다. 제거하는 대신 **`+` 로 되돌린다.**
다만 단순 여백일 수도 있어 모호하므로 순서대로 두 번 시도한다.

```java
// 줄바꿈·탭은 Base64 알파벳에도 없고 '+'로 오해될 여지도 없으므로 먼저 제거한다.
String cleaned = value.replaceAll("[\\r\\n\\t\\f\\x0B]", "");

try {
    return decodeStrict(cleaned.replace(' ', '+'));   // 1순위: 훼손된 '+' 로 본다
} catch (IllegalArgumentException e) {
    return decodeStrict(cleaned.replace(" ", ""));    // 2순위: 단순 여백으로 본다
}
```

`+` 로 잘못 복원하면 길이·패딩이 어긋나 1순위가 실패하므로 2순위가 안전망이 된다.
`decodeStrict()` 는 URL-safe 알파벳(`-` `_`)을 표준으로 되돌리고 패딩을 보정하며,
`isBase64()` 는 예외를 던지지 않고 형태 판별 결과만 돌려준다.

### 2.5 함정 — 파이프라인의 `strip()` 이 선두 공백을 지웠다

`Base64Codec` 을 고친 뒤 단위 테스트는 통과했지만 **실제 서버에서는 여전히 400이 났다.**
Base64가 `+` 로 **시작**하는 경우(`+/8ARz73...` → ` /8ARz73...`)였는데, 당시 파이프라인이
형태 판별용으로 `payload.strip()` 을 호출해 `Base64Codec` 이 손쓰기도 전에 선두 공백을 지우고 있었다.

형태 판별을 `JSONObject` 파싱으로 바꾸면서 자연히 사라졌다. 파서가 앞뒤 공백을 스스로 처리하므로
`strip()` 이 필요 없어졌고, 공백 복원 책임이 `Base64Codec` **한 곳에만** 남는다.

> **교훈**: 단위 테스트가 `Base64Codec.decode()` 를 직접 호출한 탓에 파이프라인 앞단의 `strip()` 을
> 우회해 통과했다. 컴포넌트 단위 테스트만으로는 **계층 간 상호작용 버그**를 잡을 수 없다.

### 2.6 문제 4 — `@RequestBody` 를 버리고 원시 스트림을 읽는다

`@RequestBody String` 이 타는 `ServletServerHttpRequest.getBody()` 에는 **form POST 전용 분기**가 있어,
`Content-Type` 이 form-urlencoded면 **원본 본문 대신 파라미터 맵에서 본문을 재구성**한다.
그 결과 `name=value` 구조가 아닌 본문은 통째로 사라지고, 폼 파싱 규격상 `+` 가 공백으로 바뀐다
(문제 2가 프레임워크 레벨에서 재발).

```java
@PostMapping(consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public PurchaseResponse receive(HttpServletRequest request) {
    return purchaseService.receive(readRawBody(request));   // StreamUtils + UTF-8 고정
}
```

우리가 먼저 스트림을 읽으므로 컨테이너의 폼 파싱이 트리거되지 않고 원본 바이트가 보존된다.
charset은 `Content-Type` 선언을 따르지 않고 **UTF-8로 고정**한다 — charset을 생략하거나
`ISO-8859-1` 로 잘못 선언해놓고 실제로는 UTF-8을 보내는 단말기가 있기 때문이다.

---

## 3. 결론 및 정리

### 3.1 최종 복원 흐름

```
페이로드 수신 (원시 스트림, UTF-8 고정)
   ├─ JSONObject 로 파싱되나? ──── 예 ──→ RAW_JSON           (URL 디코딩 건너뜀)
   └─ 아니오
        SafeUrlDecoder 로 퍼센트 디코딩
           ├─ JSONObject 로 파싱되나? ─ 예 ──→ URL_ENCODED_JSON
           └─ 아니오
                Base64 형식인가? ─ 아니오 ──→ 400 (P002 / P005)
                       └─ 예 ──→ Base64 디코딩(공백 → '+') → AES 복호화 → ENCRYPTED
```

응답에 판별된 `format` 을 함께 내려주므로, 운영 중 **어떤 단말기가 어떤 형태로 보내는지 분포를 관찰**할 수 있다.

### 3.2 표준 API의 계약 vs 이 엔드포인트에 필요한 계약

| | 표준 API | 필요한 계약 |
|---|---|---|
| URL 디코딩 | `+` 는 공백, 깨진 `%` 는 예외 | `+` 는 `+`, 깨진 `%` 는 리터럴, **예외 없음** |
| Base64 디코딩 | 엄격한 표준 알파벳만 | 방언 흡수 + **던지지 않는 판별** |

한 줄로 요약하면, **표준 API는 "생산자가 규격을 지켰다"를 전제하는데 이 엔드포인트는 그걸 전제할 수 없다.**
규격 위반을 예외가 아니라 데이터로 취급해야 형태 판별 분기가 성립하고, 그래서 관용적인 코덱을 따로 두게 됐다.

### 3.3 컴포넌트별 책임

| 클래스 | 역할 |
|---|---|
| `SafeUrlDecoder` | **`URLDecoder` 대체.** RFC 3986 기준 관대한 퍼센트 디코더. 어떤 입력에도 예외를 던지지 않는다 |
| `PurchasePayloadDecoder` | **`JSONObject` 파싱으로 형태 판별** 후 복원 경로 결정 (2중 방어의 바깥쪽) |
| `Base64Codec` | 단말기별 Base64 방언(URL-safe, 패딩, 줄바꿈) 흡수 + **공백 → `+` 복원** + `isBase64()` 판별 |
| `AesCipher` | AES/CBC/PKCS5Padding, IV(16B)를 암호문 앞에 붙이는 방식 |
| `PurchaseController` | `@RequestBody` 대신 **원시 입력 스트림**을 **UTF-8 고정**으로 읽는다 |

형태 판별은 파서에게, 공백 복원은 `Base64Codec` 에, `%`/`+` 보존은 `SafeUrlDecoder` 에 있고 **서로 겹치지 않는다.**

### 3.4 `URLDecoder` vs `SafeUrlDecoder` 동작 대조

| 입력 | `URLDecoder` | `SafeUrlDecoder` |
|---|---|---|
| `{"discountRate":"10%"}` | 💥 `IllegalArgumentException` | ✅ 원문 그대로 |
| `100%` / `%zz` / `%2` | 💥 예외 | ✅ 원문 그대로 |
| `+821012345678` | ⚠️ `" 821012345678"` | ✅ `+821012345678` |
| `ab+cd/ef+gh==` | ⚠️ `ab cd/ef gh==` | ✅ 원문 그대로 |
| `a%25b%c` | 💥 예외 | ✅ `a%b%c` |
| `%7B%22a%22%7D` / `%ED%95%9C%EA%B8%80` | `{"a"}` / `한글` | ✅ 동일 |

> 정상적으로 인코딩된 입력에 대해서는 결과가 같다. **`+` 해석만 규격상 다르다.**

### 3.5 남는 교훈

1. **이름이 아니라 규격을 확인한다.** `URLDecoder` 는 URL 디코더가 아니라 폼 디코더다.
2. **조용한 오염이 터지는 예외보다 위험하다.** 문제 2는 예외가 없어 catch 기반 방어를 전부 통과한다.
3. **관대한 파싱은 판별과 짝을 이뤄야 한다.** 디코더만 고치면 `%25` 같은 유효한 이스케이프를 여전히 훼손한다.
4. **단위 테스트는 계층 간 상호작용을 못 잡는다.** 선두 공백 버그는 실서버 요청으로만 드러났다.

테스트는 **버그를 `URLDecoder` 로 먼저 재현하고 나서 해결을 확인**하는 대조 구조로 작성했다
(`SafeUrlDecoderTest`, `Base64CodecTest`, `PurchasePayloadDecoderTest`, `PurchaseControllerTest`).

---

## 참고

- [RFC 3986 — URI Generic Syntax](https://datatracker.ietf.org/doc/html/rfc3986#section-2.1)
- [WHATWG URL Standard — `application/x-www-form-urlencoded`](https://url.spec.whatwg.org/#application/x-www-form-urlencoded)
- [`java.net.URLDecoder` Javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/URLDecoder.html)
