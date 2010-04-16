/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.di;

import org.springframework.security.userdetails.UserDetailsService;

import com.tabulaw.service.entity.UserService;
import com.tll.di.AcegiModule;

/**
 * @author jpk
 */
public class TabulawSecurityModule extends AcegiModule {

	@Override
	protected Class<? extends UserDetailsService> getUserDetailsImplType() {
		return UserService.class;
	}
}
