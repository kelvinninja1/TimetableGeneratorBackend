package com.swiftpot.timetable.repository.db.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Ace Programmer Rbk
 *         <Rodney Kwabena Boachie at [rodney@swiftpot.com,rbk.unlimited@gmail.com]> on
 *         23-Dec-16 @ 2:16 PM
 */
@Document(collection = "SubjectAllocationDoc")
public class SubjectAllocationDoc {

    @Id
    private String id;

    private String subjectCode;

    private int totalSubjectAllocation;

    private int yearGroup;

    public SubjectAllocationDoc() {
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public int getTotalSubjectAllocation() {
        return totalSubjectAllocation;
    }

    public void setTotalSubjectAllocation(int totalSubjectAllocation) {
        this.totalSubjectAllocation = totalSubjectAllocation;
    }

    public int getYearGroup() {
        return yearGroup;
    }

    public void setYearGroup(int yearGroup) {
        this.yearGroup = yearGroup;
    }
}