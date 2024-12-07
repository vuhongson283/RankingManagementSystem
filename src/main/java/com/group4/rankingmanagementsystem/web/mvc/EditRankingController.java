package com.group4.rankingmanagementsystem.web.mvc;

import com.group4.rankingmanagementsystem.entity.Group;
import com.group4.rankingmanagementsystem.entity.RankingDecision;
import com.group4.rankingmanagementsystem.repository.GroupRepository;
import com.group4.rankingmanagementsystem.service.GroupService;
import com.group4.rankingmanagementsystem.service.RankingDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/editRanking")
public class EditRankingController {

    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final RankingDecisionService rankingDecisionService;


    @Autowired
    public EditRankingController(GroupService groupService, GroupRepository groupRepository, RankingDecisionService rankingDecisionService) {
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.rankingDecisionService = rankingDecisionService;
    }

    @GetMapping()
 public String showEditRankingPage() {
     return "group/edit-ranking-group";
 }

    @GetMapping("/ShowEdit")
    public String showEditInfoModal(Model model, @RequestParam Long id) {
        Object groupInfo = groupService.findGroupbyID(id);
        List<RankingDecision> rankList = groupService.listAllRankNamebyGIDandStatus(id);


        model.addAttribute("rankList", rankList);

        if (groupInfo != null) {
            Object[] obj = (Object[]) groupInfo;
            model.addAttribute("groupName", obj[0]);
            model.addAttribute("rankingDecisionName", obj[1]);
        }
        model.addAttribute("groupId", id); // Add groupId to model
        return "group/edit-ranking-group";
    }


    @PostMapping("/updateGroup")
    public String updateGroupName(@RequestParam("group_id") Long id, @RequestParam("name") String name, Model model, @RequestParam("rid") Long rid, RedirectAttributes redirectAttributes) {
        Group group = groupService.findGroupbyID2(id);
        model.addAttribute("group", group);
        RankingDecision rankingDecision = rankingDecisionService.findRankingDecisionbyID(rid);

        // Kiểm tra nếu tên nhóm đã tồn tại
        if (groupService.isGroupNameExists(name) && !group.getName().equals(name)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Group name already exists! Please choose another name.");
            return "redirect:/group-list/edit?id=" + id;
        }

        if (group != null) {
            group.setName(name);
            group.setCurrentRankingDecision(rankingDecision);
            groupRepository.save(group);
            redirectAttributes.addFlashAttribute("successMessage", "Group updated successfully!");
        }
        return "redirect:/group-list/edit?id=" + id;
    }

    @PostMapping("/addRankingDecision")
    public String addRankingDecision(@RequestParam("group_id") Long groupId,
                                     @RequestParam("name") String name,
                                     @RequestParam(value = "cloneDecision", required = false) boolean cloneDecision,
                                     @RequestParam(value = "chooseDecision", required = false) Long chooseDecision,
                                     RedirectAttributes redirectAttributes) {
        if (rankingDecisionService.isRankingDecisionNameExists(name, groupId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ranking decision name already exists!");
            return "redirect:/group-list/edit?id=" + groupId;
        }

        if (cloneDecision && chooseDecision != null) {
            // Kiểm tra nếu tên của quyết định xếp hạng đã tồn tại trong nhóm đích
            String sourceDecisionName = rankingDecisionService.findRankingDecisionbyID(chooseDecision).getName();
            if (rankingDecisionService.isRankingDecisionNameExists(sourceDecisionName, groupId)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ranking decision name already exists!");
                return "redirect:/group-list/edit?id=" + groupId;
            }

            // Sao chép RankingDecision từ nhóm khác
            rankingDecisionService.cloneRankingDecision(chooseDecision, groupId);
        } else {
            // Thêm mới RankingDecision
            RankingDecision rankingDecision = new RankingDecision();
            rankingDecision.setName(name.trim().replaceAll("\\s+", " "));
            rankingDecision.setStatus(false);
            rankingDecision.setGroup(new Group());
            rankingDecision.getGroup().setId(groupId);
            rankingDecisionService.addNewRankingDecision(rankingDecision);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Ranking decision added successfully!");
        return "redirect:/group-list/edit?id=" + groupId;
    }


}
