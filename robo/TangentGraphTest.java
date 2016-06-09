package robo;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import robo.Utils.Polar;

public class TangentGraphTest {

    public TangentGraphTest() {
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

    static private TangentGraph CreateTangentGraph()
    {
        return new TangentGraph(0, 0, 1, 1);
    }

    static private TangentGraph CreateTangentGraph(double goal_angle)
    {
        final int goal_x = 1000000;
        final int goal_y = (int) ((double) goal_x * Math.tan(goal_angle));
        return new TangentGraph(0, 0, goal_x, goal_y);
    }

    @Test
    public void testAddSensorSample1() {
        TangentGraph tg = CreateTangentGraph();
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
    public void testAddSensorSample2() {
        TangentGraph tg = CreateTangentGraph();
        tg.AddSensorSample(1, 0.1);
        tg.AddSensorSample(2, 0.2);
        tg.AddSensorSample(3, 0.3);
        tg.AddSensorSample(-1, 0.4);
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
    public void testAddSensorSample3() {
        TangentGraph tg = CreateTangentGraph();
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
    public void testAddSensorSample4() {
        TangentGraph tg = CreateTangentGraph();
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
    public void testAddSensorSample5() {
        TangentGraph tg = CreateTangentGraph();
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
    public void testAddSensorSample_goalVisible1() {
        TangentGraph tg = CreateTangentGraph(0.11);
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
    public void testAddSensorSample_goalVisible2() {
        TangentGraph tg = CreateTangentGraph(0.26);
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
    }

    @Test
    public void testAddSensorSample6() {
        TangentGraph tg = CreateTangentGraph();
        tg.AddSensorSample(1, 0.05);
        tg.AddSensorSample(2, 0.1);
        tg.AddSensorSample(101, 0.15);
        tg.AddSensorSample(102, 0.2);
        tg.AddSensorSample(-1, 0.9);
        tg.Finish();
        ArrayList<Polar> nodes = tg.GetNodes();
        assertTrue(nodes.size()==4);
        assertTrue(nodes.get(0).distance==1);
        assertTrue(nodes.get(1).distance==2);
        assertTrue(nodes.get(1).angle==nodes.get(2).angle);
        assertTrue(nodes.get(2).distance==101);
        assertTrue(nodes.get(3).distance==102);
    }
}
