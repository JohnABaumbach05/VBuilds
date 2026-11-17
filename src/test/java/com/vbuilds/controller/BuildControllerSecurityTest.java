package com.vbuilds.controller;

import com.vbuilds.dto.UpdateBuildRequest;
import com.vbuilds.model.Build;
import com.vbuilds.model.Part;
import com.vbuilds.service.BuildService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuildControllerSecurityTest {
    private BuildService service;
    private BuildController controller;
    private HttpSession session;
    private Build owned;

    @BeforeEach
    void setup() throws SQLException {
        service = mock(BuildService.class);
        session = mock(HttpSession.class);
        controller = new BuildController(service);
        owned = new Build();
        owned.setBuildId(42);
        owned.setOwnerID(7);
        owned.setMainPhoto("/uploads/builds/original.jpg");
        owned.setApproved(true);
        owned.setFeatured(true);
        owned.setTitle("Original");
        when(service.getBuildForEditing(42)).thenReturn(owned);
        when(session.getAttribute("userId")).thenReturn(7);
    }

    private UpdateBuildRequest validRequest() {
        UpdateBuildRequest r = new UpdateBuildRequest();
        r.setTitle("  Updated WRX  ");
        r.setBuildYear(2022);
        r.setTrim("Premium");
        r.setHorsepower(350);
        r.setCost(25000);
        r.setHappiness(8);
        r.setReliability(7);
        r.setLoudness(6);
        r.setTuner("dmann");
        r.setDescription("Updated");
        return r;
    }

    private void status(HttpStatus expected, Executable operation) {
        ResponseStatusException e = assertThrows(ResponseStatusException.class, operation);
        assertEquals(expected, e.getStatusCode());
    }

    @Test void updateValidBuildPreservesProtectedFields() throws Exception {
        controller.updateBuild(42, validRequest(), session);
        assertEquals("Updated WRX", owned.getTitle());
        assertEquals(350, owned.getHorsepower());
        assertEquals(25000, owned.getCost());
        assertEquals(7, owned.getOwnerID());
        assertEquals(42, owned.getBuildId());
        assertTrue(owned.isApproved());
        assertTrue(owned.isFeatured());
        assertEquals("/uploads/builds/original.jpg", owned.getMainPhoto());
        verify(service).updateBuild(same(owned));
    }

    @Test void unauthenticatedUpdateRejected() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        status(HttpStatus.UNAUTHORIZED, () -> controller.updateBuild(42, validRequest(), session));
        verify(service, never()).updateBuild(any());
    }

    @Test void otherUserCannotUpdate() throws Exception {
        when(session.getAttribute("userId")).thenReturn(99);
        status(HttpStatus.FORBIDDEN, () -> controller.updateBuild(42, validRequest(), session));
        verify(service, never()).updateBuild(any());
    }

    @Test void nonexistentBuildCannotUpdate() throws Exception {
        when(service.getBuildForEditing(42)).thenReturn(null);
        status(HttpStatus.NOT_FOUND, () -> controller.updateBuild(42, validRequest(), session));
        verify(service, never()).updateBuild(any());
    }

    @Test void invalidUpdateFieldsRejectedWithoutSaving() throws Exception {
        List<java.util.function.Consumer<UpdateBuildRequest>> invalid = List.of(
            r -> r.setTitle(null), r -> r.setTitle("  "),
            r -> r.setBuildYear(null), r -> r.setBuildYear(2021),
            r -> r.setTrim(null), r -> r.setTrim(" "),
            r -> r.setHorsepower(null), r -> r.setHorsepower(-1),
            r -> r.setCost(null), r -> r.setCost(-1),
            r -> r.setHappiness(null), r -> r.setHappiness(-1), r -> r.setHappiness(11),
            r -> r.setReliability(null), r -> r.setReliability(-1), r -> r.setReliability(11),
            r -> r.setLoudness(null), r -> r.setLoudness(-1), r -> r.setLoudness(11)
        );
        for (var mutation : invalid) {
            UpdateBuildRequest r = validRequest();
            mutation.accept(r);
            status(HttpStatus.BAD_REQUEST, () -> controller.updateBuild(42, r, session));
        }
        verify(service, never()).updateBuild(any());
    }

    @Test void optionalTextFieldsCanBeNull() throws Exception {
        UpdateBuildRequest r = validRequest();
        r.setTuner(null);
        r.setDescription(null);
        controller.updateBuild(42, r, session);
        assertEquals("", owned.getTuner());
        assertEquals("", owned.getDescription());
    }

    @Test void anonymousUserCannotDelete() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        status(HttpStatus.UNAUTHORIZED, () -> controller.deleteBuild(42, session));
        verify(service, never()).deleteBuild(anyInt());
    }

    @Test void otherUserCannotDelete() throws Exception {
        when(session.getAttribute("userId")).thenReturn(99);
        status(HttpStatus.FORBIDDEN, () -> controller.deleteBuild(42, session));
        verify(service, never()).deleteBuild(anyInt());
    }

    @Test void ownerCanDelete() throws Exception {
        controller.deleteBuild(42, session);
        verify(service).deleteBuild(42);
    }

    @Test void otherUserCannotViewPrivateEditEndpoint() throws Exception {
        when(session.getAttribute("userId")).thenReturn(99);
        status(HttpStatus.FORBIDDEN, () -> controller.getBuildForEditing(42, session));
    }

    @Test void anonymousUserCannotViewPrivateEditEndpoint() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        status(HttpStatus.UNAUTHORIZED, () -> controller.getBuildForEditing(42, session));
    }

    @Test void ownerCanViewPrivateEditEndpoint() throws Exception {
        assertSame(owned, controller.getBuildForEditing(42, session));
    }

    @Test void otherUserCannotDeletePart() throws Exception {
        when(session.getAttribute("userId")).thenReturn(99);
        status(HttpStatus.FORBIDDEN, () -> controller.deleteBuildPart(42, 8, session));
    }

    @Test void anonymousUserCannotDeletePart() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        status(HttpStatus.UNAUTHORIZED, () -> controller.deleteBuildPart(42, 8, session));
    }

    @Test void otherUserCannotUpdatePart() throws Exception {
        when(session.getAttribute("userId")).thenReturn(99);
        status(HttpStatus.FORBIDDEN, () -> controller.updateBuildPart(42, 8, validPart(), session));
    }

    @Test void anonymousUserCannotUpdatePart() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        status(HttpStatus.UNAUTHORIZED, () -> controller.updateBuildPart(42, 8, validPart(), session));
    }

    @Test void invalidPartRejectedBeforeDatabaseWrite() throws Exception {
        Part p = validPart();
        p.setCategory(" ");
        status(HttpStatus.BAD_REQUEST, () -> controller.updateBuildPart(42, 8, p, session));
    }

    @Test void invalidPartDescriptionsRejected() throws Exception {
        Part p = validPart();
        p.setPartDescription("x".repeat(1001));
        status(HttpStatus.BAD_REQUEST, () -> controller.updateBuildPart(42, 8, p, session));
    }

    @Test void anonymousUserCannotAddPart() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        status(HttpStatus.UNAUTHORIZED, () -> controller.addBuildPart(42, validPart(), session));
    }

    @Test void otherUserCannotAddPart() throws Exception {
        when(session.getAttribute("userId")).thenReturn(99);
        status(HttpStatus.FORBIDDEN, () -> controller.addBuildPart(42, validPart(), session));
    }

    @Test void invalidPhotoPathRejected() throws Exception {
        when(service.getBuildByID(42)).thenReturn(owned);
        status(HttpStatus.BAD_REQUEST, () -> controller.addBuildPhoto(42,
            java.util.Map.of("filePath", "https://evil.example/photo.jpg"), session));
        verify(service, never()).addPhoto(any());
    }

    @Test void otherUserCannotAddPhoto() throws Exception {
        when(session.getAttribute("userId")).thenReturn(99);
        when(service.getBuildByID(42)).thenReturn(owned);
        status(HttpStatus.FORBIDDEN, () -> controller.addBuildPhoto(42,
            java.util.Map.of("filePath", "/uploads/builds/test.jpg"), session));
    }

    @Test void anonymousUserCannotAddPhoto() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        status(HttpStatus.UNAUTHORIZED, () -> controller.addBuildPhoto(42,
            java.util.Map.of("filePath", "/uploads/builds/test.jpg"), session));
    }

    @Test void otherUserCannotChangeMainPhoto() throws Exception {
        when(session.getAttribute("userId")).thenReturn(99);
        status(HttpStatus.FORBIDDEN, () -> controller.updateMainPhoto(42,
            java.util.Map.of("url", "/uploads/builds/test.jpg"), session));
    }

    @Test void invalidMainPhotoUrlRejected() throws Exception {
        status(HttpStatus.BAD_REQUEST, () -> controller.updateMainPhoto(42,
            java.util.Map.of("url", "/uploads/builds/../secret"), session));
    }

    @Test void missingMainPhotoUrlRejected() throws Exception {
        status(HttpStatus.BAD_REQUEST, () -> controller.updateMainPhoto(42,
            java.util.Map.of(), session));
    }

    private Part validPart() {
        Part p = new Part();
        p.setCategory("Intake");
        p.setManufacturer("BMS");
        p.setPartName("Intake");
        p.setPartCost(300);
        p.setPartReliability(8);
        return p;
    }
}
