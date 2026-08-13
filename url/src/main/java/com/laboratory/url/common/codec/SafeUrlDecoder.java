package com.laboratory.url.common.codec;

import static lombok.AccessLevel.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class SafeUrlDecoder {

	private static final char ESCAPE = '%';
	private static final int ESCAPE_LENGTH = 3;

	public static String decode(String value) {
		return decode(value, StandardCharsets.UTF_8);
	}

	public static String decode(String value, Charset charset) {
		if (value == null || value.isEmpty()) {
			return value;
		}
		// 이스케이프가 하나도 없으면 디코딩할 것이 없다. ('+'를 건드리지 않으므로 원문 그대로가 정답)
		if (value.indexOf(ESCAPE) < 0) {
			return value;
		}

		int length = value.length();
		StringBuilder decoded = new StringBuilder(length);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream(ESCAPE_LENGTH);

		int index = 0;
		while (index < length) {
			char current = value.charAt(index);

			if (current != ESCAPE) {
				flush(bytes, decoded, charset);
				decoded.append(current); // '+' 포함, 원문 그대로 보존
				index++;
				continue;
			}

			int octet = readOctet(value, index);
			if (octet < 0) {
				// %XX 형태가 아니다 → 이스케이프가 아닌 리터럴 '%'로 취급 (예외를 던지지 않는다)
				flush(bytes, decoded, charset);
				decoded.append(ESCAPE);
				index++;
				continue;
			}

			bytes.write(octet);
			index += ESCAPE_LENGTH;
		}
		flush(bytes, decoded, charset);

		return decoded.toString();
	}

	private static int readOctet(String value, int index) {
		if (index + 2 >= value.length()) {
			return -1;
		}
		int high = hexValue(value.charAt(index + 1));
		int low = hexValue(value.charAt(index + 2));
		if (high < 0 || low < 0) {
			return -1;
		}
		return (high << 4) + low;
	}

	private static int hexValue(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		}
		if (c >= 'a' && c <= 'f') {
			return c - 'a' + 10;
		}
		if (c >= 'A' && c <= 'F') {
			return c - 'A' + 10;
		}
		return -1;
	}

	private static void flush(ByteArrayOutputStream bytes, StringBuilder decoded, Charset charset) {
		if (bytes.size() == 0) {
			return;
		}
		decoded.append(new String(bytes.toByteArray(), charset));
		bytes.reset();
	}
}