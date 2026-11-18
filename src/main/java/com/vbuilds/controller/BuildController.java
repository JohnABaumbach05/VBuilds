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
import com.vbuilds.dto.UpdateBuildRequest;
import java.net.URI;
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
    public void updateBuild(@PathVariable int id, @RequestBody UpdateBuildRequest request,
                            HttpSession session) throws SQLException {
        Build existingBuild = requireOwnedBuild(id, session);

        if (request.getTitle() == null || request.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Build title is required");
        if (request.getBuildYear() == null || request.getBuildYear() < 2022)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Build year must be 2022 or newer");
        if (request.getTrim() == null || request.getTrim().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Build trim is required");
        if (request.getHorsepower() == null || request.getHorsepower() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horsepower must be zero or greater");
        if (request.getCost() == null || request.getCost() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cost must be zero or greater");
        if (request.getHappiness() == null || request.getHappiness() < 0 || request.getHappiness() > 10)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Happiness must be between 0 and 10");
        if (request.getReliability() == null || request.getReliability() < 0 || request.getReliability() > 10)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reliability must be between 0 and 10");
        if (request.getLoudness() == null || request.getLoudness() < 0 || request.getLoudness() > 10)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loudness must be between 0 and 10");

        // Update only editable fields; preserve ownership, moderation, and photos.
        existingBuild.setTitle(request.getTitle().trim());
        existingBuild.setBuildYear(request.getBuildYear());
        existingBuild.setTrim(request.getTrim().trim());
        existingBuild.setHorsepower(request.getHorsepower());
        existingBuild.setCost(request.getCost());
        existingBuild.setTuner(request.getTuner() == null ? "" : request.getTuner().trim());
        existingBuild.setDescription(request.getDescription() == null ? "" : request.getDescription().trim());
        existingBuild.setHappiness(request.getHappiness());
        existingBuild.setReliability(request.getReliability());
        existingBuild.setLoudness(request.getLoudness());
        buildService.updateBuild(existingBuild);
    }


    @PutMapping("/{id}/main-photo")
    public void updateMainPhoto(@PathVariable int id, @RequestBody Map<String, String> request,
                                HttpSession session) throws SQLException {
        Build build = requireOwnedBuild(id, session);
        String url = request.get("url");
        if (url == null || !url.startsWith("/uploads/builds/") ||
                url.contains("..") || url.length() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid photo URL");
        }
        if (!new com.vbuilds.dao.BuildDAO().updateMainPhoto(id, build.getOwnerID(), url))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Build not found");
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

        String partLink = part.getPartLink();
        if (partLink == null || partLink.isBlank()) {
            part.setPartLink(null);
            return;
        }

        partLink = partLink.strip();
        try {
            URI uri = URI.create(partLink);
            if (partLink.length() <= 200 && uri.getHost() != null &&
                    ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))) {
                part.setPartLink(partLink);
                return;
            }
        } catch (IllegalArgumentException ignored) {
            // Invalid URI syntax uses the same response as an unsupported URL.
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Part link must be an HTTP or HTTPS URL of at most 200 characters");
    }

    @PutMapping("/{id}/parts/{partId}")
    public void updateBuildPart(@PathVariable int id, @PathVariable int partId, @RequestBody Part part, HttpSession session) throws SQLException {

        requireOwnedBuild(id, session);
        validatePart(part);
        part.setPartID(partId);
        if (!new com.vbuilds.dao.PartDAO().updateBuildPart(id, part))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Part not found on this build");
    }

    @DeleteMapping("/{id}/parts/{partId}")
    public void deleteBuildPart(@PathVariable int id, @PathVariable int partId, HttpSession session) throws SQLException {

        requireOwnedBuild(id, session);
        if (!new com.vbuilds.dao.PartDAO().deleteBuildPart(id, partId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Part not found on this build");
    }

    @PostMapping("/{id}/parts")
    public void addBuildPart(@PathVariable int id, @RequestBody Part part, HttpSession session) throws SQLException {

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
