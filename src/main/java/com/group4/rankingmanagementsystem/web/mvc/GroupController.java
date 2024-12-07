package com.group4.rankingmanagementsystem.web.mvc;

import com.group4.rankingmanagementsystem.entity.Group;
import com.group4.rankingmanagementsystem.entity.RankingDecision;
import com.group4.rankingmanagementsystem.service.GroupService;
import com.group4.rankingmanagementsystem.service.RankingDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller()
@RequestMapping("/group-list")
public class GroupController {

        private final GroupService groupService;
        private final RankingDecisionService rankingDecisionService;

        @Autowired
        public GroupController(GroupService groupService, RankingDecisionService rankingDecisionService) {
            this.groupService = groupService;
            this.rankingDecisionService = rankingDecisionService;
        }

        @GetMapping()
        public String show(Model model) {
            model.addAttribute("group", groupService.listGroupList());
            return "group/list";
        }

        @GetMapping(value = "/666")
        public String login() {
            return "auth/login";
        }

    @PostMapping("/addNew")
    @ResponseBody
    public ResponseEntity<String> addGroup(@RequestParam(value = "groupName", required = false) String groupName) {
        if (groupName != null) {
        }
        if (groupService.isGroupNameExists(groupName)) {
            return new ResponseEntity<>("Group Name already exists", HttpStatus.CONFLICT); // 409 Conflict
        }
        try {
            groupService.addNewGroup(groupName.trim().replaceAll("\\s+", " "));
            return new ResponseEntity<>("Ranking Group successfully added", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred adding Ranking Group. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }



    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> delete(@RequestParam Long id) {
        try {
            groupService.deleteGroup(id);
            return new ResponseEntity<>("Group successfully deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred deleting Group. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam Long id) {
        Object groupInfo = groupService.findGroupbyID(id);
        List<RankingDecision> currentRankList = groupService.listRankingDecisionbyGroupID(id);
         model.addAttribute("RankDecisionList",currentRankList);
        List<RankingDecision> rankNotGrpup = rankingDecisionService.listRankDecisionNotGroupID(id);
        model.addAttribute("rankNotGrpup", rankNotGrpup);
        if (groupInfo != null) {
            Object[] obj = (Object[]) groupInfo;
            model.addAttribute("groupID", obj[0]);
            model.addAttribute("groupName", obj[1]);
            model.addAttribute("rankingDecisionName", obj[2]);
        }
        return "group/edit-ranking-group";
    }

}
