package com.marketcruiser.admin.user;

import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.common.entity.Role;
import com.marketcruiser.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }


    // shows a first page of users
    @GetMapping("/users")
    public String showFirstPageOfUsers(Model model) {
        return showPageOfUsers(1, model, "firstName", "asc", null);
    }

    // shows a list of all users using pagination
    @GetMapping("/users/page/{pageNumber}")
    public String showPageOfUsers(@PathVariable int pageNumber, Model model, @Param("sortField") String sortField,
                                  @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
        System.out.println("Sort field: " + sortField);
        System.out.println("Sort direction: " + sortDir);
        Page<User> page = userService.listUsersByPage(pageNumber, sortField, sortDir, keyword);
        List<User> listUsers = page.getContent();

        long startCount = (long) (pageNumber - 1) * UserServiceImpl.USERS_PER_PAGE + 1;
        long endCount = startCount + UserServiceImpl.USERS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

        return "users";
    }

    // form for creating a new user
    @GetMapping("/users/new")
    public String showFormForCreatingUser(Model model) {
        List<Role> listRoles = userService.getAllRoles();

        User user = new User();
        user.setEnabled(true);

        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");

        return "user_form";
    }

    // create new user with image file
    @PostMapping("/users/save")
    public String saveNewUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser = userService.saveUser(user);

            String uploadDir = "user-photos/" + savedUser.getUserId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {
            if (user.getPhoto().isEmpty()) user.setPhoto(null);
            userService.saveUser(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");

        return getRedirectURLToAffectedUser(user);
    }

    // method used to be returned in createNewUser() method due to readability
    private String getRedirectURLToAffectedUser(User user) {
        String firstPartOfEmailAddress = user.getEmailAddress().split("@")[0];
        return "redirect:/users/page/1?sortField=userId&sortDir=asc&keyword=" + firstPartOfEmailAddress;
    }

    // form for updating/editing already existing user with exception handler
    @GetMapping("/users/edit/{userId}")
    public String showFormForEditingUser(@PathVariable Long userId, RedirectAttributes redirectAttributes, Model model) {
        try {
            User user = userService.getUserById(userId);
            List<Role> listRoles = userService.getAllRoles();

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (ID: " + userId + ")");
            model.addAttribute("listRoles", listRoles);

            return "user_form";
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/users";
        }
    }

    // method that deletes user
    @GetMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", "The user with ID " + userId + " has been deleted successfully");
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/users";
    }

    // method that disables or enables the user
    @GetMapping("/users/{userId}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable Long userId, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        userService.updateUserEnabledStatus(userId, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user with ID " + userId + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }

    // method that exports the entire table of users to a CSV file
    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.getAllUsersSortedByFirstName();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(listUsers, response);
    }

    // method that exports the entire table of users to XLSX file
    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.getAllUsersSortedByFirstName();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(listUsers, response);
    }

    // method that exports the entire table of users to PDF file
    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.getAllUsersSortedByFirstName();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(listUsers, response);
    }


}
