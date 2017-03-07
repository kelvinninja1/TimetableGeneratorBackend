package com.swiftpot.timetable.services;

import com.sun.istack.internal.Nullable;
import com.swiftpot.timetable.model.PeriodOrLecture;
import com.swiftpot.timetable.model.ProgrammeDay;
import com.swiftpot.timetable.services.servicemodels.AllocatedPeriodSet;
import com.swiftpot.timetable.services.servicemodels.PeriodSet;
import com.swiftpot.timetable.services.servicemodels.UnallocatedPeriodSet;
import com.swiftpot.timetable.util.BusinessLogicConfigurationProperties;
import com.swiftpot.timetable.util.PrettyJSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * This will be used to retrieve helper methods for retrieving info about a {@link com.swiftpot.timetable.model.ProgrammeDay} or list of {@link com.swiftpot.timetable.model.ProgrammeDay}
 *
 * @author Ace Programmer Rbk
 *         <Rodney Kwabena Boachie at [rodney@swiftpot.com,rbk.unlimited@gmail.com]> on
 *         04-Mar-17 @ 9:28 PM
 */
@Service
public class ProgrammeDayServices {

    @Autowired
    BusinessLogicConfigurationProperties businessLogicConfigurationProperties;

    private static final Logger logger = LogManager.getLogger();

    /**
     * get the indexlocation of a {@link ProgrammeDay} in a {@link List} of {@link ProgrammeDay} list based on the @param programmeDayNameToFind passed in
     *
     * @param programmeDaysList      the list of ProgrammeDays used to search for the @param programmeDayNameToFind
     * @param programmeDayNameToFind the actual programmeDayName to find,eg search for "MONDAY" in list of days passed in
     * @return int => the index location where the programmeDayNameToFind was found
     */
    public int getProgrammeDayIndexLocation(List<ProgrammeDay> programmeDaysList, String programmeDayNameToFind) {
        int indexOfProgrammeDay = 0;
        for (int i = 0; i < programmeDaysList.size(); i++) {
            ProgrammeDay programmeDay = programmeDaysList.get(i);
            if (Objects.equals(programmeDay.getDayName(), programmeDayNameToFind)) {
                indexOfProgrammeDay = i;
                break;
            }
        }
        return indexOfProgrammeDay;
    }

    /**
     * get the {@link ProgrammeDay} to set the incoming Periods and TutorId to on the timetable object.
     *
     * @param programmeDaysList      the {@link List} of {@link ProgrammeDay} to search the {@param programmeDayNameToFind} on.
     * @param programmeDayNameToFind the actual programmeDayName to find,eg search for "MONDAY" in list of days passed in
     * @return {@link ProgrammeDay}
     */
    ProgrammeDay getProgrammeDayToSetTheIncomingPeriodsAndTutoridTo(List<ProgrammeDay> programmeDaysList, String programmeDayNameToFind) {
        ProgrammeDay programmeDayToSetThePeriodsAndTutorIdTo = null;
        for (int i = 0; i < programmeDaysList.size(); i++) {
            ProgrammeDay programmeDay = programmeDaysList.get(i);
            if (Objects.equals(programmeDay.getDayName(), programmeDayNameToFind)) {
                programmeDayToSetThePeriodsAndTutorIdTo = programmeDay;
                break;
            }
        }
        return programmeDayToSetThePeriodsAndTutorIdTo;
    }

