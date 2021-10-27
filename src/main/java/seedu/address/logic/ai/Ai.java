package seedu.address.logic.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;


public class Ai {

    private static final Logger logger = LogsCenter.getLogger(Ai.class);

    /**
     * Returns the Cosine Similarity between two vectors
     *
     * @param featureMap1 features of first vector
     * @param featureMap2 features of second vector
     * @return cosine similarity score
     */
    public static double getCosineSimilarity(HashMap<String, Double> featureMap1,
                                               HashMap<String, Double> featureMap2) {
        Set<String> featureSet = new HashSet<>(featureMap1.keySet());
        featureSet.addAll(featureMap2.keySet());
        double bigP = 0;
        double bigQ = 0;
        double pq = 0;
        for (String feature: featureSet) {
            double p = featureMap1.getOrDefault(feature, 0.0);
            double q = featureMap2.getOrDefault(feature, 0.0);
            bigP += Math.pow(p, 2);
            pq += p * q;
            bigQ += Math.pow(q, 2);
        }
        return (pq / (Math.sqrt(bigP) * Math.sqrt(bigQ)));
    }

    /**
     * Returns the Euclidean Distance between two vectors
     *
     * @param featureMap1 features of first vector
     * @param featureMap2 features of second vector
     * @return euclidean distance
     */
    public static double getEuclideanDistance(HashMap<String, Double> featureMap1,
                                           HashMap<String, Double> featureMap2) {
        Set<String> featureSet = new HashSet<>(featureMap1.keySet());
        featureSet.addAll(featureMap2.keySet());
        double dist = 0;
        for (String feature: featureSet) {
            double p = featureMap1.getOrDefault(feature, 0.0);
            double q = featureMap2.getOrDefault(feature, 0.0);
            dist += Math.pow(p - q, 2);
        }
        return Math.sqrt(dist);
    }

    /**
     * Returns the Manhattan Distance between two vectors
     *
     * @param featureMap1 features of first vector
     * @param featureMap2 features of second vector
     * @return manhattan distance
     */
    public static double getManhattanDistance(HashMap<String, Double> featureMap1,
                                              HashMap<String, Double> featureMap2) {
        Set<String> featureSet = new HashSet<>(featureMap1.keySet());
        featureSet.addAll(featureMap2.keySet());
        double dist = 0;
        for (String feature: featureSet) {
            double p = featureMap1.getOrDefault(feature, 0.0);
            double q = featureMap2.getOrDefault(feature, 0.0);
            dist += Math.abs(p - q);
        }
        return dist;
    }

    private static void normalizeRepoCount(HashMap<String, HashMap<String, Double>> features,
                                           HashMap<String, Double> mainUser) {
        double sum = features.values().parallelStream().mapToDouble(u -> u.get("repo-count")).sum()
                + mainUser.get("repo-count");
        features.values().parallelStream().forEach(k -> k.replace("repo-count", k.get("repo-count") / sum));
        mainUser.replace("repo-count", mainUser.get("repo-count") / sum);
    }

    private static HashMap<String, Double> sort(HashMap<String, Double> distances) {
        LinkedList<HashMap.Entry<String, Double>> list =
                new LinkedList<>(distances.entrySet());

        list.sort(HashMap.Entry.comparingByValue());

        HashMap<String, Double> result = new LinkedHashMap<>();

        for (Iterator<HashMap.Entry<String, Double>> it = list.descendingIterator(); it.hasNext(); ) {

            HashMap.Entry<String, Double> aa = it.next();

            result.put(aa.getKey(), aa.getValue());
        }
        return result;
    }

    private static void normalize(HashMap<String, Double> scores) {
        double sum = scores.values().parallelStream().mapToDouble(v -> v).sum();
        scores.keySet().parallelStream().forEach(k -> scores.replace(k, 1 - scores.get(k) / sum));
    }

    /**
     * Returns the Average Distance Metric between two vectors
     *
     * @param featureMap1 features of first vector
     * @param featureMap2 features of second vector
     * @return average distance score
     */
    public static double getDistanceMetric(HashMap<String, Double> featureMap1, HashMap<String, Double> featureMap2) {
        return (getEuclideanDistance(featureMap1, featureMap2)
                + getManhattanDistance(featureMap1, featureMap2)
                + (1 - getCosineSimilarity(featureMap1, featureMap2))) / 3.0;
    }

    /**
     * Returns the sorted {@code HashMap} object based on decreasing similarity
     *
     * @param mainUserStat username of the user to compare with
     * @param stats stats of all the users
     * @return a sorted dictionary based on similarity
     */
    public static HashMap<String, Double> getSimilarityScore(HashMap<String, Double> mainUserStat,
                                                             HashMap<String, HashMap<String, Double>> stats) {
        HashMap<String, Double> distance = new HashMap<>();
        normalizeRepoCount(stats, mainUserStat);
        stats.keySet().parallelStream().forEach(u -> distance.put(u, getDistanceMetric(mainUserStat, stats.get(u))));
        normalize(distance);
        return sort(distance);
    }

    /**
     * Sorts the {@code ObservableList} object that is shown
     * @param user Main user Person object
     * @param list {@code ObservableList} object of Persons
     */
    public static boolean sortProfiles(Person user, ObservableList<Person> list) {
        HashMap<String, HashMap<String, Double>> stats = new HashMap<>();
        list.parallelStream().forEach(p -> {
            HashMap<String, Double> temp = p.getGitStats();
            if (!temp.isEmpty()) {
                stats.put(p.getGithub().value,
                        (HashMap<String, Double>) temp.entrySet().stream().collect(
                                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            }
        });
        HashMap<String, Double> mainUserStats = (HashMap<String, Double>) user.getGitStats()
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (mainUserStats.isEmpty() || stats.keySet().size() != list.size()) {
            logger.info("GitHub Metadata is still being gathered.");
            return false;
        }

        HashMap<String, Double> scores = getSimilarityScore(mainUserStats, stats);
        logger.info("Similarity Scores: " + scores);
        FXCollections.sort(list, (p1, p2) -> {
            double s1 = scores.get(p1.getGithub().value);
            double s2 = scores.get(p2.getGithub().value);
            if (s1 - s2 < 0) {
                return 1;
            } else if (s1 - s2 > 0) {
                return -1;
            } else {
                return 0;
            }
        });
        return true;
    }
}
