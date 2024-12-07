package com.group4.rankingmanagementsystem.web.mvc;

import com.group4.rankingmanagementsystem.entity.Task;
import com.group4.rankingmanagementsystem.entity.Group;
import com.group4.rankingmanagementsystem.service.TaskService;
import com.group4.rankingmanagementsystem.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/task-list")
public class TaskController {

    private TaskService taskService;
    private GroupService groupService;

    @Autowired
    public TaskController(TaskService taskService, GroupService groupService) {
        this.taskService = taskService;
        this.groupService = groupService;
    }

    @GetMapping
    public String listTasks(Model model, @RequestParam("groupId") Long id) {
        model.addAttribute("tasks", taskService.getTaskByGroupId(id));
        model.addAttribute("groupId", id);
        return "task/list";
    }

    @GetMapping("/remove-task")
    public String removeTask(@RequestParam("id") Long id, @RequestParam("groupId") Long groupId, RedirectAttributes redirectAttributes) {
        taskService.removeTaskById(id);
        redirectAttributes.addFlashAttribute("taskDeleted", true);
        return "redirect:/task-list?groupId=" + groupId;
    }

    @PostMapping("/edit-task")
    public String updateTask(@RequestParam("id") Long id, @RequestParam("name") String name, @RequestParam("groupId") Long groupId, RedirectAttributes redirectAttributes) {
        Task task = taskService.getTaskById(id);
        if (taskService.isTaskNameExist(name)) {
            redirectAttributes.addFlashAttribute("taskEditNameExists", true);
            return "redirect:/task-list?groupId=" + groupId; // Redirect to show the error message
        }
        task.setName(name);
        taskService.addTask(task);
        redirectAttributes.addFlashAttribute("taskEdited", true);
        return "redirect:/task-list?groupId=" + groupId;
    }

    @PostMapping("/add-task")
    public String addTask(@ModelAttribute("task") Task task, @RequestParam("groupId") Long groupId, RedirectAttributes redirectAttributes) {
        task.setName(task.getName().trim().replaceAll("\\s+", " "));
        if (taskService.isTaskNameExist(task.getName())) {
            redirectAttributes.addFlashAttribute("taskNameExists", true);
            return "redirect:/task-list?groupId=" + groupId; // Redirect to show the error message
        }
        Group group = groupService.getGroup(groupId);
        task.setGroup(group); // Set the Group object for the new task
        taskService.saveTask(task);
        redirectAttributes.addFlashAttribute("taskAdded", true);
        return "redirect:/task-list?groupId=" + groupId;
    }
}
