package robo;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import robo.Utils.Polar;

public class TangentBugTest {

    public TangentBugTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testVisitFinish1() {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph();
        tg.Visit(1, 0.1);
        tg.Visit(2, 0.2);
        tg.Visit(3, 0.3);
        tg.Visit(-1, 0.4);
        tg.Visit(4, 0.5);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==2);
        assertTrue(nodes.get(0).distance==4);
        assertTrue(nodes.get(1).distance==3);
    }

    @Test
    public void testVisitFinish2() {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph();
        tg.Visit(1, 0.1);
        tg.Visit(2, 0.2);
        tg.Visit(3, 0.3);
        tg.Visit(-1, 0.4);
        //tg.Visit(-1, 0.5);
        tg.Visit(4, 0.6);
        tg.Visit(5, 0.7);
        tg.Visit(6, 0.8);
        tg.Visit(-1, 0.9);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==4);
        assertTrue(nodes.get(0).distance==1);
        assertTrue(nodes.get(1).distance==3);
        assertTrue(nodes.get(2).distance==4);
        assertTrue(nodes.get(3).distance==6);
    }

    @Test
    public void testVisitFinish3() {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph();
        tg.Visit(1, 0.1); // end of sector
        tg.Visit(-1, 0.2);
        tg.Visit(-1, 0.3);
        tg.Visit(10, 0.4); // begin of sector
        tg.Visit(11, 0.5);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==2);
        assertTrue(nodes.get(0).distance==10);
        assertTrue(nodes.get(1).distance==1);
    }

    @Test
    public void testVisitFinish4() {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph();
        tg.Visit(1, 0.1);
        tg.Visit(2, 0.15); // end of sector
        tg.Visit(-1, 0.2);
        tg.Visit(-1, 0.3);
        tg.Visit(10, 0.4); // begin of sector
        tg.Visit(11, 0.5);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==2);
        assertTrue(nodes.get(0).distance==10);
        assertTrue(nodes.get(1).distance==2);
    }
}
