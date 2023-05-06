package com.marketcruiser.admin.user.controller;

import com.marketcruiser.admin.AmazonS3Util;
import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.admin.category.CategoryServiceImpl;
import com.marketcruiser.admin.user.UserNotFoundException;
import com.marketcruiser.admin.user.UserServiceImpl;
import com.marketcruiser.admin.user.export.UserCsvExporter;
import com.marketcruiser.admin.user.export.UserExcelExporter;
import com.marketcruiser.admin.user.export.UserPdfExporter;
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

/**
 * CategoryController handles requests related to users including pagination,
 * creating, editing, and deleting users. It communicates with the {@link UserServiceImpl}
 * to perform CRUD operations.
 */
@Controller
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }


    /**
     * Returns a view displaying the first page of users.
     *
     * @param model a Spring MVC Model
     * @return the view displaying the first page of users
     */
    @GetMapping("/users")
    public String showFirstPageOfUsers(Model model) {
        return showPageOfUsers(1, model, "firstName", "asc", null);
    }

    /**
     * Returns a view displaying the first page of users.
     *
     * @param model a Spring MVC Model
     * @return the view displaying the first page of users
     */
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
        model.addAttribute("moduleURL", "/users");

        return "users/users";
    }

    /**
     * Shows the form for creating a new user.
     *
     * @param model the Spring Model object
     * @return the user form template
     */
    @GetMapping("/users/new")
    public String showFormForCreatingUser(Model model) {
        List<Role> listRoles = userService.getAllRoles();

        User user = new User();
        user.setEnabled(true);

        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");

        return "users/user_form";
    }

    /**
     * Saves a new user with image file or updates an existing one.
     *
     * @param user the User object to be saved or updated
     * @param redirectAttributes the Spring RedirectAttributes object
     * @param multipartFile the uploaded MultipartFile object containing user's image
     * @return the redirect URL to the affected user
     * @throws IOException if there is an I/O error while saving the image file
     */
    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser = userService.saveUser(user);

            String uploadDir = "user-photos/" + savedUser.getUserId();

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

        } else {
            if (user.getPhoto().isEmpty()) user.setPhoto(null);
            userService.saveUser(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");

        return getRedirectURLToAffectedUser(user);
    }

    /**
     * Gets the redirect URL to the affected user after saving or updating a user.
     *
     * @param user the User object
     * @return the redirect URL to the affected user
     */
    private String getRedirectURLToAffectedUser(User user) {
        String firstPartOfEmailAddress = user.getEmailAddress().split("@")[0];
        return "redirect:/users/page/1?sortField=userId&sortDir=asc&keyword=" + firstPartOfEmailAddress;
    }

    /**
     * Shows the form for updating/editing already existing user with exception handler.
     *
     * @param userId the ID of the user to be edited
     * @param redirectAttributes the Spring RedirectAttributes object
     * @param model the Spring Model object
     * @return the user form template or redirect URL to the users page
     */
    @GetMapping("/users/edit/{userId}")
    public String showFormForEditingUser(@PathVariable Long userId, RedirectAttributes redirectAttributes, Model model) {
        try {
            User user = userService.getUserById(userId);
            List<Role> listRoles = userService.getAllRoles();

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (ID: " + userId + ")");
            model.addAttribute("listRoles", listRoles);

            return "users/user_form";
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/users";
        }
    }

    /**
     * Deletes a user with the specified ID.
     *
     * @param userId the ID of the user to be deleted
     * @param redirectAttributes the Spring RedirectAttributes object
     * @return the redirect URL to the users page
     */
    @GetMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            String userDir = "user-photos/" + userId;
            AmazonS3Util.removeFolder(userDir);

            redirectAttributes.addFlashAttribute("message", "The user with ID " + userId + " has been deleted successfully");
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/users";
    }

    /**
     * Updates the enabled status of a user.
     *
     * @param userId The ID of the user to update.
     * @param enabled The new enabled status of the user.
     * @param redirectAttributes Redirect attributes to set a flash message on success.
     * @return A redirect to the /users endpoint.
     */
    @GetMapping("/users/{userId}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable Long userId, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        userService.updateUserEnabledStatus(userId, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user with ID " + userId + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }

    /**
     * Exports the entire table of users to a CSV file.
     *
     * @param response The HTTP response to write the CSV file to.
     * @throws IOException If there is an error writing the CSV file.
     */
    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.getAllUsersSortedByFirstName();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(listUsers, response);
    }

    /**
     * Exports the entire table of users to an XLSX file.
     *
     * @param response The HTTP response to write the XLSX file to.
     * @throws IOException If there is an error writing the XLSX file.
     */
    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.getAllUsersSortedByFirstName();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(listUsers, response);
    }

    /**
     * Exports the entire table of users to a PDF file.
     *
     * @param response The HTTP response to write the PDF file to.
     * @throws IOException If there is an error writing the PDF file.
     */
    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.getAllUsersSortedByFirstName();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(listUsers, response);
    }
}