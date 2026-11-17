package com.vbuilds.dao;

import com.vbuilds.DATABASEMAINFILE.Database;
import com.vbuilds.model.Build;
import com.vbuilds.model.Part;
import com.vbuilds.model.Photo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs only when VBUILDS_DB_TESTS=true. CI supplies an isolated PostgreSQL service.
 * Never point DB_URL at a real application database when enabling these tests.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostgresDaoIntegrationTest {
    private BuildDAO builds;
    private PartDAO parts;
    private PhotoDAO photos;

    @BeforeAll
    void init() throws Exception {
        org.junit.jupiter.api.Assumptions.assumeTrue("true".equalsIgnoreCase(System.getenv("VBUILDS_DB_TESTS")),
                "Database integration tests require isolated CI PostgreSQL");
        try (Connection c = Database.connect(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE builds (build_id SERIAL PRIMARY KEY, owner_id INTEGER NOT NULL, title TEXT, horsepower INTEGER, happiness INTEGER, reliability INTEGER, loudness INTEGER, build_cost INTEGER, tuner TEXT, build_year INTEGER, build_trim TEXT, featured BOOLEAN DEFAULT FALSE, main_photo TEXT, description TEXT, in_process BOOLEAN DEFAULT TRUE, approved BOOLEAN DEFAULT FALSE, date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            s.execute("CREATE TABLE parts (part_id SERIAL PRIMARY KEY, build_id INTEGER REFERENCES builds(build_id) ON DELETE CASCADE, part_name TEXT, manufacturer TEXT, category TEXT, part_reliability INTEGER, est_power_added INTEGER, part_description TEXT, part_cost INTEGER, makes_louder BOOLEAN)");
            s.execute("CREATE TABLE photos (photoID SERIAL PRIMARY KEY, build_id INTEGER REFERENCES builds(build_id) ON DELETE CASCADE, file_path TEXT, caption TEXT)");
        }
        builds = new BuildDAO();
        parts = new PartDAO();
        photos = new PhotoDAO();
    }

    private Build createBuild(int owner) throws Exception {
        Build b = new Build();
        b.setOwnerID(owner);
        b.setTitle("CI WRX");
        b.setHorsepower(300);
        b.setHappiness(8);
        b.setReliability(7);
        b.setLoudness(6);
        b.setCost(27000);
        b.setTuner("dmann");
        b.setBuildYear(2022);
        b.setTrim("Base");
        b.setMainPhoto("/uploads/builds/initial.jpg");
        b.setDescription("Test build");
        b.setBuildId(builds.addBuild(b));
        return b;
    }

    private Part createPart() {
        Part p = new Part();
        p.setPartName("Intake");
        p.setManufacturer("BMS");
        p.setCategory("Intake");
        p.setPartReliability(8);
        p.setEstPowerAdded(10);
        p.setPartCost(300);
        p.setPartDescription("Test");
        return p;
    }

    @Test void buildCrudAndOwnershipScopedMainPhoto() throws Exception {
        Build b = createBuild(11);
        int id = b.getBuildId();
        try {
            Build read = builds.getBuildByIdForModeration(id);
            assertNotNull(read);
            assertEquals(11, read.getOwnerID());
            assertEquals(27000, read.getCost());
            assertNull(builds.getBuildById(id), "Unapproved builds should not be public");

            assertFalse(builds.updateMainPhoto(id, 999, "/uploads/builds/hacked.jpg"));
            assertEquals("/uploads/builds/initial.jpg", builds.getBuildByIdForModeration(id).getMainPhoto());
            assertTrue(builds.updateMainPhoto(id, 11, "/uploads/builds/new.jpg"));

            b.setTitle("Updated CI WRX");
            b.setMainPhoto("/uploads/builds/new.jpg");
            b.setCost(29000);
            builds.updateBuild(b);
            read = builds.getBuildByIdForModeration(id);
            assertEquals("Updated CI WRX", read.getTitle());
            assertEquals(29000, read.getCost());
            assertEquals("/uploads/builds/new.jpg", read.getMainPhoto());
            assertEquals(11, read.getOwnerID());
            assertFalse(read.isApproved());
        } finally {
            builds.deleteBuild(id);
        }
        assertNull(builds.getBuildByIdForModeration(id));
    }

    @Test void partsCannotBeModifiedAcrossBuilds() throws Exception {
        Build a = createBuild(21), b = createBuild(22);
        try {
            parts.addBuildPart(a.getBuildId(), createPart());
            List<Part> list = parts.getBuildParts(a.getBuildId());
            assertEquals(1, list.size());
            int partId = list.get(0).getPartID();
            assertTrue(parts.getBuildParts(b.getBuildId()).isEmpty());

            Part changed = createPart();
            changed.setPartID(partId);
            changed.setPartName("Unauthorized edit");
            assertFalse(parts.updateBuildPart(b.getBuildId(), changed));
            assertFalse(parts.deleteBuildPart(b.getBuildId(), partId));
            assertEquals("Intake", parts.getPartById(partId).getPartName());

            changed.setPartName("Updated intake");
            assertTrue(parts.updateBuildPart(a.getBuildId(), changed));
            assertEquals("Updated intake", parts.getPartById(partId).getPartName());
            assertTrue(parts.deleteBuildPart(a.getBuildId(), partId));
            assertNull(parts.getPartById(partId));
        } finally {
            builds.deleteBuild(a.getBuildId());
            builds.deleteBuild(b.getBuildId());
        }
    }

    @Test void galleryPhotoRoundTrip() throws Exception {
        Build b = createBuild(31);
        try {
            Photo photo = new Photo();
            photo.setBuildId(b.getBuildId());
            photo.setFilePath("/uploads/builds/gallery.jpg");
            photo.setCaption("Before");
            photos.addPhoto(photo);
            List<Photo> gallery = photos.getPhotosByBuild(b.getBuildId());
            assertEquals(1, gallery.size());
            Photo saved = photos.getPhotoById(gallery.get(0).getPhotoId());
            assertEquals("Before", saved.getCaption());
            saved.setCaption("After");
            photos.updatePhoto(saved);
            assertEquals("After", photos.getPhotoById(saved.getPhotoId()).getCaption());
            photos.deletePhoto(saved.getPhotoId());
            assertTrue(photos.getPhotosByBuild(b.getBuildId()).isEmpty());
        } finally {
            builds.deleteBuild(b.getBuildId());
        }
    }

    @Test void approvedBuildAppearsInPublicQuery() throws Exception {
        Build b = createBuild(41);
        try {
            assertNull(builds.getBuildById(b.getBuildId()));
            try (Connection c = Database.connect(); var ps = c.prepareStatement("UPDATE builds SET approved = TRUE, in_process = FALSE WHERE build_id = ?")) {
                ps.setInt(1, b.getBuildId());
                ps.executeUpdate();
            }
            assertNotNull(builds.getBuildById(b.getBuildId()));
            assertTrue(builds.getApprovedUserBuilds(41).stream().anyMatch(x -> x.getBuildId() == b.getBuildId()));
        } finally {
            builds.deleteBuild(b.getBuildId());
        }
    }
}
