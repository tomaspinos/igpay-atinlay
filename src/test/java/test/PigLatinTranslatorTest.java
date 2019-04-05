package test;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class PigLatinTranslatorTest {

    private PigLatinTranslator translator;

    @Before
    public void setUp() {
        translator = new PigLatinTranslator();
    }

    @Test
    public void shouldHandleBlankInput() {
        assertThat(translator.translate("  "), is(""));
    }

    @Test
    public void shouldHandleNullInput() {
        assertThat(translator.translate(null), is(nullValue()));
    }

    @Test
    public void shouldKeepCapitalizationInSamePlace() {
        assertThat(translator.translate("Beach"), is("Eachbay"));
        assertThat(translator.translate("McCloud"), is("CcLoudmay"));
    }

    @Test
    public void shouldKeepPunctuationInPlace() {
        assertThat(translator.translate("can't"), is("antca'y"));
        assertThat(translator.translate("end."), is("endway."));
        assertThat(translator.translate("\"end\"."), is("end\"way\"."));
        assertThat(translator.translate("a'b'c'd"), is("abcd'w'a'y"));
    }

    @Test
    public void shouldTranslateComplexSentences() {
        assertThat(translator.translate("pig-latin"), is("igpay-atinlay"));
        assertThat(translator.translate("One two 'three'."), is("Oneway wotay hr'eetay'."));
    }

    @Test
    public void shouldTreatHyphensAsTwoWords() {
        assertThat(translator.translate("this-thing"), is("histay-hingtay"));
        assertThat(translator.translate("a-b-c-d"), is("away-bay-cay-day"));
    }

    @Test
    public void testConsonantAndAyRule() {
        assertThat(translator.translate("Hello"), is("Ellohay"));
    }

    @Test
    public void testEndsInWayRule() {
        assertThat(translator.translate("stairway"), is("stairway"));
    }

    @Test
    public void testVowelAndWayRule() {
        assertThat(translator.translate("apple"), is("appleway"));
    }
}