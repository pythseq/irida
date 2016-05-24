package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Integration tests for testing out Announcements
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
        IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
        WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/AnnouncementServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnnouncementServiceImplIT {

    @Autowired
    private AnnouncementService announcementService;
    @Autowired
    private UserService userService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateAnnouncementAsAdmin() {
        Announcement an = new Announcement("This is a message");
        try {
            announcementService.create(an);
        } catch (AccessDeniedException e) {
            fail("Admin should be able to create a new announcement.");
        } catch (Exception e) {
            fail("Failed for unknown reason, stack trace follows: ");
            e.printStackTrace();
        }

    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "user", roles = "USER")
    public void testCreateAnnouncementNotAdmin() {
        announcementService.create(new Announcement("This is a message"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteAnnouncementAsAdminSuccess() {
        try {
            announcementService.delete(1L);
        } catch (EntityNotFoundException e) {
            fail("Admin trying to delete announcement that doesn't exist.");
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testDeleteAnnouncementAsUserFail() {

    }

    @Test (expected = EntityNotFoundException.class)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteAnnouncementNotExists() {
        announcementService.delete(100L);
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testUserMarkAnnouncementAsReadSuccess() {
        Announcement a = announcementService.read(2L);
        try {
            announcementService.markAnnouncementAsReadByUser(a);
        } catch (AccessDeniedException e) {
            fail("User should be able able to mark announcement as read.");
        } catch (EntityExistsException e) {
            fail("User can't mark announcement as read more than one time");
        }

    }

    @Test
    public void testUserMarkAnnouncementAsReadFailed() {

    }

    @Test
    public void testUserUnmarkAnnouncementAsReadSuccess() {

    }

    @Test
    public void testUserUnmarkAnnouncementAsReadFailed() {

    }

    @Test
    public void testGetAllAnnouncements() {

    }

    @Test
    public void testGetAllUnreadAnnouncementsforUser() {

    }

}
