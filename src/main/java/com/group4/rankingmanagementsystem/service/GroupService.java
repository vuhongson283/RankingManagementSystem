package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.Group;
import com.group4.rankingmanagementsystem.entity.RankingDecision;

import java.util.List;

public interface GroupService {
    public List<Object[]> listGroupList() ;
    public void addNewGroup(String groupName) ;
    boolean isGroupNameExists(String name);
    public void deleteGroup(Long Id) ;
    public Object findGroupbyID(Long Id);
    public List<RankingDecision> listRankingDecisionbyGroupID(Long id);
    public List<Object[]> listRankingDecisionNamebyGroupID2(Long id);
    public List<RankingDecision> listAllRankNamebyGIDandStatus(Long id);
    void  upDateGroup(Long id , String name);
    public Group findGroupbyID2(Long Id);
    Group getGroup(Long id);
}
