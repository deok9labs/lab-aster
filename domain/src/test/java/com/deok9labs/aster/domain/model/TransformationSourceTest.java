package com.deok9labs.aster.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TransformationSourceTest {

    @Test
    void doublesNumberAndReversesText() {
        TransformationResult result = new TransformationSource(21, "hello").transform();

        assertEquals(new TransformationResult(42, "olleh"), result);
    }

    @Test
    void doublesIntBoundariesWithoutOverflow() {
        assertEquals(4_294_967_294L, new TransformationSource(Integer.MAX_VALUE, "").transform().number());
        assertEquals(-4_294_967_296L, new TransformationSource(Integer.MIN_VALUE, "").transform().number());
    }

    @Test
    void keepsEmptyTextEmpty() {
        assertEquals("", new TransformationSource(0, "").transform().text());
    }

    @Test
    void reversesKoreanText() {
        assertEquals("다나가", new TransformationSource(0, "가나다").transform().text());
    }

    @Test
    void keepsGraphemeClustersTogether() {
        String combiningAccent = "e\u0301";
        String thumbsUpWithSkinTone = "\uD83D\uDC4D\uD83C\uDFFD";
        String family = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67";

        String text = "a" + combiningAccent + thumbsUpWithSkinTone + family + "b";

        assertEquals("b" + family + thumbsUpWithSkinTone + combiningAccent + "a",
                new TransformationSource(0, text).transform().text());
    }

    @Test
    void rejectsNullText() {
        assertThrows(NullPointerException.class, () -> new TransformationSource(0, null));
    }
}
