package jplus.util;

import jplus.generator.TextChangeRange;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FragmentedTextTest {

    @Test
    void testUpdateSingleLine() {
        TextChangeRange range = new TextChangeRange(12, 10, 12, 29);
        String original = "ive either way rebel";
        FragmentedText fragmentedText = new FragmentedText(range, original);

        fragmentedText.update(new TextChangeRange(12,10,12, 12), "meiden");
        assertEquals("meiden either way rebel", fragmentedText.toString());

        fragmentedText.update(new TextChangeRange(12,10,12, 12), "meisje");
        assertEquals("meisje either way rebel", fragmentedText.toString());

        fragmentedText.update(new TextChangeRange(12,25,12, 29), "verzet");
        assertEquals("meisje either way verzet", fragmentedText.toString());

        fragmentedText.update(new TextChangeRange(12,25,12, 29), "rebel");
        assertEquals("meisje either way rebel", fragmentedText.toString());

        fragmentedText.update(new TextChangeRange(12,14,12, 23), "off the record");
        assertEquals("meisje off the record rebel", fragmentedText.toString());

        fragmentedText.update(new TextChangeRange(12,14,12, 23), "either way");
        assertEquals("meisje either way rebel", fragmentedText.toString());

    }

    @Test
    void testUpdateMultiLine() throws IOException {
        //String text = Files.readString(Paths.get("./src/test/samples/ElvisOperator.jplus"));
        //TextChangeRange textRange = new TextChangeRange(1,0, 9,0);
        TextChangeRange textRange = new TextChangeRange(4, 25, 9, 4);
        String text = "{\n" +
                "        String? s1 = null;\n" +
                "        String s2 = s1 ?: \"jplus\";\n" +
                "        System.out.printf(\"s1 = %s\\n\", s1 ?: \"null-value\");\n" +
                "        System.out.printf(\"s2 = %s\\n\", s2);\n" +
                "    }";
        FragmentedText fragmentedText = new FragmentedText(textRange, text);
        assertEquals(text, fragmentedText.toString());

        TextChangeRange range = new TextChangeRange(4, 25, 9, 4);
        String replaced = "{\n" +
                "        String? s1 = null;\n" +
                "        String s2 = (((s1)!=null)?(s1):(\"jplus\"));\n" +
                "        System.out.printf(\"s1 = %s\\n\", (((s1)!=null)?(s1):(\"null-value\")));\n" +
                "        System.out.printf(\"s2 = %s\\n\", s2);\n" +
                "    }";
        fragmentedText.update(range, replaced);
        //System.out.println(stringVersion.toString());
        assertEquals(replaced, fragmentedText.toString());
    }

    @Test
    void testUpdateMultiLine2() throws IOException {
        //String text = Files.readString(Paths.get("./src/test/samples/ElvisOperator.jplus"));
        //TextChangeRange textRange = new TextChangeRange(1,0, 9,0);
        TextChangeRange textRange = new TextChangeRange(6, 0, 9, 0);
        String text = "apply {\n" +
                "    User: data, builder;\n" +
                "    User.Profile: data, constructors(all);\n" +
                "}";
        FragmentedText fragmentedText = new FragmentedText(textRange, text);
        assertEquals(text, fragmentedText.toString());

        TextChangeRange range = new TextChangeRange(6, 0, 9, 0);
        String replaced = "";
        fragmentedText.update(range, replaced);
        //System.out.println(stringVersion.toString());
        assertEquals(replaced, fragmentedText.toString());
    }

//    @Test
//    void testUpdateMultiLine3() throws IOException {
//        //String text = Files.readString(Paths.get("./src/test/samples/ElvisOperator.jplus"));
//        //TextChangeRange textRange = new TextChangeRange(1,0, 9,0);
//        TextChangeRange textRange = new TextChangeRange(6, 0, 9, 0);
//        String text = "apply {\n" +
//                "    User: data, builder;\n" +
//                "    User.Profile: data, constructors(all);\n" +
//                "}";
//        FragmentedText fragmentedText = new FragmentedText(textRange, text);
//        assertEquals(text, fragmentedText.toString());
//
//        TextChangeRange range = new TextChangeRange(6, 0, 9, 0);
//        String replaced = null;
//        fragmentedText.update(range, null);
//        //System.out.println(stringVersion.toString());
//        assertEquals(replaced, fragmentedText.toString());
//    }

    @Test
    void testToString() {
    }
}