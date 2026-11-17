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



    @GetMapping
    public List<Part> getAllParts() throws SQLException {
        return partService.getAllParts();
    }

    @GetMapping("/{id}")
    public Part getPartById(@PathVariable int id) throws SQLException {
        return partService.getPartById(id);
    }



}
