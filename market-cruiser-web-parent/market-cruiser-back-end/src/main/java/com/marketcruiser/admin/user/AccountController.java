package com.marketcruiser.admin.user;

import com.marketcruiser.admin.FileUploadUtil;
import com.marketcruiser.admin.security.MarketCruiserUserDetails;
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

@Controller
public class AccountController {

    private final UserServiceImpl userService;

    @Autowired
    public AccountController(UserServiceImpl userService) {
        this.userService = userService;
    }


    // this method retrieves the currently logged-in user
    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal MarketCruiserUserDetails loggedUser, Model model) {
        String emailAddress = loggedUser.getUsername();
        User user = userService.getUserByEmailAddress(emailAddress);

        model.addAttribute("user", user);

        return "account_form";
    }

    // this method updates the user's account details in the database and handles the uploading of the user's profile photo
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
