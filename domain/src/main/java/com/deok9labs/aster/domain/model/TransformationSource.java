package com.deok9labs.aster.domain.model;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.Objects;

/**
 * 변환 규칙을 적용할 정수와 문자열이다.
 *
 * <p>변환 규칙은 정수를 2배로 만들고 문자열의 글자 순서를 뒤집는 것이다.</p>
 *
 * @param number 2배로 만들 정수
 * @param text 순서를 뒤집을 문자열. 빈 문자열은 허용한다
 */
public record TransformationSource(int number, String text) {

    /**
     * 문자열이 없는 입력은 변환할 수 없으므로 거부한다.
     *
     * @throws NullPointerException text가 {@code null}일 때
     */
    public TransformationSource {
        Objects.requireNonNull(text, "text must not be null");
    }

    /**
     * 변환 규칙을 적용한다.
     *
     * <p>정수는 {@code long}으로 계산하므로 {@code int} 범위의 모든 입력에 대해 overflow 없이 정확한 값을 반환한다.
     * 문자열은 사용자가 한 글자로 인식하는 grapheme cluster 단위로 뒤집어 결합 문자나 emoji 조합이 분리되지 않게 한다.</p>
     *
     * @return 정수는 2배, 문자열은 뒤집은 결과
     */
    public TransformationResult transform() {
        return new TransformationResult(2L * number, reverseGraphemeClusters(text));
    }

    private static String reverseGraphemeClusters(String text) {
        // code point 단위로 뒤집으면 피부색 수식자나 ZWJ로 이어진 emoji가 분리되므로 글자 경계를 기준으로 뒤집는다.
        BreakIterator boundaries = BreakIterator.getCharacterInstance(Locale.ROOT);
        boundaries.setText(text);
        StringBuilder reversed = new StringBuilder(text.length());
        int end = boundaries.last();
        for (int start = boundaries.previous(); start != BreakIterator.DONE; start = boundaries.previous()) {
            reversed.append(text, start, end);
            end = start;
        }
        return reversed.toString();
    }
}
