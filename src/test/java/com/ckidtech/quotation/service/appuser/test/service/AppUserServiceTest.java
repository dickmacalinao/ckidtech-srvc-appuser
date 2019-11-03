package com.ckidtech.quotation.service.appuser.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ckidtech.quotation.service.appuser.service.AppUserService;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.dao.VendorRepository;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.ReturnMessage;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.security.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
@ComponentScan({"com.ckidtech.quotation.service.appuser.service"})
@AutoConfigureDataMongo
public class AppUserServiceTest {
	
	@Autowired	
	VendorRepository vendorRepository;
	
	@Autowired
	AppUserService appUserService;
	
	public static AppUser MAIN_ADMIN = new AppUser("MAIN_ADMIN", "testpass", "Administrator", UserRole.APP_ADMIN, "VendorHub", "");
	public static AppUser ADMIN_USER = new AppUser("ADMIN", "testpass", "Administrator", UserRole.APP_ADMIN, "VendorHub", "");
	public static Vendor TEST_VENDOR = new Vendor("TEST_VENDOR", "Test Vendor", "imagelink", 2, 2, 2);
	public static Vendor TEST_VENDOR1 = new Vendor("TEST_VENDOR1", "Test Vendor 1", "imagelink", 2, 2, 2);
	public static Vendor TEST_VENDOR2 = new Vendor("TEST_VENDOR2", "Test Vendor 2", "imagelink", 2, 2, 2);
	
	@Before
	public  void initTest() {		
		
		MAIN_ADMIN.setActiveIndicator(true);
		
		vendorRepository.deleteAll();
		
		TEST_VENDOR.setActiveIndicator(true);
		vendorRepository.save(TEST_VENDOR);
		
		TEST_VENDOR1.setActiveIndicator(true);
		vendorRepository.save(TEST_VENDOR1);
		
		TEST_VENDOR2.setActiveIndicator(true);
		vendorRepository.save(TEST_VENDOR2);
		
		appUserService.deleteAllAppUser();
				
	}
	

