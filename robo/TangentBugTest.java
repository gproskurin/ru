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
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph(0);
        tg.AddSensorSample(1, 0.1);
        tg.AddSensorSample(2, 0.2);
        tg.AddSensorSample(3, 0.3);
        tg.AddSensorSample(-1, 0.4);
        tg.AddSensorSample(4, 0.5);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==2);
        assertTrue(nodes.get(0).distance==4);
        assertTrue(nodes.get(1).distance==3);
    }

    @Test
    public void testVisitFinish2() {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph(0);
        tg.AddSensorSample(1, 0.1);
        tg.AddSensorSample(2, 0.2);
        tg.AddSensorSample(3, 0.3);
        tg.AddSensorSample(-1, 0.4);
        //tg.Visit(-1, 0.5);
        tg.AddSensorSample(4, 0.6);
        tg.AddSensorSample(5, 0.7);
        tg.AddSensorSample(6, 0.8);
        tg.AddSensorSample(-1, 0.9);
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
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph(0);
        tg.AddSensorSample(1, 0.1); // end of sector
        tg.AddSensorSample(-1, 0.2);
        tg.AddSensorSample(-1, 0.3);
        tg.AddSensorSample(10, 0.4); // begin of sector
        tg.AddSensorSample(11, 0.5);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==2);
        assertTrue(nodes.get(0).distance==10);
        assertTrue(nodes.get(1).distance==1);
    }

    @Test
    public void testVisitFinish4() {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph(0);
        tg.AddSensorSample(1, 0.1);
        tg.AddSensorSample(2, 0.15); // end of sector
        tg.AddSensorSample(-1, 0.2);
        tg.AddSensorSample(-1, 0.3);
        tg.AddSensorSample(10, 0.4); // begin of sector
        tg.AddSensorSample(11, 0.5);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==2);
        assertTrue(nodes.get(0).distance==10);
        assertTrue(nodes.get(1).distance==2);
    }

    @Test
    public void testVisitFinish5() {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph(0);
        tg.AddSensorSample(-1, 0.05);
        tg.AddSensorSample(1, 0.1);
        tg.AddSensorSample(2, 0.15);
        tg.AddSensorSample(-1, 0.2);
        tg.AddSensorSample(-1, 0.3);
        tg.AddSensorSample(10, 0.4);
        tg.AddSensorSample(11, 0.5);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==4);
        assertTrue(nodes.get(0).distance==1);
        assertTrue(nodes.get(1).distance==2);
        assertTrue(nodes.get(2).distance==10);
        assertTrue(nodes.get(3).distance==11);
    }

    @Test
    public void testVisitFinish_goalVisible1() {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph(0.11);
        tg.AddSensorSample(10, 0.1);
        tg.AddSensorSample(20, 0.2);
        tg.AddSensorSample(-1, 0.25);
        tg.AddSensorSample(30, 0.3);
        tg.AddSensorSample(40, 0.4);
        tg.AddSensorSample(-1, 0.5);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==4);
        assertTrue(nodes.get(0).distance==10);
        assertTrue(nodes.get(1).distance==20);
        assertTrue(nodes.get(2).distance==30);
        assertTrue(nodes.get(3).distance==40);
        assertTrue(tg.goalIsVisible(9));
        assertFalse(tg.goalIsVisible(11));
    }

    @Test
    public void testVisitFinish_goalVisible2() {
        TangentBug.TangentGraph tg = new TangentBug.TangentGraph(0.26);
        tg.AddSensorSample(10, 0.1);
        tg.AddSensorSample(20, 0.2);
        tg.AddSensorSample(-1, 0.25);
        tg.AddSensorSample(30, 0.3);
        tg.AddSensorSample(40, 0.4);
        tg.AddSensorSample(-1, 0.5);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==4);
        assertTrue(nodes.get(0).distance==10);
        assertTrue(nodes.get(1).distance==20);
        assertTrue(nodes.get(2).distance==30);
        assertTrue(nodes.get(3).distance==40);
        assertTrue(tg.goalIsVisible(1));
        assertTrue(tg.goalIsVisible(100));
    }}
