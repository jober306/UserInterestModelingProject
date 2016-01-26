package iva.client.core.services;

import iva.client.core.model.User;
import iva.client.exceptions.AuthenticationException;

public interface UserService {

	User create(String username, char[] password, String firstName, String lastName);

	User authenticate(String username, char[] password) throws AuthenticationException;

	User updatePassword(String username, char[] oldPassword, char[] newPassword) throws AuthenticationException;

	User update(User user);

	void delete(User user);

}