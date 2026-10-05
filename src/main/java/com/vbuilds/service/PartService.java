package com.vbuilds.service;

import com.vbuilds.dao.PartDAO;
import com.vbuilds.model.Part;

import java.sql.SQLException;
import java.util.List;

public class PartService {
    private PartDAO partDAO;

    public PartService() {
        partDAO = new PartDAO();
    }

    public void addPart(Part part) throws SQLException {
        partDAO.addPart(part);
    }

    public List<Part> getAllParts() throws SQLException {
        return partDAO.getAllParts();
    }

    public void updatePart(Part part) throws SQLException {
        partDAO.updatePart(part);
    }

    public void deletePart(int id) throws SQLException {
        partDAO.deletePart(id);
    }

    public Part getPartById(int id) throws SQLException {
        return partDAO.getPartById(id);
    }

}
