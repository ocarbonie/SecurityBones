package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class HomeController {
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
    public String processmessageform(@Valid Message message, BindingResult result){
        if(result.hasErrors()){
            return "messageform";
        }
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
