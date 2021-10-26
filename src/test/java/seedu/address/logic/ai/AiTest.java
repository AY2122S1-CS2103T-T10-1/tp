package seedu.address.logic.ai;

import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AiTest {

    private static final HashMap<String, Double> point1 = new HashMap<>();
    private static final HashMap<String, Double> point2 = new HashMap<>();
    private static final HashMap<String, Double> point3 = new HashMap<>();

    @BeforeAll
    public static void setUp() {
        point1.put("Java", 0.3);
        point1.put("Python", 0.4);
        point1.put("repo-count", 0.2);

        point2.put("Python", 0.9);
        point2.put("C++", 0.8);
        point2.put("repo-count", 0.8);

        point3.put("Python", 0.2);
        point3.put("Java", 0.3);
        point3.put("repo-count", 0.2);
    }

    @Test
    public void getCosineSimilarity_validFeatures_success() {
        Assertions.assertTrue(1 - Ai.getCosineSimilarity(point1, point1) < 0.000001);
        Assertions.assertTrue(Ai.getCosineSimilarity(point1, point3) > Ai.getCosineSimilarity(point1, point2));
    }

    @Test
    public void getEuclideanDistance_validFeatures_success() {
        Assertions.assertEquals(0, Ai.getEuclideanDistance(point1, point1));
        Assertions.assertTrue(Ai.getEuclideanDistance(point1, point3) < Ai.getEuclideanDistance(point1, point2));
    }

    @Test
    public void getManhattanDistance_validFeatures_success() {
        Assertions.assertEquals(0, Ai.getManhattanDistance(point1, point1));
        Assertions.assertTrue(Ai.getManhattanDistance(point1, point3) < Ai.getManhattanDistance(point1, point2));
    }

    @Test
    public void getDistanceMetric_validFeatures_success() {
        Assertions.assertTrue(Ai.getDistanceMetric(point1, point1) < 0.000001);
        Assertions.assertTrue(Ai.getDistanceMetric(point1, point3) < Ai.getDistanceMetric(point1, point2));
    }

    @Test
    public void getSimilarityScore_featureList_success() {
        HashMap<String, HashMap<String, Double>> points = new HashMap<>();
        points.put("p2", point2);
        points.put("p3", point2);

        HashMap<String, Double> scores = Ai.getSimilarityScore(point1, points);
        String[] expectedOrder = new String[] {"p3", "p2"};
        String[] actualOrder = scores.keySet().toArray(new String[0]);
        Assertions.assertEquals(expectedOrder[0], actualOrder[0], "Incorrect 1st Element");
        Assertions.assertEquals(expectedOrder[1], actualOrder[1], "Incorrect 2nd Element");
    }
}
