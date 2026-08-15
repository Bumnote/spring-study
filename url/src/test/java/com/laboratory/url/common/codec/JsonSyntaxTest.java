package com.laboratory.url.common.codec;

import static org.assertj.core.api.Assertions.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonSyntaxTest {

	private final String DEFAULT_PAYLOAD = "{\"name\":\"Tom\",\"age\":\"12\"}";
	private final String WRONG_PAYLOAD = "{\"name\":\"Tom\",\"age\":\"12\"}HELLO";
	private final String NOT_JSON_PAYLOAD = "{\"name\":\"Tom\",\"age\" \"12\"}";

	/*
	 * JSONTokener: 문자열을 JSON 문법에 맞게 순차적으로 읽을 수 있도록 도와주는 토큰 읽기(cursor) 객체이다.
	 * 따라서, JSONTokener는 JSON 형식인지 검증하지 않는다. JSON 객체를 읽을 수 있도록 도와주는 역할만 수행한다.
	 * JSONObject: JSONTokener를 이용해서 실제 JSON Object를 파싱한다.
	 * */
	@Test
	@DisplayName("JSONTokener를 적용해서 JSONObject 객체를 만들면 정상적으로 생성된다.")
	void jsonTokenerToJsonObjectTest() {

		// given
		String payload = DEFAULT_PAYLOAD;

		// when
		JSONTokener tokener = new JSONTokener(payload);
		JSONObject jsonObject = new JSONObject(tokener);

		// then
		assertThat(jsonObject.toString()).isEqualTo(DEFAULT_PAYLOAD);
		assertThat(jsonObject.getString("name")).isEqualTo("Tom");
		assertThat(jsonObject.getString("age")).isEqualTo("12");
	}

	@Test
	@DisplayName("잘못된 json string에 JSONTokener을 적용해도 JSONObject 객체를 만들 수 있다.")
	void wrongJsonTokenertTest() {

		// given
		String payload = WRONG_PAYLOAD;

		// when
		JSONTokener tokener = new JSONTokener(payload);
		JSONObject jsonObject = new JSONObject(tokener);

		// then
		assertThat(jsonObject.toString()).isEqualTo(DEFAULT_PAYLOAD);
	}

	@Test
	@DisplayName("json 형식이 존재하지 않는 string 데이터는 JSONException 예외를 발생시킨다.")
	void notJsonTokenerJSONExceptionTest() {

		// given
		String payload = NOT_JSON_PAYLOAD;

		// when
		JSONTokener tokener = new JSONTokener(payload);
		System.out.println(String.format("tokener: %s", tokener));

		// then
		assertThatThrownBy(() -> {
			JSONObject jsonObject = new JSONObject(tokener);
		}).isInstanceOf(JSONException.class);
	}

	/*
	 * JSONTokener.nextClean() 반환 타입: char
	 * JSONTokener: 더 이상 읽을 문자가 없으면 NUL 문자('\0'), 즉 숫자 값으로 0인 문자 반환
	 * */
	@Test
	@DisplayName("json 데이터 뒤에 불순물 문자가 붙어있는 경우, JSONTokener 객체의 nextClean() 결과는 0이 아니다.")
	void wrongJsonTokenerNextCleanResultTest() {

		// given
		String payload = WRONG_PAYLOAD;

		// when
		JSONTokener tokener = new JSONTokener(payload);
		JSONObject jsonObject = new JSONObject(tokener);
		char nextCleanResult = tokener.nextClean();

		// then
		assertThat(nextCleanResult).isEqualTo('H');
	}

	@Test
	@DisplayName("완전한 json 데이터의 JSONTokener 객체의 nextClean() 결과는 0이다.")
	void jsonTokenerNextCleanResultTest() {

		// given
		String payload = DEFAULT_PAYLOAD;

		// when
		JSONTokener tokener = new JSONTokener(payload);
		JSONObject jsonObject = new JSONObject(tokener);
		char nextCleanResult = tokener.nextClean();

		// then
		assertThat(nextCleanResult).isEqualTo('\0');
	}
}