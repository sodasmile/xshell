package com.sodasmile.xshell.args;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author <a href="mailto:runepeter@gmail.com">Rune Peter Bj&oslash;rnstad</a>
 */
public class OptionsTest {

    enum TestType {
        SOMETHING, NOTHING
    }

    public static class TestClass {

        @Option( name = "a", description = "A required string value", required = true )
        public String requiredStringValue;

        @Option( name = "b", description = "An optional string value" )
        public String optionalStringValue;

        @Option( name = "c", description = "A string array value", delimiter = ':' )
        public String[] stringArray;

        @Option( name = "i", description = "An int array value", delimiter = ':' )
        public int[] intArray;

        @Option( name = "d", description = "An enum constant value" )
        public TestType enumValue;

        @Option( name = "e", description = "An array of enum constants value" )
        public TestType[] enumArray;

        @Option( name = "f", description = "A boolean value" )
        public boolean cool;

        @Option( name = "p", description = "A private field option." )
        private String privateString;

    }

    @Test( expectedExceptions = IllegalArgumentException.class )
    public void testNonexistentRequiredValue() throws Exception {

        TestClass instance = new TestClass();

        String[] args = { };
        Options.apply( instance, args );
    }

    @Test
    public void testRequiredStringValue() throws Exception {

        TestClass instance = new TestClass();

        String[] args = { "-a", "string" };
        Options.apply( instance, args );

        Assert.assertEquals( instance.requiredStringValue, "string" );
    }

    @Test
    public void testBooleanOption() throws Exception {

        TestClass instance = new TestClass();

        String[] args = { "-a", "string", "-f" };
        Options.apply( instance, args );

        Assert.assertTrue( instance.cool );
    }

    @Test
    public void testEnumOption() throws Exception {

        TestClass instance = new TestClass();

        String[] args = { "-a", "string", "-d", "SOMETHING" };
        Options.apply( instance, args );

        Assert.assertEquals( instance.enumValue, TestType.SOMETHING );
    }

    @Test
    public void testStringArrayOption() throws Exception {

        TestClass instance = new TestClass();

        String[] args = { "-a", "string", "-c", "a:b:c:d:e" };

        Options.apply( instance, args );

        Assert.assertEquals( instance.stringArray, "a:b:c:d:e".split( ":" ) );
    }

    @Test
    public void testIntArrayOption() throws Exception {

        TestClass instance = new TestClass();

        String[] args = { "-a", "string", "-i", "1:2:3:4:5" };

        Options.apply( instance, args );

        Assert.assertTrue( Arrays.equals( instance.intArray, new int[]{ 1, 2, 3, 4, 5 } ) );
    }

    @Test
    public void testPrivateFieldOption() throws Exception {

        TestClass instance = new TestClass();

        String[] args = { "-a", "string", "-p", "private" };

        Options.apply( instance, args );

        Assert.assertEquals( instance.privateString, "private" );
    }

}
