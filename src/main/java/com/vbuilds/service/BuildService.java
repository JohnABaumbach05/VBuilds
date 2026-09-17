package com.vbuilds.service;

import com.vbuilds.dao.BuildDAO;
import com.vbuilds.dao.PartDAO;
import com.vbuilds.dao.PhotoDAO;
import com.vbuilds.model.Build;
import com.vbuilds.model.Photo;
import com.vbuilds.model.parts;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class BuildService {
    private BuildDAO buildDAO;
    private PhotoDAO photoDAO;
    private PartDAO partDAO;

    public BuildService() throws SQLException {
        buildDAO = new BuildDAO();
        photoDAO = new PhotoDAO();
        partDAO = new PartDAO();
    }

    public List<Build> getUserBuilds(int id) throws SQLException {
        return buildDAO.getUserBuilds(id);
    }

    public List<Build> getAllBuilds() throws SQLException {
        return buildDAO.getAllBuilds();
    }

    public void addBuild(Build build) throws SQLException {
        buildDAO.addBuild(build);
    }

    public void updateBuild(Build build) throws SQLException {
        buildDAO.updateBuild(build);
    }

    public Build getBuildByID(int id) throws SQLException {
        return buildDAO.getBuildById(id);
    }

    public void deleteBuild(int id) throws SQLException {
        buildDAO.deleteBuild(id);
    }

    public Build getRandomBuild() throws SQLException {
        List<Build> builds = buildDAO.getAllBuilds();
        Random random = new Random();

        if (builds.isEmpty()) {
            return null;
        }

        int randomNum = random.nextInt(builds.size());

        return builds.get(randomNum);

    }

    public List<Build> getTopHorsePowerBuilds() throws SQLException {
        return buildDAO.getTopHorsepowerBuilds();
    }

    public List<Build> getTopHorsePowerBuilds(int i) throws SQLException {
        if (i <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        return buildDAO.getTopHorsepowerBuilds(i);
    }

    public List<parts> getBuildParts(int buildID) throws SQLException {
        return partDAO.getBuildParts(buildID);
    }

    public List<Photo> getBuildPhotos(int id) throws SQLException {
        return photoDAO.getPhotosByBuild(id);
    }

    public List<Build> getFeaturedBuilds() throws SQLException {
        return buildDAO.getFeaturedBuilds();
    }

    public List<Build> getRandomBuilds() throws SQLException {
        return buildDAO.getRandomBuilds();
    }

    public List<Build> getNewestBuilds() throws SQLException {
        return buildDAO.getNewestBuilds();
    }

}
