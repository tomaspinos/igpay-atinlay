package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PigLatinTranslatorTest {

    private PigLatinTranslator translator;

    @BeforeEach
    void setUp() {
        translator = new PigLatinTranslator();
    }

    @Test
    void shouldHandleBlankInput() {
        assertEquals("", translator.translate("  "));
    }

    @Test
    void shouldHandleNullInput() {
        assertNull(translator.translate(null));
    }

    @Test
    void shouldKeepCapitalizationInSamePlace() {
        assertAll(
                () -> assertEquals("Eachbay", translator.translate("Beach")),
                () -> assertEquals("CcLoudmay", translator.translate("McCloud"))
        );
    }

    @Test
    void shouldKeepPunctuationInPlace() {
        assertAll(
                () -> assertEquals("antca'y", translator.translate("can't")),
                () -> assertEquals("endway.", translator.translate("end.")),
                () -> assertEquals("end\"way\".", translator.translate("\"end\".")),
                () -> assertEquals("abcd'w'a'y", translator.translate("a'b'c'd"))
        );
    }

    @Test
    void shouldTranslateComplexSentences() {
        assertAll(
                () -> assertEquals("igpay-atinlay", translator.translate("pig-latin")),
                () -> assertEquals("Oneway wotay hr'eetay'.", translator.translate("One two 'three'.")),
                () -> assertEquals("Callaway Entway Thataway.", translator.translate("Callaway Went Thataway.")),
                () -> assertEquals("Hancellorcay Hilippay Ammondhay ashay aidsay ehay isway opt\"imisticway\"" +
                                " Rexitbay iscussionsday etweenbay hetay overnmentgay andway Abourlay ancay eachray om\"esay ormfay ofway agreementway\".",
                        translator.translate("Chancellor Philip Hammond has said he is \"optimistic\"" +
                                " Brexit discussions between the government and Labour can reach \"some form of agreement\"."))
        );
    }

    @Test
    void shouldTreatHyphensAsTwoWords() {
        assertAll(
                () -> assertEquals("histay-hingtay", translator.translate("this-thing")),
                () -> assertEquals("away-bay-cay-day", translator.translate("a-b-c-d"))
        );
    }

    @Test
    void testConsonantAndAyRule() {
        assertEquals("Ellohay", translator.translate("Hello"));
    }

    @Test
    void testEndsInWayRule() {
        assertEquals("stairway", translator.translate("stairway"));
    }

    @Test
    void testVowelAndWayRule() {
        assertEquals("appleway", translator.translate("apple"));
    }
}
