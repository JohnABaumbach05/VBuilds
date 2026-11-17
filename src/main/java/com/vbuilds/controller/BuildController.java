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

@RestController
@RequestMapping("/api/builds")
public class BuildController {

    private BuildService buildService;

    public BuildController() throws SQLException {
        buildService = new BuildService();
    }



    @GetMapping("/user/{userId}")
    public List<Build> getApprovedUserBuilds(@PathVariable int userId) throws SQLException {
        return buildService.getApprovedUserBuilds(userId);
    }

    @GetMapping("/mine")
    public List<Build> getMyBuilds(HttpSession session) throws SQLException {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "You must be logged in"
            );
        }

        return buildService.getUserBuilds(userId);
    }



    @GetMapping("/{id}/edit")
    public Build getBuildForEditing(
            @PathVariable int id,
            HttpSession session
    ) throws SQLException {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "You must be logged in"
            );
        }

        Build build = buildService.getBuildForEditing(id);

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

        return build;
    }



    @PutMapping("/{id}")
    public void updateBuild(
            @PathVariable int id,
            @RequestBody Build updatedBuild,
            HttpSession session
    ) throws SQLException {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "You must be logged in"
            );
        }

        Build existingBuild = buildService.getBuildForEditing(id);

        if (existingBuild == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Build not found"
            );
        }

        if (existingBuild.getOwnerID() != userId) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not own this build"
            );
        }

        updatedBuild.setBuildId(id);
        updatedBuild.setOwnerID(userId);


        if (updatedBuild.getMainPhoto() == null) {
            updatedBuild.setMainPhoto(existingBuild.getMainPhoto());
        }

        buildService.updateBuild(updatedBuild);
    }


    @DeleteMapping("/{id}")
    public void deleteBuild(@PathVariable int id, HttpSession session) throws SQLException {
        requireOwnedBuild(id, session);
        buildService.deleteBuild(id);
    }


    @PostMapping
    public Map<String, Integer> addBuild(
            @RequestBody CreateBuildRequest request,
            HttpSession session
    ) throws SQLException {


        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "You must be logged in to create a build"
            );
        }


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

    private Build requireOwnedBuild(int id, HttpSession session) throws SQLException {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        Build build = buildService.getBuildForEditing(id);
        if (build == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Build not found");
        if (build.getOwnerID() != userId)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your build");
        return build;
    }

    private void validatePart(Part part) {
        if (part == null || part.getCategory() == null || part.getCategory().isBlank() ||
                part.getCategory().length() > 60 || part.getPartName() == null ||
                part.getPartName().isBlank() || part.getPartName().length() > 150 ||
                part.getManufacturer() == null || part.getManufacturer().isBlank() ||
                part.getManufacturer().length() > 100 || part.getPartCost() < 0 ||
                part.getPartReliability() < 0 || part.getPartReliability() > 10 ||
                part.getEstPowerAdded() < 0 ||
                (part.getPartDescription() != null && part.getPartDescription().length() > 1000))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid part details");
    }

    @PutMapping("/{id}/parts/{partId}")
    public void updateBuildPart(@PathVariable int id, @PathVariable int partId,
                                @RequestBody Part part, HttpSession session) throws SQLException {
        requireOwnedBuild(id, session);
        validatePart(part);
        part.setPartID(partId);
        if (!new com.vbuilds.dao.PartDAO().updateBuildPart(id, part))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Part not found on this build");
    }

    @DeleteMapping("/{id}/parts/{partId}")
    public void deleteBuildPart(@PathVariable int id, @PathVariable int partId,
                                HttpSession session) throws SQLException {
        requireOwnedBuild(id, session);
        if (!new com.vbuilds.dao.PartDAO().deleteBuildPart(id, partId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Part not found on this build");
    }

    @PostMapping("/{id}/parts")
    public void addBuildPart(@PathVariable int id, @RequestBody Part part,
                             HttpSession session) throws SQLException {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in");
        }
        Build build = buildService.getBuildForEditing(id);
        if (build == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Build not found");
        }
        if (build.getOwnerID() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this build");
        }
        validatePart(part);
        new com.vbuilds.service.PartService().addBuildPart(id, part);
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
