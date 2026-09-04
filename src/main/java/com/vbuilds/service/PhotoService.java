package com.vbuilds.service;

import com.vbuilds.dao.BuildDAO;
import com.vbuilds.dao.PhotoDAO;
import com.vbuilds.dao.UserDAO;
import com.vbuilds.model.Photo;

import java.sql.SQLException;
import java.util.List;

public class PhotoService {
    private BuildDAO buildDAO;
    private UserDAO userDAO;
    private PhotoDAO photoDAO;

    public PhotoService() {
        buildDAO = new BuildDAO();
        userDAO = new UserDAO();
        photoDAO = new PhotoDAO();
    }

    public void addPhoto(Photo photo) throws SQLException {
        photoDAO.addPhoto(photo);
    }

    public void deletePhoto(int id) throws SQLException {
        photoDAO.deletePhoto(id);
    }

    public void updatePhoto(Photo photo) throws SQLException {
        photoDAO.updatePhoto(photo);
    }

    public Photo getPhotoById(int id) throws SQLException {
        return photoDAO.getPhotoById(id);
    }

    public List<Photo> getPhotosByBuild(int id) throws SQLException {
        return photoDAO.getPhotosByBuild(id);
    }
}
