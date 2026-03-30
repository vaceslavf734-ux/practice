package ru.mtuci.coursemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.mtuci.coursemanagement.model.Course;
import ru.mtuci.coursemanagement.repository.CourseRepository;
import ru.mtuci.coursemanagement.service.CourseService;

import java.net.InetAddress;
import java.net.URI;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CourseController {
    private final CourseRepository repo;
    private final CourseService service;

    @GetMapping("/courses")
    public String list(Model model) {
        model.addAttribute("courses", service.findAll());
        model.addAttribute("course", new Course());
        return "courses";
    }

    @PostMapping("/courses")
    public String create(@ModelAttribute Course c) {
        service.save(c);
        return "redirect:/courses";
    }

    @GetMapping("/api/courses")
    @ResponseBody
    public List<Course> apiAll() {
        return service.findAll();
    }

    @GetMapping("/api/courses/{id}")
    @ResponseBody
    public Course apiOne(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/api/courses")
    @ResponseBody
    public Course apiCreate(@RequestBody Course c) {
        return service.save(c);
    }

    @GetMapping("/api/courses/search")
    @ResponseBody
    public List<Course> search(@RequestParam String title) {
        return service.searchByTitle(title);
    }

    @GetMapping("/api/courses/import")
    @ResponseBody
    public String importFromUrl(@RequestParam String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL");
        }
        String host = uri.getHost();
        if (host == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL");
        }
        try {
            InetAddress addr = InetAddress.getByName(host);
            if (addr.isLoopbackAddress() || addr.isSiteLocalAddress() || addr.isLinkLocalAddress()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Internal addresses are not allowed");
            }
        } catch (java.net.UnknownHostException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown host");
        }
        RestTemplate rt = new RestTemplate();
        String json = rt.getForObject(url, String.class);
        log.info("Import completed");
        return "OK";
    }
}
