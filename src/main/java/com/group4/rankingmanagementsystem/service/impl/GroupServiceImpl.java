package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.Group;
import com.group4.rankingmanagementsystem.entity.RankingDecision;
import com.group4.rankingmanagementsystem.repository.GroupRepository;
import com.group4.rankingmanagementsystem.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {
private GroupRepository groupRepository;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Override
    public List<Object[]> listGroupList() {
        return groupRepository.findGroupWithRankingName();
    }

    @Override
    public void addNewGroup(String groupName) {
        Group group = new Group();
        group.setName(groupName.trim());
        groupRepository.save(group);
    }

    @Override
    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    @Override
    public Object findGroupbyID(Long Id) {
        return groupRepository.findGroupNameAndRankingDecisionNameById(Id);
    }

    @Override
    public List<RankingDecision> listRankingDecisionbyGroupID(Long id) {
        return groupRepository.findAllRankdecisionbyGID(id);
    }

    @Override
    public List<Object[]> listRankingDecisionNamebyGroupID2(Long id) {
        return groupRepository.findGroupAndRankingDecisionNamesByGroupIdAndStatus(id);
    }

    @Override
    public List<RankingDecision> listAllRankNamebyGIDandStatus(Long id) {
        return groupRepository.findAllRankingNamesByGroupIdAndStatus(id);
    }

    @Override
    public void upDateGroup(Long id, String name) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + id));
        group.setName(name);
        groupRepository.save(group);
    }

    @Override
    public Group findGroupbyID2(Long Id) {
        return groupRepository.findById(Id).orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + Id));
    }

    @Override
    public Group getGroup(Long id) {
        return groupRepository.getGroupById(id);
    }

    @Override
    public boolean isGroupNameExists(String groupName) {
        return groupRepository.existsByName(groupName);
    }


}
