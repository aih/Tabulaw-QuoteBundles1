/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tll.tabulaw.di;

import org.springframework.security.userdetails.UserDetailsService;

import com.tll.di.AcegiModule;
import com.tll.tabulaw.service.entity.UserService;


/**
 * 
 * @author jpk
 */
public class TabulawSecurityModule extends AcegiModule {

	@Override
	protected Class<? extends UserDetailsService> getUserDetailsImplType() {
		return UserService.class;
	}

}
