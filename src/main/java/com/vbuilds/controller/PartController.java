package com.vbuilds.controller;

import com.vbuilds.model.Part;
import com.vbuilds.service.PartService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/parts")

public class PartController {

    private PartService partService;

    public PartController() { partService = new PartService(); }

    @PostMapping
    public void addPart(@RequestBody Part part) throws SQLException {
        partService.addPart(part);
    }

    @GetMapping
    public List<Part> getAllParts() throws SQLException {
        return partService.getAllParts();
    }

    @PutMapping("/{id}")
    public void updatePart(@PathVariable int id, @RequestBody Part part) throws SQLException {
        part.setPartID(id);
        partService.updatePart(part);
    }

    @DeleteMapping("/{id}")
    public void deletePart(@PathVariable int id) throws SQLException {
        partService.deletePart(id);
    }

    @GetMapping("/{id}")
    public Part getPartById(@PathVariable int id) throws SQLException {
        return partService.getPartById(id);
    }



}
