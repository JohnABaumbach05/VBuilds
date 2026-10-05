package com.vbuilds.controller;

import com.vbuilds.model.Build;
import com.vbuilds.model.Photo;
import com.vbuilds.model.Part;
import com.vbuilds.service.BuildService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

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
    public void addBuild(@RequestBody Build build) throws SQLException {
        buildService.addBuild(build);
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
