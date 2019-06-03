package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    CloudinaryConfig cloudc;

    @Autowired
    UserService userService;

    @Autowired
    MessagesRepository messagesRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "/registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        model.addAttribute("user",user);
        if(result.hasErrors()) {
            return "registration";
        }
        else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Successfully Created");
        }
        return "redirect:/";
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("messages", messagesRepository.findAll());
        if(userService.getCurrentUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        return "index";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        return "login";
    }
    @PostMapping("/login")
    public String processlogin( BindingResult result){
        if(result.hasErrors()){
            return "login";
        }

        return "redirect:/";
    }

    @RequestMapping("/secure")
    public String secure(Model model) {
        // Gets the currently logged in user and maps it to "user" in the Thymeleaf template
        model.addAttribute("user", userService.getUser());
        model.addAttribute("messages", messagesRepository.findAll());

        if(userService.getCurrentUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        return "index";
    }
    @GetMapping("/add")
    public String messageform(Model model){
        model.addAttribute("message", new Message());
        return"messageform";
    }
    @PostMapping("/processform")
    public String processmessageform( @ModelAttribute Message message,@Valid @RequestParam("file")MultipartFile file, BindingResult result){
        if(result.hasErrors()){
            return "messageform";
        }
        //If it breaks delete this

        try{
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            message.setPic(uploadResult.get("url").toString());
            messagesRepository.save(message);
        }catch(IOException e){
            e.printStackTrace();
            return "redirect:/add";
        }
        //only whats before this line goes
        message.setUser(userService.getUser());
        messagesRepository.save(message);
        return "redirect:/";
    }
    @RequestMapping("/view/{id}")
    public String showCourse(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messagesRepository.findById(id).get());
        if(userService.getCurrentUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateCourse(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messagesRepository.findById(id).get());
        return "messageform";
    }

    @RequestMapping("/delete/{id}")
    public String delCourse(@PathVariable("id") long id){
        messagesRepository.deleteById(id);
        return "redirect:/";
    }

}