    /**
     * set periods and tutor of subject for the affected periods on a particular programmeDay.
     *
     * @param programmeDay                      the {@link ProgrammeDay} to set the periods on.
     * @param tutorUniqueIdInDb                 the tutor's unique {@link com.swiftpot.timetable.repository.db.model.TutorDoc#id}
     * @param subjectUniqueIdInDb               the subject's unique {@link com.swiftpot.timetable.repository.db.model.SubjectDoc#id}
     * @param periodNumberToStartSettingSubject the period number to start setting the subject and tutor ids from .
     * @param periodNumberToStopSettingSubject  the periodd number to stop setting the subject and ids.
     * @return {@link ProgrammeDay} with the periods set to the subject and tutor.
     */
    ProgrammeDay setPeriodsOnProgrammeDayTimetable(ProgrammeDay programmeDay,
                                                   String tutorUniqueIdInDb,
                                                   String subjectUniqueIdInDb,
                                                   int periodNumberToStartSettingSubject,
                                                   int periodNumberToStopSettingSubject) {
        //setPeriodsOnProgrammeDayTimetable
        List<PeriodOrLecture> periodOrLecturesInDay = programmeDay.getPeriodList();
        for (PeriodOrLecture periodOrLecture : periodOrLecturesInDay) {
            int currentPeriodNumber = periodOrLecture.getPeriodNumber();
            if ((currentPeriodNumber >= periodNumberToStartSettingSubject) && (currentPeriodNumber <= periodNumberToStopSettingSubject)) {
                periodOrLecture.setTutorUniqueId(tutorUniqueIdInDb);
                periodOrLecture.setSubjectUniqueIdInDb(subjectUniqueIdInDb);
                periodOrLecture.setIsAllocated(true);
            }
        }
        return programmeDay;
    }

    /**
     * get the total number of {@link PeriodOrLecture} with {@link PeriodOrLecture#isAllocated} == false in the passed in {@link ProgrammeDay}
     *
     * @param programmeDay the {@link ProgrammeDay} to check for the number of unallocated periods
     * @return the number of {@link PeriodOrLecture} with {@link PeriodOrLecture#isAllocated} == false,returns 0 if none is found
     */
    public int getNumberOfUnallocatedPeriodsInDay(ProgrammeDay programmeDay) {
        int numberOfUnallocatedPeriodsInTheDay = 0;
        List<PeriodOrLecture> periodOrLecturesInDay = programmeDay.getPeriodList();
        for (PeriodOrLecture periodOrLecture : periodOrLecturesInDay) {
            if (!periodOrLecture.getIsAllocated()) {
                numberOfUnallocatedPeriodsInTheDay += 1;
            }
        }
        return numberOfUnallocatedPeriodsInTheDay;
    }

    /**
     * get the first index of the location where getIsAllocated is equal to false
     *
     * @param periodOrLecturesList {@link List<PeriodOrLecture>}
     * @return int
     */
    public int getFirstIndexPositionOfPeriodWhereAllocationIsFalseInListOfPeriods(List<PeriodOrLecture> periodOrLecturesList) {
        int totalPeriodsToIterateThrough = periodOrLecturesList.size();
        List<Integer> listOfIndexesWhereFalseWasSeen = new ArrayList<>();
        for (int i = 0; i < totalPeriodsToIterateThrough; i++) {
            if (periodOrLecturesList.get(i).getIsAllocated() == false) {
                listOfIndexesWhereFalseWasSeen.add(i);
            }
        }
        //first will be first element in list
        int indexWhereFalseWasFirstSeen = listOfIndexesWhereFalseWasSeen.get(0);
        return indexWhereFalseWasFirstSeen;
    }

