import java.util.Date;

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