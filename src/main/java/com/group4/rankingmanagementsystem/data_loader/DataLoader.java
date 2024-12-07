package com.group4.rankingmanagementsystem.data_loader;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.group4.rankingmanagementsystem.entity.*;
import com.group4.rankingmanagementsystem.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    private final GroupRepository groupRepository;
    private final CriteriaRepository criteriaRepository;
    private final RankingDecisionRepository rankingDecisionRepository;
    private final RankingDecisionCriteriaRepository rankingDecisionCriteriaRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OptionRepository optionRepository;
    private final RankTitleRepository rankTitleRepository;
    private final TaskRepository taskRepository;
    private final RankTitleTaskRepository rankTitleTaskRepository;
    private final RankingHistoryRepository rankingHistoryRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public DataLoader(GroupRepository groupRepository, CriteriaRepository criteriaRepository,
                      RankingDecisionRepository rankingDecisionRepository,
                      RankingDecisionCriteriaRepository rankingDecisionCriteriaRepository,
                      RoleRepository roleRepository, UserRepository userRepository,
                      OptionRepository optionRepository, RankTitleRepository rankTitleRepository,
                      TaskRepository taskRepository, RankTitleTaskRepository rankTitleTaskRepository,
                      EmployeeRepository employeeRepository,
                      RankingHistoryRepository rankingHistoryRepository) {
        this.groupRepository = groupRepository;
        this.criteriaRepository = criteriaRepository;
        this.rankingDecisionRepository = rankingDecisionRepository;
        this.rankingDecisionCriteriaRepository = rankingDecisionCriteriaRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.optionRepository = optionRepository;
        this.rankTitleRepository = rankTitleRepository;
        this.taskRepository = taskRepository;
        this.rankTitleTaskRepository = rankTitleTaskRepository;
        this.rankingHistoryRepository = rankingHistoryRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        //Initialize user and roles
        Role role1 = new Role();
        role1.setName("C&B");
        Role role2 = new Role();
        role2.setName("FA_MANAGER");
        role1 = roleRepository.save(role1);
        role2 = roleRepository.save(role2);

        User user1 = new User();
        User user2 = new User();
        user1.setName("Nguyen Ngoc Sang");
        user2.setName("Duong Thanh Dat");
        user1.setEmail("sangnn2@fpt.com");
        user2.setEmail("datdt74@fpt.com");
        user1.setPassword("$2a$12$v795PKrT2pfDGytP8d8j/Ohh0nKW6Dc3XrX9KMAlagFl/7XbsaXIS");
        user2.setPassword("$2a$12$v795PKrT2pfDGytP8d8j/Ohh0nKW6Dc3XrX9KMAlagFl/7XbsaXIS");
        user1.setUsername("sangnn2");
        user2.setUsername("datdt74");
        user1.setRole(role1);
        user2.setRole(role2);

        userRepository.save(user1);
        userRepository.save(user2);

        //Initialize group, ranking decision, criteria and ranking_decision_criteria
        Group group1 = new Group();
        group1.setName("Trainer");
        group1 = groupRepository.save(group1);

        RankingDecision rankingDecision1 = new RankingDecision();
        rankingDecision1.setName("Ranking Decision 1");
        rankingDecision1.setStatus(false);
        rankingDecision1.setGroup(group1);

        RankingDecision rankingDecision2 = new RankingDecision();
        rankingDecision2.setName("Ranking Decision 2");
        rankingDecision2.setStatus(true);
        rankingDecision2.setGroup(group1);

        RankingDecision rankingDecision3 = new RankingDecision();
        rankingDecision3.setName("Ranking Decision 3");
        rankingDecision3.setStatus(false);
        rankingDecision3.setGroup(group1);

        rankingDecision1=rankingDecisionRepository.save(rankingDecision1);
        rankingDecision2=rankingDecisionRepository.save(rankingDecision2);
        rankingDecision3 = rankingDecisionRepository.save(rankingDecision3);

        group1.setCurrentRankingDecision(rankingDecision2);
        group1= groupRepository.save(group1);



        Criteria criteria1 = new Criteria();
        criteria1.setName("Scope of Training Assignments");
        criteria1 = criteriaRepository.save(criteria1);

        Criteria criteria2 = new Criteria();
        criteria2.setName("Technical or Professional skills");
        criteria2 = criteriaRepository.save(criteria2);

        Criteria criteria3 = new Criteria();
        criteria3.setName("Courseware Development");
        criteria3 = criteriaRepository.save(criteria3);

        //add some options for criteria
        Option option1 = new Option();
        option1.setName("1-LOW");
        option1.setScore(1);
        option1.setExplanation("Option1");
        option1.setCriteria(criteria1);

        Option option11 = new Option();
        option11.setName("2-MEDIUM");
        option11.setScore(2);
        option11.setExplanation("Option1");
        option11.setCriteria(criteria1);

        Option option12 = new Option();
        option12.setName("3-ADVANCED");
        option12.setScore(3);
        option12.setExplanation("Option1");
        option12.setCriteria(criteria1);

        Option option2 = new Option();
        option2.setName("2-MEDIUM");
        option2.setScore(2);
        option2.setExplanation("Option2");
        option2.setCriteria(criteria2);

        Option option3 = new Option();
        option3.setName("3-HIGH");
        option3.setScore(3);
        option3.setExplanation("Option3");
        option3.setCriteria(criteria3);

        option1=optionRepository.save(option1);
        option2=optionRepository.save(option2);
        option3=optionRepository.save(option3);
        option11=optionRepository.save(option11);
        option12=optionRepository.save(option12);

        Set<Option> optionSet = new LinkedHashSet<>();
        optionSet.add(option1);
        optionSet.add(option2);
        optionSet.add(option3);

        //add some rank title
        RankTitle rankTitle1 = new RankTitle();
        rankTitle1.setName("Title1");
        rankTitle1.setRankingDecision(rankingDecision1);
        rankTitle1.setOptions(optionSet);

        rankTitle1=rankTitleRepository.save(rankTitle1);

        Set<Option> optionSet1 = new LinkedHashSet<>();
        option12 = optionRepository.findById(option12.getId()).get();
        option2 = optionRepository.findById(option2.getId()).get();
        option3 = optionRepository.findById(option3.getId()).get();

        optionSet1.add(option12);
        optionSet1.add(option2);
        optionSet1.add(option3);

        RankTitle rankTitle2 = new RankTitle();
        rankTitle2.setName("Title2");
        rankTitle2.setRankingDecision(rankingDecision1);
        rankTitle2.setOptions(optionSet1);

        rankTitle2=rankTitleRepository.save(rankTitle2);


        //////

        //add some task
        Task task1 = new Task();
        task1.setName("Giảng dạy");
        task1.setGroup(group1);
        Task task2 = new Task();
        task2.setName("Tạo tài liệu");
        task2.setGroup(group1);
        Task task3 = new Task();
        task3.setName("Huong dan");
        task3.setGroup(group1);
        task1=taskRepository.save(task1);
        task2=taskRepository.save(task2);
        task3=taskRepository.save(task3);

        RankTitleTask rankTitleTask1 = new RankTitleTask(
                new RankTitleTaskId(rankTitle1, task1),
                new BigDecimal(150000),
                new BigDecimal(200000)
        );

        RankTitleTask rankTitleTask2 = new RankTitleTask(
                new RankTitleTaskId(rankTitle1, task2),
                new BigDecimal(300000),
                new BigDecimal(350000)
        );

        RankTitleTask rankTitleTask3 = new RankTitleTask(
                new RankTitleTaskId(rankTitle2, task1),
                new BigDecimal(150000),
                new BigDecimal(200000)
        );

        RankTitleTask rankTitleTask4 = new RankTitleTask(
                new RankTitleTaskId(rankTitle2, task2),
                new BigDecimal(300000),
                new BigDecimal(350000)
        );

        rankTitleTaskRepository.save(rankTitleTask1);
        rankTitleTaskRepository.save(rankTitleTask2);
        rankTitleTaskRepository.save(rankTitleTask3);
        rankTitleTaskRepository.save(rankTitleTask4);

        //////////////////////////////////
        Criteria criteria4 = new Criteria();
        criteria4.setName("Training Certificate");
        criteria4 = criteriaRepository.save(criteria4);

        Criteria criteria5 = new Criteria();
        criteria5.setName("Years of working and Teaching");
        criteria5 = criteriaRepository.save(criteria5);




        RankingDecisionCriteria rankingDecisionCriteria1 =
                new RankingDecisionCriteria(
                        new RankingDecisionCriteriaId(rankingDecision1,criteria1),
                        30
                );
        rankingDecisionCriteria1 = rankingDecisionCriteriaRepository.save(rankingDecisionCriteria1);

        RankingDecisionCriteria rankingDecisionCriteria2 =
                new RankingDecisionCriteria(
                        new RankingDecisionCriteriaId(rankingDecision1,criteria2),
                        50
                );
        rankingDecisionCriteria2 = rankingDecisionCriteriaRepository.save(rankingDecisionCriteria2);

        RankingDecisionCriteria rankingDecisionCriteria3 =
                new RankingDecisionCriteria(
                        new RankingDecisionCriteriaId(rankingDecision1,criteria3),
                        20
                );
        rankingDecisionCriteria3 = rankingDecisionCriteriaRepository.save(rankingDecisionCriteria3);

        RankingDecisionCriteria rankingDecisionCriteria4 =
                new RankingDecisionCriteria(
                        new RankingDecisionCriteriaId(rankingDecision2,criteria1),
                        30
                );
        rankingDecisionCriteria4 = rankingDecisionCriteriaRepository.save(rankingDecisionCriteria4);

        RankingDecisionCriteria rankingDecisionCriteria5 =
                new RankingDecisionCriteria(
                        new RankingDecisionCriteriaId(rankingDecision2,criteria2),
                        50
                );
        rankingDecisionCriteria5 = rankingDecisionCriteriaRepository.save(rankingDecisionCriteria5);

        RankingDecisionCriteria rankingDecisionCriteria6 =
                new RankingDecisionCriteria(
                        new RankingDecisionCriteriaId(rankingDecision2,criteria3),
                        20
                );
        rankingDecisionCriteria6 = rankingDecisionCriteriaRepository.save(rankingDecisionCriteria6);
        //------------------------End initialize-------------------------
        group1.setCurrentRankingDecision(rankingDecision1);
        groupRepository.save(group1);

        Employee e1 = new Employee();
        e1.setName("Duong Thanh Dat");
        e1.setEmail("datdt73@fpt.com");
        e1.setUsername("datdt73");
        e1.setGroup(group1);
        e1.setRankTitle(rankTitle1);

        Employee e2 = new Employee();
        e2.setName("Duong Thanh Dat666");
        e2.setEmail("datdt74@fpt.com");
        e2.setUsername("datdt74");
        e2.setGroup(group1);
        e2.setRankTitle(rankTitle1);

        employeeRepository.save(e1);
        employeeRepository.save(e2);

        // Create Set<RankingDecisionCriteria> for ranking decision
        // and criteria because the relationship is M-M
        Set<RankingDecisionCriteria> rankingDecisionCriteria_Set1 = new LinkedHashSet<>();
        rankingDecisionCriteria_Set1.add(rankingDecisionCriteria1);
        rankingDecisionCriteria_Set1.add(rankingDecisionCriteria2);
        rankingDecisionCriteria_Set1.add(rankingDecisionCriteria3);

        Set<RankingDecisionCriteria> rankingDecisionCriteria_Set2 = new LinkedHashSet<>();
        rankingDecisionCriteria_Set2.add(rankingDecisionCriteria4);
        rankingDecisionCriteria_Set2.add(rankingDecisionCriteria5);
        rankingDecisionCriteria_Set2.add(rankingDecisionCriteria6);

        //set  Set<RankingDecisionCriteria> for ranking decision 1,2
        rankingDecision1.setRankingDecisionCriterias(rankingDecisionCriteria_Set1);

        rankingDecision2.setRankingDecisionCriterias(rankingDecisionCriteria_Set2);


        //set  Set<RankingDecisionCriteria> for criteria 1,2,3
        Set<RankingDecisionCriteria> rankingDecisionCriteria_Set3 = new LinkedHashSet<>();
        rankingDecisionCriteria_Set3.add(rankingDecisionCriteria1);
        rankingDecisionCriteria_Set3.add(rankingDecisionCriteria4);

        criteria1.setRankingDecisionCriterias(rankingDecisionCriteria_Set3);

        Set<RankingDecisionCriteria> rankingDecisionCriteria_Set4 = new LinkedHashSet<>();
        rankingDecisionCriteria_Set4.add(rankingDecisionCriteria2);
        rankingDecisionCriteria_Set4.add(rankingDecisionCriteria5);

        criteria2.setRankingDecisionCriterias(rankingDecisionCriteria_Set4);

        Set<RankingDecisionCriteria> rankingDecisionCriteria_Set5 = new LinkedHashSet<>();
        rankingDecisionCriteria_Set5.add(rankingDecisionCriteria3);
        rankingDecisionCriteria_Set5.add(rankingDecisionCriteria6);

        criteria3.setRankingDecisionCriterias(rankingDecisionCriteria_Set5);



        // create set of ranking decision for group1
        Set<RankingDecision> rankingDecision_Set = new LinkedHashSet<>();
        rankingDecision_Set.add(rankingDecision1);
        rankingDecision_Set.add(rankingDecision2);


        Instant now = Instant.now();
        Timestamp timestamp = Timestamp.from(now);
        RankingHistory rankingHistory1 = new RankingHistory();
        rankingHistory1.setGroup(group1);
        rankingHistory1.setStatus(true);
        rankingHistory1.setNote("This is a test");
        rankingHistory1.setFileName("Bulk_Ranking_1.xls");
        rankingHistory1.setUploadedAt(timestamp);
        rankingHistory1.setUploadedBy("sangnn2");
        rankingHistory1.setRankingDecision("Ranking Decision 2");
        rankingHistoryRepository.save(rankingHistory1);

        Employee employee1 = new Employee();
        employee1.setEmail("sonvh33@fpt.com");
        employee1.setName("Vu Hong Son");
        employee1.setGroup(group1);
        employee1.setRankTitle(rankTitle1);
        employee1.setUsername("sonvh33");
        employeeRepository.save(employee1);

    }
}
