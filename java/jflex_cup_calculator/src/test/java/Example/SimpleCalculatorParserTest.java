package Example;

import java.io.ByteArrayInputStream;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

public class SimpleCalculatorParserTest extends TestCase {

    public SimpleCalculatorParserTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testCanAddTwoNumbers() throws Exception{
        List<Integer> results = Parser.getResults(new ByteArrayInputStream("4+4;".getBytes()));
        assertEquals("Should parse the expression and return 8", (Integer)8,results.get(0));
    }
    
    public void testCanMultiplyTwoNumbers() throws Exception{
        List<Integer> results = Parser.getResults(new ByteArrayInputStream("4*4;".getBytes()));
        assertEquals("Should parse the expression and return 16", (Integer)16,results.get(0));   
    }
    
    public void testSingleNumber() throws Exception{
        List<Integer> results = Parser.getResults(new ByteArrayInputStream("57;".getBytes()));
        assertEquals("Should parse the expression and return 57", (Integer)57,results.get(0));           
    }

    public void testParenthesis() throws Exception{
        List<Integer> results = Parser.getResults(new ByteArrayInputStream("4*(1+4);".getBytes()));
        assertEquals("Should parse the expression and return 20", (Integer)20,results.get(0));  
    }

}
