package com.vbuilds.service;

import com.vbuilds.dao.PartDAO;
import com.vbuilds.model.parts;

import java.sql.SQLException;
import java.util.List;

public class PartService {
    private PartDAO partDAO;

    public PartService() {
        partDAO = new PartDAO();
    }

    public void addPart(parts part) throws SQLException {
        partDAO.addPart(part);
    }

    public List<parts> getAllParts() throws SQLException {
        return partDAO.getAllParts();
    }

    public void updatePart(parts part) throws SQLException {
        partDAO.updatePart(part);
    }

    public void deletePart(int id) throws SQLException {
        partDAO.deletePart(id);
    }

    public parts getPartById(int id) throws SQLException {
        return partDAO.getPartById(id);
    }

}
