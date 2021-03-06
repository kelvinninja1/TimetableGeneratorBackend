/*
 * Copyright (c) SwiftPot Solutions Limited
 */

package com.swiftpot.timetable;

import com.google.gson.Gson;
import com.swiftpot.timetable.base.impl.TimeTableDefaultPeriodsAllocatorDefaultImpl;
import com.swiftpot.timetable.model.PeriodOrLecture;
import com.swiftpot.timetable.model.ProgrammeDay;
import com.swiftpot.timetable.model.ProgrammeGroup;
import com.swiftpot.timetable.model.YearGroup;
import com.swiftpot.timetable.repository.DepartmentDocRepository;
import com.swiftpot.timetable.repository.ProgrammeGroupDocRepository;
import com.swiftpot.timetable.repository.ProgrammeGroupPersonalTimeTableDocRepository;
import com.swiftpot.timetable.repository.db.model.DepartmentDoc;
import com.swiftpot.timetable.repository.db.model.ProgrammeGroupDoc;
import com.swiftpot.timetable.repository.db.model.TimeTableSuperDoc;
import com.swiftpot.timetable.services.TimeTablePopulatorService;
import com.swiftpot.timetable.services.TimeTableSuperDocServices;
import com.swiftpot.timetable.util.PrettyJSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Ace Programmer Rbk
 *         <Rodney Kwabena Boachie at [rodney@swiftpot.com,rbk.unlimited@gmail.com]> on
 *         29-Dec-16 @ 7:05 PM
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TimeTableGenerationWithMockitoTests {

    @Autowired
    TimeTablePopulatorService timeTablePopulatorService;
    @Autowired
    TimeTableDefaultPeriodsAllocatorDefaultImpl timeTableDefaultPeriodsAllocatorDefault;
    @Autowired
    TimeTableSuperDocServices timeTableSuperDocServices;

    @MockBean
    ProgrammeGroupDocRepository programmeGroupDocRepository;
    @MockBean
    private DepartmentDocRepository departmentDocRepository;
    @MockBean
    private ProgrammeGroupPersonalTimeTableDocRepository programmeGroupPersonalTimeTableDocRepository;

    TimeTableSuperDoc timeTableSuperDoc;
    private static final Logger logger = LogManager.getLogger();
    String programmeFullName1;
    String programmeCode1;

    @Before
    public void setupMock() throws Exception {
        MockitoAnnotations.initMocks(this);


        programmeFullName1 = "Building Construction Technology";
        programmeCode1 = "BCT-1A";
        String programmeInitials1 = "BCT";
        String programmeInitials2 = "BUS-ACC";
        String programmeInitials3 = "CTECH-IT-HARDWARE";
        ProgrammeGroupDoc programmeGroupDoc1 = new ProgrammeGroupDoc();
        programmeGroupDoc1.setProgrammeFullName(programmeFullName1);
        programmeGroupDoc1.setProgrammeInitials(programmeInitials1);
        programmeGroupDoc1.setYearGroup(1);
        programmeGroupDoc1.setProgrammeCode(programmeCode1);
        programmeGroupDoc1.setDefaultClassRoomId("class1kj");
        programmeGroupDoc1.setYearGroupList(new ArrayList<>(Arrays.asList(1, 2, 3)));
        programmeGroupDoc1.setProgrammeSubjectsCodeList(new ArrayList<>(Arrays.asList("ENG", "MATH", "INTSCIENCE", "SOCSTUDIES", "FDT", "RET", "RRT", "ECTR", "ICT", "PE")));
        programmeGroupDoc1.setTechnicalWorkshopOrLabRequired(true);

        ProgrammeGroupDoc programmeGroupDoc2 = new ProgrammeGroupDoc();
        programmeGroupDoc2.setProgrammeFullName("Business Studies in Accounting");
        programmeGroupDoc2.setProgrammeInitials(programmeInitials2);
        programmeGroupDoc2.setYearGroup(2);
        programmeGroupDoc2.setProgrammeCode("BUS-ACC2A");
        programmeGroupDoc2.setDefaultClassRoomId("class1kj");
        programmeGroupDoc2.setYearGroupList(new ArrayList<>(Arrays.asList(1, 2, 3)));
        programmeGroupDoc2.setProgrammeSubjectsCodeList(new ArrayList<>(Arrays.asList("ENG", "MATH", "INTSCIENCE", "SOCSTUDIES", "ECONS", "CETTR", "RRYT", "ECPOTR", "ICT", "PE")));
        programmeGroupDoc2.setTechnicalWorkshopOrLabRequired(false);

        ProgrammeGroupDoc programmeGroupDoc3 = new ProgrammeGroupDoc();
        programmeGroupDoc3.setProgrammeFullName("Computer Technology - Hardware");
        programmeGroupDoc3.setProgrammeInitials(programmeInitials2);
        programmeGroupDoc3.setYearGroup(3);
        programmeGroupDoc3.setProgrammeCode("CTECH-IT-HARDWARE3A");
        programmeGroupDoc3.setDefaultClassRoomId("class1kj");
        programmeGroupDoc3.setYearGroupList(new ArrayList<>(Arrays.asList(1, 2, 3)));
        programmeGroupDoc3.setProgrammeSubjectsCodeList(new ArrayList<>(Arrays.asList("ENG", "MATH", "INTSCIENCE", "SOCSTUDIES", "PHYSICS", "HARDWARE", "PLMP", "ECPOTR", "ICT", "PE")));
        programmeGroupDoc3.setTechnicalWorkshopOrLabRequired(false);

        List<ProgrammeGroupDoc> programmeGroupDocList = new ArrayList<>();
        programmeGroupDocList.add(programmeGroupDoc1);
        programmeGroupDocList.add(programmeGroupDoc2);
        programmeGroupDocList.add(programmeGroupDoc3);



        Mockito.when(programmeGroupDocRepository.findAll()).thenReturn(programmeGroupDocList);
        Mockito.when(programmeGroupDocRepository.findByYearGroup(1)).thenReturn(Arrays.asList(programmeGroupDoc1));
        Mockito.when(programmeGroupDocRepository.findByYearGroup(2)).thenReturn(Arrays.asList(programmeGroupDoc2));
        Mockito.when(programmeGroupDocRepository.findByYearGroup(3)).thenReturn(Arrays.asList(programmeGroupDoc3));
        DepartmentDoc departmentDoc = new DepartmentDoc();
        departmentDoc.setProgrammeSubjectsDocIdList(Arrays.asList("3452", "EF34R"));
        Mockito.when(departmentDocRepository.findByDeptProgrammeInitials(programmeInitials1)).thenReturn(departmentDoc);
        Mockito.when(departmentDocRepository.findByDeptProgrammeInitials(programmeInitials2)).thenReturn(departmentDoc);
        Mockito.when(departmentDocRepository.findByDeptProgrammeInitials(programmeInitials2)).thenReturn(departmentDoc);

        Mockito.when(programmeGroupPersonalTimeTableDocRepository.findAll()).thenReturn(new ArrayList<>(0));

        timeTableSuperDoc = timeTablePopulatorService.partOneGenerateInitialTimeTableSuperDocWithInitialData();
    }

    @Test
    public void testMockCreation(){
        assertNotNull(programmeGroupDocRepository);
    }
    @Test
    public void partOneSetYearGroups() throws Exception {

        String timetableSuperDocString = new Gson().toJson(timeTableSuperDoc);
        logger.info("\n \n ********************Timetable pretty print json = " + PrettyJSON.toPrettyFormat(timetableSuperDocString));
        assertThat(5, equalTo(timeTableSuperDoc.getYearGroupsList().get(0).getProgrammeGroupList().get(0).getProgrammeDaysList().size()));
    }

//    @Test(expected = Exception.class)
//    public void partTwoSetDefaultPeriodsWhenThereIsWorkshopRequiredForSomeProgrammeGroups() {
//        TimeTableSuperDoc timeTableSuperDocWithPracticalsSet = timeTablePopulatorService.partTwoAllocateDefaultPeriods("DEFAULT", timeTableSuperDoc);
//        String timeTableSuperDocWithPracticalsSetString = new Gson().toJson(timeTableSuperDocWithPracticalsSet);
//        System.out.println("TimeTable with Default Periods,ie Practicals set = "+PrettyJSON.toPrettyFormat(timeTableSuperDocWithPracticalsSetString));
//        assertThat(timeTableSuperDoc, equalTo(timeTableSuperDocWithPracticalsSet));
//    }

//    @Test
//    public void partTwoSetDefaultPeriodsWhenThereIsNoWorkshopRequiredForAllProgrammeGroups() {
//        //set first subject from true to false for this test to pass
//        timeTableSuperDoc.getYearGroupsList().get(0).getProgrammeGroupList().get(0).setIsProgrammeRequiringPracticalsClassroom(false);
//        TimeTableSuperDoc timeTableSuperDocWithPracticalsSet = timeTablePopulatorService.partTwoAllocateDefaultPeriods("DEFAULT", timeTableSuperDoc);
//        String timeTableSuperDocWithPracticalsSetString = new Gson().toJson(timeTableSuperDocWithPracticalsSet);
//        System.out.println("TimeTable with Default Periods,ie Practicals set = "+PrettyJSON.toPrettyFormat(timeTableSuperDocWithPracticalsSetString));
//        assertThat(timeTableSuperDoc, equalTo(timeTableSuperDocWithPracticalsSet));
//    }

    @Test
    public void setWorshipPeriods() throws Exception {
        TimeTableSuperDoc timetableSuperDocWithWorshipPeriodsSet = timeTableDefaultPeriodsAllocatorDefault.allocateWorshipPeriodForAllProgrammeGroups(timeTableSuperDoc, "WORSHIP");
        String timetableSuperDocWithWorshipPeriodsSetString = new Gson().toJson(timetableSuperDocWithWorshipPeriodsSet);
        logger.info("TimeTable with  Worship periods set = {}", PrettyJSON.toPrettyFormat(timetableSuperDocWithWorshipPeriodsSetString));
        int totalWorshipPeriodsExpected =0;
        int totalWorshipPeriods = 0;
        for (YearGroup yearGroup : timetableSuperDocWithWorshipPeriodsSet.getYearGroupsList()) {
            for (ProgrammeGroup programmeGroup : yearGroup.getProgrammeGroupList()) {
                totalWorshipPeriodsExpected++;
                for (ProgrammeDay programmeDay : programmeGroup.getProgrammeDaysList()) {
                    for (PeriodOrLecture periodOrLecture : programmeDay.getPeriodList()) {
                        if (periodOrLecture.getSubjectUniqueIdInDb() != null)
                            if (periodOrLecture.getSubjectUniqueIdInDb() == "WORSHIP") {
                            totalWorshipPeriods++;
                        }
                    }
                }
            }
        }

        assertTrue("Has all periods set? Totalperiods expected="+totalWorshipPeriodsExpected+" .Total periods got="+totalWorshipPeriods,totalWorshipPeriodsExpected == totalWorshipPeriods);
    }

    @Test
    public void getProgrammeGroupObjectFromTimeTableSuperDocObjectAssertTrue() {
        ProgrammeGroup programmeGroup =
                timeTableSuperDocServices.getProgrammeGroupObjectFromTimeTableSuperDocObject(timeTableSuperDoc, programmeCode1);

        logger.info("ProgrammeGroup Obtained ==>>>\n\n{}",
                PrettyJSON.toPrettyFormat(new Gson().toJson(programmeGroup)));
        assertTrue(programmeGroup.getProgrammeCode() == programmeCode1);
    }
}
