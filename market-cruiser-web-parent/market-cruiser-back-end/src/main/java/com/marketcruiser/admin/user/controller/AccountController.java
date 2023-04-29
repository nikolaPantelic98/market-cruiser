package com.marketcruiser.admin.user.controller;

import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.admin.security.MarketCruiserUserDetails;
import com.marketcruiser.admin.user.UserServiceImpl;
import com.marketcruiser.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

/**
 * This class represents the AccountController, which is responsible for handling requests related to a user's account.
 * It provides methods for viewing and updating the user's account details in the database.
 */
@Controller
public class AccountController {

    private final UserServiceImpl userService;

    @Autowired
    public AccountController(UserServiceImpl userService) {
        this.userService = userService;
    }


    /**
     * Retrieves the currently logged-in user and displays their account details.
     *
     * @param loggedUser the user that is currently logged in
     * @param model      the model used to pass data to the view
     * @return the account_form.html page with the user's details
     */
    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal MarketCruiserUserDetails loggedUser, Model model) {
        String emailAddress = loggedUser.getUsername();
        User user = userService.getUserByEmailAddress(emailAddress);

        model.addAttribute("user", user);

        return "users/account_form";
    }

    /**
     * Updates the user's account details in the database and handles the uploading of the user's profile photo.
     *
     * @param user                the user whose account details are being updated
     * @param redirectAttributes the attributes used to pass messages between requests
     * @param multipartFile      the user's profile photo
     * @param loggedUser          the user that is currently logged in
     * @return a redirect to the account page with a success message
     * @throws IOException if there is an error while uploading the profile photo
     */
    @PostMapping("/account/update")
    public String saveDetails(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile,
                              @AuthenticationPrincipal MarketCruiserUserDetails loggedUser) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser = userService.updateAccount(user);

            String uploadDir = "user-photos/" + savedUser.getUserId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {
            if (user.getPhoto().isEmpty()) user.setPhoto(null);
            userService.updateAccount(user);
        }

        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());

        redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");

        return "redirect:/account";
    }
}
