package com.ckidtech.quotation.service.appuser.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ckidtech.quotation.service.appuser.service.AppUserService;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.security.UserRole;
import com.ckidtech.quotation.service.core.utils.Util;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class QuotationControllerAppUser {
	
	private static final Logger LOG = Logger.getLogger(QuotationControllerAppUser.class.getName());
	
	@Autowired
	private AppUserService appUserService;
	
	// Admin service
		
	@RequestMapping(value = "/appadmin/findallappusers")
	public ResponseEntity<Object> adminFindAppUsers(@RequestHeader("authorization") String authorization) throws Exception {
		LOG.log(Level.INFO, "Calling API /appadmin/findallappusers");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.adminFindAllAppUsers(loginUser), HttpStatus.OK);		
	}	
	
	@RequestMapping(value = "/appadmin/searchappusers/{name}")
	public ResponseEntity<Object> adminSearchAppUsers(@RequestHeader("authorization") String authorization,
			@PathVariable("name") String name) throws Exception {
		LOG.log(Level.INFO, "Calling API /appadmin/searchappusers");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.adminSearchAppUsers(loginUser, name), HttpStatus.OK);		
	}	

	@RequestMapping(value = "/appadmin/createappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> adminCreateAppUser(@RequestHeader("authorization") String authorization,
			@RequestBody AppUser appUser) throws Exception {
		LOG.log(Level.INFO, "Calling API /appadmin/createappuser:" + appUser + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.addAppUser(loginUser, appUser), HttpStatus.CREATED);		
	}	
	
	@RequestMapping(value = "/appadmin/updateappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> adminUpdateAppUser(@RequestHeader("authorization") String authorization,
			@RequestBody AppUser appUser) throws Exception {
		LOG.log(Level.INFO, "Calling API /appadmin/updateappuser:" + appUser + ")");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.updateAppUser(loginUser, appUser), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appadmin/generatePassword/{appuserid}")
	public ResponseEntity<Object> adminGeneratePassword(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /appadmin/generatePassword/" + appuserid + ")");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.generatePassword(loginUser, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appadmin/deleteappuser/{appuserid}")
	public ResponseEntity<Object> adminDeleteAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /appadmin/deleteappuser/" + appuserid + ")");		
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.deleteAppUser(loginUser, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appadmin/activateappuser/{appuserid}")
	public ResponseEntity<Object> adminActivateAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /appadmin/activateappuser:" + appuserid + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.activateAppUser(loginUser, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appadmin/deactivateappuser/{appuserid}")
	public ResponseEntity<Object> adminDeActivateAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /appadmin/deactivateappuser:" + appuserid + ")");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.deActivateAppUser(loginUser, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appadmin/changepassword/{oldpassword}/{newpassword}")
	public ResponseEntity<Object> adminChangePassword(@RequestHeader("authorization") String authorization,
			@PathVariable("oldpassword") String oldpassword,
			@PathVariable("newpassword") String newpassword) throws Exception {
		LOG.log(Level.INFO, "Calling API /appadmin/changepassword");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.changePassword(loginUser, oldpassword, newpassword), HttpStatus.OK);		
	}
	
	// Vendor Services
	
	@RequestMapping(value = "/vendoradmin/findallappusers")
	public ResponseEntity<Object> vendorFindAppUsers(@RequestHeader("authorization") String authorization) throws Exception {
		LOG.log(Level.INFO, "Calling API /vendoradmin/findallappusers");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.vendorFindAllAppUsers(loginUser), HttpStatus.OK);		
	}	
	
	@RequestMapping(value = "/vendoradmin/searchappusers/{name}")
	public ResponseEntity<Object> vendorSearchAppUsers(@RequestHeader("authorization") String authorization,
			@PathVariable("name") String name) throws Exception {
		LOG.log(Level.INFO, "Calling API /vendoradmin/searchappusers");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.vendorSearchAppUsers(loginUser, name), HttpStatus.OK);		
	}	
	
	@RequestMapping(value = "/vendoradmin/createappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> vendorCreateAppUser(@RequestHeader("authorization") String authorization,
			@RequestBody AppUser appUser) throws Exception {
		LOG.log(Level.INFO, "Calling API /vendoradmin/createappuser/" + appUser + ")");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, appUser.getObjectRef());		
		return new ResponseEntity<Object>(appUserService.addAppUser(loginUser, appUser), HttpStatus.CREATED);		
	}	
	
	@RequestMapping(value = "/vendoradmin/updateappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> vendorUpdateAppUser(@RequestHeader("authorization") String authorization,
			@RequestBody AppUser appUser) throws Exception {
		LOG.log(Level.INFO, "Calling API /vendoradmin/updateappuser/" + appUser + ")");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, appUser.getObjectRef());	
		return new ResponseEntity<Object>(appUserService.updateAppUser(loginUser, appUser), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendoradmin/generatePassword/{appuserid}")
	public ResponseEntity<Object> vendorGeneratePassword(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /vendoradmin/generatePassword/" + appuserid + ")");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.generatePassword(loginUser, appuserid), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/vendoradmin/deleteappuser/{appuserid}")
	public ResponseEntity<Object> vendorDeleteAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /vendoradmin/deleteappuser/" + appuserid + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);	
		return new ResponseEntity<Object>(appUserService.deleteAppUser(loginUser, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendoradmin/activateappuser/{appuserid}")
	public ResponseEntity<Object> vendorActivateAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /vendoradmin/activateappuser/" + appuserid + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.activateAppUser(loginUser, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendoradmin/deactivateappuser/{appuserid}")
	public ResponseEntity<Object> vendorDeActivateAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /vendoradmin/deactivateappuser/" + appuserid + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.deActivateAppUser(loginUser, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendoradmin/changepassword/{oldpassword}/{newpassword}")
	public ResponseEntity<Object> vendorChangePassword(@RequestHeader("authorization") String authorization,
			@PathVariable("oldpassword") String oldpassword,
			@PathVariable("newpassword") String newpassword) throws Exception {
		LOG.log(Level.INFO, "Calling API /vendoradmin/changepassword/");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(appUserService.changePassword(loginUser, oldpassword, newpassword), HttpStatus.OK);		
	}
	
	// Vendor Users
	
	@RequestMapping(value = "/vendoruser/changepassword/{oldpassword}/{newpassword}")
	public ResponseEntity<Object> userChangePassword(@RequestHeader("authorization") String authorization,
			@PathVariable("oldpassword") String oldpassword,
			@PathVariable("newpassword") String newpassword) throws Exception {
		LOG.log(Level.INFO, "Calling API /vendoruser/changepassword/");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_USER, null);
		return new ResponseEntity<Object>(appUserService.changePassword(loginUser, oldpassword, newpassword), HttpStatus.OK);		
	}
			
}
