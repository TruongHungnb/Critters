package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.EmployeeSkill;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class EmployeeManagedRepository {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Find employee IDs of the employees that have ALL the skills requested.
     * This is done through the use of a native query grouping skills and counting instances.
     *
     * The instances of the count should match the number of skills sent to the method.
     *
     * NOTE - Hibernate does not have a way to execute a query to find
     * all employees that have ALL skills.  The only way I could solve this
     * problem was with a native query.
     *
     * @param skillsSet
     * @return
     */
    public List<Long> findEmployeeIdsWithAllSkillsOnDay(Set<EmployeeSkill> skillsSet, DayOfWeek dayOfWeek) {
        // query to return a list of ids that include all the given skills.
        String selectStmt = "select e.id " +
                "FROM Employee AS e, Employee_Skill AS es, Day_of_week AS d " +
                "WHERE e.id = es.id " +
                "AND e.id = d.id " +
                "AND es.skill in (" +
                    skillsSet
                        .stream()
                        .map((skill) -> { return String.valueOf(skill.ordinal()); })
                        .collect(Collectors.joining(",")) +
                ") AND d.day = " + dayOfWeek.ordinal() + " " +
                "GROUP BY e.id HAVING count(es.skill) = " + skillsSet.size();

        // T?o the query and set parameters
        Query selectQuery = entityManager.createNativeQuery(selectStmt);

        // truy v?n
        List<BigInteger> result = selectQuery.getResultList();

        // chuy?n ð?i ki?u d? li?u
        return result.stream()
                .map(BigInteger::longValue)
                .collect(Collectors.toList());

    }
}
