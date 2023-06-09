import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower
 * version). This is just a demo, and you can extend and implement functions based on this demo, or
 * implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

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

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        return courses.stream()
            .collect(Collectors.groupingBy(Course::getInstitution,
                Collectors.summingInt(Course::getParticipants))).entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    //2
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

    //3
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
//        return result.entrySet()
//            .stream()
//            .sorted(Map.Entry.comparingByKey())
//            .collect(Collectors.toMap(
//                Map.Entry::getKey,
//                Map.Entry::getValue,
//                (oldValue, newValue) -> oldValue,
//                LinkedHashMap::new));
        return result;
    }

    //4
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

    //5
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

    //6
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
                return ageDifference * ageDifference + genderDifference * genderDifference +
                    degreeDifference * degreeDifference;
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


class Course {

    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;
    double averageMedianAge;
    double averagePercentMale;
    double averagePercentBachelorOrHigher;

    public String getInstitution() {
        return institution;
    }

    public int getParticipants() {
        return participants;
    }

    public String getSubject() {
        return subject;
    }

    public String getTitle() {
        return title;
    }

    public String getInstructors() {
        return instructors;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getPercentAudited() {
        return percentAudited;
    }

    public String getNumber() {
        return number;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public double getMedianAge() {
        return medianAge;
    }

    public double getPercentMale() {
        return percentMale;
    }

    public double getPercentFemale() {
        return percentFemale;
    }

    public double getPercentDegree() {
        return percentDegree;
    }

    public double getAverageMedianAge() {
        return averageMedianAge;
    }

    public double getAveragePercentMale() {
        return averagePercentMale;
    }

    public double getAveragePercentBachelorOrHigher() {
        return averagePercentBachelorOrHigher;
    }

    public Course(String institution, String number, Date launchDate,
        String title, String instructors, String subject,
        int year, int honorCode, int participants,
        int audited, int certified, double percentAudited,
        double percentCertified, double percentCertified50,
        double percentVideo, double percentForum, double gradeHigherZero,
        double totalHours, double medianHoursCertification,
        double medianAge, double percentMale, double percentFemale,
        double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) {
            title = title.substring(1);
        }
        if (title.endsWith("\"")) {
            title = title.substring(0, title.length() - 1);
        }
        this.title = title;
        if (instructors.startsWith("\"")) {
            instructors = instructors.substring(1);
        }
        if (instructors.endsWith("\"")) {
            instructors = instructors.substring(0, instructors.length() - 1);
        }
        this.instructors = instructors;
        if (subject.startsWith("\"")) {
            subject = subject.substring(1);
        }
        if (subject.endsWith("\"")) {
            subject = subject.substring(0, subject.length() - 1);
        }
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    public Course(Course course, double averageMedianAge, double averagePercentMale,
        double averagePercentBachelorOrHigher) {
        this.institution = course.institution;
        this.number = course.number;
        this.launchDate = course.launchDate;
        this.title = course.title;
        this.instructors = course.instructors;
        this.subject = course.subject;
        this.year = course.year;
        this.honorCode = course.honorCode;
        this.participants = course.participants;
        this.audited = course.audited;
        this.certified = course.certified;
        this.percentAudited = course.percentAudited;
        this.percentCertified = course.percentCertified;
        this.percentCertified50 = course.percentCertified50;
        this.percentVideo = course.percentVideo;
        this.percentForum = course.percentForum;
        this.gradeHigherZero = course.gradeHigherZero;
        this.totalHours = course.totalHours;
        this.medianHoursCertification = course.medianHoursCertification;
        this.medianAge = course.medianAge;
        this.percentMale = course.percentMale;
        this.percentFemale = course.percentFemale;
        this.percentDegree = course.percentDegree;
        this.averageMedianAge = averageMedianAge;
        this.averagePercentMale = averagePercentMale;
        this.averagePercentBachelorOrHigher = averagePercentBachelorOrHigher;
    }

    public boolean isIndependentlyResponsible() {
        return !instructors.contains(",");
    }
}