    /**
     * check if {@link ProgrammeDay}'s {@link List} of {@link PeriodOrLecture} are all allocated
     *
     * @param programmeDay
     * @return true if {@link ProgrammeDay}'s {@link List} of {@link PeriodOrLecture} are all {@link PeriodOrLecture#isAllocated} ==true,false if otherwise
     */
    public boolean isProgrammeDayFullyAllocated(ProgrammeDay programmeDay) {
        if (this.getNumberOfUnallocatedPeriodsInDay(programmeDay) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<UnallocatedPeriodSet> getListOfUnallocatedPeriodSetsInDay(ProgrammeDay programmeDay) {
        List<UnallocatedPeriodSet> finalUnallocatedPeriodSetsList = new ArrayList<>();
        if (this.isProgrammeDayFullyAllocated(programmeDay)) {
            //nothing to check for because all days are allocated,thus return 0
        } else {
            finalUnallocatedPeriodSetsList =
                    this.getListOfUnallocatedPeriodSetsInDayAfterMakingSureProgrammeDayIsNotFullyAllocated(programmeDay);
        }
        return finalUnallocatedPeriodSetsList;
    }

    public List<UnallocatedPeriodSet> getListOfUnallocatedPeriodSetsInDayAfterMakingSureProgrammeDayIsNotFullyAllocated(ProgrammeDay programmeDay) {
        Set<UnallocatedPeriodSet> finalUnallocatedPeriodSetsList = new HashSet<>();
        if (this.doesProgrammeDayHaveAtLeastOnePeriodAssignedAlready(programmeDay)) {
            List<AllocatedPeriodSet> allocatedPeriodSets =
                    this.getListOfAllocatedPeriodSetsInDayAfterMakingSureSomePeriodsAreAllocatedInDay(programmeDay);
            for (AllocatedPeriodSet allocatedPeriodSet : allocatedPeriodSets) {
                int periodEndingNumberOfAllocatedPeriodSet = allocatedPeriodSet.getPeriodEndingNumber();
                List<PeriodOrLecture> periodOrLecturesListInProgDay = programmeDay.getPeriodList();

                List<UnallocatedPeriodSet> preUnallocatedPeriodSetList =
                        this.getPreUnallocatedPeriodSetListInAllocatedPeriodSet(allocatedPeriodSet, programmeDay);
                if (!preUnallocatedPeriodSetList.isEmpty()) {
                    finalUnallocatedPeriodSetsList.addAll(preUnallocatedPeriodSetList);
                }
                List<UnallocatedPeriodSet> postUnallocatedPeriodSetList =
                        this.getPostUnallocatedPeriodSetListInAllocatedPeriodSet(allocatedPeriodSet, programmeDay);
                //todo URGENT!!! CONTINUE HERE TOMORROW FOR PRE-UNALLOCATED PERIODS sET
                if (!postUnallocatedPeriodSetList.isEmpty()) {
                    finalUnallocatedPeriodSetsList.addAll(postUnallocatedPeriodSetList);
                }

            }
        }

        return new ArrayList<>(finalUnallocatedPeriodSetsList);
    }

    /**
     * this will get all the {@link List} of {@link UnallocatedPeriodSet} that come before the <br>
     * {@link UnallocatedPeriodSet#periodStartingNumber},this will return 0 or empty list if none is found
     *
     * @param allocatedPeriodSet
     * @param programmeDay
     * @return {@link List} of {@link UnallocatedPeriodSet} hat come before the <br>
     * {@link UnallocatedPeriodSet#periodStartingNumber}
     */
    public List<UnallocatedPeriodSet> getPreUnallocatedPeriodSetListInAllocatedPeriodSet(AllocatedPeriodSet allocatedPeriodSet, ProgrammeDay programmeDay) {
        Set<UnallocatedPeriodSet> finalUnallocatedPeriodSetsListPostAllocationPeriodSet = new HashSet<>(0);

        List<PeriodOrLecture> periodOrLecturesListInProgDay = programmeDay.getPeriodList();
        int periodStartingNumberOfAllocatedPeriodSet = allocatedPeriodSet.getPeriodStartingNumber();
        int totalPeriodsInDay = programmeDay.getPeriodList().size();
        int totalPeriodsBeforePeriodStartingNumber = totalPeriodsInDay - (totalPeriodsInDay - periodStartingNumberOfAllocatedPeriodSet);
        if (periodStartingNumberOfAllocatedPeriodSet == 1) {
            //meaning all the unallocated will be after 1 so return empty
            return new ArrayList<>(0);
        } else {
            int findTruePositionBeforePeriodStartNumberIfAny = 0;
            for (PeriodOrLecture periodOrLecture : periodOrLecturesListInProgDay) {
                int currentPeriodNumber = periodOrLecture.getPeriodNumber();
                if (currentPeriodNumber < totalPeriodsBeforePeriodStartingNumber && periodOrLecture.getIsAllocated()) {
                    findTruePositionBeforePeriodStartNumberIfAny += 1;
                    break;
                }
            }
            if (findTruePositionBeforePeriodStartNumberIfAny == 0) {
                int periodStartingNumber = 1;
                int periodEndingNumber = totalPeriodsBeforePeriodStartingNumber - 1;
                int totalPeriodsAllocationNumber = (periodEndingNumber - periodStartingNumber) + 1;

                UnallocatedPeriodSet unallocatedPeriodSet = new UnallocatedPeriodSet();
                unallocatedPeriodSet.setPeriodStartingNumber(periodStartingNumber);
                unallocatedPeriodSet.setPeriodEndingNumber(periodEndingNumber);
                unallocatedPeriodSet.setTotalNumberOfPeriodsForSet(totalPeriodsAllocationNumber);

                return new ArrayList<>(Arrays.asList(unallocatedPeriodSet));
            } else {
                return new ArrayList<>(0);//meaning that post will take care of that.
            }
        }

    }

    public List<UnallocatedPeriodSet> getNumberOfSubjectUniqueIdSetListInProgrammeDayListBeforeAParticularPeriodNumber
            (int periodOrLectureNumberThatItShouldBeLessThan, List<PeriodOrLecture> periodOrLectureList) {

        //int
        Set<UnallocatedPeriodSet> uniqueUnallocatedPeriodSets = new HashSet<>();

        int periodOrLectureListSize = Integer.valueOf(businessLogicConfigurationProperties.TIMETABLE_PERIOD_TOTAL);
        int numberOfTimesToIterate = periodOrLectureNumberThatItShouldBeLessThan; //TODO set to peRIODoRlECTURETHATITSHOULDBELESSTHAN
        while (numberOfTimesToIterate > 0) {
            numberOfTimesToIterate -= 1;//decrement by 1

            @Nullable
            UnallocatedPeriodSet unallocatedPeriodSetNullableAllowed = this.
                    getUnallocatedPeriodSet
                            (periodOrLectureNumberThatItShouldBeLessThan, periodOrLectureList);
            if (!Objects.isNull(unallocatedPeriodSetNullableAllowed)) {
                uniqueUnallocatedPeriodSets.add(unallocatedPeriodSetNullableAllowed);
            } else {

            }
        }

        return new ArrayList<>(uniqueUnallocatedPeriodSets);
    }


    @Nullable
    public UnallocatedPeriodSet getUnallocatedPeriodSet(int periodOrLectureNumberThatItShouldBeLessThan, List<PeriodOrLecture> periodOrLectureList) {
        Set<UnallocatedPeriodSet> uniqueUnallocatedPeriodSets = new HashSet<>();
        int periodNumberWhereFirstPositionOfFalseWasFoundAndIsLessThanPeriodNumberToCheckAgainst = 0;
        for (PeriodOrLecture periodOrLecture : periodOrLectureList) {
            int currentPeriodNumber = periodOrLecture.getPeriodNumber();
            boolean isBooleanAllocated = periodOrLecture.getIsAllocated();
            if ((currentPeriodNumber < periodOrLectureNumberThatItShouldBeLessThan) &&
                    (!isBooleanAllocated)) {
                periodNumberWhereFirstPositionOfFalseWasFoundAndIsLessThanPeriodNumberToCheckAgainst = currentPeriodNumber;
                break;
            }
        }

        if (periodNumberWhereFirstPositionOfFalseWasFoundAndIsLessThanPeriodNumberToCheckAgainst == 0) {
            //add nothing to set of unallocatedPeriodSet List
            return null;
        } else {
            int periodNumberWhereThereIsTrueAfterFalseFound = 0;
            for (PeriodOrLecture periodOrLecture : periodOrLectureList) {
                int currentPeriodNumber = periodOrLecture.getPeriodNumber();
                if (periodOrLecture.getIsAllocated() && currentPeriodNumber > periodNumberWhereFirstPositionOfFalseWasFoundAndIsLessThanPeriodNumberToCheckAgainst) {
                    periodNumberWhereThereIsTrueAfterFalseFound = currentPeriodNumber;
                    break;
                }
            }
            int periodStartingNumber = periodNumberWhereFirstPositionOfFalseWasFoundAndIsLessThanPeriodNumberToCheckAgainst;
            int periodEndingNumber = (periodNumberWhereThereIsTrueAfterFalseFound - 1);
            //we subtract 1 because the variable uses a reference that is true,
            // hence minus 1 will actually point to a false,which is the acutal ending period number of the unallocated subject.
            int totalNumberOfPeriodsForSet = (periodEndingNumber + 1) - (periodStartingNumber);
            UnallocatedPeriodSet unallocatedPeriodSet = new UnallocatedPeriodSet();
            unallocatedPeriodSet.setPeriodStartingNumber(periodStartingNumber); //set period starting number
            unallocatedPeriodSet.setPeriodEndingNumber(periodEndingNumber); //set period ending number
            unallocatedPeriodSet.setTotalNumberOfPeriodsForSet(totalNumberOfPeriodsForSet);//set totalNumberOfPeriodsForSet


            return unallocatedPeriodSet;
        }
    }

    public int getNumberOfFalseGreaterThanAPeriodNumberButLessThanAnotherPeriodNumber(int periodNumberToBeGreaterThan,
                                                                                      int periodNumberToBeLessThan,
                                                                                      List<PeriodOrLecture> periodOrLectureList) {
        int getNumberOfFalseGreaterThanAPeriodNumberButLessThanAnotherPeriodNumber = 0;
        for (PeriodOrLecture periodOrLecture : periodOrLectureList) {
            int currentPeriodLectureNumber = periodOrLecture.getPeriodNumber();
            if ((currentPeriodLectureNumber > periodNumberToBeGreaterThan) &&
                    (currentPeriodLectureNumber < periodNumberToBeLessThan) &&
                    (!periodOrLecture.getIsAllocated())) {
                getNumberOfFalseGreaterThanAPeriodNumberButLessThanAnotherPeriodNumber += 1;
            }
        }
        return getNumberOfFalseGreaterThanAPeriodNumberButLessThanAnotherPeriodNumber;
    }

    /**
     * this will get all the {@link List} of {@link UnallocatedPeriodSet} that come after the <br>
     * {@link UnallocatedPeriodSet#periodEndingNumber},this will return 0 or empty list if none is found
     *
     * @param allocatedPeriodSet
     * @param programmeDay
     * @return {@link List} of {@link UnallocatedPeriodSet}
     */
    public List<UnallocatedPeriodSet> getPostUnallocatedPeriodSetListInAllocatedPeriodSet(AllocatedPeriodSet allocatedPeriodSet, ProgrammeDay programmeDay) {
        Set<UnallocatedPeriodSet> finalUnallocatedPeriodSetsListPostAllocationPeriodSet = new HashSet<>(0);

        List<PeriodOrLecture> periodOrLecturesListInProgDay = programmeDay.getPeriodList();
        int periodEndingNumberOfAllocatedPeriodSet = allocatedPeriodSet.getPeriodEndingNumber();
        int totalNumberOfFalseAfterAllocatedPeriodSetsPeriodEndingNumber = 0;
        for (PeriodOrLecture periodOrLecture : periodOrLecturesListInProgDay) {
            if ((!periodOrLecture.getIsAllocated()) && (periodOrLecture.getPeriodNumber() > periodEndingNumberOfAllocatedPeriodSet)) {
                totalNumberOfFalseAfterAllocatedPeriodSetsPeriodEndingNumber++;//add 1
            }
        }
        logger.info("totalNumberOfFalseAfterAllocatedPeriodSetsPeriodEndingNumber => {}", totalNumberOfFalseAfterAllocatedPeriodSetsPeriodEndingNumber);
        if (totalNumberOfFalseAfterAllocatedPeriodSetsPeriodEndingNumber == 0) {
            return new ArrayList<>(0);//return empty List
        } else {
            while (totalNumberOfFalseAfterAllocatedPeriodSetsPeriodEndingNumber > 0) {
                totalNumberOfFalseAfterAllocatedPeriodSetsPeriodEndingNumber = totalNumberOfFalseAfterAllocatedPeriodSetsPeriodEndingNumber - 1;//decrement by 1

                int totalPeriodAllocationForUnallocatedPeriodSet = 0;
                for (PeriodOrLecture periodOrLecture : periodOrLecturesListInProgDay) {
                    int currentPeriodNumber = periodOrLecture.getPeriodNumber();
                    if ((currentPeriodNumber > periodEndingNumberOfAllocatedPeriodSet) && (!periodOrLecture.getIsAllocated())) {
                        totalPeriodAllocationForUnallocatedPeriodSet += 1;
                    }
                    if ((currentPeriodNumber > periodEndingNumberOfAllocatedPeriodSet) && periodOrLecture.getIsAllocated()) {
                        break;
                    }
                }
                if (totalPeriodAllocationForUnallocatedPeriodSet > 0) {
                    int startingPeriodNumber = (periodEndingNumberOfAllocatedPeriodSet) + 1;
                    int endingPeriodNumber = (startingPeriodNumber) + (totalPeriodAllocationForUnallocatedPeriodSet - 1);
                    // we subtract 1 from totalPeriodAllocationForUnallocatedPeriodSet because if startingPeriodNumber is 3,
                    // and totalPeriodAllocation of subject is 2,it means that the subject will end at period 4,not 5!
                    // So that's why we subtract 1 otherwise the subject will end at 5 instead,which is not what we want.
                    int totalNumberOfPeriodsForSet = totalPeriodAllocationForUnallocatedPeriodSet;

                    UnallocatedPeriodSet unallocatedPeriodSet = new UnallocatedPeriodSet();
                    unallocatedPeriodSet.setTotalNumberOfPeriodsForSet(totalNumberOfPeriodsForSet); //set total number of PeriodsForSet
                    unallocatedPeriodSet.setPeriodStartingNumber(startingPeriodNumber); //set period starting number
                    unallocatedPeriodSet.setPeriodEndingNumber(endingPeriodNumber); //set period ending number

                    finalUnallocatedPeriodSetsListPostAllocationPeriodSet.add(unallocatedPeriodSet);
                } else {
                    //do not add anything to final list
                }
            }
            logger.info("finalUnallocatedPeriodSetsListPostAllocationPeriodSet ==>{}", PrettyJSON.toListPrettyFormat(new ArrayList<>(finalUnallocatedPeriodSetsListPostAllocationPeriodSet)));
            return new ArrayList<>(finalUnallocatedPeriodSetsListPostAllocationPeriodSet);
        }
    }

    /**
     * get the total number of false before a specific Period or lecture number ie {@link PeriodOrLecture#periodNumber} in day ,not the index,<b>NOTE THAT!!,not the index position in the list!!</b>
     *
     * @param periodOrLectureNumberThatItShouldBeLessThan
     * @param periodOrLecturesList
     * @return
     */
    int getNumberOfFalseBeforeSpecificPeriodOrLectureNumberInDay(int periodOrLectureNumberThatItShouldBeLessThan, List<PeriodOrLecture> periodOrLecturesList) {
        int getNumberOfFalseBeforeSpecificPeriodOrLectureNumberInDay = 0;
        for (PeriodOrLecture periodOrLecture : periodOrLecturesList) {
            int currentPeriodNumber = periodOrLecture.getPeriodNumber();
            if ((currentPeriodNumber < periodOrLectureNumberThatItShouldBeLessThan) && (!periodOrLecture.getIsAllocated())) {
                getNumberOfFalseBeforeSpecificPeriodOrLectureNumberInDay += 1;
            }
        }
        return getNumberOfFalseBeforeSpecificPeriodOrLectureNumberInDay;
    }

    /**
     * check to see whether {@link ProgrammeDay} has at least one {@link PeriodOrLecture} assigned to a subject and tutor,<br>
     * ie at least one {@link PeriodOrLecture} in the {@link List} of {@link PeriodOrLecture} is equal to true => {@link PeriodOrLecture#isAllocated}==true
     *
     * @param programmeDay
     * @return true if at least one of the list of periodOrLecture has a property of {@link PeriodOrLecture#isAllocated} == true,false if otherwise
     */
    public boolean doesProgrammeDayHaveAtLeastOnePeriodAssignedAlready(ProgrammeDay programmeDay) {
        List<PeriodOrLecture> periodOrLectureListInDay = programmeDay.getPeriodList();
        boolean doesProgrammeDayHaveAtLeastOnePeriodAssignedAlready = false;
        for (PeriodOrLecture periodOrLecture : periodOrLectureListInDay) {
            if (periodOrLecture.getIsAllocated()) {
                doesProgrammeDayHaveAtLeastOnePeriodAssignedAlready = true;
                break;
            }
        }
        return doesProgrammeDayHaveAtLeastOnePeriodAssignedAlready;
    }

    private List<AllocatedPeriodSet> getListOfAllocatedPeriodSetsInDayAfterMakingSureSomePeriodsAreAllocatedInDay(ProgrammeDay programmeDay) {
        List<AllocatedPeriodSet> finalAllocatedPeriodSetsList = new ArrayList<>();

        Set<String> subjectUniqueIdsAssignedInDay = new HashSet<>();
        List<PeriodOrLecture> periodOrLectureListInDay = programmeDay.getPeriodList();
        for (PeriodOrLecture periodOrLecture : periodOrLectureListInDay) {
            //adding to set will ensure that no duplicate of the subjectUniqueIdInDb is added
            if (Objects.nonNull(periodOrLecture.getSubjectUniqueIdInDb()) && (!periodOrLecture.getSubjectUniqueIdInDb().isEmpty())) {
                subjectUniqueIdsAssignedInDay.add(periodOrLecture.getSubjectUniqueIdInDb());
            }
        }
        if (subjectUniqueIdsAssignedInDay.isEmpty()) {
            return new ArrayList<>(0);//return empty as no subject is allocated in day,not likely because we may have checked some periods are allocated already
        } else {
            for (String subjectUniqueId : subjectUniqueIdsAssignedInDay) {
                AllocatedPeriodSet allocatedPeriodSet = this.getAllocatedPeriodSetEntityForSubjectInDay(subjectUniqueId, periodOrLectureListInDay);
                finalAllocatedPeriodSetsList.add(allocatedPeriodSet);
            }
            return finalAllocatedPeriodSetsList;
        }
    }

    /**
     * get {@link AllocatedPeriodSet} for a passed in subject id in the {@link List} of {@link PeriodOrLecture}
     *
     * @param subjectUniqueIdInDb
     * @param periodOrLectureList
     * @return AllocatedPeriodSet
     */
    private AllocatedPeriodSet getAllocatedPeriodSetEntityForSubjectInDay(String subjectUniqueIdInDb, List<PeriodOrLecture> periodOrLectureList) {

        int totalNumberOfPeriodsForSubjectId = 0;
        //we find the total number of periods that the subject has been assigned in the day by looping and comparing the subjectUniqueId
        for (PeriodOrLecture periodOrLecture : periodOrLectureList) {
            if (Objects.equals(periodOrLecture.getSubjectUniqueIdInDb(), subjectUniqueIdInDb)) {
                totalNumberOfPeriodsForSubjectId += 1;
            }
        }
        int periodStartingNumber = this.getFirstPeriodNumberOfSubjectInDay(subjectUniqueIdInDb, periodOrLectureList);
        int periodEndingNumber = periodStartingNumber + (totalNumberOfPeriodsForSubjectId - 1); //if the starting period = 1,and the totalNumberOfPeriods =1,without subracting 1,the periodEndingNumber will be 2,which will mean 2 periods,and that's not what we want.

        AllocatedPeriodSet allocatedPeriodSet = new AllocatedPeriodSet();
        allocatedPeriodSet.setPeriodStartingNumber(periodStartingNumber); //set periodStartingNumber
        allocatedPeriodSet.setPeriodEndingNumber(periodEndingNumber); //set periodEndingNumber
        allocatedPeriodSet.setTotalNumberOfPeriodsForSet(totalNumberOfPeriodsForSubjectId); //set totalNumberOfPeriodsForSubjectId
        return allocatedPeriodSet;
    }

    /**
     * get the first position where {@link PeriodOrLecture#subjectUniqueIdInDb} == the passed in subjectUniqueIdInDb in the {@link List} of {@link PeriodOrLecture}
     * this will normally be used to get the {@link PeriodSet#periodStartingNumber} but may be applied in other scenarios in addition.
     * <br>
     * Algo :: {@code we iterate through periodOrLectureList,the first time we find a match,we break out of the loop.}
     *
     * @param subjectUniqueIdInDb the {@link com.swiftpot.timetable.repository.db.model.SubjectDoc#id}
     * @param periodOrLectureList the {@link List} of {@link PeriodOrLecture}
     * @return int
     */
    private int getFirstPeriodNumberOfSubjectInDay(String subjectUniqueIdInDb, List<PeriodOrLecture> periodOrLectureList) {
        int periodStartingNumber = 0;
        for (PeriodOrLecture periodOrLecture : periodOrLectureList) {
            if (Objects.equals(periodOrLecture.getSubjectUniqueIdInDb(), subjectUniqueIdInDb)) {
                periodStartingNumber = periodOrLecture.getPeriodNumber();
                break;
            }
        }
        return periodStartingNumber;
    }

}
