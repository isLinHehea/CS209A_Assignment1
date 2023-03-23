import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * . a
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    /**
     * . b
     */

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4],
                    info[5],
                    Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                    Integer.parseInt(info[9]), Integer.parseInt(info[10]),
                    Double.parseDouble(info[11]),
                    Double.parseDouble(info[12]), Double.parseDouble(info[13]),
                    Double.parseDouble(info[14]),
                    Double.parseDouble(info[15]), Double.parseDouble(info[16]),
                    Double.parseDouble(info[17]),
                    Double.parseDouble(info[18]), Double.parseDouble(info[19]),
                    Double.parseDouble(info[20]),
                    Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * . c
     */

    public Map<String, Integer> getPtcpCountByInst() {
        return courses.stream()
            .collect(Collectors.groupingBy(Course::getInstitution,
                Collectors.summingInt(Course::getParticipants))).entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    /**
     * . d
     */

    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        return courses.stream().collect(
                Collectors.groupingBy(course -> course.getInstitution() + "-" + course.getSubject(),
                    Collectors.summingInt(Course::getParticipants)))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    /**
     * . e
     */

    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> result = new HashMap<>();
        Map<String, List<Course>> independentCoursesByInstructor = courses.stream()
            .filter(Course::isIndependentlyResponsible)
            .collect(Collectors.groupingBy(Course::getInstructors));

        independentCoursesByInstructor.forEach((instructorName, instructorCourses) -> {
            List<List<String>> instructorCourseLists = new ArrayList<>();
            List<String> independentCourseTitles = instructorCourses.stream()
                .map(Course::getTitle)
                .sorted()
                .collect(Collectors.toList());
            instructorCourseLists.add(independentCourseTitles);
            List<String> coDevelopedCourseTitles = new ArrayList<>();
            instructorCourseLists.add(coDevelopedCourseTitles);
            result.put(instructorName, instructorCourseLists);
        });

        Map<String, List<Course>> coDevelopedCoursesByInstructor = courses.stream()
            .filter(course -> !course.isIndependentlyResponsible())
            .collect(Collectors.groupingBy(Course::getInstructors));

        coDevelopedCoursesByInstructor.forEach((instructorName, instructorCourses) -> {
            String[] instructors = instructorName.split(", ");
            for (int i = 0; i < instructors.length; i++) {
                String instructor = instructors[i];
                List<String> coDevelopedCourseTitles = instructorCourses.stream()
                    .map(Course::getTitle)
                    .sorted()
                    .toList();
                if (result.containsKey(instructor)) {
                    result.get(instructor)
                        .set(1, Stream.concat(result.get(instructor).get(1).stream(),
                                coDevelopedCourseTitles.stream())
                            .collect(Collectors.toList()));
                } else {
                    List<List<String>> instructorCourseLists = new ArrayList<>();
                    List<String> independentCourseTitles = new ArrayList<>();
                    instructorCourseLists.add(independentCourseTitles);
                    instructorCourseLists.add(coDevelopedCourseTitles);
                    result.put(instructor, instructorCourseLists);
                }
            }
        });
        result.entrySet().forEach(entry -> {
            entry.setValue(entry.getValue().stream()
                .map(innerList -> {
                    List<String> copy = new ArrayList<>(innerList);
                    Collections.sort(copy);
                    return copy.stream().distinct().collect(Collectors.toList());
                })
                .collect(Collectors.toList()));
        });
        return result;
    }

    /**
     * . f
     */

    public List<String> getCourses(int topK, String by) {
        if (by.equals("hours")) {
            courses = courses.stream()
                .sorted(Comparator.comparing(Course::getTotalHours)
                    .reversed().thenComparing(Course::getTitle))
                .collect(Collectors.toList());
        } else if (by.equals("participants")) {
            courses = courses.stream()
                .sorted(Comparator.comparing(Course::getParticipants)
                    .reversed().thenComparing(Course::getTitle))
                .collect(Collectors.toList());
        }
        return courses.stream()
            .map(Course::getTitle)
            .distinct()
            .limit(topK)
            .collect(Collectors.toList());
    }

    /**
     * . g
     */

    public List<String> searchCourses(String courseSubject, double percentAudited,
        double totalCourseHours) {
        String subject = courseSubject.toLowerCase();
        return courses.stream()
            .filter(course -> course.getSubject().toLowerCase().contains(subject))
            .filter(course -> course.getPercentAudited() >= percentAudited)
            .filter(course -> course.getTotalHours() <= totalCourseHours)
            .map(Course::getTitle)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    /**
     * . h
     */

    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        courses = courses.stream().sorted(Comparator.comparing(Course::getLaunchDate).reversed())
            .toList();
        Map<String, Course> coursesMap = courses.stream()
            .collect(Collectors.groupingBy(Course::getNumber,
                Collectors.collectingAndThen(Collectors.toList(), courseList -> {
                    double averageMedianAge = courseList.stream()
                        .mapToDouble(Course::getMedianAge)
                        .average()
                        .orElse(0.0);
                    double averagePercentMale = courseList.stream()
                        .mapToDouble(Course::getPercentMale)
                        .average()
                        .orElse(0.0);
                    double averagePercentBachelorOrHigher = courseList.stream()
                        .mapToDouble(Course::getPercentDegree)
                        .average()
                        .orElse(0.0);
                    return new Course(courseList.get(0), averageMedianAge, averagePercentMale,
                        averagePercentBachelorOrHigher);
                })));
        Map<String, Double> similarityValues = coursesMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                Course course = entry.getValue();
                double ageDifference = age - course.getAverageMedianAge();
                double genderDifference = gender * 100 - course.getAveragePercentMale();
                double degreeDifference =
                    isBachelorOrHigher * 100 - course.getAveragePercentBachelorOrHigher();
                return ageDifference * ageDifference + genderDifference * genderDifference
                    + degreeDifference * degreeDifference;
            }));
        List<String> recommendedCourses = similarityValues.entrySet().stream()
            .sorted(Comparator.comparing(entry -> coursesMap.get(entry.getKey()).getTitle()))
            .sorted(Map.Entry.comparingByValue())
            .map(entry -> coursesMap.get(entry.getKey()).getTitle())
            .distinct()
            .limit(10)
            .collect(Collectors.toList());
        return recommendedCourses;
    }
}