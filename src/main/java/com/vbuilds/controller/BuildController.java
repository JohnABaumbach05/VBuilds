package com.vbuilds.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.vbuilds.model.Build;
import com.vbuilds.model.Photo;
import com.vbuilds.model.Part;
import com.vbuilds.service.BuildService;
import org.springframework.web.bind.annotation.*;
import com.vbuilds.dto.CreateBuildRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map;

@RestController
@RequestMapping("/api/builds")
public class BuildController {

    private BuildService buildService;

    public BuildController() throws SQLException {
        buildService = new BuildService();
    }

    @PutMapping("/{id}")
    public void updateBuild(@PathVariable int id, @RequestBody Build build) throws SQLException {
        build.setBuildId(id);
        buildService.updateBuild(build);
    }

    @DeleteMapping("/{id}")
    public void deleteBuild(@PathVariable int id) throws SQLException {
        buildService.deleteBuild(id);
    }


    @PostMapping
    public Map<String, Integer> addBuild(
            @RequestBody CreateBuildRequest request,
            HttpSession session
    ) throws SQLException {

        // GET AUTHENTICATED USER
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "You must be logged in to create a build"
            );
        }

        // VALIDATE REQUIRED INFORMATION
        if (request.getTitle() == null ||
                request.getTitle().isBlank() ||
                request.getTrim() == null ||
                request.getTrim().isBlank() ||
                request.getBuildYear() < 2022) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid build information"
            );
        }

        // CONVERT REQUEST DTO INTO BUILD MODEL
        Build build = new Build();

        build.setOwnerID(userId);
        build.setTitle(request.getTitle());
        build.setHorsepower(request.getHorsepower());
        build.setHappiness(request.getHappiness());
        build.setReliability(request.getReliability());
        build.setLoudness(request.getLoudness());
        build.setCost(request.getCost());
        build.setTuner(request.getTuner());
        build.setBuildYear(request.getBuildYear());
        build.setTrim(request.getTrim());
        build.setMainPhoto(request.getMainPhoto());
        build.setDescription(request.getDescription());

        // SAVE BUILD TO POSTGRESQL
        int buildId = buildService.addBuild(build);

        return Map.of("buildId", buildId);
    }





    @PostMapping("/{id}/photos")
    public void addBuildPhoto(
            @PathVariable int id,
            @RequestBody Map<String, String> request,
            HttpSession session
    ) throws SQLException {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "You must be logged in"
            );
        }

        Build build = buildService.getBuildByID(id);

        if (build == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Build not found"
            );
        }

        if (build.getOwnerID() != userId) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not own this build"
            );
        }

        String filePath = request.getOrDefault("filePath", "");
        String caption = request.getOrDefault("caption", "");

        if (!filePath.startsWith("/uploads/builds/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid photo path"
            );
        }

        Photo photo = new Photo();

        photo.setBuildId(id);
        photo.setFilePath(filePath);
        photo.setCaption(caption);

        buildService.addPhoto(photo);
    }


    @GetMapping("/allbuilds")
    public List<Build> getAllBuilds() throws SQLException {
        return buildService.getAllBuilds();
    }

    @GetMapping("/random")
    public Build getRandomBuild() throws SQLException {
        return buildService.getRandomBuild();
    }

    @GetMapping("/top/limit")
    public List<Build> getTopHorsepowerBuilds (@RequestParam int limit) throws SQLException {
        return buildService.getTopHorsePowerBuilds(limit);
    }

    @GetMapping("/top")
    public List<Build> getTopHorsepowerBuilds() throws SQLException {
        return buildService.getTopHorsePowerBuilds();
    }

    @GetMapping("/{id}/parts")
    public List<Part> getBuildParts(@PathVariable int id) throws SQLException {
        return buildService.getBuildParts(id);
    }

    @GetMapping("/{id}/photos")
    public List<Photo> getBuildPhotos(@PathVariable int id) throws SQLException {
        return buildService.getBuildPhotos(id);
    }

    @GetMapping("/{id}")
    public Build getBuildById(@PathVariable int id) throws SQLException {
        return buildService.getBuildByID(id);
    }

    @GetMapping("/featured")
    public List<Build> getFeaturedBuilds() throws SQLException {
        return buildService.getFeaturedBuilds();
    }

    @GetMapping("/randomlist")
    public List<Build> getRandomBuilds() throws SQLException {
        return buildService.getRandomBuilds();
    }

    @GetMapping("/newest")
    public List<Build> getNewestBuilds() throws SQLException {
        return buildService.getNewestBuilds();
    }


}