	@Test
	public void adminFindAllAppUsersTest() {
		
		
		QuotationResponse response = appUserService.addAppUser(MAIN_ADMIN, ADMIN_USER);
		assertTrue("User created.", response.getMessages().contains(new ReturnMessage("User created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		response = appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_ADMIN", "password", "Vendor Admin", UserRole.VENDOR_ADMIN, "VendorHub", "TEST_VENDOR1"));
		assertTrue("User created.", response.getMessages().contains(new ReturnMessage("User created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		response = appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_USER", "password", "Vendor User", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR1"));
		assertTrue("User created.", response.getMessages().contains(new ReturnMessage("User created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		List<AppUser> allAppUser = appUserService.adminFindAllAppUsers(MAIN_ADMIN);
		assertEquals(3, allAppUser.size());
		
	}
	
	@Test
	public void adminSearchAppUsersTest() {
		
		QuotationResponse response = appUserService.addAppUser(MAIN_ADMIN, ADMIN_USER);
		response = appUserService.activateAppUser(MAIN_ADMIN, ADMIN_USER.getId());	
		ADMIN_USER = response.getAppUser();
		
		appUserService.addAppUser(ADMIN_USER, new AppUser("TEST_VENDOR1_ADMIN", "password", "Vendor Admin", UserRole.VENDOR_ADMIN, "VendorHub", "TEST_VENDOR1"));
		appUserService.addAppUser(ADMIN_USER, new AppUser("TEST_VENDOR1_USER", "password", "Vendor User", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR1"));
		
		List<AppUser> allAppUser = appUserService.adminSearchAppUsers(MAIN_ADMIN, "Vendor");
		assertEquals(2, allAppUser.size());
	}
	
	
	@Test
	public void vendorFindAllAppUsersTest() {		
		
		AppUser appUserVendor1Admin = new AppUser("TEST_VENDOR1_ADMIN", "password", "Vendor 1 Admin", UserRole.VENDOR_ADMIN, "VendorHub", TEST_VENDOR1.getId());
		appUserVendor1Admin = appUserService.addAppUser(MAIN_ADMIN, appUserVendor1Admin).getAppUser();
		appUserVendor1Admin = appUserService.activateAppUser(MAIN_ADMIN, appUserVendor1Admin.getId()).getAppUser();
		appUserService.addAppUser(appUserVendor1Admin, new AppUser("TEST_VENDOR1_USER", "password", "Vendor User", UserRole.VENDOR_USER, "VendorHub", TEST_VENDOR1.getId()));
		
		
		AppUser appUserVendor2Admin = new AppUser("TEST_VENDOR2_ADMIN", "password", "Vendor 2 Admin", UserRole.VENDOR_ADMIN, "VendorHub", TEST_VENDOR2.getId());
		appUserVendor2Admin = appUserService.addAppUser(MAIN_ADMIN, appUserVendor2Admin).getAppUser();
		appUserVendor2Admin = appUserService.activateAppUser(MAIN_ADMIN, appUserVendor2Admin.getId()).getAppUser();
		appUserService.addAppUser(appUserVendor2Admin, new AppUser("TEST_VENDOR2_USER", "password", "Vendor User", UserRole.VENDOR_USER, "VendorHub",  TEST_VENDOR2.getId()));
		
		List<AppUser> allAppUser1 = appUserService.vendorFindAllAppUsers(appUserVendor1Admin);
		assertEquals(1, allAppUser1.size());
		
		List<AppUser> allAppUser2 = appUserService.vendorFindAllAppUsers(appUserVendor2Admin);
		assertEquals(1, allAppUser2.size());
	}
	
	
	@Test
	public void vendorSearchAppUsersTest() {
		
		AppUser appUserVendor1Admin = new AppUser("TEST_VENDOR1_ADMIN", "password", "Vendor Admin", UserRole.VENDOR_ADMIN, "VendorHub", "TEST_VENDOR1");
		
		QuotationResponse response = appUserService.addAppUser(MAIN_ADMIN, appUserVendor1Admin);
		appUserVendor1Admin = response.getAppUser();
		
		response = appUserService.activateAppUser(MAIN_ADMIN, appUserVendor1Admin.getId());
		appUserVendor1Admin =  response.getAppUser();
		
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_USER", "password", "Vendor User", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR1"));
		
		AppUser appUserVendor2Admin = new AppUser("TEST_VENDOR2_ADMIN", "password", "Vendor Admin", UserRole.VENDOR_ADMIN, "VendorHub",  "TEST_VENDOR2");		
		appUserVendor2Admin = appUserService.addAppUser(MAIN_ADMIN, appUserVendor2Admin).getAppUser();
		appUserVendor2Admin = appUserService.activateAppUser(MAIN_ADMIN, appUserVendor2Admin.getId()).getAppUser();
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR2_USER1", "password", "Vendor User 1", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR2"));
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR2_USER2", "password", "Vendor User 2", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR2"));
		
		
		List<AppUser> allAppUser1 = appUserService.vendorSearchAppUsers(appUserVendor1Admin, "Vendor");
		assertEquals(1, allAppUser1.size());
		
		List<AppUser> allAppUser2 = appUserService.vendorSearchAppUsers(appUserVendor2Admin, "Vendor");
		assertEquals(2, allAppUser2.size());
	}
	
	@Test
	public void addAppUserTest() {	
		
		// Missing mandatory fields
		QuotationResponse response = appUserService.addAppUser(MAIN_ADMIN, new AppUser("", "", "", null, "", ""));	
		assertEquals(5, response.getMessages().size());
		assertTrue("User Name is required.", response.getMessages().contains(new ReturnMessage("User Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Password is required.", response.getMessages().contains(new ReturnMessage("Password is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Name is required.", response.getMessages().contains(new ReturnMessage("Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Role is required.", response.getMessages().contains(new ReturnMessage("Role is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Object Reference is required.", response.getMessages().contains(new ReturnMessage("Object Reference is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		
		// Mandatory Vendor ID scenario
		response = appUserService.addAppUser(MAIN_ADMIN, new AppUser(TEST_VENDOR1 + "_ADMIN", "password", "Vendor Admin", UserRole.VENDOR_ADMIN, "VendorHub",  ""));
		assertTrue("Object Reference is required.", response.getMessages().contains(new ReturnMessage("Object Reference is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		
		// Successful Test
		response = appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR_ADMIN", "password", "Vendor Admin", UserRole.VENDOR_ADMIN, "VendorHub", "TEST_VENDOR1"));

		assertTrue("User created.", response.getMessages().contains(new ReturnMessage("User created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		AppUser appUser = response.getAppUser();
		assertNotEquals(null, appUser.getId());
		assertEquals("TEST_VENDOR_ADMIN", appUser.getUsername());
		assertEquals("Vendor Admin", appUser.getName());
		assertEquals("TEST_VENDOR1", appUser.getObjectRef());
		assertEquals(UserRole.VENDOR_ADMIN, appUser.getRole());
		
		// Duplicate Test scenario
		response = appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR_ADMIN", "password", "Vendor Admin", UserRole.VENDOR_ADMIN, "VendorHub", "TEST_VENDOR1"));	
		assertTrue("User already exists.", response.getMessages().contains(new ReturnMessage("User already exists.", ReturnMessage.MessageTypeEnum.ERROR)));	
				
	}
	
	
	@Test
	public void updateAppUserTest() {
		
		// User not found
		QuotationResponse response = appUserService.updateAppUser(MAIN_ADMIN, new AppUser("NOT_FOUND_ID", "TEST_VENDOR1_ADMIN", "password", "Vendor Admin New", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR2"));
		assertTrue("User not found.", response.getMessages().contains(new ReturnMessage("User not found.", ReturnMessage.MessageTypeEnum.ERROR)));
		
		// Successful update
		addAppUserTest();
		for ( AppUser appUser : appUserService.adminSearchAppUsersByUserName("TEST_VENDOR_ADMIN") ) {
			
			response = appUserService.updateAppUser(MAIN_ADMIN, new AppUser(appUser.getId(), "TEST_VENDOR_ADMIN", "password", "Vendor Admin New", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR2"));
			assertTrue("User updated.", response.getMessages().contains(new ReturnMessage("User updated.", ReturnMessage.MessageTypeEnum.INFO)));
			
			AppUser updatedAppUser = response.getAppUser();
			assertEquals(appUser.getId(), updatedAppUser.getId());
			assertEquals("TEST_VENDOR_ADMIN", updatedAppUser.getUsername());
			assertEquals("Vendor Admin New", updatedAppUser.getName());
			assertEquals("TEST_VENDOR2", updatedAppUser.getObjectRef());
			assertEquals(UserRole.VENDOR_USER, updatedAppUser.getRole());
			assertEquals("VendorHub", updatedAppUser.getApp());
		}
		
	}

	
	@Test
	public void deleteAppUserTest() {
		
		// Successful delete		
		AppUser appUserVendor1Admin = new AppUser("TEST_VENDOR1_ADMIN", "password", "Vendor 1 Admin", UserRole.VENDOR_ADMIN, "VendorHub", "TEST_VENDOR1");
		appUserVendor1Admin = appUserService.addAppUser(MAIN_ADMIN, appUserVendor1Admin).getAppUser();
		appUserVendor1Admin = appUserService.activateAppUser(MAIN_ADMIN, appUserVendor1Admin.getId()).getAppUser();
		
		AppUser appUserVendor1User = new AppUser("TEST_VENDOR1_USER", "password", "Vendor 1 User", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR1");		
		appUserVendor1User = appUserService.addAppUser(MAIN_ADMIN, appUserVendor1User).getAppUser();
		
		
		AppUser appUserVendor2Admin = new AppUser("TEST_VENDOR2_ADMIN", "password", "Vendor 2 Admin", UserRole.VENDOR_ADMIN, "VendorHub", "TEST_VENDOR2");
		appUserVendor2Admin = appUserService.addAppUser(MAIN_ADMIN, appUserVendor2Admin).getAppUser();
		appUserVendor2Admin = appUserService.activateAppUser(MAIN_ADMIN, appUserVendor2Admin.getId()).getAppUser();
				
		AppUser appUserVendor2User = new AppUser("TEST_VENDOR2_USER", "password", "Vendor 2 User", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR2");		
		appUserVendor2User = appUserService.addAppUser(MAIN_ADMIN, appUserVendor2User).getAppUser();
		
		
		List<AppUser> allAppUser1 = appUserService.vendorFindAllAppUsers(appUserVendor1Admin);
		assertEquals(1, allAppUser1.size());
		
		appUserService.deleteAllAppUser(MAIN_ADMIN, "TEST_VENDOR1");
		
		allAppUser1 = appUserService.vendorFindAllAppUsers(appUserVendor1Admin);
		assertEquals(0, allAppUser1.size());
		
		List<AppUser> allAppUser2 = appUserService.vendorFindAllAppUsers(appUserVendor2Admin);
		assertEquals(1, allAppUser2.size());
		
	}
	
	@Test
	public void activateAppUserTest() {
		
		AppUser appUserVendorAdmin = new AppUser("TEST_VENDOR1_ADMIN", "password", "Vendor 1 Admin", UserRole.VENDOR_ADMIN, "VendorHub", "TEST_VENDOR");
		
		QuotationResponse response = appUserService.addAppUser(MAIN_ADMIN, appUserVendorAdmin);appUserVendorAdmin = response.getAppUser();
		appUserVendorAdmin = appUserService.activateAppUser(MAIN_ADMIN, appUserVendorAdmin.getId()).getAppUser();
		
		AppUser appUserVendorUser = new AppUser("TEST_VENDOR1_USER", "password", "Vendor 2 User", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR");	
		
		response = appUserService.addAppUser(MAIN_ADMIN, appUserVendorUser);
		appUserVendorUser = response.getAppUser();
		
		
		assertEquals(false, appUserVendorUser.isActiveIndicator());
		
		appUserVendorUser = appUserService.activateAppUser(appUserVendorAdmin, appUserVendorUser.getId()).getAppUser();
		
		assertEquals(true, appUserVendorUser.isActiveIndicator());
		
	}
	
	@Test
	public void deActivateAppUserTest() {
		
		activateAppUserTest();
		
		for (AppUser appUser : appUserService.adminSearchAppUsersByUserName("TEST_VENDOR1_USER")) {
			AppUser appUserToDeactivate = appUserService.deActivateAppUser(MAIN_ADMIN, appUser.getId()).getAppUser();			
			assertEquals(false, appUserToDeactivate.isActiveIndicator());
		}
		
	}
	
	@Test
	public void deActivateAllAppUserToOneVendorOnlyTest() {
		
		// Create and activate a user under Vendor 1
		AppUser appUser1 = appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_USER", "password", "Vendor User", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR1")).getAppUser();
		appUser1 = appUserService.activateAppUser(MAIN_ADMIN, appUser1.getId()).getAppUser();
		assertEquals(true, appUser1.isActiveIndicator());
		
		// Create and activate a user under Vendor 2
		AppUser appUser2 = appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR2_USER", "password", "Vendor User", UserRole.VENDOR_USER, "VendorHub", "TEST_VENDOR2")).getAppUser();
		appUser2 = appUserService.activateAppUser(MAIN_ADMIN, appUser2.getId()).getAppUser();
		assertEquals(true, appUser2.isActiveIndicator());
		
		
		appUserService.deActivateAllAppUser(MAIN_ADMIN, "TEST_VENDOR1");
		
		//System.out.println("appUser1:" + appUser1);
		
		// Vendor 1 user should be inactive 
		for (AppUser appUser : appUserService.adminSearchAppUsersByUserName("TEST_VENDOR1_USER")) {
			//System.out.println("asppuser:" + appUser);
			assertEquals(false, appUser.isActiveIndicator());
		}
		 
		// Vendor 2 user should be still active 
		for (AppUser appUser : appUserService.adminSearchAppUsersByUserName("TEST_VENDOR2_USER")) {
			assertEquals(true, appUser.isActiveIndicator());
		}
		
	}
	
}
