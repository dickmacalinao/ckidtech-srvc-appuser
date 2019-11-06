package com.ckidtech.quotation.service.appuser.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ckidtech.quotation.service.appuser.util.PasswordGenerator;
import com.ckidtech.quotation.service.core.controller.MessageController;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.dao.AppUserRepository;
import com.ckidtech.quotation.service.core.dao.VendorRepository;
import com.ckidtech.quotation.service.core.exception.ServiceAccessResourceFailureException;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.security.UserRole;
import com.ckidtech.quotation.service.core.utils.Util;

@ComponentScan({"com.ckidtech.quotation.service.core.controller"})
@EnableMongoRepositories ("com.ckidtech.quotation.service.core.dao")
@Service
public class AppUserService {

	private static final Logger LOG = Logger.getLogger(AppUserService.class.getName());

	@Autowired
	private AppUserRepository appUserRepository;
	
	@Autowired
	private VendorRepository vendorRepository;
	
	@Autowired
	private MessageController msgController;
	
	/**
	 * Get App User by ID
	 * @param id
	 * @return
	 */
	public AppUser getAppUserById(String id) {
		LOG.log(Level.INFO, "Calling Vendor Service getAppUserById()");
		return appUserRepository.findById(id).orElse(null);
	}
	
	/**
	 * View all AppUser records
	 * 
	 * @return
	 */
	public List<AppUser> adminFindAllAppUsers(AppUser loginUser) {

		LOG.log(Level.INFO, "Calling AppUser Service adminFindAllAppUsers()");		
		Pageable pageable = new PageRequest(0, 1000, Sort.Direction.ASC, "role, name");
		List<AppUser> listAppUser = appUserRepository.adminSearchByName(loginUser.getApp(), "", pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	/**
	 * Use by Admin to search App User
	 * @param name
	 * @return
	 */
	public List<AppUser> adminSearchAppUsers(AppUser loginUser, String name) {

		LOG.log(Level.INFO, "Calling AppUser Service adminSearchAppUsers()");	
		Pageable pageable = new PageRequest(0, 1000, Sort.Direction.ASC, "vendor, role, name");
		List<AppUser> listAppUser = appUserRepository.adminSearchByName(loginUser.getApp(), name, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	public List<AppUser> adminSearchAppUsersByUserName(String username) {

		LOG.log(Level.INFO, "Calling AppUser Service adminSearchAppUsers()");	
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "vendor, role, name");
		List<AppUser> listAppUser = appUserRepository.adminFindByUserName(username, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	/**
	 * List all App User
	 * @param loginUser - Currently login user
	 * @return
	 */
	public List<AppUser> vendorFindAllAppUsers(AppUser loginUser) {

		LOG.log(Level.INFO, "Calling AppUser Service vendorFindAllAppUsers()");	
		
		Util.checkIfAlreadyActivated(loginUser);
		
		Pageable pageable = new PageRequest(0, 1000, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorFindAllAppUsers(loginUser.getApp(), loginUser.getObjectRef(), pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	/**
	 * Method to search App User used by Vendor Admin
	 * @param loginUser - Currently login user
	 * @param name
	 * @return
	 */
	public List<AppUser> vendorSearchAppUsers(AppUser loginUser, String name) {

		LOG.log(Level.INFO, "Calling AppUser Service vendorSearchAppUsers()");	
		
		Util.checkIfAlreadyActivated(loginUser);
		
		Pageable pageable = new PageRequest(0, 1000, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorSearchByName(loginUser.getApp(), loginUser.getObjectRef(), name, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	/**
	 * Create new AppUser
	 * @param loginUser - Currently login user
	 * @param appUser - App User object
	 * @return
	 */
	public QuotationResponse addAppUser(AppUser loginUser, AppUser appUser) {		
		
		LOG.log(Level.INFO, "Calling AppUser Service addAppUser()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();
		
		// Validate mandatory fields
		if ( appUser.getUsername()==null || "".equals(appUser.getUsername()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "User Name"));
		if ( appUser.getName()==null || "".equals(appUser.getName()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Name"));				
		if ( appUser.getRole()==null )
			quotation.addMessage(msgController.createMsg("error.MFE", "Role"));
		if ( UserRole.APP_ADMIN.equals(loginUser.getRole()) && !UserRole.APP_ADMIN.equals(appUser.getRole()) ) {
			if ( appUser.getObjectRef()==null || "".equals(appUser.getObjectRef()) )  // Vendor ID is required for Vendor and User Type
				quotation.addMessage(msgController.createMsg("error.MFE", "Object Reference"));
		}	
		
		// Proceed to creation if validation is successful
		if( quotation.getMessages().isEmpty() ) {
			
			appUser.setUsername(appUser.getUsername().toUpperCase());
			
			// Check if same UserName, App and Environment exists. For Admin user only UserName and App is checked			
			Pageable pageable = new PageRequest(0, 1000, Sort.Direction.ASC, "name");
			List<AppUser> appUsers = appUserRepository.adminFindByAppAndUserName(loginUser.getApp(), appUser.getUsername(), pageable);
			if ( appUsers.size()>0 ) {	
				quotation.addMessage(msgController.createMsg("error.AUAEE"));
			}
			
			String vendorId = "";
			if ( UserRole.VENDOR_ADMIN.equals(loginUser.getRole()) ) {
				vendorId = loginUser.getObjectRef();			
				Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
				if ( vendor == null) {
					quotation.addMessage(msgController.createMsg("error.VNFE"));
				} else {
					if ( !vendor.isActiveIndicator() ) {
						quotation.addMessage(msgController.createMsg("error.VNYAE"));
					} else {
						
						// Verify if exceed maximum limit						
						int appUserCount = appUserRepository.vendorFindAllAppUsers(loginUser.getApp(), vendorId, pageable).size();		
						//LOG.log(Level.INFO, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX:appUserCount=" + appUserCount + ", getMaxUserAllowed=" + vendor.getMaxUserAllowed());
						if ( appUserCount >= vendor.getMaxUserAllowed() ) {
							quotation.addMessage(msgController.createMsg("error.AUEML", vendor.getMaxUserAllowed()));								
						}						
					}	
				}
			} else {
				vendorId = appUser.getObjectRef();
			}
			
			
			if( quotation.getMessages().isEmpty() ) {
				
				appUser.setId(null);
				appUser.setRole(appUser.getRole());
				appUser.setActiveIndicator(false);
				appUser.setApp(loginUser.getApp());
				appUser.setObjectRef(vendorId);
				
				Util.initalizeCreatedInfo(appUser, loginUser.getUsername(), msgController.getMsg("info.AURC"));					
				appUserRepository.save(appUser);
				
				appUser.setPassword("[Protected]");
				quotation.setAppUser(appUser);
				
				quotation.addMessage(msgController.createMsg("info.AURC"));
				
			}
		}
		
		return quotation;
			
	}
	
	/**
	 * Update App User
	 * @param loginUser - Currently login user
	 * @param appUser - App User object
	 * @return
	 */
	public QuotationResponse updateAppUser(AppUser loginUser, AppUser appUser) {		
		LOG.log(Level.INFO, "Calling AppUser Service updateAppUser()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();
		
		// Validate mandatory fields
		if ( appUser.getId()==null || "".equals(appUser.getId()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "User ID"));
		if ( appUser.getUsername()==null || "".equals(appUser.getUsername()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "User Name"));
		if ( appUser.getName()==null || "".equals(appUser.getName()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Name"));				
		if ( appUser.getRole()==null )
			quotation.addMessage(msgController.createMsg("error.MFE", "Role"));
		if ( UserRole.APP_ADMIN.equals(loginUser.getRole()) ) {
			if ( appUser.getObjectRef()==null || "".equals(appUser.getObjectRef() ) )  // Vendor ID is required for Vendor and User Type
				quotation.addMessage(msgController.createMsg("error.MFE", "Object Reference"));

		}	
		
		// Proceed to creation if validation is successful
		if( quotation.getMessages().isEmpty() ) {
			
			appUser.setUsername(appUser.getUsername().toUpperCase());
			
			AppUser appUserRep = appUserRepository.findById(appUser.getId()).orElse(null);
			// Verify if App User exists
			if ( appUserRep==null ) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			}
			
			if( quotation.getMessages().isEmpty() ) {
				
				Util.initalizeUpdatedInfo(appUserRep, loginUser.getUsername(), appUserRep.getDifferences(appUser));	
				appUserRep.setActiveIndicator(appUser.isActiveIndicator());
				appUserRep.setName(appUser.getName());
				appUserRep.setRole(appUser.getRole());		
				if ( UserRole.APP_ADMIN.equals(loginUser.getRole()) ) {
					appUserRep.setObjectRef(appUser.getObjectRef());
				} else {
					appUserRep.setObjectRef(loginUser.getObjectRef());
				}	
				
				appUserRepository.save(appUserRep);
				
				appUserRep.setPassword("[Protected]");
				quotation.setAppUser(appUserRep);	
				
				quotation.addMessage(msgController.createMsg("info.AURU"));
				
			}
		}
		
		return quotation;
			
	}
	
	/**
	 * Update App User
	 * @param loginUser - Currently login user
	 * @param appUser - App User object
	 * @return
	 */
	public QuotationResponse generatePassword(AppUser loginUser, String userId) {		
		LOG.log(Level.INFO, "Calling AppUser Service generatePassword()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();
		
		// Validate mandatory fields
		if ( "".equals(userId) ) { 
			quotation.addMessage(msgController.createMsg("error.MFE", "User ID"));
		} else {
			
			AppUser appUserRep = appUserRepository.findById(userId).orElse(null);
			// Verify if App User exists
			if ( appUserRep==null ) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			} else {
				
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				String generatedPassword = PasswordGenerator.generateRandomPassword(8);
				
				Util.initalizeUpdatedInfo(appUserRep, loginUser.getUsername(), "Password reset");	
				appUserRep.setPassword(encoder.encode(generatedPassword));
				
				appUserRepository.save(appUserRep);
				
				appUserRep.setPassword(generatedPassword); // return the actual password and display to admin
				quotation.setAppUser(appUserRep);	
				
				quotation.addMessage(msgController.createMsg("info.AURU"));
				
			}
		}
		
		return quotation;
			
	}
	
	/**
	 * Delete App User
	 * @param loginUser - Currently login user
	 * @param appUserId - App User ID
	 * @return
	 */
	public QuotationResponse deleteAppUser(AppUser loginUser, String appUserId) {
		LOG.log(Level.INFO, "Calling AppUser Service deleteAppUser()");

		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();

		if (appUserId == null || "".equals(appUserId))
			quotation.addMessage(msgController.createMsg("error.MFE", "AppUser ID"));

		if (quotation.getMessages().isEmpty()) {

			AppUser appUserRep = appUserRepository.findById(appUserId).orElse(null);

			if (appUserRep == null) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			} else {
				
				if ( UserRole.VENDOR_ADMIN.equals(loginUser.getRole()) && loginUser.getObjectRef()!=null && !loginUser.getObjectRef().equals(appUserRep.getObjectRef()) ) {
					throw new ServiceAccessResourceFailureException();
				}
			
				appUserRepository.delete(appUserRep);
				quotation.addMessage(msgController.createMsg("info.AURD"));
				appUserRep.setPassword("[Protected]");
			}
			
			quotation.setAppUser(appUserRep);

		}

		return quotation;
	}
	
	/**
	 * Activate App User
	 * @param loginUser - Currently login user
	 * @param appUserId - App User ID
	 * @return
	 */
	public QuotationResponse activateAppUser(AppUser loginUser, String appUserId) {
		
		LOG.log(Level.INFO, "Calling AppUser Service activateAppUser()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();
		
		if ( appUserId == null || "".equals(appUserId) )
			quotation.addMessage(msgController.createMsg("error.MFE", "AppUser ID"));

		if (quotation.getMessages().isEmpty()) {

			AppUser appUserRep = appUserRepository.findById(appUserId).orElse(null);
			
			//System.out.println("***********" + appUserRep);

			if ( appUserRep == null ) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			} else {	
				
				if ( appUserRep.isActiveIndicator() ) {
					quotation.addMessage(msgController.createMsg("error.AUAAE"));
				} else {
					
					if ( UserRole.VENDOR_ADMIN.equals(loginUser.getRole()) && loginUser.getObjectRef()!=null && !loginUser.getObjectRef().equals(appUserRep.getObjectRef()) ) {
						throw new ServiceAccessResourceFailureException();
					}
					
					appUserRep.setActiveIndicator(true);
					Util.initalizeUpdatedInfo(appUserRep, loginUser.getUsername(), msgController.getMsg("info.AURA"));
					appUserRepository.save(appUserRep);
					quotation.addMessage(msgController.createMsg("info.AURA"));				
				}
				
				appUserRep.setPassword("[Protected]");

			}
			
			quotation.setAppUser(appUserRep);
			

		}
		
		return quotation;
	}
	
		
	/**
	 * DeActivate App User
	 * @param loginUser - Currently login user
	 * @param appUserId - App User ID
	 * @return
	 */
	public QuotationResponse deActivateAppUser(AppUser loginUser, String appUserId) {
		
		LOG.log(Level.INFO, "Calling AppUser Service deActivateAppUser()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();
		
		if (appUserId == null || "".equals(appUserId))
			quotation.addMessage(msgController.createMsg("error.MFE", "AppUser ID"));

		if (quotation.getMessages().isEmpty()) {

			AppUser appUserRep = appUserRepository.findById(appUserId).orElse(null);

			if (appUserRep == null) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			} else {	
				
				if ( !appUserRep.isActiveIndicator() ) {
					quotation.addMessage(msgController.createMsg("error.AUADAE"));
				} else {
					
					if ( UserRole.VENDOR_ADMIN.equals(loginUser.getRole()) && loginUser.getObjectRef()!=null && !loginUser.getObjectRef().equals(appUserRep.getObjectRef()) ) {
						throw new ServiceAccessResourceFailureException();
					}
					
					appUserRep.setActiveIndicator(false);
					Util.initalizeUpdatedInfo(appUserRep, loginUser.getUsername(), msgController.getMsg("info.AURDA"));
					appUserRepository.save(appUserRep);
					quotation.addMessage(msgController.createMsg("info.AURDA"));				
				}
				
				appUserRep.setPassword("[Protected]");

			}
			
			quotation.setAppUser(appUserRep);

		}
		
		return quotation;
	}
	
	/**
	 * DeActivate all App User under same Vendor
	 * @param loginUser - Currently login user
	 * @param vendor - Vendor ID
	 * @return
	 */
	public void deActivateAllAppUser(AppUser loginUser, String vendor) {

		Util.checkIfAlreadyActivated(loginUser);
		
		LOG.log(Level.INFO, "Calling AppUser Service deActivateAllAppUser()");		
		Pageable pageable = new PageRequest(0, 1000, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorFindAllAppUsers(loginUser.getApp(), vendor, pageable);
		for(AppUser appUser : listAppUser) {
			deActivateAppUser(loginUser, appUser.getId());
		}

	}
	
	/**
	 * Delete all App User under same Vendor
	 * @param loginUser - Currently login user
	 * @param vendor - Vendor ID
	 * @return
	 */
	public void deleteAllAppUser(AppUser loginUser, String vendor) {
		
		Util.checkIfAlreadyActivated(loginUser);

		LOG.log(Level.INFO, "Calling AppUser Service deActivateAllAppUser()");		
		Pageable pageable = new PageRequest(0, 1000, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorFindAllAppUsers(loginUser.getApp(), vendor, pageable);
		for(AppUser appUser : listAppUser) {
			deleteAppUser(loginUser, appUser.getId());
		}

	}
	
	/**
	 * Should not be called in the service. This is for unit testing purposes
	 * @return
	 */
	public QuotationResponse deleteAllAppUser() {

		LOG.log(Level.INFO, "Calling Vendor Service deleteAllAppUser()");
		QuotationResponse quotation = new QuotationResponse();
		appUserRepository.deleteAll();
		quotation.addMessage(msgController.createMsg("info.AAUSD"));
		return quotation;

	}
	
	public QuotationResponse resetPassword(AppUser loginUser, String appUserId, String newPassword) {
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();
		
		if (appUserId == null || "".equals(appUserId))
			quotation.addMessage(msgController.createMsg("error.MFE", "AppUser ID"));
		if (newPassword == null || "".equals(newPassword))
			quotation.addMessage(msgController.createMsg("error.MFE", "Password"));

		if (quotation.getMessages().isEmpty()) {

			AppUser appUserRep = appUserRepository.findById(appUserId).orElse(null);

			if (appUserRep == null) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			} else {	
					
				if ( UserRole.VENDOR_ADMIN.equals(loginUser.getRole()) 
						&& loginUser.getObjectRef()!=null 
						&& !loginUser.getObjectRef().equals(appUserRep.getObjectRef()) ) {
					throw new ServiceAccessResourceFailureException();
				}
				
				if ( UserRole.VENDOR_USER.equals(loginUser.getRole()) 
						&& loginUser.getObjectRef()!=null 
						&& !loginUser.getObjectRef().equals(appUserRep.getObjectRef()) 
						&& !loginUser.getId().equals(appUserRep.getId())) {
					throw new ServiceAccessResourceFailureException();
				}
				
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				
				appUserRep.setPassword(encoder.encode(newPassword));
				
				Util.initalizeUpdatedInfo(appUserRep, loginUser.getUsername(), msgController.getMsg("info.AUPR"));
				appUserRepository.save(appUserRep);
				quotation.addMessage(msgController.createMsg("info.AUPR"));	
				appUserRep.setPassword("[Protected]");
			
				quotation.setAppUser(appUserRep);				
			}
		}
		return quotation;		
	}
	
	
	
}
